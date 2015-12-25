package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.context.AppContext;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Student;
import com.vipkid.model.StudentPerformance;
import com.vipkid.model.TeacherComment;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.CustomizedPrincipal;
import com.vipkid.security.SecurityService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.TeacherCommentView;

@Service
public class TeacherCommentService {
	private Logger logger = LoggerFactory.getLogger(TeacherCommentService.class.getSimpleName());

	@Resource
	private TeacherCommentRepository teacherCommentRepository;

	@Resource
	private StudentRepository studentRepository;

	@Resource
	private SecurityService securityService;

	public TeacherComment find(long id) {
		logger.info("find teacherComment for id = {}", id);
		return teacherCommentRepository.find(id);
	}

	public List<TeacherComment> findTByTeacherIdAndTimeRange(long teacherId, @QueryParam("beginDate") DateParam startDate, @QueryParam("endDate") DateParam endDate) {
		logger.info("find TeacherComments for teacherId = {}, startDate = {}, endDate = {}", teacherId, startDate, endDate);
		return teacherCommentRepository.findByTeacherIdAndTimeRange(teacherId, startDate.getValue(), endDate.getValue());
	}

	public List<TeacherComment> findByStudentId(long studentId) {
		logger.info("find TeacherComments for studentId = {}", studentId);
		return teacherCommentRepository.findByStudentId(studentId);
	}

	// @GET
	// @Path("/findByStudentIdAndTimeRange")
	// public List<TeacherComment> findByStudentIdAndTimeRange( long studentId,
	// @QueryParam("beginDate") DateParam startDate, @QueryParam("endDate")
	// DateParam endDate) {
	// logger.info("find TeacherComments for studentId = {}, startDate = {}, endDate = {}",
	// studentId, startDate, endDate);
	// List<TeacherComment> teacherComments =
	// teacherCommentRepository.findByStudentIdAndTimeRange(studentId,
	// startDate.getValue(), endDate.getValue());
	// List<OnlineClass> lessonHistories =
	// onlineClassAccessor.findAsScheduledByStudentIdAndStatusAndStartDateAndEndDate(studentId,
	// OnlineClass.Status.FINISHED, startDate.getValue(), endDate.getValue());
	//
	// for (TeacherComment teacherComment : teacherComments) {
	// if (lessonHistories.contains(teacherComment.getOnlineClass())) {
	// lessonHistories.remove(teacherComment.getOnlineClass());
	// }
	// }
	//
	// for (OnlineClass lh : lessonHistories) {
	// TeacherComment teacherComment = new TeacherComment();
	// teacherComment.setOnlineClass(lh);
	// teacherComments.add(teacherComment);
	// }
	//
	// return teacherComments;
	// }

	public List<TeacherComment> findRecentByStudentIdAndAmount(long studentId, long amount) {
		logger.info("find TeacherComments for studentId = {}, amount = {}", studentId, amount);
		return teacherCommentRepository.findRecentByStudentIdAndAmount(studentId, amount);
	}

	public List<TeacherComment> findRecentByStudentIdAndClassIdAndAmount(long studentId, long onlineClassId, long amount) {
		logger.info("find TeacherComments for studentId = {}, onlineClassId={}, amount = {}", studentId, onlineClassId, amount);
		return teacherCommentRepository.findRecentByStudentIdAndClassIdAndAmount(studentId, onlineClassId, amount);
	}

