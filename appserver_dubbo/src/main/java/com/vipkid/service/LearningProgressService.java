package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.vipkid.ext.dby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.User;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.SecurityService;
import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;

@Service
public class LearningProgressService {
	private Logger logger = LoggerFactory.getLogger(LearningProgressService.class.getSimpleName());
	
	@Resource
	private LearningProgressRepository learningProgressRepository;
	
	@Resource
	private LessonRepository lessonRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private SecurityService securityService;
	
	public List<LearningProgress> findByStudentId(long studentId) {
		logger.debug("find learningProgress for studentId = {}",  studentId);
		List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentId(studentId);
		for(LearningProgress learningProgress : learningProgresses){
			List<OnlineClass> onlineClasses = learningProgress.getCompletedOnlineClasses();
			OnlineClass firstCompletedOnlineClass = onlineClassRepository.findFirstByTimeInOnlineClassList(onlineClasses);
			OnlineClass lastCompletedOnlineClass = onlineClassRepository.findLastByTimeInOnlineClassList(onlineClasses);
			learningProgress.setFirstCompletedOnlineClass(firstCompletedOnlineClass);
			learningProgress.setLastCompletedOnlineClass(lastCompletedOnlineClass);
		}
		return learningProgresses;
	}

    /**
     * 查找已经开始的learning progress
     * @param studentId
     * @return
     */
    public List<LearningProgress> findStartedByStudentId(long studentId) {
        logger.debug("find learningProgress for studentId = {}",  studentId);
        List<LearningProgress> learningProgresses = learningProgressRepository.findByStudentIdAndStatus(studentId, LearningProgress.Status.STARTED);
        for(LearningProgress learningProgress : learningProgresses){
            List<OnlineClass> onlineClasses = learningProgress.getCompletedOnlineClasses();
            OnlineClass firstCompletedOnlineClass = onlineClassRepository.findFirstByTimeInOnlineClassList(onlineClasses);
            OnlineClass lastCompletedOnlineClass = onlineClassRepository.findLastByTimeInOnlineClassList(onlineClasses);
            learningProgress.setFirstCompletedOnlineClass(firstCompletedOnlineClass);
            learningProgress.setLastCompletedOnlineClass(lastCompletedOnlineClass);
        }
        return learningProgresses;
    }
	
	public LearningProgress findByStudentId(long studentId, long courseId) {
		logger.debug("Find learningProgress by studentId = {}, courseId={} ", studentId, courseId);
		return learningProgressRepository.findByStudentIdAndCourseId(studentId, courseId);
	}
	
	public List<LearningProgress> findNotMajorByStudentId(long studentId) {
		logger.debug("Find learningProgress by studentId = {}", studentId);
		return learningProgressRepository.findNotMajorByStudentId(studentId);
	}
	
	public List<LearningProgress> findMajorByStudentId(long studentId) {
		logger.debug("Find learningProgress by studentId = {}", studentId);
		return learningProgressRepository.findMajorByStudentId(studentId);
	}
	
	public List<LearningProgress> findLeftClassHourByStudentIdWithoutTestClass(long studentId) {
		logger.debug("Find learningProgress by studentId = {}", studentId);
		return learningProgressRepository.findLeftClassHourByStudentIdWithoutTestClass(studentId);
	}
	
	public LearningProgress update(LearningProgress learningProgressFromJson) {	
		LearningProgress learningProgress = learningProgressRepository.find(learningProgressFromJson.getId());
		refreshLearningProgress(learningProgress, learningProgressFromJson);
		learningProgressRepository.update(learningProgress);
		securityService.logAudit(Level.INFO, Category.LEARNING_PROGRESS_UPDATE, "Change learning progress for student: " + learningProgress.getStudent().getName());
		return learningProgress;
	}
    public Response findPracticumByStudentId(long studentId){
        LearningProgress learningProgress = learningProgressRepository.findPracticumByStudentId(studentId);
        learningProgress.setCompletedOnlineClasses(null);
        learningProgress.setLeftAvaiableClassHour(10);
        learningProgress.setTotalClassHour(10);
        learningProgress.setNextShouldTakeLesson(null);
        learningProgress.setStatus(LearningProgress.Status.STARTED);
        learningProgressRepository.update(learningProgress);
        return new Response(HttpStatus.OK.value());
    }
	
