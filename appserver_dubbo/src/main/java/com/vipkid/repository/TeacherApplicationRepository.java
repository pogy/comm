package com.vipkid.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass_;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherApplication.Result;
import com.vipkid.model.TeacherApplication.Status;
import com.vipkid.model.TeacherApplication_;
import com.vipkid.model.User_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.util.DateTimeUtils;

@Repository
public class TeacherApplicationRepository extends BaseRepository<TeacherApplication> {

	public TeacherApplicationRepository() {
		super(TeacherApplication.class);
	}
	
	public List<TeacherApplication> findByTeacherId(long teacherId){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId ORDER BY ta.applyDateTime DESC";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("teacherId", teacherId);
		
		return typedQuery.getResultList();
	}
	
	public List<TeacherApplication> findByTeacherIdAndStatusAndResult(long teacherId, Status status, Result result){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId AND ta.result = :result AND ta.status = :status";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("result", result);
		
		return typedQuery.getResultList();
		
	}
	
	public TeacherApplication findCurrentByTeacherIdAndStatus(long teacherId, Status status){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId AND ta.current = :current AND ta.status = :status";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("current", true);
		
		List<TeacherApplication> teacherApplications = typedQuery.getResultList();
		if(teacherApplications.isEmpty()) {
			return null;
		}else {
			return teacherApplications.get(0);
		}
	}
	
	public TeacherApplication findCurrentByTeacherId(long teacherId){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId AND ta.current = :current";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("current", true);
		
		List<TeacherApplication> teacherApplications = typedQuery.getResultList();
		if(teacherApplications.isEmpty()) {
			return null;
		}else {
			return teacherApplications.get(0);
		}
	}
	
	public List<TeacherApplication> findInterviewTeacherApplicationByStartDateAndEndDate(Date startDate, Date endDate){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.onlineClass.scheduledDateTime > :startDate AND ta.onlineClass.scheduledDateTime < :endDate AND ta.status = :status";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("status", Status.INTERVIEW);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList();
		
	}
	
	public List<TeacherApplication> findInterviewTeacherApplicationByStartDateAndEndDateAndDiffDayBetweenScheduleTimeBookTime(Date startDate, Date endDate, long diffDayStart, long diffDayEnd){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TeacherApplication> criteriaQuery = criteriaBuilder.createQuery(TeacherApplication.class).distinct(true);
		Root<TeacherApplication> teacherApplication = criteriaQuery.from(TeacherApplication.class);		
		
		Join<TeacherApplication, OnlineClass> onlineClass = teacherApplication.join(TeacherApplication_.onlineClass, JoinType.LEFT);
		
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), startDate));
		andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), endDate));			
		
		Expression<Long> dateDiff = criteriaBuilder.function(
		            "DATEDIFF",
		            Long.class,
		            onlineClass.<Date>get( "scheduledDateTime" ),
		            onlineClass.<Date>get( "bookDateTime" ));
		
		andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(dateDiff, diffDayStart));
		andPredicates.add(criteriaBuilder.lessThan(dateDiff, diffDayEnd));
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));		
		
		criteriaQuery.where(andPredicate);
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}
	

	/**
	 * contract 阶段 - 116小时还没有处理，通知
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Teacher> findApplicantWithoutContractSignInfoBy116Hours(Date startDate, Date endDate) {
		String sql = "select t from Teacher t where t.lifeCycle = :lifeCycle and t.id in ( "
				+ "select distinct ta.teacher.id from TeacherApplication ta where ta.current = 1 and ta.status != :status and ta.auditDateTime > :startDate and ta.auditDateTime < :endDate)";
		
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("lifeCycle", Teacher.LifeCycle.SIGN_CONTRACT);
		typedQuery.setParameter("status", TeacherApplication.Status.SIGN_CONTRACT);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList(); 
	}
	
	/**
	 * 查找当前sign-contract过期未处理的teacher applicant
	 * @return
	 */
	public List<Teacher> findApplicantContractSignInfoTerminate() {
		String sql = "select t from Teacher t where t.lifeCycle = :lifeCycle and t.id in ("
				+ "select distinct ta.teacher.id from TeacherApplication ta where ta.current = 1 and ta.status != :status and ta.auditDateTime > :startDate and ta.auditDateTime < :endDate)";
		
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("lifeCycle", Teacher.LifeCycle.SIGN_CONTRACT);
		typedQuery.setParameter("status", com.vipkid.model.TeacherApplication.Status.SIGN_CONTRACT);
		
		//before 7 days. 每半小时 一次
		Date startDate = DateTimeUtils.getDateByOffset(-7, -15);
		Date endDate = DateTimeUtils.getDateByOffset(-7, 15);
		
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		
		return typedQuery.getResultList(); 
	}
	
	public List<TeacherApplication> list(String search, DateTimeParam applyDateTimeFrom, DateTimeParam applyDateTimeTo, DateTimeParam auditDateTimeFrom, DateTimeParam auditDateTimeTo, Status status, Result result, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TeacherApplication> criteriaQuery = criteriaBuilder.createQuery(TeacherApplication.class);
		Root<TeacherApplication> application = criteriaQuery.from(TeacherApplication.class);

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (search != null){
			Join<TeacherApplication, Teacher> applicant = application.join(TeacherApplication_.teacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.like(applicant.get(User_.name), "%" + search + "%"));
		}
		
		if (applyDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(application.get(TeacherApplication_.applyDateTime), applyDateTimeFrom.getValue()));
		}
		
		if (applyDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(application.get(TeacherApplication_.applyDateTime), DateTimeUtils.getNextDay(applyDateTimeTo.getValue())));
		}
		
		if (auditDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(application.get(TeacherApplication_.auditDateTime), auditDateTimeFrom.getValue()));
		}
		
		if (auditDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(application.get(TeacherApplication_.auditDateTime), DateTimeUtils.getNextDay(auditDateTimeTo.getValue())));
		}
		
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(application.get(TeacherApplication_.status), status));
		}
		if (result != null) {
			andPredicates.add(criteriaBuilder.equal(application.get(TeacherApplication_.result), result));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!andPredicates.isEmpty()) {
			criteriaQuery.where(andPredicate);
		}

		criteriaQuery.orderBy(criteriaBuilder.desc(application.get(TeacherApplication_.applyDateTime)));
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(criteriaQuery);
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

	public long count(String search, DateTimeParam applyDateTimeFrom, DateTimeParam applyDateTimeTo, DateTimeParam auditDateTimeFrom, DateTimeParam auditDateTimeTo, Status status, Result result) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<TeacherApplication> application = criteriaQuery.from(TeacherApplication.class);
		criteriaQuery.select(criteriaBuilder.count(application));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (search != null){
			Join<TeacherApplication, Teacher> applicant = application.join(TeacherApplication_.teacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.like(applicant.get(User_.name), "%" + search + "%"));
		}
		
		if (applyDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(application.get(TeacherApplication_.applyDateTime), applyDateTimeFrom.getValue()));
		}
		
		if (applyDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(application.get(TeacherApplication_.applyDateTime), DateTimeUtils.getNextDay(applyDateTimeTo.getValue())));
		}
		
		if (auditDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(application.get(TeacherApplication_.auditDateTime), auditDateTimeFrom.getValue()));
		}
		
		if (auditDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(application.get(TeacherApplication_.auditDateTime), DateTimeUtils.getNextDay(auditDateTimeTo.getValue())));
		}
		
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(application.get(TeacherApplication_.status), status));
		}
		if (result != null) {
			andPredicates.add(criteriaBuilder.equal(application.get(TeacherApplication_.result), result));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!andPredicates.isEmpty()) {
			criteriaQuery.where(andPredicate);
		}
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public List<TeacherApplication> findCurrentTeacherApplicationByOnlineClassStartDateAndEndDate(Date startDate, Date endDate) {
		String sql = "SELECT ta FROM TeacherApplication ta JOIN ta.onlineClass tao WHERE ta.current =:current AND tao.studentEnterClassroomDateTime != :time AND tao.scheduledDateTime BETWEEN :startDateTime AND :endDateTime";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);		
		typedQuery.setParameter("current", true);
		typedQuery.setParameter("time", null);
		typedQuery.setParameter("startDateTime", startDate);
		typedQuery.setParameter("endDateTime", endDate);
		
		List<TeacherApplication> teacherApplications = typedQuery.getResultList();
		
		return teacherApplications;