	public TeacherComment findByOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		logger.info("find TeacherComments for studentId = {}, amount = {}", onlineClassId);
		return teacherCommentRepository.findByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}

	public TeacherCommentView findTeacherCommentViewByOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		logger.info("find TeacherComments for studentId = {}, amount = {}", onlineClassId);
		return teacherCommentRepository.findTeacherCommentViewByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}

	public List<TeacherComment> list(Boolean empty, Long courseId, Long teacherId, Long studentId, Integer start, Integer length) {
		logger.info("list teacherComment with params: empty = {}, courseId = {}, teacherId = {}, studentId = {}, start = {}, length = {}.", empty, courseId, teacherId, studentId, start, length);
		return teacherCommentRepository.list(empty, courseId, teacherId, studentId, start, length);
	}

	public Count count(Boolean empty, Long courseId, Long teacherId, Long studentId) {
		logger.info("list teacherComment with params: empty = {}, courseId = {}, teacherId = {}, studentId = {}.", empty, courseId, teacherId, studentId);
		return new Count(teacherCommentRepository.count(empty, courseId, teacherId, studentId));
	}

	public TeacherComment updateStar(TeacherComment teacherComment) {
		if (teacherComment == null){
			return null;
		}

		TeacherComment foundTeacherComment = teacherCommentRepository.find(teacherComment.getId());
		if (foundTeacherComment == null){
			return null;
		}
		
		if (teacherComment.getStars() < 0){
			teacherComment.setStars(0);
		}
		
		if (teacherComment.getStars() > 5){
			teacherComment.setStars(5);
		}

		int delta = teacherComment.getStars() - foundTeacherComment.getStars();
		logger.info("Update stars from {} to {} in classroom={}", foundTeacherComment.getStars(), teacherComment.getStars(), foundTeacherComment.getOnlineClass().getClassroom());
		Student student = studentRepository.find(foundTeacherComment.getStudent().getId());
		student.setStars(student.getStars() + delta);
		studentRepository.update(student);
		foundTeacherComment.setStars(teacherComment.getStars());
		teacherCommentRepository.update(foundTeacherComment);
		
		return foundTeacherComment;
	}

	public Response update(TeacherComment teacherComment) {
		logger.info("update teacherComment: {}", teacherComment);

		try {
			securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_UPDATE, "Request updating teacherComment: " + teacherComment.getId());

			TeacherComment foundTeacherComment = teacherCommentRepository.find(teacherComment.getId());

			if (StringUtils.isNotBlank(teacherComment.getTeacherFeedback()) || StringUtils.isNotBlank(teacherComment.getTipsForOtherTeachers()) || StringUtils.isNotBlank(teacherComment.getReportIssues())
					|| teacherComment.getAbilityToFollowInstructions() != 0 || teacherComment.getRepetition() != 0 || teacherComment.getClearPronunciation() != 0
					|| teacherComment.getReadingSkills() != 0 || teacherComment.getActivelyInteraction() != 0) {
				foundTeacherComment.setEmpty(false);

				foundTeacherComment.setTeacherFeedback(teacherComment.getTeacherFeedback());
				foundTeacherComment.setTipsForOtherTeachers(teacherComment.getTipsForOtherTeachers());
				foundTeacherComment.setReportIssues(teacherComment.getReportIssues());
				foundTeacherComment.setAbilityToFollowInstructions(teacherComment.getAbilityToFollowInstructions());
				foundTeacherComment.setRepetition(teacherComment.getRepetition());
				foundTeacherComment.setClearPronunciation(teacherComment.getClearPronunciation());
				foundTeacherComment.setReadingSkills(teacherComment.getReadingSkills());
				foundTeacherComment.setActivelyInteraction(teacherComment.getActivelyInteraction());
				foundTeacherComment.setSpellingAccuracy(teacherComment.getSpellingAccuracy());
				// performance
				foundTeacherComment.setPerformance(teacherComment.getPerformance());
				// 2015-08-31 Trial exam Result 
				if (foundTeacherComment.getCourseType() == Course.Type.TRIAL) {
					String trialLevelExamResult = teacherComment.getTrialLevelExamResult();
					foundTeacherComment.setTrialLevelExamResult(trialLevelExamResult.toUpperCase());
				}
			}
			foundTeacherComment = teacherCommentRepository.update(foundTeacherComment);
			securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_UPDATE, "Update teacherComment for onlineClass: " + foundTeacherComment.getOnlineClass().getOnlineClassName());
			
			TeacherComment updatedTeacherComment = teacherCommentRepository.find(teacherComment.getId());
			if (updatedTeacherComment != null && updatedTeacherComment.isEmpty()) {
				logger.error("[VK-1713]User {} just updated teacher comment {}, but the isEmpty is still true", ((CustomizedPrincipal)AppContext.getPrincipal()).getUser().getId(), updatedTeacherComment.getId());
			}
