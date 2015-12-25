package com.vipkid.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Course_;
import com.vipkid.model.DemoReport;
import com.vipkid.model.DemoReport.LifeCycle;
import com.vipkid.model.DemoReport_;
import com.vipkid.model.Family;
import com.vipkid.model.Family_;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.model.FiremanToStudentComment_;
import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.model.FiremanToTeacherComment_;
import com.vipkid.model.Leads;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.LearningCycle_;
import com.vipkid.model.Lesson;
import com.vipkid.model.Lesson_;
import com.vipkid.model.Medal;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.OnlineClass_;
import com.vipkid.model.Order;
import com.vipkid.model.Parent;
import com.vipkid.model.Parent_;
import com.vipkid.model.PayrollItem;
import com.vipkid.model.PayrollItem_;
import com.vipkid.model.Staff;
import com.vipkid.model.Staff_;
import com.vipkid.model.Student;
import com.vipkid.model.StudentComment;
import com.vipkid.model.StudentComment_;
import com.vipkid.model.Student_;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.TeacherComment_;
import com.vipkid.model.Teacher_;
import com.vipkid.model.Unit;
import com.vipkid.model.Unit_;
import com.vipkid.model.User_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.CountOnlineClassByCourseView;
import com.vipkid.service.pojo.DateWrapper;
import com.vipkid.service.pojo.OnlineClassPeakTimeView;
import com.vipkid.service.pojo.leads.CourseVo;
import com.vipkid.service.pojo.leads.LearningCycleVo;
import com.vipkid.service.pojo.leads.LessonVo;
import com.vipkid.service.pojo.leads.OnlineClassVo;
import com.vipkid.service.pojo.leads.StudentVo;
import com.vipkid.service.pojo.leads.TeacherVo;
import com.vipkid.service.pojo.leads.UnitVo;
import com.vipkid.service.pojo.parent.LessonsView;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;



@Repository
public class OnlineClassRepository extends BaseRepository<OnlineClass> {

	private static final String LT1_U1_LC1_L1 = "LT1-U1-LC1-L1";
	private Logger logger = LoggerFactory.getLogger(OnlineClass.class);
    //private static List<Type> TypeList = ImmutableList.of(Type.TRIAL,Type.ELECTIVE_LT,Type.ASSESSMENT2);
    private static List<Type> TypeList = ImmutableList.of(Type.TRIAL,Type.ASSESSMENT2);

	OnlineClassRepository() {
		super(OnlineClass.class);
	}
	