//		
//		if(teacherApplications.isEmpty()) {
//			return null;
//		}else {
//			return teacherApplications.get(0);
//		}
	}
	
	public TeacherApplication findPreStepPassedTeacherApplicationByTeacherId(Status status, long teacherId) {
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.status =:status AND ta.result =:result AND ta.teacher.id = :teacherId";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		Status preStepStatus = null;
		switch(status){
		case PRACTICUM:
			preStepStatus = Status.TRAINING;
			break;
		case TRAINING:
			preStepStatus = Status.SIGN_CONTRACT;
			break;
		case SIGN_CONTRACT:
			preStepStatus = Status.INTERVIEW;
			break;
		case INTERVIEW:
			preStepStatus = Status.BASIC_INFO; // 2015-08-25 SIGNUP --> BASIC_INFO
			break;
		default:
			return null;			
		}
		typedQuery.setParameter("status", preStepStatus);
		typedQuery.setParameter("result", Result.PASS);
		typedQuery.setParameter("teacherId", teacherId);
		
		List<TeacherApplication> teacherApplications = typedQuery.getResultList();
		
		if(teacherApplications.isEmpty()) {
			return null;
		}else {
			return teacherApplications.get(0);
		}
	}
	
	public List<TeacherApplication> findReapplyByTeacherIdAndStatus(long teacherId, Status status){
		String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId AND ta.status = :status AND ta.result = :result ORDER BY ta.applyDateTime DESC";
		TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("result", Result.REAPPLY);
		
		return typedQuery.getResultList();
	}
    public List<TeacherApplication> findWhetherHasResultPracticum2ByTeacherId(long teacherId){
        String sql = "SELECT ta FROM TeacherApplication ta WHERE ta.teacher.id = :teacherId AND ta.status = :status AND ta.result = :result ORDER BY ta.applyDateTime DESC";
        TypedQuery<TeacherApplication> typedQuery = entityManager.createQuery(sql, TeacherApplication.class);
        typedQuery.setParameter("teacherId", teacherId);
        typedQuery.setParameter("status", Status.PRACTICUM);
        typedQuery.setParameter("result", Result.PRACTICUM2);

        return typedQuery.getResultList();
    }


}