	public LearningProgress doReSchedule(LearningProgress learningProgress){		
		LearningProgress dbLearningProgress = learningProgressRepository.find(learningProgress.getId());
		
		if (dbLearningProgress == null){
			throw new IllegalStateException("LearningProgress must exist!");
		}
		
		//fetch from db due to the lesson was cut off
		Lesson nextShouldTakeLesson = lessonRepository.find(learningProgress.getNextShouldTakeLesson().getId());
		
		if (nextShouldTakeLesson == null){
			throw new IllegalStateException("nextShouldTakeLesson must exist!");
		}
		
		Course course = dbLearningProgress.getCourse();
		
		OnlineClass lastCompletedOnlineClass = onlineClassRepository.findLastByTimeInOnlineClassList(dbLearningProgress.getCompletedOnlineClasses());
		Lesson lastScheduledLesson = lessonRepository.findPrevByCourseIdAndSequence(course.getId(), nextShouldTakeLesson.getSequence());
		
		Date lastCompletedDateTime = null;
		if (lastCompletedOnlineClass != null){
			lastCompletedDateTime  = lastCompletedOnlineClass.getScheduledDateTime();
		} 		
		
		
		List<OnlineClass> onlineClasses = onlineClassRepository.findLaterBookedByStudentIdAndCourseId(dbLearningProgress.getStudent().getId(), course.getId(), lastCompletedDateTime);
		logger.info("There are {} classes need to be rescheduled", onlineClasses.size());
		if(!onlineClasses.isEmpty()) {
			final int length = onlineClasses.size();
			Lesson nextLesson = nextShouldTakeLesson;
			for(int i = 0; i <length; i++) {
				OnlineClass tmpOnlineClass = onlineClasses.get(i);
				tmpOnlineClass.setLesson(nextLesson);
				lastScheduledLesson = nextLesson;
				nextLesson = lessonRepository.findNextByCourseIdAndSequence(course.getId(), nextLesson.getSequence());
				tmpOnlineClass.setLastEditor(securityService.getCurrentUser());
				onlineClassRepository.update(tmpOnlineClass);
				
				reScheduleDBYClassroom(tmpOnlineClass);
			}
		
		}
		
		Lesson origNextShouldTakeLesson = dbLearningProgress.getNextShouldTakeLesson();
		String origLessonSN = "";
		if (origNextShouldTakeLesson != null && origNextShouldTakeLesson.getSerialNumber() != null){
			origLessonSN = origNextShouldTakeLesson.getSerialNumber();
		}
		dbLearningProgress.setNextShouldTakeLesson(nextShouldTakeLesson);
		dbLearningProgress.setLastScheduledLesson(lastScheduledLesson);
		learningProgressRepository.update(dbLearningProgress);
		
		String operation = "Re-scheduled the LearingProgress from the lesson: " + origLessonSN + " to the lesson:" + nextShouldTakeLesson.getSerialNumber() +  " for student:" + dbLearningProgress.getStudent().getName();
		securityService.logAudit(Level.INFO, Category.STUDENT_LEARNING_PROGRESS_UPDATE, operation);
		return dbLearningProgress;		
	}
	
	private void reScheduleDBYClassroom(final OnlineClass reScheduledOnlineClass) {
		User user = securityService.getCurrentUser();
		reScheduledOnlineClass.setLastEditor(user);
		if (!TextUtils.isEmpty(reScheduledOnlineClass.getClassroom())) {
			UpdateRoomTitleResult updateRoomTitleResult = DBYAPI.updateRoomTitle(reScheduledOnlineClass.getClassroom(), reScheduledOnlineClass
					.getLesson().getName());
			if (updateRoomTitleResult.isSuccess()) {
				boolean gotDocument = false;
				ListDocumentsResult results = DBYAPI.listDocuments(reScheduledOnlineClass.getClassroom());
				if (results.isSuccess()) {
					for (int i = 0; i < results.getDocuments().size(); i++) {
						Document doc = results.getDocuments().get(i);
						if (doc != null && doc.getDocumentId() != null) {
							if (doc.getDocumentId().equals(reScheduledOnlineClass.getLesson().getDbyDocument())) {
								gotDocument = true;
								reScheduledOnlineClass.setDbyDocument(reScheduledOnlineClass.getLesson().getDbyDocument());
							} else {
								DBYAPI.removeDocument(reScheduledOnlineClass.getClassroom(), doc.getDocumentId());
								securityService.logAudit(Level.WARNING, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "remove document! ,teacher name"
										+ reScheduledOnlineClass.getTeacher().getName() + "serial number:" + reScheduledOnlineClass.getSerialNumber(),user);
							}
						}
					}
				}
				if (!gotDocument) {
					AttachDocumentResult attachDocumentResult = DBYAPI.attachDocument(reScheduledOnlineClass.getClassroom(), reScheduledOnlineClass.getLesson()
                            .getDbyDocument());
					securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE,
							"Update classroom for reScheduledOnlineClass: coming ......................" + reScheduledOnlineClass.getSerialNumber(), user);

					if (attachDocumentResult.isSuccess()) {
						reScheduledOnlineClass.setDbyDocument(attachDocumentResult.getDocumentId());
						securityService.logAudit(Level.INFO, Category.ONLINE_CLASS_CLASSROOM_UPDATE, "Update classroom for reScheduledOnlineClass: "
								+ reScheduledOnlineClass.getSerialNumber(), user);
						reScheduledOnlineClass.setAttatchDocumentSucess(true);
					} else {
						//String error = attatchDocumentResult.getError();
						// if(!"repeat_arrange_to_course_error".equals(error))
						{
							reScheduledOnlineClass.setAttatchDocumentSucess(false);
							securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE,
									"Fail to update DuoBeiYun classroom document for online class id: " + reScheduledOnlineClass.getId() + ", the error code is: "
											+ attachDocumentResult.getError(), user);
							EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.FIRE_MAN, reScheduledOnlineClass,
                                    attachDocumentResult, user, "update");
							EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.TESTERHOU, reScheduledOnlineClass,
                                    attachDocumentResult, user, "update");
							EMail.sendToFiremanAttatchDocumentFailReminder(Configurations.EMail.DEVDENG, reScheduledOnlineClass,
                                    attachDocumentResult, user, "update");
						}
					}
					onlineClassRepository.update(reScheduledOnlineClass);
				}
			} else {
				securityService.logAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_UPDATE,
						"Fail to update DuoBeiYun classroom title for online class id: " + reScheduledOnlineClass.getId() + ", the error code is: "
								+ updateRoomTitleResult.getError());
			}
		}
	}
	
	private void refreshLearningProgress(final LearningProgress learningProgress, final LearningProgress LearningProgressFromJson){
		List<OnlineClass> dbCompletedOnlineClasses = LearningProgressFromJson.getCompletedOnlineClasses();
		BeanUtils.copyProperties(LearningProgressFromJson, learningProgress);
		learningProgress.setCompletedOnlineClasses(dbCompletedOnlineClasses);
	}
	
	public LearningProgress findByStudentIdAndCourseType(long studentId){
		logger.debug("find learningProgress for studentId = {}",  studentId);
		return learningProgressRepository.findByStudentIdAndCourseType(studentId);
	}
	
	
}