	public List<OnlineClass> findAll() {
		String sql = "SELECT o FROM OnlineClass o";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
	    
	    return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAsScheduledByStudentIdAndStatusAndStartDateAndEndDate(long studentId, Status status, Date startDate, Date endDate) {
		String sql = "SELECT l FROM onlineClass l WHERE l.student.id = :studentId AND l.status = :status AND l.scheduledDateTime >= :startDate AND l.scheduledDateTime <= :endDate AND l.finishType = :ASSCHEDULED";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("ASSCHEDULED", FinishType.AS_SCHEDULED);
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findLaterBookedByStudentIdAndCourseId(long studentId, long courseId, Date dateTime) {
		 String sql = null;
		 if (dateTime != null){
			 sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.lesson.learningCycle.unit.course.id = :courseId AND o.status = :status AND o.scheduledDateTime > :dateTime ORDER BY o.scheduledDateTime ASC";
		 } else{
			  sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.lesson.learningCycle.unit.course.id = :courseId AND o.status = :status ORDER BY o.scheduledDateTime ASC";
		 }
		 
		 TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		 typedQuery.setParameter("studentId", studentId);
		 typedQuery.setParameter("courseId", courseId);
		 typedQuery.setParameter("status", Status.BOOKED);
		 if (dateTime != null ){
			 typedQuery.setParameter("dateTime", dateTime);
		 }
		  
		 List<OnlineClass> lessonHistoryList = typedQuery.getResultList();
		 return lessonHistoryList;  
    }
	
	
	public List<OnlineClass> findByBackupTeacherIdAndScheduledDateTime(long backupTeacherId, Date scheduledDateTime){
		String sql = "SELECT o FROM OnlineClass o JOIN o.backupTeachers obts WHERE obts.id = :backupTeacherId AND o.status = :status AND o.scheduledDateTime = :scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("backupTeacherId", backupTeacherId);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		return typedQuery.getResultList();
	}
	
	/**
	 * 找到本节课，和前一节 相关的所有onlineClass
	 * @param courseId
	 * @param studentId
	 * @param lessonSequence
	 * @return
	 */
	public List<OnlineClass> findByCourseIdAndStudentIdAndLessonSequence(long courseId, long studentId, int lessonSequence) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.lesson.learningCycle.unit.course.id = :courseId AND o.status = :status AND (o.lesson.sequence = :lessonSequence OR o.lesson.sequence + 1 = :lessonSequence)";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("lessonSequence", lessonSequence);
	    typedQuery.setParameter("status", Status.FINISHED);
		return typedQuery.getResultList();			
	}
	
	public OnlineClass findLastedFinishedClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.status = :status  ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
	    typedQuery.setParameter("status", Status.FINISHED);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			OnlineClass onlineClassResult = onlineClassList.get(0);
			return onlineClassResult;
		}else {
			return null;
		}				
	}
	
	public OnlineClass findLastedFinishedTailClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.status = :status AND o.lesson.learningCycle.unit.course.type = :type AND o.finishType = :finishType ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
	    typedQuery.setParameter("status", Status.FINISHED);
	    typedQuery.setParameter("type", Course.Type.TRIAL);
	    typedQuery.setParameter("finishType", FinishType.AS_SCHEDULED);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			OnlineClass onlineClassResult = onlineClassList.get(0);
			return onlineClassResult;
		}else {
			return null;
		}				
	}
	
	public OnlineClass findLastedTailClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.status = :status AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
	    typedQuery.setParameter("status", Status.BOOKED);
	    typedQuery.setParameter("type", Course.Type.TRIAL);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			OnlineClass onlineClassResult = onlineClassList.get(0);
			return onlineClassResult;
		}else {
			return null;
		}				
	}
	
	public OnlineClass findFirstFinishedClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.status = :status ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
	    typedQuery.setParameter("status", Status.FINISHED);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			OnlineClass onlineClassResult = onlineClassList.get(0);
			return onlineClassResult;
		}else {
			return null;
		}
	}
	
	public OnlineClass findFirstByTimeInOnlineClassList(List<OnlineClass> onlineClasses) {
		if(onlineClasses.size() > 0){
			OnlineClass resultOnlineClass = onlineClasses.get(0);
			for(OnlineClass onlineClass : onlineClasses){
				if( onlineClass.getScheduledDateTime().getTime() < resultOnlineClass.getScheduledDateTime().getTime() ){
					resultOnlineClass = onlineClass;
				}				
			}
			return resultOnlineClass;
		}else {
			return null;
		}		
	}
	
	public OnlineClass findLastByTimeInOnlineClassList(List<OnlineClass> onlineClasses) {
		if(onlineClasses.size() > 0){
			OnlineClass resultOnlineClass = onlineClasses.get(onlineClasses.size() - 1);
			for(OnlineClass onlineClass : onlineClasses){
				if( onlineClass.getScheduledDateTime().getTime() > resultOnlineClass.getScheduledDateTime().getTime() ){
					resultOnlineClass = onlineClass;
				}				
			}
			return resultOnlineClass;
		}else {
			return null;
		}		
	}
	
	public OnlineClass findByTeacherIdAndScheduledDateTime(long teacherId, Date dateTime) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND (o.status <> :removedStatus AND o.status <> :canceledStatus) AND o.scheduledDateTime = :dateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("removedStatus", Status.REMOVED);
		typedQuery.setParameter("canceledStatus", Status.CANCELED);
	    typedQuery.setParameter("dateTime", dateTime);
		
	    if (typedQuery.getResultList().isEmpty()){
	    	return null;
	    } else {
	    	return typedQuery.getResultList().get(0);
	    }
	}
	
	public List<OnlineClass> findByTeacherIdAndStudentIdAndScheduledDateTime(long teacherId, long studentId, Date scheduledDateTime) {	
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE o.teacher.id = :teacherId AND s.id = :studentId AND o.scheduledDateTime = :scheduledDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findByStudentIdAndScheduledDateTime(long studentId, Date scheduledDateTime) {	
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.scheduledDateTime = :scheduledDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDate(long teacherId, Date startDate, Date endDate) {	
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
	}
	
	public List<DateWrapper> findExccedMaxParallelTrialCountByStartDateAndEndDate(Date startDate, Date endDate, int parallelCount) {	
		String sql = "SELECT DISTINCT NEW com.vipkid.service.model.DateWrapper(o.scheduledDateTime) FROM OnlineClass o "
				+ "WHERE o.id IN (SELECT o2.id FROM OnlineClass o2 WHERE o2.status = :booked AND "
				+ "(o2.lesson.learningCycle.unit.course.type in :typeList OR (o2.lesson.learningCycle.unit.course.type = :type AND o2.lesson.serialNumber = :serialNumber))"
				+ "AND o2.scheduledDateTime >= :startDate AND o2.scheduledDateTime <= :endDate "
				+ "GROUP BY o2.scheduledDateTime "
				+ "HAVING COUNT(o2.scheduledDateTime) >= :parallelCount) "
				+ "ORDER BY o.scheduledDateTime ASC";
		TypedQuery<DateWrapper> typedQuery = entityManager.createQuery(sql, DateWrapper.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("booked", Status.BOOKED);
		typedQuery.setParameter("typeList", TypeList);
		typedQuery.setParameter("type", Type.ELECTIVE_LT);
		typedQuery.setParameter("serialNumber", LT1_U1_LC1_L1);
		typedQuery.setParameter("parallelCount", parallelCount);
		
		return typedQuery.getResultList();
	}
	
	public long countParallelTrialByScheduledDateTime(Date scheduledDateTime) {	
		String sql = "SELECT COUNT(o.id) FROM OnlineClass o WHERE o.scheduledDateTime = :scheduledDateTime AND "
				+ "(o.lesson.learningCycle.unit.course.type in :typeList OR (o.lesson.learningCycle.unit.course.type = :type AND o.lesson.serialNumber = :serialNumber))";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("typeList", TypeList);
		typedQuery.setParameter("type", Type.ELECTIVE_LT);
		typedQuery.setParameter("serialNumber", LT1_U1_LC1_L1);
		return typedQuery.getSingleResult();
	}
	
	/**
	 * 查询获取指定老师的available online-class。用于管理端teacher available time-slot获取
	 * @param teacherId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<OnlineClass> findAvailableByTeacherIdAndStartDateAndEndDate(long teacherId, Date startDate, Date endDate) {	
		//
		Date beginDate = new Date(); 
		if (startDate.before(beginDate)) {
			// set the new start datetime
		}
		
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate ORDER BY o.scheduledDateTime ASC"; //// AND o.status=:availableCondition1 or o.status=:availableCondition2
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);

//		typedQuery.setParameter("availableCondition1",OnlineClass.Status.AVAILABLE);
//		typedQuery.setParameter("availableCondition2",OnlineClass.Status.OPEN);
		
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByTeacherIdAndScheduleDateTime(long teacherId, Date scheduledDateTime) {	
		
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime = :scheduledDateTime AND o.status = :status"; 
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList();
	}
	
	public boolean findIsAvailableByTeacherIdAndStartDateAndEndDate(long teacherId, Date startDate, Date endDate) {	
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList().size() > 0 ? true : false;
	}
	
	public List<OnlineClass> findAvailableByTeacherId(long teacherId) {	
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.status = :status ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByTeacherIdAndLimitParallelByCourseType(long teacherId, Type type, int parallelCount) {
		String sql = null;
		switch(type) {
		case TRIAL:
		case ELECTIVE_LT:
		case ASSESSMENT2:
				sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.status = :status AND o.scheduledDateTime "
						+ "NOT IN (SELECT o2.scheduledDateTime FROM OnlineClass o2 WHERE o2.status = :booked AND "
						+ "(o2.lesson.learningCycle.unit.course.type in :typeList OR (o2.lesson.learningCycle.unit.course.type = :type AND o2.lesson.serialNumber = :serialNumber))"
						+ "GROUP BY o2.scheduledDateTime "
						+ "HAVING COUNT(o2.scheduledDateTime)  >= :parallelCount)";
				break;
			default:
				sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.status = :status ORDER BY o.scheduledDateTime DESC";
				break;
		}
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
		if(type == Type.TRIAL||type ==Type.ELECTIVE_LT||type==Type.ASSESSMENT2) {
			typedQuery.setParameter("booked", Status.BOOKED);
			typedQuery.setParameter("typeList", TypeList);
			typedQuery.setParameter("type", Type.ELECTIVE_LT);
			typedQuery.setParameter("serialNumber", LT1_U1_LC1_L1);
			typedQuery.setParameter("parallelCount", parallelCount);
		}
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByScheduledDateTime(Date scheduledDateTime) {	
		String sql = "SELECT o FROM OnlineClass o WHERE o.scheduledDateTime = :scheduledDateTime AND o.status = :status";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByByTeacherCertificatedCourseIdScheduledDateTime(long courseId, Date scheduledDateTime) {	
		String sql = "SELECT o FROM OnlineClass o JOIN o.teacher.certificatedCourses c WHERE o.scheduledDateTime = :scheduledDateTime AND o.status = :status AND c.id = :courseId";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("status", Status.AVAILABLE);
		typedQuery.setParameter("courseId", courseId);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findOpenByCourseIdAndScheduledDateTime(long courseId, Date scheduledDateTime) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.lesson.learningCycle.unit.course.id = :courseId And o.scheduledDateTime = :scheduledDateTime AND o.status = :status";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("status", Status.OPEN);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(long courseId, Date startDate, Date endDate) {
//		String sql = "SELECT DISTINCT o.id, o.scheduled_date_time, o.teacher_id, t.real_name, o.serial_number FROM vipkid.online_class o , teacher t, teacher_certificated_course tcc WHERE tcc.teacher_id = t.id AND tcc.course_id = ? AND o.teacher_id = t.id AND o.scheduled_date_time >= ? AND o.scheduled_date_time <= ? AND o.status = ? ORDER BY o.scheduled_date_time";
//		Query query = entityManager.createNativeQuery(sql);
//		query.setParameter(1, courseId);
//		query.setParameter(2, DateTimeUtils.format(startDate, DateTimeUtils.DATETIME_FORMAT));
//		query.setParameter(3, DateTimeUtils.format(endDate, DateTimeUtils.DATETIME_FORMAT));
//		query.setParameter(4, Status.AVAILABLE.name());
//		
//		List<Object> rows = query.getResultList();
//		List<OnlineClass> onlineClasses = Lists.newArrayList();
//        if (CollectionUtils.isEmpty(rows)) {
//            return onlineClasses;
//        }
//		for (Object row : rows) {
//			OnlineClass onlineClass = new OnlineClass();
//			Object[] cells = (Object[])row;
//			onlineClass.setId((long) (cells[0]));
//			onlineClass.setScheduledDateTime((Date)cells[1]);
//			Teacher teacher = new Teacher();
//			teacher.setId((long) (cells[2]));
//			teacher.setRealName((String)cells[3]);
//			onlineClass.setTeacher(teacher);
//			onlineClass.setSerialNumber((String)cells[4]);
//			onlineClasses.add(onlineClass);
//		}
//		return onlineClasses;
//	}
	
	//work first TODO:opt
	public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDate(long courseId, Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.teacher.certificatedCourses c WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status AND c.id = :courseId ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", Status.AVAILABLE);
		
		return typedQuery.getResultList();
        }
	
	public List<OnlineClass> findAvailableByTeacherCertificatedCourseIdStartDateAndEndDateAndLimitParallelByCourseType(long courseId, Date startDate, Date endDate, Type type, int parallelCount) {
		String sql = null;
		switch(type) {
		case TRIAL:
		case ELECTIVE_LT:
		case ASSESSMENT2:
				sql = "SELECT o FROM OnlineClass o JOIN o.teacher.certificatedCourses c WHERE "
						+ "o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status AND c.id = :courseId "
						
						+ "AND ("
							+ "(o.scheduledDateTime IN ("
								+ "SELECT t2.timePoint FROM TrialThreshold t2 WHERE t2.timePoint >= :startDate AND t2.timePoint <= :endDate) "
								+ "AND "
								+ "o.scheduledDateTime NOT IN ("
								+ "SELECT o2.scheduledDateTime FROM OnlineClass o2, TrialThreshold t WHERE o2.status = :booked AND "
								+ "o2.scheduledDateTime = t.timePoint AND (o2.lesson.learningCycle.unit.course.type in :typeList OR "
								+ "(o2.lesson.learningCycle.unit.course.type = :type AND o2.lesson.serialNumber = :serialNumber))"
								+ "AND o2.scheduledDateTime >= :startDate AND o2.scheduledDateTime <= :endDate "
								+ "GROUP BY o2.scheduledDateTime, t.trialAmount HAVING COUNT(o2.scheduledDateTime) >= t.trialAmount)) "
						
							+ "OR "
								+ "(o.scheduledDateTime NOT IN ("
								+ "SELECT t2.timePoint FROM TrialThreshold t2 WHERE t2.timePoint >= :startDate AND t2.timePoint <= :endDate) "
								+ "AND "
								+ "o.scheduledDateTime NOT IN ("
								+ "SELECT o3.scheduledDateTime FROM OnlineClass o3 WHERE o3.status = :booked AND "
								+ "(o3.lesson.learningCycle.unit.course.type in :typeList OR "
								+ "(o3.lesson.learningCycle.unit.course.type = :type AND o3.lesson.serialNumber = :serialNumber))"
								+ "AND o3.scheduledDateTime >= :startDate AND o3.scheduledDateTime <= :endDate "
								+ "GROUP BY o3.scheduledDateTime HAVING COUNT(o3.scheduledDateTime) >= :parallelCount)))"
						
						+ "ORDER BY o.scheduledDateTime";
				break;
			default:
				sql = "SELECT o FROM OnlineClass o JOIN o.teacher.certificatedCourses c WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status AND c.id = :courseId ORDER BY o.scheduledDateTime";
				break;
		}
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
		if (type == Type.TRIAL || type == Type.ASSESSMENT2 || type == Type.ELECTIVE_LT) {
			typedQuery.setParameter("booked", Status.BOOKED);
			typedQuery.setParameter("typeList", TypeList);
			typedQuery.setParameter("type", Type.ELECTIVE_LT);
			typedQuery.setParameter("serialNumber", LT1_U1_LC1_L1);
			typedQuery.setParameter("parallelCount", parallelCount);
		}
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findOpenTeacherRecruitmentByStartDateAndEndDate(Date startDate, Date endDate,String type) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", Status.OPEN);
		if (type.equalsIgnoreCase("PRACTICUM")) {
			typedQuery.setParameter("type", Type.PRACTICUM);
		} else if (type.equalsIgnoreCase("TEACHER_RECRUITMENT")) {
			typedQuery.setParameter("type", Type.TEACHER_RECRUITMENT);
		}		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findOpenByCourseIdAndStartDateAndEndDate(long courseId, Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.lesson.learningCycle.unit.course.id = :courseId AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", Status.OPEN);
		
		return typedQuery.getResultList();
	}
	
	public boolean hasScheduled(long onlineClassId, long studentId, Date scheduledDateTime){
		logger.info("hasScheduled(), onlineClassId={}, studentId={}, scheduledDateTime", onlineClassId, studentId, scheduledDateTime); 
		String sql = "SELECT o FROM OnlineClass o JOIN o.students os WHERE o.id <> :onlineClassId AND os.id = :studentId AND (o.status = :bookStatus OR o.status = :openStatus) AND o.scheduledDateTime = :scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("bookStatus", Status.BOOKED);
		typedQuery.setParameter("openStatus", Status.OPEN);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		List<OnlineClass> onlineClasses =  typedQuery.getResultList();
		if (onlineClasses.size() > 0){
			return true;
		} else{
			return false;
		}
	}
	
	public boolean hasSwitchScheduled(long onlineClassId, long studentId, Date scheduledDateTime){
		String sql = "SELECT o FROM OnlineClass o JOIN o.students os WHERE o.id <> :onlineClassId AND os.id = :studentId AND (o.status = :bookStatus OR o.status = :openStatus) AND o.scheduledDateTime = :scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("bookStatus", Status.BOOKED);
		typedQuery.setParameter("openStatus", Status.OPEN);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		List<OnlineClass> onlineClasses =  typedQuery.getResultList();
		if (onlineClasses.size() > 1){
			return true;
		} else{
			return false;
		}
	}
	
	public boolean hasBookedAlreadyByTeacherIdAndScheduledDateTime(long teacherId, Date scheduledDateTime){
		String sql = "SELECT o FROM OnlineClass o JOIN o.students os WHERE o.teacher.id = :teacherId AND (o.status = :bookStatus OR o.status = :openStatus) AND o.scheduledDateTime = :scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("bookStatus", Status.BOOKED);
		typedQuery.setParameter("openStatus", Status.OPEN);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		List<OnlineClass> onlineClasses =  typedQuery.getResultList();
		if (onlineClasses.size() > 0){
			return true;
		} else{
			return false;
		}
	}
	
	public List<OnlineClass> findByStudentIdAndStartDateAndEndDate(long studentId, Date startDate, Date endDate) {	
		String sql = "SELECT o FROM OnlineClass o JOIN o.students os WHERE os.id = :studentId AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate and o.status <> :invalidStatus ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("invalidStatus", Status.INVALID);
		
		return typedQuery.getResultList();
	}
	
	public Count countClassByStudentIdAndStartDateOrEndDate(long studentId, Date startDate, Date endDate) {
		if(startDate != null && endDate == null) {
			String sql = "SELECT COUNT(o.id) FROM OnlineClass o JOIN o.students os WHERE os.id = :studentId AND o.scheduledDateTime >= :startDate";
			TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
			typedQuery.setParameter("studentId", studentId);
			typedQuery.setParameter("startDate", startDate);
			long result = typedQuery.getSingleResult();
			return new Count(result);
		}
		if(startDate == null && endDate != null) {
			String sql = "SELECT COUNT(o.id) FROM OnlineClass o JOIN o.students os WHERE os.id = :studentId AND o.scheduledDateTime <= :endDate";
			TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
			typedQuery.setParameter("studentId", studentId);
			typedQuery.setParameter("endDate", endDate);
			long result = typedQuery.getSingleResult();
			return new Count(result);
		}
		return null;
	}
	
	public List<OnlineClass> countClassByStudentIdAndEndDate(long studentId, Date endDate) {	
		String sql = "SELECT o FROM OnlineClass o JOIN o.students os WHERE os.id = :studentId AND o.scheduledDateTime <= :endDate ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAllAvaiableByStartDateAndEndDate (Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE (o.status = :avaiableStatus OR o.status = :openStatus) AND o.scheduledDateTime > :startDate AND o.scheduledDateTime < :endDate";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("avaiableStatus", Status.AVAILABLE);
		typedQuery.setParameter("openStatus", Status.OPEN);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	/**
	 * It's used to arrange back teachers, this method is to find all booked classes except the ones whose teacher is full-time type.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<OnlineClass> findBookedByStartDateAndEndDateAndCourseId(Date startDate, Date endDate, long courseId) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.teacher.type <> :teacherType AND o.lesson.learningCycle.unit.course.id = :courseId ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("teacherType", Teacher.Type.FULL_TIME);
		typedQuery.setParameter("courseId", courseId);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findBookedByStartDateAndEndDateAndCourseIdNotFullTime(Date startDate, Date endDate, long courseId,long pcourseId) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND (o.lesson.learningCycle.unit.course.id = :courseId OR o.lesson.learningCycle.unit.course.id = :pcourseId)ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		//typedQuery.setParameter("teacherType", Teacher.Type.FULL_TIME);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("pcourseId", pcourseId);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAvailableByScheduledTimeAndCourseId(Date scheduledDateTime, long courseId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.teacher ot JOIN ot.certificatedCourses otccs WHERE o.status = :status AND o.scheduledDateTime = :scheduledDateTime AND o.teacher.type <> :teacherType AND otccs.id = :courseId";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.AVAILABLE);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("teacherType", Teacher.Type.FULL_TIME);
		typedQuery.setParameter("courseId", courseId);
		return typedQuery.getResultList();
	}
	
	public long getTotalAvailableSchedules(long teacherId, Date startDate, Date endDate) {
		String sql = "SELECT count(o.id) FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", Status.AVAILABLE);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		long result = typedQuery.getSingleResult();
		return result;

	}
	
	/**
	 * It's used to find whether the substitute teacher has available or not at given scheduled date time.
	 * @param teacherId
	 * @param scheduledDateTime
	 * @param status
	 * @return
	 */
	public List<OnlineClass> findByTeacherIdAndScheduledDateTimeANDStatues(long teacherId, Date scheduledDateTime, Status status) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime = :scheduledDateTime AND o.status = :status";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		 typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
	    typedQuery.setParameter("status", status);
	    
	    return typedQuery.getResultList();
	}
	
	public boolean isBackupClass(long teacherId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.backupTeachers obts WHERE obts.id = :teacherId";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		if (typedQuery.getResultList().isEmpty()){
			return false;
		} else{
			return true;
		}
	}
	
	public long findAvaiableBookingTimeSlot(long studentId, long courseId){
		String sql = "SELECT count(distinct o) FROM OnlineClass o JOIN o.students os WHERE os.id = :studentId AND o.lesson.learningCycle.unit.course.id = :courseId AND (o.status = :bookedStatus OR o.status = :openStatus)";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("bookedStatus", Status.BOOKED);
		typedQuery.setParameter("openStatus", Status.OPEN);
		
		return typedQuery.getSingleResult();
	}
	
	public List<OnlineClass> findByStudentIdAndCourseIdAndStatus(long studentId, long courseId, Status status, int start, int length) {
		logger.info("Query condition studentId={}, courseId={}, status={}", studentId, courseId, status);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
		Join<OnlineClass, Lesson> lesson = onlineClass.join(OnlineClass_.lesson, JoinType.LEFT);
		Join<Lesson, LearningCycle> learningCycle = lesson.join(Lesson_.learningCycle, JoinType.LEFT);
		Join<LearningCycle, Unit> unit = learningCycle.join(LearningCycle_.unit, JoinType.LEFT);
		Join<Unit, Course> course = unit.join(Unit_.course, JoinType.LEFT);
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(students.get(User_.id), studentId));
		andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
		
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
	    
		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		criteriaQuery.where(andPredicate, archivedPredicate);
	    TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findByStudentIdAndCourseIdAndFinishType(long studentId, long courseId, FinishType type) {
		logger.info("Query condition studentId={}, courseId={}, FinishType={}", studentId, courseId, type);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
		Join<OnlineClass, Lesson> lesson = onlineClass.join(OnlineClass_.lesson, JoinType.LEFT);
		Join<Lesson, LearningCycle> learningCycle = lesson.join(Lesson_.learningCycle, JoinType.LEFT);
		Join<LearningCycle, Unit> unit = learningCycle.join(LearningCycle_.unit, JoinType.LEFT);
		Join<Unit, Course> course = unit.join(Unit_.course, JoinType.LEFT);
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(students.get(User_.id), studentId));
		andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
		
		if (type != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), type));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
	    
		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		criteriaQuery.where(andPredicate, archivedPredicate);
	    TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> list(List<Long> courseIds, String searchTeacherText, String searchStudentText, String searchSalesText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Status status, FinishType finishType, Boolean shortNotice, Boolean hasClassroom, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			if(status == null || status != Status.AVAILABLE){//Exclude status is available, it will be handled specially
				Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
				for(Long courseId : courseIds){
					orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
				}
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		List<Predicate> orTeacherPredicates = new LinkedList<Predicate>();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			orTeacherPredicates.add(criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%")));
			orTeacherPredicates.add(criteriaBuilder.or(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + searchTeacherText + "%")));
		}
		teacherPredicate = criteriaBuilder.or(orTeacherPredicates.toArray(new Predicate[orTeacherPredicates.size()]));
		
        
		// compose sales predicate
		List<Predicate> orSalesPredicates = new LinkedList<Predicate>();
		if (searchSalesText != null && !searchSalesText.equals("")) {
			Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
			Join<Student, Staff> sales = students.join(Student_.sales);
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(User_.name), "%" + searchSalesText + "%")));
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(Staff_.englishName), "%" + searchSalesText + "%")));
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(User_.username), "%" + searchSalesText + "%")));
		}
		Predicate orSalesPredicate = criteriaBuilder.or(orSalesPredicates.toArray(new Predicate[orSalesPredicates.size()]));
		 
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
//			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
//			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
//			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeTo.getValue()));
		}
		if (status != null) {
			if (status == Status.AVAILABLE && !courseIds.isEmpty()){
				List<Predicate> coursePredicates = new LinkedList<Predicate>();
				Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
				Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses, JoinType.LEFT);
				for(Long courseId : courseIds){
					coursePredicates.add(criteriaBuilder.equal(courses.get(Course_.id), courseId));
				}
				Predicate coursePredicate = criteriaBuilder.or(coursePredicates.toArray(new Predicate[coursePredicates.size()]));
				andPredicates.add(coursePredicate);
			}
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		if (finishType != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), finishType));
		}
		if(shortNotice != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.shortNotice), shortNotice));
		}
		if(hasClassroom != null) {
			if(hasClassroom) {
				andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.classroom)));
			}else {
				andPredicates.add(criteriaBuilder.isNull(onlineClass.get(OnlineClass_.classroom)));
			}
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new ArrayList<Predicate>();
		if (orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if (orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if (orTeacherPredicates.size() > 0) {
			finalPredicates.add(teacherPredicate);
		}
		if (orSalesPredicates.size() > 0) {
			finalPredicates.add(orSalesPredicate);
		}
		if (andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		List<javax.persistence.criteria.Order> orders = new ArrayList<javax.persistence.criteria.Order>();
		orders.add(criteriaBuilder.asc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		orders.add(criteriaBuilder.asc(onlineClass.get(OnlineClass_.teacher).get(Teacher_.realName)));
		criteriaQuery.orderBy(orders);
//		criteriaQuery.orderBy(criteriaBuilder.asc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();	
		return onlineClassList;
	}
	
	
	public List<OnlineClass> listOnlineClassesAndPayrollItems(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		
		onlineClass.join(OnlineClass_.payrollItem, JoinType.LEFT);
		if (!courseIds.isEmpty()) {// 选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit)
					.join(Unit_.course);
			for (Long courseId : courseIds) {
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}

		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.FINISHED));

		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();	
	}
	
	public Long countOnlineClassesAndPayrollItems(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		
		if (!courseIds.isEmpty()) {// 选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit)
					.join(Unit_.course);
			for (Long courseId : courseIds) {
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}

		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.FINISHED));

		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}
	
	public Float getSalary(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Float> criteriaQuery = criteriaBuilder.createQuery(Float.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
        Join<OnlineClass, PayrollItem> payrollItem = onlineClass.join(OnlineClass_.payrollItem);
        criteriaQuery.select(criteriaBuilder.sum(payrollItem.get(PayrollItem_.salary)));



        // compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		
		if (!courseIds.isEmpty()) {// 选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit)
					.join(Unit_.course);
			for (Long courseId : courseIds) {
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}

		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.FINISHED));

		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		List<Predicate> finalPredicates = new ArrayList<Predicate>();
		
		
		
        if (orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if (orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if (searchTeacherText!=null) {
			finalPredicates.add(teacherPredicate);
		}
		
		if (andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
	    Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
	       
		
		criteriaQuery.where(finalPredicate,archivedPredicate);
        //criteriaQuery.where(orCoursePredicate, orStudentPredicate, teacherPredicate, andPredicate, archivedPredicate);

        TypedQuery<Float> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Float> salaryList =  typedQuery.getResultList();
        if (CollectionUtils.isNotEmpty(salaryList)) {
            return salaryList.get(0);
        } else {
            return null;
        }
	}
	
	public long count(List<Long> courseIds, String searchTeacherText, String searchStudentText, String searchSalesText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Status status, FinishType finishType, Boolean shortNotice, Boolean hasClassroom) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!(courseIds ==null||courseIds.isEmpty())) {//选择了course
			if(status == null || status != Status.AVAILABLE){//Exclude status is available, it will be handled specially
				Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
				for(Long courseId : courseIds){
					orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
				}
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
		
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
				
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
 		if (StringUtils.isNotBlank(searchTeacherText)) {
            List<Predicate> orTeacherPredicates = new LinkedList<Predicate>();
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
            orTeacherPredicates.add(criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%")));
 			orTeacherPredicates.add(criteriaBuilder.or(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + searchTeacherText + "%")));
            teacherPredicate = criteriaBuilder.or(orTeacherPredicates.toArray(new Predicate[orTeacherPredicates.size()]));
		}
		
		// compose sales predicate
		List<Predicate> orSalesPredicates = new LinkedList<Predicate>();
		if (searchSalesText != null && !searchSalesText.equals("")) {
			Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
			Join<Student, Staff> sales = students.join(Student_.sales);
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(User_.name), "%" + searchSalesText + "%")));
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(Staff_.englishName), "%" + searchSalesText + "%")));
			orSalesPredicates.add(criteriaBuilder.or(criteriaBuilder.like(sales.get(User_.username), "%" + searchSalesText + "%")));
		}
		Predicate orSalesPredicate = criteriaBuilder.or(orSalesPredicates.toArray(new Predicate[orSalesPredicates.size()]));
		
 
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			//Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			//logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeTo.getValue()));
		}
		if (status != null) {
			if (status == Status.AVAILABLE && !courseIds.isEmpty()){
				List<Predicate> coursePredicates = new LinkedList<Predicate>();
				Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
				Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses, JoinType.LEFT);
				for(Long courseId : courseIds){
					coursePredicates.add(criteriaBuilder.equal(courses.get(Course_.id), courseId));
				}
				Predicate coursePredicate = criteriaBuilder.or(coursePredicates.toArray(new Predicate[coursePredicates.size()]));
				andPredicates.add(coursePredicate);
			}
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		if (finishType != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), finishType));
		}
		if(shortNotice != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.shortNotice), shortNotice));
		}
		if(hasClassroom != null) {
			if(hasClassroom) {
				andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.classroom)));
			}else {
				andPredicates.add(criteriaBuilder.isNull(onlineClass.get(OnlineClass_.classroom)));
			}
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new ArrayList<Predicate>();
		if (orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if (orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}
		if (orSalesPredicates.size() > 0) {
			finalPredicates.add(orSalesPredicate);
		}
		if (andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		
		criteriaQuery.where(finalPredicate);
		//criteriaQuery.where(orCoursePredicate, orStudentPredicate, teacherPredicate, orSalesPredicate, andPredicate, archivedPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}
	
	public List<OnlineClass> listForComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String finishType, Integer start, Integer length, Boolean isTeacherCommentsEmpty, Boolean isFiremanCommentsEmpty, String searchOnlineClassText) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate+++
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
		Predicate orTeacherCommentPredicate = criteriaBuilder.or();
		if (isTeacherCommentsEmpty != null) {
			Join<OnlineClass, TeacherComment> teacherComment = onlineClass.join(OnlineClass_.teacherComments);
			if (isTeacherCommentsEmpty) {
				orTeacherCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(teacherComment.get(TeacherComment_.empty)));
			} else {
				orTeacherCommentPredicate = criteriaBuilder.and(criteriaBuilder.isFalse(teacherComment.get(TeacherComment_.empty)));
			}

		}
		Predicate orFiremanTeacherCommentPredicate = criteriaBuilder.or();
		Predicate orFiremanStudentCommentPredicate = criteriaBuilder.or();
		if(isFiremanCommentsEmpty!=null){
			Join<OnlineClass, FiremanToTeacherComment> firemantTeacherComment = onlineClass.join(OnlineClass_.firemanToTeacherComment);
			orFiremanTeacherCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(firemantTeacherComment.get(FiremanToTeacherComment_.empty)));

			
			Join<OnlineClass, FiremanToStudentComment> firemantStudentComment = onlineClass.join(OnlineClass_.firemanToStudentComments);
			if (isFiremanCommentsEmpty) {
				orFiremanStudentCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(firemantStudentComment.get(FiremanToStudentComment_.empty)));
			} else {
				orFiremanStudentCommentPredicate = criteriaBuilder.and(criteriaBuilder.isFalse(firemantStudentComment.get(FiremanToStudentComment_.empty)));
			}
			
		}
		
		
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (finishType != null && !finishType.equals("")) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}
		
		andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		// compose onlineclass predicate
		Predicate orOnlineclassPredicate = criteriaBuilder.or();
		if (searchOnlineClassText != null) {
			Join<OnlineClass,Lesson> lesson = onlineClass.join(OnlineClass_.lesson);
			orOnlineclassPredicate = criteriaBuilder.or(criteriaBuilder.like(lesson.get(Lesson_.serialNumber), "%" + searchOnlineClassText + "%"));
		}
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}		
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		if(isTeacherCommentsEmpty != null) {
			finalPredicates.add(orTeacherCommentPredicate);
		}
		if(isFiremanCommentsEmpty!=null){
			finalPredicates.add(orFiremanTeacherCommentPredicate);
			finalPredicates.add(orFiremanStudentCommentPredicate);
		}
		if(searchOnlineClassText != null) {
			finalPredicates.add(orOnlineclassPredicate);
		}
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();	
	}
	
	public long countForComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String finishType, Boolean isTeacherCommentsEmpty, Boolean isFiremanCommentsEmpty, String searchOnlineClassText) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
		
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
        Predicate orTeacherCommentPredicate = criteriaBuilder.or();;
		if (isTeacherCommentsEmpty != null) {
			Join<OnlineClass, TeacherComment> teacherComment = onlineClass.join(OnlineClass_.teacherComments);
			if (isTeacherCommentsEmpty) {
				orTeacherCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(teacherComment.get(TeacherComment_.empty)));
			} else {
				orTeacherCommentPredicate = criteriaBuilder.and(criteriaBuilder.isFalse(teacherComment.get(TeacherComment_.empty)));
			}

		}
		
		
		Predicate orFiremanTeacherCommentPredicate = criteriaBuilder.or();
		Predicate orFiremanStudentCommentPredicate = criteriaBuilder.or();
		if(isFiremanCommentsEmpty!=null){
			Join<OnlineClass, FiremanToTeacherComment> firemantTeacherComment = onlineClass.join(OnlineClass_.firemanToTeacherComment);
			orFiremanTeacherCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(firemantTeacherComment.get(FiremanToTeacherComment_.empty)));

			
			Join<OnlineClass, FiremanToStudentComment> firemantStudentComment = onlineClass.join(OnlineClass_.firemanToStudentComments);
			if (isFiremanCommentsEmpty) {
				orFiremanStudentCommentPredicate = criteriaBuilder.or(criteriaBuilder.isTrue(firemantStudentComment.get(FiremanToStudentComment_.empty)));
			} else {
				orFiremanStudentCommentPredicate = criteriaBuilder.and(criteriaBuilder.isFalse(firemantStudentComment.get(FiremanToStudentComment_.empty)));
			}
			
		}
				
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
 
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}

		if (finishType != null && !finishType.equals("")) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}

		andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));

		// compose onlineclass predicate
		Predicate orOnlineclassPredicate = criteriaBuilder.or();
		if (searchOnlineClassText != null && !searchOnlineClassText.equals("")) {
			Join<OnlineClass,Lesson> lesson = onlineClass.join(OnlineClass_.lesson);
			orOnlineclassPredicate = criteriaBuilder.or(criteriaBuilder.like(lesson.get(Lesson_.serialNumber), "%" + searchOnlineClassText + "%"));
		}
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}		
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		if(isTeacherCommentsEmpty != null) {
			finalPredicates.add(orTeacherCommentPredicate);
		}
		if(isFiremanCommentsEmpty!=null){
			finalPredicates.add(orFiremanTeacherCommentPredicate);
			finalPredicates.add(orFiremanStudentCommentPredicate);
		}
		if(searchOnlineClassText != null) {
			finalPredicates.add(orOnlineclassPredicate);
		}
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}
	
	public List<OnlineClass> listForDemoReport(String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String lifeCycle, Long salesId,String searchStatus,String finishType, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// only list class finish as schedule OR the report is submitted or confirmed data		
		List<Predicate> finishedOnlineClassPredicates = new LinkedList<Predicate>();
		finishedOnlineClassPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.AS_SCHEDULED));
		finishedOnlineClassPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate finishedOnlinePredicate = criteriaBuilder.and(finishedOnlineClassPredicates.toArray(new Predicate[finishedOnlineClassPredicates.size()]));
		
		List<Predicate> submittedOrConfirmedDemoReportPredicates = new LinkedList<Predicate>();
		Join<OnlineClass, DemoReport> demoReportRoot = onlineClass.join(OnlineClass_.demoReport, JoinType.INNER);
		submittedOrConfirmedDemoReportPredicates.add(criteriaBuilder.equal(demoReportRoot.get(DemoReport_.lifeCycle), LifeCycle.SUBMITTED));
		submittedOrConfirmedDemoReportPredicates.add(criteriaBuilder.equal(demoReportRoot.get(DemoReport_.lifeCycle), LifeCycle.CONFIRMED));
		Predicate submittedOrConfirmedDemoReportPredicate = criteriaBuilder.or(submittedOrConfirmedDemoReportPredicates.toArray(new Predicate[submittedOrConfirmedDemoReportPredicates.size()]));
		
		List<Predicate> demoOnlineClassPredicates = new LinkedList<Predicate>();
		demoOnlineClassPredicates.add(finishedOnlinePredicate);
		demoOnlineClassPredicates.add(submittedOrConfirmedDemoReportPredicate);
		Predicate demoOnlineClassPredicate = criteriaBuilder.or(demoOnlineClassPredicates.toArray(new Predicate[demoOnlineClassPredicates.size()]));
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		 // only demo online class
		Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
		Predicate p1 = criteriaBuilder.equal(course.get(Course_.type), Type.DEMO);
		Predicate p2 = criteriaBuilder.equal(course.get(Course_.type), Type.ASSESSMENT2);
		Predicate p3 = criteriaBuilder.or(p1,p2);		
		andPredicates.add(p3);

		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if(lifeCycle != null && !lifeCycle.equals("")) {
			Join<OnlineClass, DemoReport> demoReport = onlineClass.join(OnlineClass_.demoReport, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(demoReport.get(DemoReport_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		
		// onlineClass status predicate
		if(searchStatus != null && !searchStatus.equals("")){
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.valueOf(searchStatus)));
		}
		//onlineclass finishType predicate
		if(finishType != null && !finishType.equals("")){
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}
		
		if (salesId != null) {
			Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
			Join<Student, Staff> sales = students.join(Student_.sales);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesId));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(demoOnlineClassPredicates.size() > 0) {
			finalPredicates.add(demoOnlineClassPredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null) { // 只有一个条件, 没有定义List
			finalPredicates.add(teacherPredicate);
		}	
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate); // 始终加入
			
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
//		criteriaQuery.where(demoOnlineClassPredicate, orStudentPredicate, teacherPredicate, andPredicate, archivedPredicate);
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		if(start == null) {
			start = 0;
		}
		if(length == null) {
			length = 0;
		}
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long countForDemoReport(String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, String lifeCycle, Long salesId,String searchStatus,String finishType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// only list class finish as schedule OR the report is submitted or confirmed data		
		List<Predicate> finishedOnlineClassPredicates = new LinkedList<Predicate>();
		finishedOnlineClassPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.AS_SCHEDULED));
		finishedOnlineClassPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate finishedOnlinePredicate = criteriaBuilder.and(finishedOnlineClassPredicates.toArray(new Predicate[finishedOnlineClassPredicates.size()]));
				
		List<Predicate> submittedOrConfirmedDemoReportPredicates = new LinkedList<Predicate>();
		Join<OnlineClass, DemoReport> demoReportRoot = onlineClass.join(OnlineClass_.demoReport, JoinType.INNER);
		submittedOrConfirmedDemoReportPredicates.add(criteriaBuilder.equal(demoReportRoot.get(DemoReport_.lifeCycle), LifeCycle.SUBMITTED));
		submittedOrConfirmedDemoReportPredicates.add(criteriaBuilder.equal(demoReportRoot.get(DemoReport_.lifeCycle), LifeCycle.CONFIRMED));
		Predicate submittedOrConfirmedDemoReportPredicate = criteriaBuilder.or(submittedOrConfirmedDemoReportPredicates.toArray(new Predicate[submittedOrConfirmedDemoReportPredicates.size()]));
				
		List<Predicate> demoOnlineClassPredicates = new LinkedList<Predicate>();
		demoOnlineClassPredicates.add(finishedOnlinePredicate);
		demoOnlineClassPredicates.add(submittedOrConfirmedDemoReportPredicate);
		Predicate demoOnlineClassPredicate = criteriaBuilder.or(demoOnlineClassPredicates.toArray(new Predicate[demoOnlineClassPredicates.size()]));
		
		// compose student predicate
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null && !searchStudentText.equals("")){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		Predicate teacherPredicate = criteriaBuilder.or();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			teacherPredicate = criteriaBuilder.or(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
		}
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		 // only demo online class
		Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
		Predicate p1 = criteriaBuilder.equal(course.get(Course_.type), Type.DEMO);
		Predicate p2 = criteriaBuilder.equal(course.get(Course_.type), Type.ASSESSMENT2);
		Predicate p3 = criteriaBuilder.or(p1,p2);
		andPredicates.add(p3);
		
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if(lifeCycle != null && !lifeCycle.equals("")) {
			Join<OnlineClass, DemoReport> demoReport = onlineClass.join(OnlineClass_.demoReport, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(demoReport.get(DemoReport_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		
		// onlineClass status predicate
		if(searchStatus != null && !searchStatus.equals("")){
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.valueOf(searchStatus)));
		}
		//onlineclass finishType predicate
		if(finishType != null && !finishType.equals("")){
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}
		
		if (salesId != null) {
			Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
			Join<Student, Staff> sales = students.join(Student_.sales);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesId));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(demoOnlineClassPredicates.size() > 0) {
			finalPredicates.add(demoOnlineClassPredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) { // 只有一个条件, 没有定义List
			finalPredicates.add(teacherPredicate);
		}	
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate); // 始终加入
			
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
//		criteriaQuery.where(demoOnlineClassPredicate, orStudentPredicate, teacherPredicate, andPredicate, archivedPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}


	public OnlineClass findNextShouldTakeClass(long studentId) {
		String sql = "SELECT oc FROM OnlineClass oc JOIN oc.students ocs WHERE oc.status = :booked AND ocs.id = :studentId ORDER BY oc.scheduledDateTime";
		TypedQuery<OnlineClass> query = entityManager.createQuery(sql, OnlineClass.class);
		query.setParameter("studentId", studentId);
		query.setParameter("booked", Status.BOOKED);
		query.setMaxResults(1);
		List<OnlineClass> classes = query.getResultList();
		if(classes.isEmpty()) {
			return null;
		}else{
			return classes.get(0);
		}
	}
	
	public List<OnlineClass> findByUnitId(long studentId, long unitId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId AND o.lesson.learningCycle.unit.id = :unitId ORDER BY o.lesson.sequence";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("unitId", unitId);
		typedQuery.setParameter("studentId", studentId);
		
		List<OnlineClass> lessonHistoryList = typedQuery.getResultList();
		return lessonHistoryList;
	}
	
	public List<OnlineClass> findBookedAndFinishedByStudentIdAndUnitId(long studentId, long unitId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs "
				+ "WHERE ( o.status = :booked OR ( o.status = :finished "
				+ "AND o.finishType = :asScheduled ) ) "
				+ "AND ocs.id = :studentId "
				+ "AND o.lesson.learningCycle.unit.id = :unitId";
		
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
	
		typedQuery.setParameter("unitId", unitId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("booked", Status.BOOKED);
		typedQuery.setParameter("finished", Status.FINISHED);
		typedQuery.setParameter("asScheduled", FinishType.AS_SCHEDULED);
		
		List<OnlineClass> lessonHistoryList = typedQuery.getResultList();
		
		return lessonHistoryList;
	}

	public List<OnlineClass> findFinishedByUnitId(long studentId, long unitId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId AND o.status = :finishedStatus AND o.lesson.learningCycle.unit.id = :unitId ORDER BY o.lesson.sequence";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("unitId", unitId);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("finishedStatus", Status.FINISHED);
		
		List<OnlineClass> lessonHistoryList = typedQuery.getResultList();
		return lessonHistoryList;
	}
	
	//TODO why does it call findByStudentId????
	public List<OnlineClass> findByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId AND o.status = :status ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("status", Status.FINISHED);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findAllByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);

		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findTestClassAndITByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId AND (o.status = :status1 OR o.status = :status2) AND (o.lesson.learningCycle.unit.course.type = :itTest OR o.lesson.learningCycle.unit.course.type = :demo OR o.lesson.learningCycle.unit.course.type = :trial) ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("status1", Status.FINISHED);
		typedQuery.setParameter("status2", Status.BOOKED);
		typedQuery.setParameter("itTest", Type.IT_TEST);
		typedQuery.setParameter("demo", Type.DEMO);
		typedQuery.setParameter("trial", Type.TRIAL); // trial课从Type demo 中，取出所以新增此项
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	} 

	public List<OnlineClass> findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(long learningProgressCourseId, long studentId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o JOIN o.students s WHERE o.lesson.learningCycle.unit.course.id = :courseId AND s.id = :studentId AND o.status = :status AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", learningProgressCourseId);
		typedQuery.setParameter("status", Status.BOOKED);
		Calendar startOfThisWeekCalendar = Calendar.getInstance();
		Calendar endofnextWeekCalendar = Calendar.getInstance();
		startOfThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int minute = startOfThisWeekCalendar.get(Calendar.MINUTE);
		if (minute > 30) {
			startOfThisWeekCalendar.set(Calendar.MINUTE, 30);
		} else {
			startOfThisWeekCalendar.add(Calendar.HOUR_OF_DAY, -1);
			startOfThisWeekCalendar.set(Calendar.MINUTE, 59);
		}
//		startOfThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//		startOfThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
//		startOfThisWeekCalendar.set(Calendar.MINUTE, 0);
//		startOfThisWeekCalendar.set(Calendar.SECOND, 0);
//		startOfThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		endofnextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endofnextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endofnextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endofnextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endofnextWeekCalendar.set(Calendar.MINUTE, 59);
		endofnextWeekCalendar.set(Calendar.SECOND, 59);
		endofnextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfThisWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endofnextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}

	public List<OnlineClass> findNextWeekAvailableOnlineClassesByLearningProgressCourseIdAndStudentId(long learningProgressCourseId, long studentId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o JOIN o.students s WHERE o.status = :status AND s.id = :studentId AND o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime AND "
				+ "o.lesson.learningCycle.unit.course.id = :courseId ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.AVAILABLE);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", learningProgressCourseId);
		Calendar startOfThisWeekCalendar = Calendar.getInstance();
		Calendar endofThisWeekCalendar = Calendar.getInstance();
		startOfThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfThisWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfThisWeekCalendar.set(Calendar.MINUTE, 0);
		startOfThisWeekCalendar.set(Calendar.SECOND, 0);
		startOfThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		endofThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endofThisWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endofThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endofThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endofThisWeekCalendar.set(Calendar.MINUTE, 59);
		endofThisWeekCalendar.set(Calendar.SECOND, 59);
		endofThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfThisWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endofThisWeekCalendar.getTime());
		
		return typedQuery.getResultList();
	}	
	
	public List<OnlineClass> findNextWeekOpenOnlineClassesByLearningProgressCourseId(long learningProgressCourseId) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime AND "
				+ "o.lesson.learningCycle.unit.course.id = :courseId AND o.lesson.learningCycle.unit.course.mode = :mode";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.OPEN);
		typedQuery.setParameter("mode", Course.Mode.ONE_TO_MANY);
		typedQuery.setParameter("courseId", learningProgressCourseId);
		Calendar startOfThisWeekCalendar = Calendar.getInstance();
		Calendar endofThisWeekCalendar = Calendar.getInstance();
		startOfThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfThisWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfThisWeekCalendar.set(Calendar.MINUTE, 0);
		startOfThisWeekCalendar.set(Calendar.SECOND, 0);
		startOfThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		endofThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endofThisWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endofThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endofThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endofThisWeekCalendar.set(Calendar.MINUTE, 59);
		endofThisWeekCalendar.set(Calendar.SECOND, 59);
		endofThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfThisWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endofThisWeekCalendar.getTime());
		
		return typedQuery.getResultList();
	}

	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatus(long teacherId, Date startDate, Date endDate, List<Status> statuses) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		for(Status status : statuses) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		andPredicates.add(criteriaBuilder.between(onlineClass.get(OnlineClass_.scheduledDateTime), startDate, endDate));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		criteriaQuery.where(orPredicate, andPredicate);
		
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findByTeacherIdAndStartDateAndEndDateAndStatusAndPage(long teacherId, Date startDate, Date endDate, List<Status> statuses, int page, int size) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
		criteriaQuery.orderBy(criteriaBuilder.asc(onlineClass.get(OnlineClass_.scheduledDateTime)));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		for(Status status : statuses) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		andPredicates.add(criteriaBuilder.between(onlineClass.get(OnlineClass_.scheduledDateTime), startDate, endDate));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		criteriaQuery.where(orPredicate, andPredicate);
		
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult((page-1) * size);
		typedQuery.setMaxResults(size);
		return typedQuery.getResultList();
	}
	
	public Long countByTeacherIdAndStartDateAndEndDateAndStatus(long teacherId, Date startDate, Date endDate, List<Status> statuses) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		for(Status status : statuses) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		andPredicates.add(criteriaBuilder.between(onlineClass.get(OnlineClass_.scheduledDateTime), startDate, endDate));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		criteriaQuery.where(orPredicate, andPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	// 高源，请正视自身代码问题，仔细编代码
	// 霍震中，没看清楚怎么回事之前，不要乱说
	public List<OnlineClass> findByStudentIdAndFinishType(long studentId, String finishType, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Student> student = onlineClass.join(OnlineClass_.students, JoinType.LEFT);
		criteriaQuery.distinct(true);
		
		List<Predicate> andPredicates = new ArrayList<Predicate>();
		List<Predicate> orPredicates = new ArrayList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
		if (finishType.equals("normal")) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.AS_SCHEDULED));
		} else if (finishType.equals("abnormal")) {
			orPredicates.add(criteriaBuilder.notEqual(onlineClass.get(OnlineClass_.finishType), FinishType.AS_SCHEDULED));
			orPredicates.add(criteriaBuilder.isNull(onlineClass.get(OnlineClass_.finishType)));
		} else if (finishType.equals("all")) {
			// do nothing here
		}
		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), Status.FINISHED));
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		if (orPredicates.size() > 0) {
			criteriaQuery.where(criteriaBuilder.and(andPredicate, orPredicate));
		} else {
			criteriaQuery.where(andPredicate);
		}
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findByStudentIdAndCourseType(long studentId, Type type, int start, int length) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students ocs WHERE ocs.id = :studentId AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("type", type);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();
	}

	public long countByStudentIdAndFinishType(long studentId,
			String finishType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Student> student = onlineClass.join(OnlineClass_.students);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));
		
		List<Predicate> orPredicates = new ArrayList<Predicate>();
		orPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
		for (String type : finishType.split("\\|")) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(type)));
		}
		Predicate andPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		criteriaQuery.where(andPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}

	public long countByStudentIdAndFinishTypeForAttendence(String teacherId ,String studentId,
			FinishType finishType, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		Join<OnlineClass, Student> student = onlineClass.join(OnlineClass_.students,JoinType.LEFT);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher,JoinType.LEFT);
		List<Predicate> andPredicates = new LinkedList<Predicate>();

		List<Predicate> orPredicates = new ArrayList<Predicate>();
		if (studentId != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
			
		}
		if (teacherId != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		
		}
		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), finishType));
		
		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status),Status.FINISHED));
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		if (scheduledDateTimeFrom != null) {
			andPredicates
					.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		criteriaQuery.where(orPredicate,andPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

		return typedQuery.getSingleResult();
	}

	public List<OnlineClassPeakTimeView> listPeakTimeView(DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Long teacherId, Long courseId, boolean isEmptySlot) {
		String exeSql = null;
		String preWithCourse = "SELECT DISTINCT NEW com.vipkid.service.pojo.OnlineClassPeakTimeView(o.id, o.status,o.finishType,o.scheduledDateTime,o.lesson.learningCycle.unit.course.type,o.teacher.id)   FROM OnlineClass o  WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate";
		String preWithoutCourse = "SELECT DISTINCT NEW com.vipkid.service.pojo.OnlineClassPeakTimeView(o.id, o.status,o.finishType,o.scheduledDateTime,o.teacher.id) FROM OnlineClass o JOIN o.teacher.certificatedCourses tcs WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate";

		String withTeacher = " AND o.teacher.id = :teacherId";
		String withCourse = " AND o.lesson.learningCycle.unit.course.id = :courseId";
		String withStatus = " AND o.status =:status";
		String order = " ORDER BY o.scheduledDateTime ASC";
		String withCourseA = " AND tcs.id = :courseId";

		if (isEmptySlot) {
			// search the courses without ordered .
			if (courseId != null) {
				preWithoutCourse += withCourseA;
			}

			exeSql = preWithoutCourse;

			if (teacherId != null) {
				exeSql += withTeacher;
			}
			exeSql += order;

		} else {
			// search the courses have been ordered.
			if (teacherId != null) {
				if (courseId != null) {
					exeSql = preWithCourse + withTeacher + withCourse + order;
				} else {
					exeSql = preWithCourse + withTeacher + order;
				}
			} else {
				if (courseId != null) {
					exeSql = preWithCourse + withCourse + order;
				} else {
					exeSql = preWithCourse + order;
				}
			}
		}

		TypedQuery<OnlineClassPeakTimeView> typedQuery = entityManager.createQuery(exeSql, OnlineClassPeakTimeView.class);
		typedQuery.setParameter("startDate", scheduledDateTimeFrom.getValue());
		typedQuery.setParameter("endDate", scheduledDateTimeTo.getValue());

		if (teacherId != null) {
			typedQuery.setParameter("teacherId", teacherId);
		}
		if (courseId != null) {
			typedQuery.setParameter("courseId", courseId);
		}

		return typedQuery.getResultList();

	}
	
	public List<OnlineClass> listByStudentIdAndFinishTypeForAttendence(String teacherId ,String studentId,
			FinishType finishType, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo,int start,int length) {
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		Join<OnlineClass, Student> student = onlineClass.join(OnlineClass_.students,JoinType.LEFT);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher,JoinType.LEFT);
		List<Predicate> andPredicates = new LinkedList<Predicate>();
	

		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (studentId != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
			
		}
		if (teacherId != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		
		}
		if (finishType != null) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), finishType));
		}
		
		andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status),Status.FINISHED));
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		if (scheduledDateTimeFrom != null) {
			andPredicates
					.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		criteriaQuery.where(orPredicate,andPredicate);
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();	
	}

	public long countBackupDutyByTeacher(Teacher teacher) {
		String sql = "SELECT COUNT(oc) FROM OnlineClass oc WHERE oc.backup = true AND oc.teacher = :teacher";
		Query query = entityManager.createQuery(sql, OnlineClass.class);
		query.setParameter("teacher", teacher);
	    
		return (Long) query.getSingleResult();
	}

	public long countBackupDutyByTeacherIdAndDate(long teacherId, Date date) {
		String sql = "SELECT COUNT(oc) FROM OnlineClass oc WHERE oc.backup = true AND oc.teacher.id = :teacherId AND oc.scheduledDateTime BETWEEN :startDate AND :endDate";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("teacherId", teacherId);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(date);
		startCalendar.set(Calendar.DATE, 1);
		typedQuery.setParameter("startDate", startCalendar.getTime());
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(date);
		endCalendar.set(Calendar.DATE, -1);
		typedQuery.setParameter("endDate", endCalendar.getTime());
		
		try {
			return typedQuery.getFirstResult();
		} catch (NoResultException e) {
			return 0;
		}
	}

	public long countInterviewEnrolledByTeacherIdAndDate(long teacherId, Date date) {
		// first get all the enrolled student this month, get the interview online class they took, count plus one if the teacher is teacherId
		String sql = "SELECT COUNT(DISTINCT coc) FROM Student s JOIN s.learningProgresses lp JOIN lp.completedOnlineClasses coc "
				+ "JOIN s.orders o WHERE "
				+ "(coc.lesson.learningCycle.unit.course.type = :demo OR coc.lesson.learningCycle.unit.course.type = :trial) AND coc.teacher.id = :teacherId AND coc.scheduledDateTime BETWEEN :startDate AND :endDate AND "
				+ "o.status = :orderStatus";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("demo", Type.DEMO);
		typedQuery.setParameter("trial", Type.TRIAL); // trial课从Type demo 中，取出所以新增此项
		typedQuery.setParameter("teacherId", teacherId);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(date);
		startCalendar.set(Calendar.DATE, 1);
		typedQuery.setParameter("startDate", startCalendar.getTime());
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(date);
		endCalendar.set(Calendar.DATE, -1);
		typedQuery.setParameter("endDate", endCalendar.getTime());
		typedQuery.setParameter("orderStatus", Order.Status.PAY_CONFIRMED);

		try {
			return typedQuery.getFirstResult();
		} catch (NoResultException e) {
			return 0;
		}
	}
	
	public OnlineClass findByStudentIdAndLessonId(long studentId, long lessonId) {
		String sql = "SELECT oc FROM OnlineClass oc JOIN oc.students s WHERE s.id = :studentId AND oc.lesson.id = :lessonId AND oc.finishType = :ASSCHEDULED";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("lessonId", lessonId);
		typedQuery.setParameter("ASSCHEDULED", FinishType.AS_SCHEDULED);
		
		List<OnlineClass> list = typedQuery.getResultList();
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public List<OnlineClass> findNextWeekBookedOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.status = :status AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", Status.BOOKED);
		Calendar startOfNextWeekCalendar = Calendar.getInstance();
		Calendar endOfNextWeekCalendar = Calendar.getInstance();
		startOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfNextWeekCalendar.set(Calendar.MINUTE, 0);
		startOfNextWeekCalendar.set(Calendar.SECOND, 0);
		startOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		endOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfNextWeekCalendar.set(Calendar.MINUTE, 59);
		endOfNextWeekCalendar.set(Calendar.SECOND, 59);
		endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfNextWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfNextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findNextWeekBackUpOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.backup = :backup AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("backup", true);
		Calendar startOfNextWeekCalendar = Calendar.getInstance();
		Calendar endOfNextWeekCalendar = Calendar.getInstance();
		startOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfNextWeekCalendar.set(Calendar.MINUTE, 0);
		startOfNextWeekCalendar.set(Calendar.SECOND, 0);
		startOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		endOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfNextWeekCalendar.set(Calendar.MINUTE, 59);
		endOfNextWeekCalendar.set(Calendar.SECOND, 59);
		endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfNextWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfNextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findTodayBookedOrBackupOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND (o.status = :status OR o.backup = :backup) AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("backup", true);
		Calendar startOfTodayCalendar = Calendar.getInstance();
		Calendar endOfTodayCalendar = Calendar.getInstance();
		startOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfTodayCalendar.set(Calendar.MINUTE, 0);
		startOfTodayCalendar.set(Calendar.SECOND, 0);
		startOfTodayCalendar.set(Calendar.MILLISECOND, 0);
		endOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfTodayCalendar.set(Calendar.MINUTE, 59);
		endOfTodayCalendar.set(Calendar.SECOND, 59);
		endOfTodayCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfTodayCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfTodayCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findTomorrowBookedOrBackupOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.teacher.id = :teacherId AND (o.status = :status OR o.backup = :backup) AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("backup", true);
		Calendar startOfTomorrowCalendar = Calendar.getInstance();
		Calendar endOfTomorrowCalendar = Calendar.getInstance();
		startOfTomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
		startOfTomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfTomorrowCalendar.set(Calendar.MINUTE, 0);
		startOfTomorrowCalendar.set(Calendar.SECOND, 0);
		startOfTomorrowCalendar.set(Calendar.MILLISECOND, 0);
		endOfTomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
		endOfTomorrowCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfTomorrowCalendar.set(Calendar.MINUTE, 59);
		endOfTomorrowCalendar.set(Calendar.SECOND, 59);
		endOfTomorrowCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfTomorrowCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfTomorrowCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findNextWeekBookedOnlineClassesByStudentId(long studentId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o JOIN o.students oss WHERE oss.id = :studentId AND o.status = :status AND "
				+ "o.scheduledDateTime >= :startDateTime AND o.scheduledDateTime <= :endDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("status", Status.BOOKED);
		Calendar startOfNextWeekCalendar = Calendar.getInstance();
		Calendar endOfNextWeekCalendar = Calendar.getInstance();
		startOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfNextWeekCalendar.set(Calendar.MINUTE, 0);
		startOfNextWeekCalendar.set(Calendar.SECOND, 0);
		startOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		endOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfNextWeekCalendar.set(Calendar.MINUTE, 59);
		endOfNextWeekCalendar.set(Calendar.SECOND, 59);
		endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfNextWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfNextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findNextWeekBookedItTestOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.status = :status AND o.lesson.learningCycle.unit.course.type = :type AND o.teacher.id = :teacherId AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("type", Type.IT_TEST);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("teacherId", teacherId);
		Calendar startOfNextWeekCalendar = Calendar.getInstance();
		Calendar endOfNextWeekCalendar = Calendar.getInstance();
		startOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		startOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfNextWeekCalendar.set(Calendar.MINUTE, 0);
		startOfNextWeekCalendar.set(Calendar.SECOND, 0);
		startOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		endOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfNextWeekCalendar.set(Calendar.MINUTE, 59);
		endOfNextWeekCalendar.set(Calendar.SECOND, 59);
		endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfNextWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfNextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findTodayBookedItTestOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.status = :status AND o.lesson.learningCycle.unit.course.type = :itTest AND o.teacher.id = :teacherId AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("itTest", Type.IT_TEST);
		typedQuery.setParameter("teacherId", teacherId);
		Calendar startOfTodayCalendar = Calendar.getInstance();
		Calendar endOfTodayCalendar = Calendar.getInstance();
		startOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfTodayCalendar.set(Calendar.MINUTE, 0);
		startOfTodayCalendar.set(Calendar.SECOND, 0);
		startOfTodayCalendar.set(Calendar.MILLISECOND, 0);
		endOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfTodayCalendar.set(Calendar.MINUTE, 59);
		endOfTodayCalendar.set(Calendar.SECOND, 59);
		endOfTodayCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfTodayCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfTodayCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findTomorrowBookedItTestOnlineClassesByTeacherId(long teacherId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.status = :status AND o.lesson.learningCycle.unit.course.type = :itTest AND o.teacher.id = :teacherId AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("itTest", Type.IT_TEST);
		typedQuery.setParameter("teacherId", teacherId);
		Calendar startOfTomorrowCalendar = Calendar.getInstance();
		Calendar endOfTomorrowCalendar = Calendar.getInstance();
		startOfTomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
		startOfTomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfTomorrowCalendar.set(Calendar.MINUTE, 0);
		startOfTomorrowCalendar.set(Calendar.SECOND, 0);
		startOfTomorrowCalendar.set(Calendar.MILLISECOND, 0);
		endOfTomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
		endOfTomorrowCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfTomorrowCalendar.set(Calendar.MINUTE, 59);
		endOfTomorrowCalendar.set(Calendar.SECOND, 59);
		endOfTomorrowCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfTomorrowCalendar.getTime());
		typedQuery.setParameter("endDateTime", endOfTomorrowCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> findBookedNormalOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.NORMAL);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findBookedMajorOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.MAJOR);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}

	public List<OnlineClass> findBookedOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate  ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}

	public List<OnlineClass> findBookedDemoOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND (o.lesson.learningCycle.unit.course.type = :demo OR o.lesson.learningCycle.unit.course.type = :trial) ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("demo", Type.DEMO);
		typedQuery.setParameter("trial", Type.DEMO); // trial课从Type demo 中，取出所以新增此项
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findBookedTrialOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Course.Type.TRIAL);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findBookedItTestOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.IT_TEST);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findBookedLTOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :LTType  ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("LTType", Type.ELECTIVE_LT);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);

		return typedQuery.getResultList();
	}



	public List<OnlineClass> findBookedAssessment2OnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type  ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.ASSESSMENT2);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);

		return typedQuery.getResultList();
	}

	public OnlineClass findFinishAsScheduledDemoOnlineClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students oss WHERE o.status = :status AND o.finishType = :finishType AND (o.lesson.learningCycle.unit.course.type = :demo OR o.lesson.learningCycle.unit.course.type = :trial) AND oss.id = :studentId ORDER BY o.scheduledDateTime DESC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.FINISHED);
		typedQuery.setParameter("finishType", FinishType.AS_SCHEDULED);
		typedQuery.setParameter("demo", Type.DEMO);
		typedQuery.setParameter("trial", Type.TRIAL);
		typedQuery.setParameter("studentId", studentId);
	     
		return typedQuery.getSingleResult();
	}

	public Count findBookedLessonNumberThisWeek(long studentId) {
		// TODO Auto-generated method stub
		String sql = "SELECT COUNT(DISTINCT o) FROM OnlineClass o JOIN o.students oss "
				+ "WHERE oss.id = :studentId AND "
				+ "o.scheduledDateTime >= :startDateTime AND o.scheduledDateTime <= :endDateTime ";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		endCalendar.set(Calendar.HOUR_OF_DAY, 0);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.add(Calendar.DATE, 7);
		typedQuery.setParameter("startDateTime", startCalendar.getTime());
		typedQuery.setParameter("endDateTime", endCalendar.getTime());
	     
		Count count = new Count();
		count.setTotal(typedQuery.getSingleResult());
		return count;
	}

	public Count findBookedLessonNumberNextWeek(long studentId) {
		// TODO Auto-generated method stub
		String sql = "SELECT COUNT(DISTINCT o) FROM OnlineClass o JOIN o.students oss "
				+ "WHERE oss.id = :studentId AND "
				+ "o.scheduledDateTime >= :startDateTime AND o.scheduledDateTime <= :endDateTime ";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		startCalendar.add(Calendar.DATE, 7);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		endCalendar.set(Calendar.HOUR_OF_DAY, 0);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.add(Calendar.DATE, 14);
		typedQuery.setParameter("startDateTime", startCalendar.getTime());
		typedQuery.setParameter("endDateTime", endCalendar.getTime());
		     
		Count count = new Count();
		count.setTotal(typedQuery.getSingleResult());
		return count;
	}
	
	
	public OnlineClass findLatestBookedClassByStudentId(long studentId) {
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s WHERE s.id = :studentId AND o.status = :status  ORDER BY o.scheduledDateTime ASC";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
    	typedQuery.setParameter("status", Status.BOOKED);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			OnlineClass onlineClassResult = onlineClassList.get(0);
			return onlineClassResult;
		}else {
			return null;
		}				
	}

	public long countByTeacherIdAndEndDateAndStatus(long teacherId, Date endDate, List<Status> statueList) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		for(Status status : statueList) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		andPredicates.add(criteriaBuilder.lessThan(onlineClass.get(OnlineClass_.scheduledDateTime), endDate));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		criteriaQuery.where(orPredicate, andPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public long countByTeacherIdAndStartDateAndStatus(long teacherId, Date startDate, List<Status> statueList) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		for(Status status : statueList) {
			orPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.equal(teacher.get(User_.id), teacherId));
		andPredicates.add(criteriaBuilder.greaterThan(onlineClass.get(OnlineClass_.scheduledDateTime), startDate));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		criteriaQuery.where(orPredicate, andPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	
	public List<OnlineClass> findNeedFinishAutomaticallyOnlineClasses() {
		String sql = "SELECT DISTINCT o FROM OnlineClass o WHERE o.status = :status AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime AND o.lesson.learningCycle.unit.course.type != :practicumCourse AND o.lesson.learningCycle.unit.course.type != :teacherRecruitmentCourse";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", OnlineClass.Status.BOOKED);

		Date startDateTime = DateTimeUtils.getYesterday(0);
		Calendar endOfTodayCalendar = Calendar.getInstance();
		endOfTodayCalendar.add(Calendar.DATE, -1);
		endOfTodayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endOfTodayCalendar.set(Calendar.MINUTE, 59);
		endOfTodayCalendar.set(Calendar.SECOND, 59);
		endOfTodayCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startDateTime);
		typedQuery.setParameter("endDateTime", endOfTodayCalendar.getTime());
		typedQuery.setParameter("practicumCourse", Course.Type.PRACTICUM);
		typedQuery.setParameter("teacherRecruitmentCourse", Course.Type.TEACHER_RECRUITMENT);
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public List<OnlineClass> listForFireman(List<Long> courseIds, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, List<Status> statusList, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
		 
		// compose OR predicate
		List<Predicate> orStatusPredicates = new LinkedList<Predicate>();
		if (statusList.isEmpty() != true) {
			for(Status status : statusList){
				orStatusPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
			}
		}
		Predicate orStatusPredicate = criteriaBuilder.or(orStatusPredicates.toArray(new Predicate[orStatusPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = scheduledDateTimeTo.getValue();
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThan(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStatusPredicates.size() > 0) {
			finalPredicates.add(orStatusPredicate);
		}
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();	
	}
	
	public long countForFireman(List<Long> courseIds, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, List<Status> statusList) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));

		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
		
		// compose OR predicate
		List<Predicate> orStatusPredicates = new LinkedList<Predicate>();
		if (statusList.isEmpty() != true) {
			for(Status status : statusList){
				orStatusPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), status));
			}
		}
		Predicate orStatusPredicate = criteriaBuilder.or(orStatusPredicates.toArray(new Predicate[orStatusPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = scheduledDateTimeTo.getValue();
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThan(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStatusPredicates.size() > 0) {
			finalPredicates.add(orStatusPredicate);
		}
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}

	public long countBackupDutyByTeacherIdAndStartDateAndEndDate(long teacherId, Date startDate, Date endDate) {
		
		String sql = "SELECT COUNT(o) FROM OnlineClass o WHERE o.teacher.id = :teacherId AND o.scheduledDateTime BETWEEN :start AND :end AND o.backup = :isBackup";
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("start", startDate);
		typedQuery.setParameter("end", endDate);
		typedQuery.setParameter("isBackup", true);
		
		return typedQuery.getSingleResult();
	}
	public List<LessonsView> listForLessons(long studentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.LessonsView(OL.id,OL.scheduledDateTime,OL.finishType,OL.lesson.learningCycle.unit.level,OL.lesson.learningCycle.unit.number,OL.lesson.learningCycle.name,OL.lesson.name,TC.stars,TC.id,OL.teacher.name,OL.teacher.id,ST.id,TC.teacherFeedback,OL.lesson.learningCycle.unit.unitTestPath) FROM ");
		sql.append(" OnlineClass OL LEFT JOIN OL.students ST LEFT JOIN OL.teacherComments TC");
		sql.append(" WHERE ST.id = :studentId AND TC.student.id = :studentId");
		sql.append(" AND (OL.status = :status1 or (OL.status = :status2");
		sql.append(" AND  OL.scheduledDateTime <= :scheduledDateTime ))");
		sql.append(" ORDER BY OL.scheduledDateTime DESC");
		
		TypedQuery<LessonsView> query = entityManager.createQuery(sql.toString(),LessonsView.class);
		Date scheduledDateTime = new Date(new Date().getTime()-25*60*1000);
		query.setParameter("studentId", studentId);
		query.setParameter("status1", Status.FINISHED);
		query.setParameter("status2", Status.BOOKED);
		query.setParameter("scheduledDateTime", scheduledDateTime);
		query.setMaxResults(1);
		List<LessonsView>list = query.getResultList();
		if(list!=null&&list.size()!=0){
			return list;
		}
		return new ArrayList<LessonsView>();
	}
	public List<LessonsView> listForLessons(long studentId,int rowNum,int currNum){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.LessonsView(OL.id,OL.scheduledDateTime,OL.finishType,OL.lesson.learningCycle.unit.level,OL.lesson.learningCycle.unit.number,OL.lesson.learningCycle.name,OL.lesson.name,TC.stars,TC.id,OL.teacher.name,OL.teacher.id,ST.id,TC.teacherFeedback,OL.lesson.learningCycle.unit.unitTestPath) FROM ");
		sql.append(" OnlineClass OL LEFT JOIN OL.students ST LEFT JOIN OL.teacherComments TC");
		sql.append(" WHERE ST.id = :studentId AND TC.student.id = :studentId");
		sql.append(" AND (OL.status = :status1 or (OL.status = :status2");
		sql.append(" AND  OL.scheduledDateTime <= :scheduledDateTime ))");
		sql.append(" ORDER BY OL.scheduledDateTime DESC");
		
		TypedQuery<LessonsView> query = entityManager.createQuery(sql.toString(),LessonsView.class);
		Date scheduledDateTime = new Date(new Date().getTime()-25*60*1000);
		query.setParameter("studentId", studentId);
		query.setParameter("status1", Status.FINISHED);
		query.setParameter("status2", Status.BOOKED);
		query.setParameter("scheduledDateTime", scheduledDateTime);
		query.setFirstResult((currNum-1)*rowNum);
		query.setMaxResults(rowNum);
		List<LessonsView>list = query.getResultList();
		if(list!=null&&list.size()!=0){
			return list;
		}
		return new ArrayList<LessonsView>();
		
	}
	
	public LessonsView getNearestLessons(long studentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.LessonsView(OL.id,OL.scheduledDateTime,OL.finishType,OL.lesson.learningCycle.unit.level,OL.lesson.learningCycle.unit.number,OL.lesson.learningCycle.name,OL.lesson.name,OL.teacher.name,OL.teacher.id,ST.id) FROM ");
		sql.append(" OnlineClass OL LEFT JOIN OL.students ST");
		sql.append(" WHERE ST.id = :studentId");
		sql.append(" AND OL.status = :status");
		sql.append(" AND OL.scheduledDateTime <= :scheduledDateTime");
		sql.append(" ORDER BY OL.scheduledDateTime DESC");
		
		TypedQuery<LessonsView> query = entityManager.createQuery(sql.toString(),LessonsView.class);
		Date scheduledDateTime = new Date(new Date().getTime()-25*60*1000);
		query.setParameter("studentId", studentId);
		query.setParameter("status", Status.BOOKED);
		query.setParameter("scheduledDateTime", scheduledDateTime);
		query.setMaxResults(1);
		List<LessonsView>list = query.getResultList();
		if(list!=null&&list.size()!=0){
			return list.get(0);
		}
		return new LessonsView();
		
	}
	
	public long countForLessons(long studentId,int rowNum,int currNum){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(OL.id) FROM ");
		sql.append(" OnlineClass OL LEFT JOIN OL.students ST LEFT JOIN OL.teacherComments TC");
		sql.append(" WHERE  ST.id = :studentId AND TC.student.id = :studentId");
		sql.append(" AND (OL.status = :status1 or (OL.status = :status2");
		sql.append(" AND  OL.scheduledDateTime <= :scheduledDateTime ))");
		sql.append(" ORDER BY OL.scheduledDateTime DESC");
		
		TypedQuery<Long> query = entityManager.createQuery(sql.toString(),Long.class);
		Date scheduledDateTime = new Date(new Date().getTime()-25*60*1000);
		query.setParameter("studentId", studentId);
		query.setParameter("status1", Status.FINISHED);
		query.setParameter("status2", Status.BOOKED);
		query.setParameter("scheduledDateTime", scheduledDateTime);
		return query.getSingleResult();
		
	}
	
	public LessonsView findStudiedLessonsDetail(long onlineClassId,long teacherCommentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.LessonsView(OL.id,OL.scheduledDateTime,OL.finishType,OL.lesson.learningCycle.unit.level,OL.lesson.learningCycle.unit.number,OL.lesson.learningCycle.name,OL.lesson.name,TC.stars,TC.id,OL.teacher.name,OL.teacher.id,ST.id,TC.teacherFeedback,TC.abilityToFollowInstructions,TC.repetition,TC.clearPronunciation,TC.readingSkills,TC.spellingAccuracy,TC.activelyInteraction,OL.lesson.objective,OL.lesson.vocabularies,OL.lesson.sentencePatterns,OL.lesson.learningCycle.grammar) FROM ");
		sql.append(" OnlineClass OL LEFT JOIN OL.students ST LEFT JOIN OL.teacherComments TC");
		sql.append(" WHERE  OL.id = :onlineClassId");
		if(teacherCommentId!=-1){
			sql.append(" AND TC.id = :teacherCommentId");
		}
		sql.append(" ORDER BY OL.scheduledDateTime DESC");
		
		TypedQuery<LessonsView> query = entityManager.createQuery(sql.toString(),LessonsView.class);
		query.setParameter("onlineClassId", onlineClassId);
		if(teacherCommentId!=-1){
			query.setParameter("teacherCommentId", teacherCommentId);
		}
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<LessonsView>list = query.getResultList();
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return new LessonsView();
	}
	public Medal findMedalId(long onlineClassId,long studentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m from Medal m ,OnlineClass OL  ");
		sql.append(" WHERE OL.lesson.learningCycle.unit.id = m.unit.id AND  OL.id = :onlineClassId");
		sql.append(" and m.student.id = :studentId");
		TypedQuery<Medal> query = entityManager.createQuery(sql.toString(),Medal.class);
		query.setParameter("onlineClassId", onlineClassId);
		query.setParameter("studentId", studentId);
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<Medal>medal  = query.getResultList();
		if(medal!=null&&medal.size()>0){
			return medal.get(0);
		}
		return new Medal();
	}
	
	public long countByStudentIdAndFinishedAsScheduledAndThisWeek(long studentId) {
		String sql = "SELECT count(o.id) FROM OnlineClass o JOIN o.students oss WHERE o.status = :status AND o.finishType = :finishType AND oss.id = :studentId AND o.scheduledDateTime BETWEEN :start AND :end";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("status", Status.FINISHED);
		typedQuery.setParameter("finishType", FinishType.AS_SCHEDULED);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("start", DateTimeUtils.getThisMonday());
		typedQuery.setParameter("end", DateTimeUtils.getTomorrow());
		return typedQuery.getSingleResult();
	}
	public long countParallelByScheduledDateTimeAndCourseType(Date scheduledDateTime, Type type) {
		String sql = "SELECT COUNT(o) FROM OnlineClass o WHERE o.status = :booked AND o.scheduledDateTime = :scheduledDateTime AND "
				+ "(o.lesson.learningCycle.unit.course.type in :typeList OR (o.lesson.learningCycle.unit.course.type = :type AND o.lesson.serialNumber = :serialNumber))";
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("booked", Status.BOOKED);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("typeList", TypeList);
		typedQuery.setParameter("type", Type.ELECTIVE_LT);
		typedQuery.setParameter("serialNumber", LT1_U1_LC1_L1);
		return typedQuery.getSingleResult();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<OnlineClassVo> listOnlineClassForLeads(Date scheduledTimeFrom, Date scheduledTimeTo,
			List<Long> courseIds, Status status, String finishType, Long salesId, Long tmkId,
			String teacherName, String searchText, Integer start, Integer length) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select onlineClass.id,onlineClass.scheduledDateTime,onlineClass.lesson,onlineClass.lesson.serialNumber")
			.append(",onlineClass.status,onlineClass.teacher.id,onlineClass.teacher.name,onlineClass.teacher.realName,onlineClass.finishType,stu.id,stu.name,stu.englishName,leads.salesId")
			.append(",leads.salesName,leads.tmkId,leads.tmkName,onlineClass.classroom,onlineClass.canUndoFinish,onlineClass.attatchDocumentSucess,onlineClass.dbyDocument");
		jpql.append(" from OnlineClass onlineClass join onlineClass.students stu join Leads leads on stu.id = leads.studentId");
				
		// scheduledTimeFrom/scheduledTimeTo
		if (scheduledTimeFrom != null) {
			jpql.append(" and onlineClass.scheduledDateTime >= :scheduledTimeFrom");
			params.put("scheduledTimeFrom", scheduledTimeFrom);
		}
		if (scheduledTimeTo != null) {
			scheduledTimeTo = DateTimeUtils.getNextDay(scheduledTimeTo);
			jpql.append(" and onlineClass.scheduledDateTime < :scheduledTimeTo");
			params.put("scheduledTimeTo", scheduledTimeTo);
		}
		
		//salesId
		if (salesId != null) {
			jpql.append(" and leads.salesId = :salesId");
			params.put("salesId", salesId);
		}
		
		//tmkId
		if (tmkId != null) {
			jpql.append(" and leads.tmkId = :tmkId");
			params.put("tmkId", tmkId);
		}
		
		//status
		if (status != null) {
			jpql.append(" and onlineClass.status = :status");
			params.put("status", status);
		}
		
		//courseIds
		if (CollectionUtils.isNotEmpty(courseIds)) {
			jpql.append(" and onlineClass.lesson.learningCycle.unit.course.id in :courseIds");
			params.put("courseIds", courseIds);
		}
		
		//finishType
		if (StringUtils.isNotBlank(finishType)) {
			if ("WITH_PROBLEM".equals(StringUtils.trimToEmpty(finishType))) {//非正常结束
				jpql.append(" and onlineClass.finishType != :finishType");
				params.put("finishType", FinishType.AS_SCHEDULED);
			} else{
				jpql.append(" and onlineClass.finishType = :finishType");
				params.put("finishType", FinishType.valueOf(finishType));
			}
		}
		//teacherName
		if (StringUtils.isNotBlank(teacherName)) {
			jpql.append(" and onlineClass.teacher.name like :teacherName");
			params.put("teacherName", "%" + teacherName + "%");
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = stu.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or stu.name like :searchTextLike or stu.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		jpql.append(" order by onlineClass.scheduledDateTime desc");
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		
		if (start != null) {
			query.setFirstResult(start);
		}
		if (length != null) {
			query.setMaxResults(length);
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		List<OnlineClassVo> onlineClassList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] row : resultList) {
				Long id = (Long)row[0];
				Date scheduledDateTime = (Date)row[1];
				Lesson lesson = (Lesson)row[2];
				String lessonSerialNumber = (String)row[3];
				status = (Status)row[4];
				Long teacherId = (Long)row[5];
				teacherName = (String)row[6];
				String teacherRealName = (String)row[7];
				FinishType finishType1 = (FinishType)row[8];
				Long stuId = (Long)row[9];	
				String stuName = (String)row[10];
				String stuEnglishName = (String)row[11];
				salesId = (Long)row[12];
				String salesName = (String)row[13];
				tmkId = (Long)row[14];
				String tmkName = (String)row[15];
				String classroom = (String)row[16];
				boolean canUndoFinish = (boolean)row[17];
				boolean attatchDocumentSucess = (boolean)row[18];
				String dbyDocument = (String)row[19];
				
				String courseName = null;
				LessonVo lessonVo = null;
				if (lesson != null) {
					lessonVo = new LessonVo();
					LearningCycleVo learningCycleVo = null;
					if (lesson.getLearningCycle() != null) {
						learningCycleVo = new LearningCycleVo();
						UnitVo unitVo = null;
						if (lesson.getLearningCycle().getUnit() != null) {
							unitVo = new UnitVo();
							CourseVo courseVo = null;
							if (lesson.getLearningCycle().getUnit().getCourse() != null) {
								courseVo = new CourseVo();
								Course course = lesson.getLearningCycle().getUnit().getCourse();
								courseVo.setId(course.getId());
								courseVo.setMode(course.getMode());
								courseVo.setName(course.getName());
								courseVo.setSequential(course.isSequential());
								courseVo.setType(course.getType());
								courseName = course.getName();
							}
							unitVo.setId(lesson.getLearningCycle().getUnit().getId());
							unitVo.setCourse(courseVo);
						}
						
						learningCycleVo.setId(learningCycleVo.getId());
						learningCycleVo.setUnit(unitVo);
					}
					lessonVo.setId(lesson.getId());
					lessonVo.setDbyDocument(lesson.getDbyDocument());
					lessonVo.setLearningCycle(learningCycleVo);
					
				}
				TeacherVo teacherVo = new TeacherVo(); 
				teacherVo.setId(teacherId);
				teacherVo.setName(teacherName);
				teacherVo.setRealName(teacherRealName);
				
				StudentVo studentVo = new StudentVo();
				studentVo.setId(stuId);
				studentVo.setName(stuName);
				studentVo.setEnglishName(stuEnglishName);
				
				OnlineClassVo onlineClassVo = new OnlineClassVo();
				onlineClassVo.setId(id);
				onlineClassVo.setScheduledDateTime(scheduledDateTime != null ? scheduledDateTime.getTime() : null);
				onlineClassVo.setCourseName(courseName);
				onlineClassVo.setLessonSerialNumber(lessonSerialNumber);
				onlineClassVo.setStatus(status);
				onlineClassVo.setTeacher(teacherVo);
				onlineClassVo.setFinishType(finishType1);
				onlineClassVo.setStudents(Lists.newArrayList(studentVo));
				onlineClassVo.setSalesId(salesId);
				onlineClassVo.setSalesName(salesName);
				onlineClassVo.setTmkId(tmkId);
				onlineClassVo.setTmkName(tmkName);
				onlineClassVo.setLesson(lessonVo);
				onlineClassVo.setClassroom(classroom);
				onlineClassVo.setCanUndoFinish(canUndoFinish);
				onlineClassVo.setAttatchDocumentSucess(attatchDocumentSucess);
				onlineClassVo.setDbyDocument(dbyDocument);
				
				onlineClassList.add(onlineClassVo);
			}
		}
		return onlineClassList;
	}
	
	public long countOnlineClassForLeads(Date scheduledTimeFrom, Date scheduledTimeTo,
			List<Long> courseIds, Status status, String finishType, Long salesId, Long tmkId,
			String teacherName, String searchText) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		
		jpql.append("select count(onlineClass) ");
		jpql.append(" from OnlineClass onlineClass join onlineClass.students stu join Leads leads on stu.id = leads.studentId");
				
		// scheduledTimeFrom/scheduledTimeTo
		if (scheduledTimeFrom != null) {
			jpql.append(" and onlineClass.scheduledDateTime >= :scheduledTimeFrom");
			params.put("scheduledTimeFrom", scheduledTimeFrom);
		}
		if (scheduledTimeTo != null) {
			scheduledTimeTo = DateTimeUtils.getNextDay(scheduledTimeTo);
			jpql.append(" and onlineClass.scheduledDateTime < :scheduledTimeTo");
			params.put("scheduledTimeTo", scheduledTimeTo);
		}
		
		//salesId
		if (salesId != null) {
			jpql.append(" and leads.salesId = :salesId");
			params.put("salesId", salesId);
		}
		
		//tmkId
		if (tmkId != null) {
			jpql.append(" and leads.tmkId = :tmkId");
			params.put("tmkId", tmkId);
		}
		
		//status
		if (status != null) {
			jpql.append(" and onlineClass.status = :status");
			params.put("status", status);
		}
		
		//courseIds
		if (CollectionUtils.isNotEmpty(courseIds)) {
			jpql.append(" and onlineClass.lesson.learningCycle.unit.course.id in :courseIds");
			params.put("courseIds", courseIds);
		}
		
		//finishType
		if (StringUtils.isNotBlank(finishType)) {
			if ("WITH_PROBLEM".equals(StringUtils.trimToEmpty(finishType))) {//非正常结束
				jpql.append(" and onlineClass.finishType != :finishType");
				params.put("finishType", FinishType.AS_SCHEDULED);
			} else{
				jpql.append(" and onlineClass.finishType = :finishType");
				params.put("finishType", FinishType.valueOf(finishType));
			}
		}
		//teacherName
		if (StringUtils.isNotBlank(teacherName)) {
			jpql.append(" and onlineClass.teacher.name like :teacherName");
			params.put("teacherName", "%" + teacherName + "%");
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = stu.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or stu.name like :searchTextLike or stu.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);

		long count = (long) query.getSingleResult();
		return count;
	}
	
	public long countTrialClassForLeads(Leads.OwnerType ownerType, List<Long> staffIds, Date scheduledDateTimeFrom,Date scheduledDateTimeTo,
			List<Status> statusInclude, List<Status> statusExclude,List<FinishType> finishTypeInclude,
			List<FinishType> finishTypeExclude) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(distinct onlineClass) from OnlineClass onlineClass join onlineClass.students stu join Leads leads on stu.id = leads.studentId where onlineClass.lesson.learningCycle.unit.course.type = :type ");
		params.put("type", Type.TRIAL);
		
		if (CollectionUtils.isNotEmpty(staffIds)) {
			if (staffIds.size() > 1) {
				if (ownerType == Leads.OwnerType.STAFF_SALES) {
					jpql.append(" and leads.salesId in :staffIds");
				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
					jpql.append(" and leads.tmkId in :staffIds");
					
				}
				params.put("staffIds", staffIds);
			} else {
				if (ownerType == Leads.OwnerType.STAFF_SALES) {
					jpql.append(" and leads.salesId = :staffId");
				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
					jpql.append(" and leads.tmkId = :staffId");
					
				}
				params.put("staffId", staffIds.get(0));
				
			}
		}
 		
		// scheduledDateTimeFrom/scheduledDateTimeTo
		if (scheduledDateTimeFrom != null) {
			jpql.append(" and onlineClass.scheduledDateTime >= :scheduledDateTimeFrom");
			params.put("scheduledDateTimeFrom", scheduledDateTimeFrom);
		}
		if (scheduledDateTimeTo != null) {
			scheduledDateTimeTo = DateTimeUtils.getNextDay(scheduledDateTimeTo);
			jpql.append(" and onlineClass.scheduledDateTime < :scheduledDateTimeTo");
			params.put("scheduledDateTimeTo", scheduledDateTimeTo);
		}
		
		//status
		if (statusInclude != null) {
			jpql.append(" and onlineClass.status in :statusInclude");
			params.put("statusInclude", statusInclude);
		}
		
		if (statusExclude != null) {
			jpql.append(" and onlineClass.status not in :statusExclude");
			params.put("statusExclude", statusExclude);
		}
		
		//finishType
		if (finishTypeInclude != null) {
			jpql.append(" and onlineClass.finishType in :finishTypeInclude");
			params.put("finishTypeInclude", finishTypeInclude);
		}
		
		if (finishTypeExclude != null) {
			jpql.append(" and onlineClass.finishType not in :finishTypeExclude");
			params.put("finishTypeExclude", finishTypeExclude);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<CountOnlineClassByCourseView> countOnlineClassByCourse(){
		String sql = "select count(t.id),t.name from (select o.id,c.name from online_class o join lesson l on l.id = o.lesson_id join learning_cycle lc on l.learning_cycle_id = lc.id join unit u on lc.unit_id = u.id join course c on u.course_id = c.id join user us on o.teacher_id = us.id join teacher t on o.teacher_id = t.id  where c.type !='IT_TEST'  and o.status = 'BOOKED' and us.name not like '%测试%' and t.real_name not like '%test%' and us.account_type = 'NORMAL' and us.status = 'NORMAL' and t.type !='TEST' and o.scheduled_date_time>? and o.scheduled_date_time<=?) t GROUP BY t.name";
		Query query = entityManager.createNativeQuery(sql);
		Calendar c1 = new GregorianCalendar();
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		Calendar c2 = new GregorianCalendar();
		c2.set(Calendar.HOUR_OF_DAY, 23);
		c2.set(Calendar.MINUTE, 59);
		c2.set(Calendar.SECOND, 59);
		query.setParameter(1, c1.getTime());
		query.setParameter(2, c2.getTime());
		List<Object> rows = query.getResultList();
		List<CountOnlineClassByCourseView> cViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
		for (Object row : rows) {
			CountOnlineClassByCourseView cView = new CountOnlineClassByCourseView();
			Object[] cells = (Object[])row;
			cView.setNum(cells[0]==null?0:(long) (cells[0]));
			cView.setCourseName((String) (cells[1]));
			cViews.add(cView);
		}
		return cViews;
	}
	
	public long countByStudentId(long studentId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		Join<OnlineClass, Student> student = onlineClass.join(OnlineClass_.students);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));
		
		List<Predicate> orPredicates = new ArrayList<Predicate>();
		orPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
		Predicate andPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		criteriaQuery.where(andPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		
		return typedQuery.getSingleResult();	
	}
	
	public void doBookOnToManyForOpen(long studentId,long onlineClassId){
		String sql = "insert into online_class_student (online_class_id,student_id) values (?,?)";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, onlineClassId);
		query.setParameter(2, studentId);
		query.executeUpdate();
	}
	
	public long findOnlineClassByScheduledDateTime(Date time,long studentId){
		String sql = "select count(o.id) from online_class o where o.scheduled_date_time = ? and (o.status='BOOKED' or o.status='OPEN') and o.id in (select os.online_class_id from online_class_student os where os.student_id =? ) ";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, time);
		query.setParameter(2, studentId);
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	public long countByStudentIdAndCourseType(long studentId, Type type) {	
		String sql = "SELECT COUNT(o.id) FROM OnlineClass o JOIN o.students os WHERE o.lesson.learningCycle.unit.course.type = :type AND os.id = :studentId";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("type", type);
		typedQuery.setParameter("studentId", studentId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.debug("no countByStudentIdAndCourseType found");
			return 0;
		}
	}

	//查询第二天要进行ITTest的课程信息
	public List<OnlineClass> getITTestClassInfo(){
		String sql = "SELECT o FROM OnlineClass o JOIN o.students s JOIN o.lesson l JOIN l.learningCycle lc JOIN lc.unit u JOIN u.course c WHERE o.scheduledDateTime >= :scheduledDateTimeBegin and o.scheduledDateTime < :scheduledDateTimeEnd and ((s.name not like '%测试%' and s.name not like '%test%') or s.name is null) and o.status  = :onlineStatus and s.status = :studentStatus and c.id in (597804 , 597805, 597767) order by o.scheduledDateTime ";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		Calendar tomorrowBegin = Calendar.getInstance();
		tomorrowBegin.add(Calendar.DAY_OF_MONTH, 1);
		tomorrowBegin.set(Calendar.HOUR_OF_DAY, 0);
		tomorrowBegin.set(Calendar.MINUTE, 0);
		tomorrowBegin.set(Calendar.SECOND, 0);
		tomorrowBegin.set(Calendar.MILLISECOND, 0);
		Calendar tomorrowEnd = Calendar.getInstance();
		tomorrowEnd.add(Calendar.DAY_OF_MONTH, 2);
		tomorrowEnd.set(Calendar.HOUR_OF_DAY, 0);
		tomorrowEnd.set(Calendar.MINUTE, 0);
		tomorrowEnd.set(Calendar.SECOND, 0);
		tomorrowEnd.set(Calendar.MILLISECOND, 0);
		
		typedQuery.setParameter("scheduledDateTimeBegin", tomorrowBegin.getTime());
		typedQuery.setParameter("scheduledDateTimeEnd", tomorrowEnd.getTime());
		typedQuery.setParameter("onlineStatus", OnlineClass.Status.BOOKED);
		typedQuery.setParameter("studentStatus", Student.Status.NORMAL);
		List<OnlineClass> onlineClassList = typedQuery.getResultList();
		if(!onlineClassList.isEmpty()){
			return onlineClassList;
		}else {
			return null;
		}
	}

	public List<OnlineClass> findBookedKickoffOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.GUIDE);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}

	public List<OnlineClass> findBookedCLTCourseOnlineClassByStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.lesson.learningCycle.unit.course.type = :type ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("status", Status.BOOKED);
		typedQuery.setParameter("type", Type.REVIEW);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
	     
		return typedQuery.getResultList();
	}
	
	public Date findScheduledDateTimeById(long id){
		String sql = "SELECT o.scheduledDateTime FROM OnlineClass o WHERE o.id = :id";
		TypedQuery<Date> typedQuery = entityManager.createQuery(sql, Date.class);
		typedQuery.setParameter("id", id);
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.debug("no countByStudentIdAndCourseType found");
			return null;
		}
	}

	public List<OnlineClass> listForStudentComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Integer scores, Boolean existStudentComment, String finishType, String cltId, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OnlineClass> criteriaQuery = criteriaBuilder.createQuery(OnlineClass.class).distinct(true);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate+++
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
		
		
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		List<Predicate> orTeacherPredicates = new LinkedList<Predicate>();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			orTeacherPredicates.add(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
			orTeacherPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + searchTeacherText + "%"));
		}
		Predicate teacherPredicate = criteriaBuilder.or(orTeacherPredicates.toArray(new Predicate[orTeacherPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (finishType != null && !finishType.equals("")) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}
		
		if(scores != null){
			Join<OnlineClass, StudentComment> studentComment = onlineClass.join(OnlineClass_.studentComments, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(studentComment.get(StudentComment_.scores), scores));
		}
		
		if(existStudentComment != null && existStudentComment){
			Join<OnlineClass, StudentComment> studentComment = onlineClass.join(OnlineClass_.studentComments, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.isNotNull(studentComment.get(StudentComment_.comment)));
			andPredicates.add(criteriaBuilder.notEqual(studentComment.get(StudentComment_.comment), ""));
		}
		
		if(StringUtils.isNotBlank(cltId)){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
        	Join<Student, Staff> cltStaff = students.join(Student_.chineseLeadTeacher);
        	andPredicates.add(criteriaBuilder.equal(cltStaff.get(User_.id), cltId));
		}
		
		andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}		
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();	
	}

	/**
	 * 查询条件与上面的函数不是完全一致，修改时需注意
	 */
	public long countForStudentComments(List<Long> courseIds, String searchTeacherText, String searchStudentText, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Integer scores, Boolean existStudentComment, String finishType, String cltId, Boolean existCommentScore) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<OnlineClass> onlineClass = criteriaQuery.from(OnlineClass.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(onlineClass));
		
		// compose OR predicate
		List<Predicate> orCoursePredicates = new LinkedList<Predicate>();
		if (!courseIds.isEmpty()) {//选择了course
			Join<Unit, Course> course = onlineClass.join(OnlineClass_.lesson).join(Lesson_.learningCycle).join(LearningCycle_.unit).join(Unit_.course);
			for(Long courseId : courseIds){
				orCoursePredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			}
		}
		Predicate orCoursePredicate = criteriaBuilder.or(orCoursePredicates.toArray(new Predicate[orCoursePredicates.size()]));
	
		// compose student predicate+++
		List<Predicate> orStudentPredicates = new LinkedList<Predicate>();
        if (searchStudentText != null){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
        	Join<Student, Family> family = students.join(Student_.family);
			Join<Family, Parent> parents = family.join(Family_.parents);
        	orStudentPredicates.add(criteriaBuilder.like(students.get(User_.name), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(students.get(Student_.englishName), "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + searchStudentText + "%"));
        	orStudentPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + searchStudentText + "%"));
        }
		
		
        Predicate orStudentPredicate = criteriaBuilder.or(orStudentPredicates.toArray(new Predicate[orStudentPredicates.size()]));
		        
		// compose teacher predicate
		List<Predicate> orTeacherPredicates = new LinkedList<Predicate>();
		if (searchTeacherText != null && !searchTeacherText.equals("")) {
			Join<OnlineClass, Teacher> teacher = onlineClass.join(OnlineClass_.teacher, JoinType.LEFT);
			orTeacherPredicates.add(criteriaBuilder.like(teacher.get(User_.name), "%" + searchTeacherText + "%"));
			orTeacherPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + searchTeacherText + "%"));
		}
		Predicate teacherPredicate = criteriaBuilder.or(orTeacherPredicates.toArray(new Predicate[orTeacherPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (scheduledDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.info("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.info("actual scheduleDateTimeTo=" + actualToDate);
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (finishType != null && !finishType.equals("")) {
			andPredicates.add(criteriaBuilder.equal(onlineClass.get(OnlineClass_.finishType), FinishType.valueOf(finishType)));
		}
		
		if(scores != null){
			Join<OnlineClass, StudentComment> studentComment = onlineClass.join(OnlineClass_.studentComments, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(studentComment.get(StudentComment_.scores), scores));
		}
		
		if(existStudentComment != null && existStudentComment){
			Join<OnlineClass, StudentComment> studentComment = onlineClass.join(OnlineClass_.studentComments, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.isNotNull(studentComment.get(StudentComment_.comment)));
			andPredicates.add(criteriaBuilder.notEqual(studentComment.get(StudentComment_.comment), ""));
		}
		
		if(existCommentScore != null && existCommentScore){
			Join<OnlineClass, StudentComment> studentComment = onlineClass.join(OnlineClass_.studentComments, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.isNotNull(studentComment.get(StudentComment_.scores)));
		}
		
		if(StringUtils.isNotBlank(cltId)){
        	Join<OnlineClass, Student> students = onlineClass.join(OnlineClass_.students);
        	Join<Student, Staff> cltStaff = students.join(Student_.chineseLeadTeacher);
        	andPredicates.add(criteriaBuilder.equal(cltStaff.get(User_.id), cltId));
		}
		
		andPredicates.add(criteriaBuilder.isNotNull(onlineClass.get(OnlineClass_.finishType)));
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(onlineClass.get(OnlineClass_.archived));
		
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orCoursePredicates.size() > 0) {
			finalPredicates.add(orCoursePredicate);
		}
		if(orStudentPredicates.size() > 0) {
			finalPredicates.add(orStudentPredicate);
		}
		if(searchTeacherText != null && !searchTeacherText.equals("")) {
			finalPredicates.add(teacherPredicate);
		}		
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		finalPredicates.add(archivedPredicate);
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(onlineClass.get(OnlineClass_.scheduledDateTime)));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

		return typedQuery.getSingleResult();	
	}

	
	@SuppressWarnings("unchecked")
	public List<OnlineClassVo> listOnlineClassForCLT(Date scheduledTimeFrom, Date scheduledTimeTo,
			List<Long> courseIds, Status status, String finishType, Long cltId,
			String teacherName, String searchText, Integer start, Integer length) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();

		jpql.append("select ");
		jpql.append("onlineClass.id,");
		jpql.append("onlineClass.scheduledDateTime,");
		jpql.append("onlineClass.lesson,");
		jpql.append("onlineClass.status,");
		jpql.append("onlineClass.finishType,");
		jpql.append("onlineClass.classroom,");
		jpql.append("onlineClass.canUndoFinish,");
		jpql.append("onlineClass.attatchDocumentSucess,");
		jpql.append("onlineClass.dbyDocument,");
		jpql.append("onlineClass.teacher.id,");
		jpql.append("onlineClass.teacher.name,");
		jpql.append("onlineClass.teacher.realName,");
		jpql.append("stu.id,");
		jpql.append("stu.name,");
		jpql.append("stu.englishName,");
		jpql.append("clt.id,");
		jpql.append("clt.name");
		
		jpql.append(" from OnlineClass onlineClass join onlineClass.students stu on ");
		jpql.append(" (1=1 ");
		
		// scheduledTimeFrom/scheduledTimeTo
		if (scheduledTimeFrom != null) {
			jpql.append(" and onlineClass.scheduledDateTime >= :scheduledTimeFrom");
			params.put("scheduledTimeFrom", scheduledTimeFrom);
		}
		if (scheduledTimeTo != null) {
			scheduledTimeTo = DateTimeUtils.getNextDay(scheduledTimeTo);
			jpql.append(" and onlineClass.scheduledDateTime < :scheduledTimeTo");
			params.put("scheduledTimeTo", scheduledTimeTo);
		}
		
		//cltId
		if (cltId != null) {
			jpql.append(" and stu.chineseLeadTeacher.id = :cltId");
			params.put("cltId", cltId);
		}
		
		//courseIds
		if (CollectionUtils.isNotEmpty(courseIds)) {
			jpql.append(" and onlineClass.lesson.learningCycle.unit.course.id in :courseIds");
			params.put("courseIds", courseIds);
		}
		
		//status
		if (status != null) {
			jpql.append(" and onlineClass.status = :status");
			params.put("status", status);
		}
		
		//finishType
		if (StringUtils.isNotBlank(finishType)) {
			if ("WITH_PROBLEM".equals(StringUtils.trimToEmpty(finishType))) {//非正常结束
				jpql.append(" and onlineClass.finishType != :finishType");
				params.put("finishType", FinishType.AS_SCHEDULED);
			} else{
				jpql.append(" and onlineClass.finishType = :finishType");
				params.put("finishType", FinishType.valueOf(finishType));
			}
		}
		//teacherName
		if (StringUtils.isNotBlank(teacherName)) {
			jpql.append(" and (onlineClass.teacher.name like :teacherName or onlineClass.teacher.realName like :teacherName)");
			params.put("teacherName", "%" + teacherName + "%");
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and (stu.name like :searchTextLike or stu.englishName like :searchTextLike or stu.username like :searchTextLike")
			.append(" or exists (select 1 from Parent parent where parent.family = stu.family and (parent.mobile = :searchText or parent.name like :searchTextLike))")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		jpql.append(" )");
		jpql.append(" left join stu.chineseLeadTeacher clt");
		jpql.append(" order by onlineClass.scheduledDateTime asc");
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		
		if (start != null) {
			query.setFirstResult(start);
		}
		if (length != null) {
			query.setMaxResults(length);
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		List<OnlineClassVo> onlineClassList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] row : resultList) {
				Long id = (Long)row[0];
				Date scheduledDateTime = (Date)row[1];
				Lesson lesson = (Lesson)row[2];
				status = (Status)row[3];
				FinishType finishType1 = (FinishType)row[4];
				String classroom = (String)row[5];
				boolean canUndoFinish = (boolean)row[6];
				boolean attatchDocumentSucess = (boolean)row[7];
				String dbyDocument = (String)row[8];
				Long teacherId = (Long)row[9];
				teacherName = (String)row[10];
				String teacherRealName = (String)row[11];
				Long stuId = (Long)row[12];
				String stuName = (String)row[13];
				String stuEnglishName = (String)row[14];
				cltId = (Long)row[15];
				String cltName = (String)row[16];
				
				String courseName = null;
				LessonVo lessonVo = null;
				String lessonSerialNumber = null;
				if (lesson != null) {
					lessonSerialNumber = lesson.getSerialNumber();
					lessonVo = new LessonVo();
					LearningCycleVo learningCycleVo = null;
					if (lesson.getLearningCycle() != null) {
						learningCycleVo = new LearningCycleVo();
						UnitVo unitVo = null;
						if (lesson.getLearningCycle().getUnit() != null) {
							unitVo = new UnitVo();
							CourseVo courseVo = null;
							if (lesson.getLearningCycle().getUnit().getCourse() != null) {
								courseVo = new CourseVo();
								Course course = lesson.getLearningCycle().getUnit().getCourse();
								courseVo.setId(course.getId());
								courseVo.setMode(course.getMode());
								courseVo.setName(course.getName());
								courseVo.setSequential(course.isSequential());
								courseVo.setType(course.getType());
								courseName = course.getName();
							}
							unitVo.setId(lesson.getLearningCycle().getUnit().getId());
							unitVo.setCourse(courseVo);
						}
						
						learningCycleVo.setId(learningCycleVo.getId());
						learningCycleVo.setUnit(unitVo);
					}
					lessonVo.setId(lesson.getId());
					lessonVo.setDbyDocument(lesson.getDbyDocument());
					lessonVo.setLearningCycle(learningCycleVo);
					
				}
				TeacherVo teacherVo = new TeacherVo(); 
				teacherVo.setId(teacherId);
				teacherVo.setName(teacherName);
				teacherVo.setRealName(teacherRealName);
				
				StudentVo studentVo = new StudentVo();
				studentVo.setId(stuId);
				studentVo.setName(stuName);
				studentVo.setEnglishName(stuEnglishName);
				
				OnlineClassVo onlineClassVo = new OnlineClassVo();
				onlineClassVo.setId(id);
				onlineClassVo.setScheduledDateTime(scheduledDateTime != null ? scheduledDateTime.getTime() : null);
				onlineClassVo.setCourseName(courseName);
				onlineClassVo.setLessonSerialNumber(lessonSerialNumber);
				onlineClassVo.setStatus(status);
				onlineClassVo.setTeacher(teacherVo);
				onlineClassVo.setFinishType(finishType1);
				onlineClassVo.setStudents(Lists.newArrayList(studentVo));
				onlineClassVo.setLesson(lessonVo);
				onlineClassVo.setClassroom(classroom);
				onlineClassVo.setCanUndoFinish(canUndoFinish);
				onlineClassVo.setAttatchDocumentSucess(attatchDocumentSucess);
				onlineClassVo.setDbyDocument(dbyDocument);
				onlineClassVo.setCltId(cltId != null && cltId != 0 ? cltId : null);
				onlineClassVo.setCltName(cltName);
				
				onlineClassList.add(onlineClassVo);
			}
		}
		return onlineClassList;
	}
	
	public long countOnlineClassForCLT(Date scheduledTimeFrom, Date scheduledTimeTo,
			List<Long> courseIds, Status status, String finishType, Long cltId,
			String teacherName, String searchText) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();

		jpql.append("select count(1) ");
		
		jpql.append(" from OnlineClass onlineClass join onlineClass.students stu on ");
		jpql.append(" (1=1 ");
		
		// scheduledTimeFrom/scheduledTimeTo
		if (scheduledTimeFrom != null) {
			jpql.append(" and onlineClass.scheduledDateTime >= :scheduledTimeFrom");
			params.put("scheduledTimeFrom", scheduledTimeFrom);
		}
		if (scheduledTimeTo != null) {
			scheduledTimeTo = DateTimeUtils.getNextDay(scheduledTimeTo);
			jpql.append(" and onlineClass.scheduledDateTime < :scheduledTimeTo");
			params.put("scheduledTimeTo", scheduledTimeTo);
		}
		
		//cltId
		if (cltId != null) {
			jpql.append(" and stu.chineseLeadTeacher.id = :cltId");
			params.put("cltId", cltId);
		}
		
		//courseIds
		if (CollectionUtils.isNotEmpty(courseIds)) {
			jpql.append(" and onlineClass.lesson.learningCycle.unit.course.id in :courseIds");
			params.put("courseIds", courseIds);
		}
		
		//status
		if (status != null) {
			jpql.append(" and onlineClass.status = :status");
			params.put("status", status);
		}
		
		//finishType
		if (StringUtils.isNotBlank(finishType)) {
			if ("WITH_PROBLEM".equals(StringUtils.trimToEmpty(finishType))) {//非正常结束
				jpql.append(" and onlineClass.finishType != :finishType");
				params.put("finishType", FinishType.AS_SCHEDULED);
			} else{
				jpql.append(" and onlineClass.finishType = :finishType");
				params.put("finishType", FinishType.valueOf(finishType));
			}
		}
		//teacherName
		if (StringUtils.isNotBlank(teacherName)) {
			jpql.append(" and (onlineClass.teacher.name like :teacherName or onlineClass.teacher.realName like :teacherName)");
			params.put("teacherName", "%" + teacherName + "%");
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and (stu.name like :searchTextLike or stu.englishName like :searchTextLike or stu.username like :searchTextLike")
			.append(" or exists (select 1 from Parent parent where parent.family = stu.family and (parent.mobile = :searchText or parent.name like :searchTextLike))")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		jpql.append(" )");
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		
		return (Long) query.getSingleResult();
	}
	
	public List<Date> findOnlineClassTimeSlots(long courseId, Date startDate, Date endDate) {
		String sql = "SELECT distinct(o.scheduledDateTime) FROM OnlineClass o JOIN o.teacher.certificatedCourses c WHERE o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND o.status = :status AND c.id = :courseId ORDER BY o.scheduledDateTime";
		TypedQuery<Date> typedQuery = entityManager
				.createQuery(sql, Date.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
		return typedQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Date> findOnlineClassTrialTimeSlotsWithParallelLimit(long courseId, Date startDate, Date endDate, int parallelCount) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select distinct(filtered_time_slot.date_time) from ");
		buffer.append(" (");//filtered_time_slot
		
			buffer.append(" select time_slot.date_time as date_time,case when booked_time.num is null then 0 else booked_time.num end as num,tt.trial_amount from");
			buffer.append(" (");//time_slot
			
				buffer.append(" select distinct(oc.scheduled_date_time) as date_time");
				buffer.append(" from online_class oc,teacher t,teacher_certificated_course tcc");  
				buffer.append(" where oc.teacher_id = t.id and tcc.teacher_id = t.id");
				buffer.append(" and tcc.course_id = ").append("?1");
				buffer.append(" and oc.`status` = ").append("?2");
				buffer.append(" and oc.scheduled_date_time between ").append("?3").append(" and ").append("?4").append("");
				
			buffer.append(" ) as time_slot");
			buffer.append(" left join (");//booked_time
			
				buffer.append(" select count(oc.scheduled_date_time) as num ,oc.scheduled_date_time as date_time");
				buffer.append(" from online_class oc,teacher t,lesson l,learning_cycle lc,unit u,course c");
				buffer.append(" where oc.teacher_id=t.id and oc.lesson_id = l.id and l.learning_cycle_id = lc.id and lc.unit_id = u.id and u.course_id = c.id");
				buffer.append(" and oc.`status`='BOOKED'");
				buffer.append(" and oc.scheduled_date_time between ").append("?5").append(" and ").append("?6").append("");
				buffer.append(" and (");
					buffer.append(" c.`type` in('TRIAL','ASSESSMENT2')")
						  .append(" or ").append("(c.`type`=").append("?7").append(" and l.serial_number=").append("?8").append(")");
				buffer.append(" )");
				buffer.append(" group by oc.scheduled_date_time");
			buffer.append(" ) as booked_time on time_slot.date_time = booked_time.date_time");
			buffer.append(" left join  trial_threshold tt on tt.time_point = time_slot.date_time");
			
		buffer.append(" ) as filtered_time_slot");
		buffer.append(" where filtered_time_slot.num < filtered_time_slot.trial_amount");
		buffer.append(" or (filtered_time_slot.trial_amount is null and filtered_time_slot.num < ").append("?9").append(" )");
		buffer.append(" order by filtered_time_slot.date_time ;");
		
		String sql = buffer.toString();
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, courseId);
		query.setParameter(2, OnlineClass.Status.AVAILABLE.name());
		query.setParameter(3, startDate);
		query.setParameter(4, endDate);
		query.setParameter(5, startDate);
		query.setParameter(6, endDate);
		query.setParameter(7, Type.ELECTIVE_LT.name());
		query.setParameter(8, LT1_U1_LC1_L1);
		query.setParameter(9, parallelCount);
		List<Date> result =  query.getResultList();
		return result;
	}
}