//			// 2015-07-02 计算更新student的current performance
//			long studentId = foundTeacherComment.getStudent().getId();
//			TeacherComment teacherCommentNew = this.updateTeacherCommentPerformanceByStudentId(studentId);
//			if (teacherCommentNew != null) {
//				// 更新student
//				student.setCurrentPerformance(teacherComment.getCurrentPerforance());
//				studentRepository.update(student);
//			}
			
		} catch (Exception e) {
			logger.error("Found exception when updating TeacherComment={}", teacherComment.getTeacherCommentContent(), e);
			securityService.logAudit(Level.ERROR, Category.TEACHER_COMMENT_UPDATE, "Failed to update teacherComment for onlineClass: " + teacherComment.getTeacherCommentContent());
		}

		return new Response(HttpStatus.OK.value());
	}

	public Response create(TeacherComment teacherComment) {
		logger.info("create teacherComment: {}", teacherComment);

		TeacherComment commentFound = this.find(teacherComment.getId());
		if (commentFound != null) {
			commentFound.setAbilityToFollowInstructions(teacherComment.getAbilityToFollowInstructions());
			commentFound.setActivelyInteraction(teacherComment.getActivelyInteraction());
			commentFound.setClearPronunciation(teacherComment.getClearPronunciation());
			commentFound.setTipsForOtherTeachers(teacherComment.getTipsForOtherTeachers());
			commentFound.setReadingSkills(teacherComment.getReadingSkills());
			commentFound.setRepetition(teacherComment.getRepetition());
			commentFound.setSpellingAccuracy(teacherComment.getSpellingAccuracy());
			commentFound.setTeacherFeedback(teacherComment.getTeacherFeedback());
			commentFound.setReportIssues(teacherComment.getReportIssues());
			commentFound.setSpellingAccuracy(teacherComment.getSpellingAccuracy());
			// performance
			commentFound.setPerformance(teacherComment.getPerformance());
			teacherCommentRepository.update(commentFound);
			securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_UPDATE, "Update teacherComment for onlineClass: " + teacherComment.getOnlineClass().getSerialNumber());
		} else {
			teacherCommentRepository.create(teacherComment);
			securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_CREATE, "Create teacherComment for onlineClass: " + teacherComment.getOnlineClass().getSerialNumber());
		}

		return new Response(HttpStatus.OK.value());
	}
	
	public List<TeacherComment> findByTeacherIdAndTimeRange(long teacherId, Date startDate, Date endDate) {
		logger.info("find teacherComment for id = {}, startDate = {}, endDate = {}.", teacherId, startDate, endDate);
		return teacherCommentRepository.findByTeacherIdAndTimeRange(teacherId, startDate, endDate);
	}
	
	public long findStarsByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		logger.info("find stars for id = {}, startDate = {}, endDate = {}.", studentId, startDate, endDate);
		return teacherCommentRepository.findStarsByStudentIdAndTimeRange(studentId, startDate, endDate);
	}
	
	/**
	 * 2015-06-30
	 * 获取该学生的performance值。最多获取最近3次 finished as scheduled的performance 值，计算得到平均值，进行评判，并保持
	 * @param studentId
	 * @return
	 */
	public List<Long>  getStudentListForPerformance() {
		// student --> [teacherComments-->onlineClass]
		return teacherCommentRepository.findStudentId4UpdateCurrentPerformance();
	}
	
	/**
	 * 获得指定学生的teacherComment记录，进行current performance设置,并更新
	 * @param studentId
	 * @return
	 */
	public TeacherComment updateTeacherCommentPerformanceByStudentId(long studentId) {
		return teacherCommentRepository.findAndUpdateByStudentId_CurrentPerformance(studentId);
	}
	
	/**
	 * 2015-06-30
	 * 获取该学生的performance值。最多获取最近3次 finished as scheduled的performance 值，计算得到平均值，进行评判，并保持
	 * @param studentId
	 * @return
	 */
	public TeacherComment commentPerformance(TeacherComment teacherComment, StudentPerformance studentPerformance ) {
		teacherComment.setCurrentPerforance(studentPerformance);
		teacherComment = teacherCommentRepository.update(teacherComment);
		return teacherComment;
	}
	
	/**
	 * 
	* @Title: findTeacherCommentByIds 
	* @Description: 判断公开课是否领取了星星
	* @param parameter
	* @author zhangfeipeng 
	* @return TeacherComment
	* @throws
	 */
	public TeacherComment findTeacherCommentByIds(long teacheId,long onlineClassId,long studentId){
		logger.info("findTeacherCommentByIds teacheId={},onlineClassId={},studentId={}",teacheId,onlineClassId,studentId);
		return teacherCommentRepository.findTeacherCommentByIds(teacheId, onlineClassId, studentId);
	}
	
}
