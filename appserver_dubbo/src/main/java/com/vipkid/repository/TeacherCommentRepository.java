package com.vipkid.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Course_;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.LearningCycle_;
import com.vipkid.model.Lesson;
import com.vipkid.model.Lesson_;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.OnlineClass_;
import com.vipkid.model.Student;
import com.vipkid.model.StudentPerformance;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.TeacherComment_;
import com.vipkid.model.Unit;
import com.vipkid.model.Unit_;
import com.vipkid.model.User_;
import com.vipkid.service.pojo.TeacherCommentView;
import com.vipkid.util.DateTimeUtils;

@Repository
public class TeacherCommentRepository extends BaseRepository<TeacherComment> {

	private static final Logger logger = LoggerFactory.getLogger(TeacherCommentRepository.class);
	 
	public TeacherCommentRepository() {
		super(TeacherComment.class);
	}

	public List<TeacherComment> list(Boolean empty, Long courseId, Long teacherId, Long studentId, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TeacherComment> criteriaQuery = criteriaBuilder.createQuery(TeacherComment.class).distinct(true);
		Root<TeacherComment> teacherComment = criteriaQuery.from(TeacherComment.class);
				
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// 只列出finish的数据
		Join<TeacherComment, OnlineClass> notCancelledOnlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
		andPredicates.add(criteriaBuilder.equal(notCancelledOnlineClass.get(OnlineClass_.status), OnlineClass.Status.FINISHED));
				
		if (empty != null) {
			andPredicates.add(criteriaBuilder.equal(teacherComment.get(TeacherComment_.empty), empty));
		}
		if (courseId != null) {
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Lesson> lesson = onlineClass.join(OnlineClass_.lesson, JoinType.LEFT);
			Join<Lesson, LearningCycle> learningCycle = lesson.join(Lesson_.learningCycle, JoinType.LEFT);
			Join<LearningCycle, Unit> unit = learningCycle.join(LearningCycle_.unit, JoinType.LEFT);
			Join<Unit, Course> course = unit.join(Unit_.course, JoinType.LEFT);
			
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
		}
		if (teacherId != null) {
			Join<TeacherComment, Teacher> teacher = teacherComment.join(TeacherComment_.teacher, JoinType.LEFT);			
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
			// 保证teacher comment 的 teacher 和 onlineClass 的 teacher 是同一个
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Teacher> onlineClassTeacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), onlineClassTeacher.get(User_.id)));
		}
		if (studentId != null) {
			Join<TeacherComment, Student> student = teacherComment.join(TeacherComment_.student, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
			// 保证teacher comment 的 student 和 onlineClass 的 student 是同一个
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Student> onlineClassStudents = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), onlineClassStudents.get(User_.id)));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
        if (CollectionUtils.isNotEmpty(andPredicates)) {
            criteriaQuery.where(andPredicate);
        }

		criteriaQuery.orderBy(criteriaBuilder.desc(teacherComment.get(TeacherComment_.createDateTime)));
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(criteriaQuery);
		if(start == null) {
			start = 0;
		}
		if(length == null) {
			length = 10;
		}
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();		
	}

	public long count(Boolean empty, Long courseId, Long teacherId, Long studentId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<TeacherComment> teacherComment = criteriaQuery.from(TeacherComment.class);
		criteriaQuery.select(criteriaBuilder.count(teacherComment));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// 只列出finish的数据
		Join<TeacherComment, OnlineClass> notCancelledOnlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
		andPredicates.add(criteriaBuilder.equal(notCancelledOnlineClass.get(OnlineClass_.status), OnlineClass.Status.FINISHED));
						
		if (empty != null) {
			andPredicates.add(criteriaBuilder.equal(teacherComment.get(TeacherComment_.empty), empty));
		}
		if (courseId != null) {
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Lesson> lesson = onlineClass.join(OnlineClass_.lesson, JoinType.LEFT);
			Join<Lesson, LearningCycle> learningCycle = lesson.join(Lesson_.learningCycle, JoinType.LEFT);
			Join<LearningCycle, Unit> unit = learningCycle.join(LearningCycle_.unit, JoinType.LEFT);
			Join<Unit, Course> course = unit.join(Unit_.course, JoinType.LEFT);
			
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
		}
		if (teacherId != null) {
			Join<TeacherComment, Teacher> teacher = teacherComment.join(TeacherComment_.teacher, JoinType.LEFT);			
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
			// 保证teacher comment 的 teacher 和 onlineClass 的 teacher 是同一个
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Teacher> onlineClassTeacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), onlineClassTeacher.get(User_.id)));
		}
		if (studentId != null) {
			Join<TeacherComment, Student> student = teacherComment.join(TeacherComment_.student, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
			// 保证teacher comment 的 student 和 onlineClass 的 student 是同一个
			Join<TeacherComment, OnlineClass> onlineClass = teacherComment.join(TeacherComment_.onlineClass, JoinType.LEFT);
			Join<OnlineClass, Student> onlineClassStudents = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), onlineClassStudents.get(User_.id)));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		criteriaQuery.where(andPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();		
	}

	public List<TeacherComment> findByOnlineClassId(long onlineClassId) {
		String sql = "SELECT t FROM TeacherComment t WHERE t.onlineClass.id = :onlineClassId";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		return typedQuery.getResultList();
	}

	public List<TeacherComment> findByTeacherIdAndTimeRange(long teacherId, Date startDate, Date endDate) {
		String sql = "SELECT tc FROM TeacherComment tc WHERE tc.onlineClass.teacher.id = :teacherId AND tc.onlineClass.scheduledDateTime >= :startDate AND tc.onlineClass.scheduledDateTime <= :endDate ORDER BY tc.onlineClass.scheduledDateTime DESC";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
	}
	
	public List<TeacherComment> findByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		String sql = "SELECT tc FROM TeacherComment tc WHERE tc.onlineClass.student.id = :studentId AND "
				+ "tc.onlineClass.scheduledDateTime >= :startDate AND tc.onlineClass.scheduledDateTime <= :endDate";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
	}

	public List<TeacherComment> findRecentByStudentIdAndAmount(long studentId, long amount) {

		String sql = "SELECT tc FROM TeacherComment tc JOIN tc.onlineClass toc JOIN toc.students tocss WHERE tocss.id = :studentId ORDER BY toc.scheduledDateTime DESC";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setFirstResult(1);
		typedQuery.setMaxResults((int)amount);
		
		return typedQuery.getResultList();
	}
	
	public List<TeacherComment> findRecentByStudentIdAndClassIdAndAmount(long studentId, Long onlineClassId, long amount) {

		String sql = "SELECT tc FROM TeacherComment tc JOIN tc.onlineClass toc JOIN toc.students tocss WHERE tc.onlineClass.id = :onlineClassId AND tocss.id = :studentId ORDER BY toc.scheduledDateTime DESC";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults((int)amount);
		
		return typedQuery.getResultList();
	}

	public TeacherComment findByOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		String sql = "SELECT t FROM TeacherComment t WHERE t.onlineClass.id = :onlineClassId AND t.student.id = :studentId";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);

		if (typedQuery.getResultList() != null) {
			if (typedQuery.getResultList().size() > 0) {
				return typedQuery.getResultList().get(0);
			} else
				return null;
		}
		return null;
	}
	/**
	 * 2015-07-06 增加了performance
	 * @param onlineClassId
	 * @param studentId
	 * @return
	 */
	public TeacherCommentView findTeacherCommentViewByOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		String sql = "SELECT NEW com.vipkid.service.pojo.TeacherCommentView(t.id, t.abilityToFollowInstructions, t.repetition, t.clearPronunciation, t.readingSkills, t.spellingAccuracy, t.activelyInteraction, t.teacherFeedback, t.tipsForOtherTeachers, t.reportIssues, t.stars, t.performance) FROM TeacherComment t WHERE t.onlineClass.id = :onlineClassId AND t.student.id = :studentId";
		TypedQuery<TeacherCommentView> typedQuery = entityManager.createQuery(sql, TeacherCommentView.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);

		if (typedQuery.getResultList() != null) {
			if (typedQuery.getResultList().size() > 0) {
				return typedQuery.getResultList().get(0);
			} else
				return null;
		}
		return null;
	}

	public List<TeacherComment> findByStudentId(long studentId) {

		String sql = "SELECT tc FROM TeacherComment tc JOIN tc.onlineClass toc JOIN toc.students tocss WHERE tocss.id = :studentId ORDER BY toc.scheduledDateTime DESC";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}
	
	
	/**
	 * 
	 * @param studentId
	 * @return
	 */
	public long  findMaxIdByStudentId_CurrentPerformance(long studentId) {

		String sql = "SELECT Max(tc.id) FROM TeacherComment tc  WHERE tc.student.id = :studentId ORDER BY tc.id asc and tc.currentPerforance != :performance";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("performance", StudentPerformance.NOPERF);
		
		if (typedQuery.getResultList() != null) {
			if (typedQuery.getResultList().size() > 0) {
				Long tcId = typedQuery.getResultList().get(0);
				return tcId.longValue();
			} 
		}
		
		return 0l;
	}
	
	/**
	 * 获取可以进行更新current_performance操作  != :performance
	 * @return
	 */
	public List<Long>  findStudentId4UpdateCurrentPerformance() {

		String sql = "SELECT distinct(tc.student.id) FROM TeacherComment tc JOIN tc.onlineClass oc  WHERE oc.finishType = :finishType"
				+ " and tc.performance>0 and  tc.currentPerformance is null ORDER BY tc.id asc ";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("finishType", FinishType.AS_SCHEDULED);
//		typedQuery.setParameter("performance", StudentPerformance.NOPERF);
		
		if (typedQuery.getResultList() != null) {
			if (typedQuery.getResultList().size() > 0) {
				return typedQuery.getResultList();
				
			} 
		}
		
		return null;
	}
	
	/**
	 * 计算performance评级--根据performance得分
	 * @param nScore
	 */
	final static int kStudentPerformanceScoreBlow = 6;
	final static int kStudentPerformanceScoreAbove = 12;
	
	private StudentPerformance getPerformanceWithScore(int nScore) {
		if (nScore>=kStudentPerformanceScoreAbove) {
			return StudentPerformance.ABOVE;
		}
		
		if (nScore<=kStudentPerformanceScoreBlow) {
			return StudentPerformance.BELOW;
		}
		
		return StudentPerformance.ONTARGET;
	}
	
	/**
	 * 获得指定学生的teacherComment记录，进行current performance设置
	 * 返回的teacher comment数据已经修改了currentPerformance
	 * @param studentId
	 * @return
	 */
	public TeacherComment findAndUpdateByStudentId_CurrentPerformance(long studentId) {

//		String sql = "select c from TeacherComment c where c.student.id = :studentId  and c.onlineClass.finishType = :finishType  and "
//				+ " c.id > ( SELECT Max(tc.id) FROM TeacherComment tc  WHERE tc.id = c.id and  tc.currentPerformance is null ) order by c.id desc";  // != :performance
		String sql = "select c from TeacherComment c where c.student.id = :studentId  and c.onlineClass.finishType = :finishType and c.performance>0 and "
				+ " c.id >= ( SELECT min(tc.id) FROM TeacherComment tc  WHERE tc.student.id = c.student.id and tc.performance>0 and  tc.currentPerformance is null ) order by c.id asc";

		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		typedQuery.setParameter("finishType", OnlineClass.FinishType.AS_SCHEDULED);
		typedQuery.setParameter("studentId", studentId);
//		typedQuery.setParameter("performance", StudentPerformance.NOPERF);
		
		typedQuery.setMaxResults(3);
		
		List<TeacherComment> teacherCommentList = null;
		if (typedQuery.getResultList() != null) {
			if (typedQuery.getResultList().size() > 0) {
				teacherCommentList = typedQuery.getResultList();
			} 
		}
		
		if (null == teacherCommentList) {
			//
			return null;
		}
		
		// 每3条处理一次
		int nLen = teacherCommentList.size();
		if (nLen<3) {
			return null;
		}
		
		// 计算performance评级
		int nScore = 0;
		for (TeacherComment teacherComment : teacherCommentList) {
			nScore += teacherComment.getPerformance();
		}
		
		//
		StudentPerformance studentPerformance = getPerformanceWithScore(nScore);
		logger.info("calculate {} -- {}", studentId, studentPerformance);
		for (TeacherComment teacherComment : teacherCommentList) {
			teacherComment.setCurrentPerforance(studentPerformance);
			teacherComment = update(teacherComment);
		}
		
		TeacherComment teacherComment = teacherCommentList.get(0);
		teacherComment.setCurrentPerforance(studentPerformance);
		return teacherComment;
	}

	public long findStarsByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		String sql = "SELECT SUM(tc.stars) FROM TeacherComment tc,OnlineClass oc WHERE tc.onlineClass.id = oc.id AND oc.scheduledDateTime BETWEEN :startDate AND :endDate AND tc.student.id = :studentId";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("studentId", studentId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.debug("no teacherComment found");
			return 0;
		}
	}
	
	public TeacherComment findTeacherCommentByIds(long teacheId,long onlineClassId,long studentId){
		String sql = "select stars from teacher_comment where online_class_id =? and student_id =? and teacher_id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, onlineClassId);
		query.setParameter(2, studentId);
		query.setParameter(3, teacheId);
		query.setMaxResults(1);
		List<Object> rows = query.getResultList();
		TeacherComment tComment = new TeacherComment();
        if (CollectionUtils.isEmpty(rows)) {
            return new TeacherComment();
        }else{
        	Integer star = (Integer) rows.get(0);
        	tComment.setStars(star==null?0:star);
        	return tComment;
        }
	}
	
	/**
	 * 
	* @Title: findTeacherCommentFinishedBeforeTwoHours 
	* @Description: 查询两小时前正常结束的主修课的teachercomment
	* @param parameter
	* @author zhangfeipeng 
	* @return List<TeacherComment>
	* @throws
	 */
	public List<TeacherComment> findTeacherCommentFinishedBeforeTwoHours() {
		String sql = "SELECT tc FROM TeacherComment tc WHERE tc.stars=0 AND (tc.onlineClass.finishType =:finishType or tc.onlineClass.status =:status) AND tc.onlineClass.lesson.learningCycle.unit.course.type=:type AND tc.onlineClass.scheduledDateTime >= :startDate AND tc.onlineClass.scheduledDateTime <= :endDate";
		TypedQuery<TeacherComment> typedQuery = entityManager.createQuery(sql, TeacherComment.class);
		Date endDate=DateTimeUtils.getNthMinutesLater(-95);
	    Date startDate=DateTimeUtils.getNthMinutesLater(-125);
		typedQuery.setParameter("finishType", FinishType.AS_SCHEDULED);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.MAJOR);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		return typedQuery.getResultList();
	}
}
