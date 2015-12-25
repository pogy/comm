package com.vipkid.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vipkid.model.Country;
import com.vipkid.model.Course;
import com.vipkid.model.Course_;
import com.vipkid.model.Gender;
import com.vipkid.model.ItTest;
import com.vipkid.model.ItTest.FinalResult;
import com.vipkid.model.ItTest_;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass_;
import com.vipkid.model.Partner;
import com.vipkid.model.Staff;
import com.vipkid.model.Teacher;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.Teacher.Hide;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.Type;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherApplication.Result;
import com.vipkid.model.TeacherApplication_;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.TeacherLifeCycleLog_;
import com.vipkid.model.Teacher_;
import com.vipkid.model.User;
import com.vipkid.model.User.AccountType;
import com.vipkid.model.User.Status;
import com.vipkid.model.User_;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.TeacherNameView;
import com.vipkid.service.pojo.TeacherView;
import com.vipkid.service.pojo.parent.OnlineClassesView;
import com.vipkid.service.pojo.parent.TeView;
import com.vipkid.service.pojo.parent.TeacherDetailView;
import com.vipkid.service.pojo.parent.TeachersView;
import com.vipkid.util.DateTimeUtils;

@Repository
public class TeacherRepository extends BaseRepository<Teacher> {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(TeacherRepository.class.getSimpleName());
	
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private OnlineClassRepository OnlineClassRepository;

	public TeacherRepository() {
		super(Teacher.class);
	}
	
	public List<TeacherNameView> findBySearchCondition(String search, long courseId, int length){
		String sql = "SELECT DISTINCT NEW com.vipkid.service.pojo.TeacherNameView(t.id, t.realName) FROM Teacher t JOIN t.certificatedCourses tcs WHERE tcs.id = :courseId AND t.status =:status AND t.lifeCycle = :lifeCycle AND (t.realName like :search OR t.name like :search)";
		TypedQuery<TeacherNameView> typedQuery = entityManager.createQuery(sql, TeacherNameView.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("status", Status.NORMAL);
		typedQuery.setParameter("lifeCycle", LifeCycle.REGULAR);
		typedQuery.setParameter("search", "%" + search + "%");
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();
	}
	
	public List<Teacher> list(List<String> lifeCycles, String search, Gender gender, Status status, Country country, DateTimeParam contractStartDate, DateTimeParam contractEndDate, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Long certificatedCourseId, Long partnerId,String teacherType, FinalResult finalResult, Integer start, Integer length,String teacherTags) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		
    	// compose OR predicate
	    List<Predicate> orLifeCyclePredicates = new LinkedList<Predicate>();
		if (!lifeCycles.isEmpty()) {
			for (String lifeCycle : lifeCycles){
				orLifeCyclePredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
			}
		}
		Predicate orLifeCyclePredicate = criteriaBuilder.or(orLifeCyclePredicates.toArray(new Predicate[orLifeCyclePredicates.size()]));
		
		// compose OR predicate
	    List<Predicate> orPredicates = new LinkedList<Predicate>();
		
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(
					teacher.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (gender != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.gender), gender));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status), status));
		}
		if (country != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.country), country));
		}
		if (contractStartDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(Teacher_.contractStartDate), contractStartDate.getValue()));
		}
		if (contractEndDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getNextDay(contractEndDate.getValue())));
		}
		
		if (scheduledDateTimeFrom != null) {
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.debug("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.debug("actual scheduleDateTimeTo=" + actualToDate);
			
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (certificatedCourseId != null) {
			Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses);
			andPredicates.add(criteriaBuilder.equal(courses.get(Course_.id), certificatedCourseId));
		}
//		if (partner != null) {
//			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.recruitmentChannel), partner));
//		}
		if (partnerId !=null) {
			Join<Teacher, Partner> partnerJoin = teacher.join(Teacher_.partner);
			andPredicates.add(criteriaBuilder.equal(partnerJoin.get(User_.id), partnerId));
		}
		
		if (teacherType != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.type), Type.valueOf(teacherType)));
		}
		if(StringUtils.isNotBlank(teacherTags)){
			andPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.teacherTags), "%"+teacherTags+"%"));
		}
		
		if (finalResult !=null) {
			Join<Teacher, ItTest> itTests = teacher.join(Teacher_.itTests);
			switch(finalResult) {
			case NORMAL:
			case ABNORMAL:
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), true));				
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.current), true));
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.finalResult), finalResult));
				break;
			case NONE:List<Predicate> hasTestedIsFalseOrNullPredicates = new LinkedList<Predicate>();
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), false));
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.isNull(teacher.get(Teacher_.hasTested)));
				Predicate hasTestedIsFalseOrNullPredicate = criteriaBuilder.or(hasTestedIsFalseOrNullPredicates.toArray(new Predicate[hasTestedIsFalseOrNullPredicates.size()]));
				andPredicates.add(hasTestedIsFalseOrNullPredicate);			
				break;
			}
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		if(orLifeCyclePredicates.size() > 0) {
			finalPredicates.add(orLifeCyclePredicate);
		}				
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(teacher.get(User_.lastEditDateTime)));
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
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
	
	/**
	 * 2015-08-08 practicum阶段教师查询
	 * @param lifeCycles
	 * @param search
	 * @param gender
	 * @param status
	 * @param country
	 * @param contractStartDate
	 * @param contractEndDate
	 * @param scheduledDateTimeFrom
	 * @param scheduledDateTimeTo
	 * @param certificatedCourseId
	 * @param partnerId
	 * @param teacherType
	 * @param finalResult
	 * @param start
	 * @param length
	 * @param teacherTags
	 * @return
	 */
	public List<Teacher> list(List<String> lifeCycles, String search, Gender gender, Status status, 
			Country country, DateTimeParam contractStartDate, DateTimeParam contractEndDate, 
			DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, 
			Long certificatedCourseId, Long partnerId,String teacherType, FinalResult finalResult, 
			Integer start, Integer length,String teacherTags,String strAccountType,
			// 2015-08-13 添加其他条件 TM-improve quit-time 时间段
			DateTimeParam quitStartDate,
			DateTimeParam quitEndDate,
			// 2015-08-13 添加其他条件 TM-improve apply-time 时间段 -- (teacher.registerDateTime)
			DateTimeParam applyFromDate,
			DateTimeParam applyEndDate,
			//2015-08-14 basic_info需要筛选这个
			String applyResult,
			//2015-08-18 interview需要这个
			Long interviewerId,
			Long practicumTeacherId,
			String[] managers) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		
		//-----
//		// 2015-08-22 每次仅一种类型
//		// compose OR predicate
//	    List<Predicate> orLifeCyclePredicates = new LinkedList<Predicate>();
//		if (!lifeCycles.isEmpty()) {
//			for (String lifeCycle : lifeCycles){
//				orLifeCyclePredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
//			}
//		}
//		Predicate orLifeCyclePredicate = criteriaBuilder.or(orLifeCyclePredicates.toArray(new Predicate[orLifeCyclePredicates.size()]));
		

		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// life cycle compose AND predicate
		Teacher.LifeCycle lifeCycleValue = Teacher.LifeCycle.REGULAR;
		try { 
			lifeCycleValue = Teacher.LifeCycle.valueOf(lifeCycles.get(0));
		} catch (Exception e ) {
			logger.error("error lifeCycle value");
		}
		andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle),lifeCycleValue ));
		
		// seach name : compose OR predicate
	    List<Predicate> orSearchPredicates = new LinkedList<Predicate>();
		
		if (search != null) {
			orSearchPredicates.add(criteriaBuilder.like(teacher.get(User_.name), "%" + search + "%"));
			orSearchPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orSearchPredicates.toArray(new Predicate[orSearchPredicates.size()]));
		
		// compose OR predicate
	    List<Predicate> orManagerPredicates = new LinkedList<Predicate>();
	    Join<Teacher, Staff> staffJoin = teacher.join(Teacher_.manager, JoinType.LEFT);
		if (null != managers && managers.length > 0) {
			for (String manager : managers){
				orManagerPredicates.add(criteriaBuilder.equal(staffJoin.get(User_.id),manager));
			}
		}
		Predicate orManagerPredicate = criteriaBuilder.or(orManagerPredicates.toArray(new Predicate[orManagerPredicates.size()]));
		
		// gender
		if (gender != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.gender), gender));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status), status));
		}
		if (country != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.country), country));
		}
		if (contractStartDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(Teacher_.contractStartDate), contractStartDate.getValue()));
		}
		if (contractEndDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getNextDay(contractEndDate.getValue())));
		}
		
		// 2015-08-08 添加accountType
		if (!StringUtils.isEmpty(strAccountType)) {
			try {
				AccountType accountTypeValue = AccountType.valueOf(strAccountType);
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.accountType), accountTypeValue));
			
			} catch (Exception e) {
				logger.error("Invalid account type:"+strAccountType);
			}
		}
		
		Join<Teacher, TeacherApplication> teacherAlc = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
		if(applyResult != null){
			if(applyResult.equals("Fail")){
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), Result.FAIL));
			}else if(applyResult.equals("Pass")){
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), Result.PASS));
			}else{
				List<Predicate> failAndPrdts = new LinkedList<Predicate>();
				failAndPrdts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				failAndPrdts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				Predicate currentPredict = criteriaBuilder.and(failAndPrdts.toArray(new Predicate[failAndPrdts.size()]));
				
				List<Predicate> notFailPassPredicts = new LinkedList<Predicate>();
				notFailPassPredicts.add(criteriaBuilder.isNull(teacherAlc.get(TeacherApplication_.result)));
				notFailPassPredicts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result),Result.REAPPLY));
				notFailPassPredicts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result),Result.PRACTICUM2));
				Predicate notFailPassPredict = criteriaBuilder.or(notFailPassPredicts.toArray(new Predicate[notFailPassPredicts.size()]));
				
				List<Predicate> UnPassOrFailPrdts = new LinkedList<Predicate>();
				UnPassOrFailPrdts.add(currentPredict);
				UnPassOrFailPrdts.add(notFailPassPredict);
				Predicate UnPassOrFailPrdt = criteriaBuilder.and(UnPassOrFailPrdts.toArray(new Predicate[UnPassOrFailPrdts.size()]));
				
				TeacherApplication.Status prevStatus = TeacherApplication.Status.prevStatus(TeacherApplication.Status.valueOf(lifeCycles.get(0)));
				
				List<Predicate> andCurrentApplicationResultPredicates2 = new LinkedList<Predicate>();
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), prevStatus));
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), TeacherApplication.Result.PASS));
				Predicate andCurrentApplicationResultPredicate2 = criteriaBuilder.and(andCurrentApplicationResultPredicates2.toArray(new Predicate[andCurrentApplicationResultPredicates2.size()]));
				
				List<Predicate> applicationPredicts = new LinkedList<Predicate>();
				applicationPredicts.add(UnPassOrFailPrdt);
				applicationPredicts.add(andCurrentApplicationResultPredicate2);
				
				Predicate applicationPredict = criteriaBuilder.or(applicationPredicts.toArray(new Predicate[applicationPredicts.size()]));
				andPredicates.add(applicationPredict);
			}
		}
		
		
		
		//2015-08-18
		if(null != interviewerId){
			Join<TeacherApplication, OnlineClass> onlineClass = teacherAlc.join(TeacherApplication_.onlineClass);
			Join<OnlineClass,Teacher> tcher = onlineClass.join(OnlineClass_.teacher);
			andPredicates.add(criteriaBuilder.equal(tcher.get(User_.id),interviewerId));
		}
		
		if(null != practicumTeacherId){
			Join<TeacherApplication, OnlineClass> onlineClass = teacherAlc.join(TeacherApplication_.onlineClass);
			Join<OnlineClass,Teacher> tcher = onlineClass.join(OnlineClass_.teacher);
			andPredicates.add(criteriaBuilder.equal(tcher.get(User_.id),practicumTeacherId));
		}
				
			
		if (scheduledDateTimeFrom != null) {
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.debug("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.debug("actual scheduleDateTimeTo=" + actualToDate);
			
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (certificatedCourseId != null) {
			Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses);
			andPredicates.add(criteriaBuilder.equal(courses.get(Course_.id), certificatedCourseId));
		}
//		if (partner != null) {
//			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.recruitmentChannel), partner));
//		}
		if (partnerId !=null) {
			Join<Teacher, Partner> partnerJoin = teacher.join(Teacher_.partner);
			andPredicates.add(criteriaBuilder.equal(partnerJoin.get(User_.id), partnerId));
		}
		
		if (teacherType != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.type), Type.valueOf(teacherType)));
		}
		if(StringUtils.isNotBlank(teacherTags)){
			andPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.teacherTags), "%"+teacherTags+"%"));
		}
		
		
		if (finalResult !=null) {
			Join<Teacher, ItTest> itTests = teacher.join(Teacher_.itTests);
			switch(finalResult) {
			case NORMAL:
			case ABNORMAL:
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), true));				
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.current), true));
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.finalResult), finalResult));
				break;
			case NONE:List<Predicate> hasTestedIsFalseOrNullPredicates = new LinkedList<Predicate>();
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), false));
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.isNull(teacher.get(Teacher_.hasTested)));
				Predicate hasTestedIsFalseOrNullPredicate = criteriaBuilder.or(hasTestedIsFalseOrNullPredicates.toArray(new Predicate[hasTestedIsFalseOrNullPredicates.size()]));
				andPredicates.add(hasTestedIsFalseOrNullPredicate);			
				break;
			}
		}
		
		// 2015-08-13 添加apply time
		if (null != quitStartDate || null != quitEndDate) {
			List<Predicate> andQuitTimePredicates = new LinkedList<Predicate>();
			if (null != quitStartDate) {
				Date quitStartDateV = quitStartDate.getValue();
				andQuitTimePredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(User_.registerDateTime), quitStartDateV));
			}
			
			if (null != quitEndDate) {
				Date quitToDate = DateTimeUtils.getNextDay(quitEndDate.getValue());
				andQuitTimePredicates.add(criteriaBuilder.lessThan(teacher.get(User_.registerDateTime), quitToDate));
			}
			
			//
			Predicate applyPredict = criteriaBuilder.and(andQuitTimePredicates.toArray(new Predicate[andQuitTimePredicates.size()]));
			andPredicates.add(applyPredict);
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orSearchPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		if(orManagerPredicates.size() > 0) {
			finalPredicates.add(orManagerPredicate);
		}
				
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(teacher.get(User_.lastEditDateTime)));
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
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
	public long totalCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		criteriaQuery.select(criteriaBuilder.count(teacher));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public long count(List<String> lifeCycles,String search, Gender gender, Status status, Country country, DateTimeParam contractStartDate, DateTimeParam contractEndDate, DateTimeParam scheduledDateTimeFrom, DateTimeParam scheduledDateTimeTo, Long certificatedCourseId, Long partnerId,String teacherType, FinalResult finalResult,String teacherTags,String strAccountType,String applyResult,String interviewerId,String practicumTeacherId,String[] managers) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(teacher));

//		// 2015-08-22 每次仅一种类型
//		// compose OR predicate
//	    List<Predicate> orLifeCyclePredicates = new LinkedList<Predicate>();
//		if (!lifeCycles.isEmpty()) {
//			for (String lifeCycle : lifeCycles){
//				orLifeCyclePredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
//			}
//		}
//		Predicate orLifeCyclePredicate = criteriaBuilder.or(orLifeCyclePredicates.toArray(new Predicate[orLifeCyclePredicates.size()]));
		

		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// life cycle compose AND predicate
		Teacher.LifeCycle lifeCycleValue = Teacher.LifeCycle.REGULAR;
		try { 
			lifeCycleValue = Teacher.LifeCycle.valueOf(lifeCycles.get(0));
		} catch (Exception e ) {
			logger.error("error lifeCycle value");
		}
		andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle),lifeCycleValue ));
		
		// seach name : compose OR predicate
	    List<Predicate> orSearchPredicates = new LinkedList<Predicate>();
		
		if (search != null) {
			orSearchPredicates.add(criteriaBuilder.like(teacher.get(User_.name), "%" + search + "%"));
			orSearchPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orSearchPredicates.toArray(new Predicate[orSearchPredicates.size()]));
		
		// compose OR predicate
	    List<Predicate> orManagerPredicates = new LinkedList<Predicate>();
	    Join<Teacher, Staff> staffJoin = teacher.join(Teacher_.manager, JoinType.LEFT);
		if (null != managers && managers.length > 0) {
			for (String manager : managers){
				orManagerPredicates.add(criteriaBuilder.equal(staffJoin.get(User_.id), manager));
			}
		}
		Predicate orManagerPredicate = criteriaBuilder.or(orManagerPredicates.toArray(new Predicate[orManagerPredicates.size()]));
		
		// gender
		if (gender != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.gender), gender));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status), status));
		}
		if (country != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.country), country));
		}
		if (contractStartDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(Teacher_.contractStartDate), contractStartDate.getValue()));
		}
		if (contractEndDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getNextDay(contractEndDate.getValue())));
		}
		
		// 2015-08-08 添加accountType
		if (!StringUtils.isEmpty(strAccountType)) {
			try {
				AccountType accountTypeValue = AccountType.valueOf(strAccountType);
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.accountType), accountTypeValue));
			
			} catch (Exception e) {
				logger.error("Invalid account type:"+strAccountType);
			}
		}
		
		Join<Teacher, TeacherApplication> teacherAlc = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
		
		if(null != applyResult){
			if(applyResult.equals("Fail")){
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), Result.FAIL));
			}else if(applyResult.equals("Pass")){
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				andPredicates.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), Result.PASS));
			}else{
				List<Predicate> failAndPrdts = new LinkedList<Predicate>();
				failAndPrdts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				failAndPrdts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), TeacherApplication.Status.valueOf(lifeCycles.get(0))));
				Predicate currentPredict = criteriaBuilder.and(failAndPrdts.toArray(new Predicate[failAndPrdts.size()]));
				
				List<Predicate> notFailPassPredicts = new LinkedList<Predicate>();
				notFailPassPredicts.add(criteriaBuilder.isNull(teacherAlc.get(TeacherApplication_.result)));
				notFailPassPredicts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result),Result.REAPPLY));
				notFailPassPredicts.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result),Result.PRACTICUM2));
				Predicate notFailPassPredict = criteriaBuilder.or(notFailPassPredicts.toArray(new Predicate[notFailPassPredicts.size()]));
				
				List<Predicate> UnPassOrFailPrdts = new LinkedList<Predicate>();
				UnPassOrFailPrdts.add(currentPredict);
				UnPassOrFailPrdts.add(notFailPassPredict);
				Predicate UnPassOrFailPrdt = criteriaBuilder.and(UnPassOrFailPrdts.toArray(new Predicate[UnPassOrFailPrdts.size()]));
				
				TeacherApplication.Status prevStatus = TeacherApplication.Status.prevStatus(TeacherApplication.Status.valueOf(lifeCycles.get(0)));
				
				List<Predicate> andCurrentApplicationResultPredicates2 = new LinkedList<Predicate>();
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.current), true));
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.status), prevStatus));
				andCurrentApplicationResultPredicates2.add(criteriaBuilder.equal(teacherAlc.get(TeacherApplication_.result), TeacherApplication.Result.PASS));
				Predicate andCurrentApplicationResultPredicate2 = criteriaBuilder.and(andCurrentApplicationResultPredicates2.toArray(new Predicate[andCurrentApplicationResultPredicates2.size()]));
				
				List<Predicate> applicationPredicts = new LinkedList<Predicate>();
				applicationPredicts.add(UnPassOrFailPrdt);
				applicationPredicts.add(andCurrentApplicationResultPredicate2);
				
				Predicate applicationPredict = criteriaBuilder.or(applicationPredicts.toArray(new Predicate[applicationPredicts.size()]));
				andPredicates.add(applicationPredict);
			}
		}
		
		
		//2015-08-18
		if(null != interviewerId){
			Join<TeacherApplication, OnlineClass> onlineClass = teacherAlc.join(TeacherApplication_.onlineClass);
			Join<OnlineClass,Teacher> tcher = onlineClass.join(OnlineClass_.teacher);
			andPredicates.add(criteriaBuilder.equal(tcher.get(User_.id),interviewerId));
		}
		
		if(null != practicumTeacherId){
			Join<TeacherApplication, OnlineClass> onlineClass = teacherAlc.join(TeacherApplication_.onlineClass);
			Join<OnlineClass,Teacher> tcher = onlineClass.join(OnlineClass_.teacher);
			andPredicates.add(criteriaBuilder.equal(tcher.get(User_.id),practicumTeacherId));
		}
		
		if (scheduledDateTimeFrom != null) {
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), scheduledDateTimeFrom.getValue()));
		}
		if (scheduledDateTimeTo != null) {
			// To support query within one day. For example select all things from Jul.15th, you can use 2014/7/15 - 2014/7/15
			logger.debug("scheduleDateTimeTo=" + scheduledDateTimeTo.getValue());
			Date actualToDate = DateTimeUtils.getNextDay(scheduledDateTimeTo.getValue());
			logger.debug("actual scheduleDateTimeTo=" + actualToDate);
			
			Join<Teacher, TeacherApplication> teacherApplications = teacher.join(Teacher_.teacherApplications, JoinType.LEFT);
			Join<TeacherApplication, OnlineClass> onlineClass = teacherApplications.join(TeacherApplication_.onlineClass);
			andPredicates.add(criteriaBuilder.isTrue(teacherApplications.get(TeacherApplication_.current)));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(onlineClass.get(OnlineClass_.scheduledDateTime), actualToDate));
		}
		
		if (certificatedCourseId != null) {
			Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses);
			andPredicates.add(criteriaBuilder.equal(courses.get(Course_.id), certificatedCourseId));
		}
//		if (partner != null) {
//			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.recruitmentChannel), partner));
//		}
		if (partnerId !=null) {
			Join<Teacher, Partner> partnerJoin = teacher.join(Teacher_.partner);
			andPredicates.add(criteriaBuilder.equal(partnerJoin.get(User_.id), partnerId));
		}

		if (teacherType != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.type), Type.valueOf(teacherType)));
		}
		if(StringUtils.isNotBlank(teacherTags)){
			andPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.teacherTags), "%"+teacherTags+"%"));
		}

		if (finalResult !=null) {
			Join<Teacher, ItTest> itTests = teacher.join(Teacher_.itTests);
			switch(finalResult) {
			case NORMAL:
			case ABNORMAL:
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), true));				
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.current), true));
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.finalResult), finalResult));
				break;
			case NONE:List<Predicate> hasTestedIsFalseOrNullPredicates = new LinkedList<Predicate>();
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.hasTested), false));
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.isNull(teacher.get(Teacher_.hasTested)));
				Predicate hasTestedIsFalseOrNullPredicate = criteriaBuilder.or(hasTestedIsFalseOrNullPredicates.toArray(new Predicate[hasTestedIsFalseOrNullPredicates.size()]));
				andPredicates.add(hasTestedIsFalseOrNullPredicate);			
				break;
			}
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orSearchPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		if(orManagerPredicates.size() > 0) {
			finalPredicates.add(orManagerPredicate);
		}		
		
//		Predicate resultPreticator = criteriaBuilder.or(orPrdt.toArray(new Predicate[orPrdt.size()]));
//		finalPredicates.add(resultPreticator);
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public Teacher findByEmail(String email) {
		String sql = "SELECT t FROM Teacher t WHERE t.email = :email";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("email", email);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Teacher findByUsername(String username) {
		String sql = "SELECT t FROM Teacher t WHERE t.username = :username";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}

	public Teacher findByUsernameAndPassword(String username, String password) {
		String sql = "SELECT t FROM Teacher t WHERE t.username = :username AND t.password = :password";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("username", username);
		typedQuery.setParameter("password", PasswordEncryptor.encrypt(password));

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Teacher findByIdAndToken(long id, String token) {
		String sql = "SELECT t FROM Teacher t WHERE t.id = :id AND t.token = :token";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("id", id);
		typedQuery.setParameter("token", token);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<Teacher> findByName(String name) {
		String sql = "SELECT t FROM Teacher t WHERE t.name LIKE :name";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("name", "%" + name + "%");
		
		return typedQuery.getResultList();
	}
	
	public List<Teacher> search(String gender, String teacherName, Hide hide, Date date) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		Join<Teacher, OnlineClass> onlineClass = teacher.join(Teacher_.onlineClasses);

		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		Calendar beginMoment = Calendar.getInstance();
		Calendar endMoment = Calendar.getInstance();
		if (date != null) {
			beginMoment.setTime(date);
			beginMoment.set(Calendar.HOUR_OF_DAY, 0);
			beginMoment.set(Calendar.MINUTE, 0);
			beginMoment.set(Calendar.SECOND, 0);
			beginMoment.set(Calendar.MILLISECOND, 0);

			endMoment.setTime(date);
			endMoment.add(Calendar.DATE, 1);
			endMoment.set(Calendar.HOUR_OF_DAY, 0);
			endMoment.set(Calendar.MINUTE, 0);
			endMoment.set(Calendar.SECOND, 0);
			endMoment.set(Calendar.MILLISECOND, 0);
			
			Predicate timePredicate = criteriaBuilder.between(onlineClass.get(OnlineClass_.scheduledDateTime), beginMoment.getTime(), endMoment.getTime());
			andPredicates.add(timePredicate);
			
			Predicate availablePredicate = criteriaBuilder.equal(onlineClass.get(OnlineClass_.status), OnlineClass.Status.AVAILABLE);
			andPredicates.add(availablePredicate);
		}
		
		if (gender != null && gender.length() != 0 && !gender.equals("noLimit")) {
			if (gender.equals("MALE")) {
				Predicate genderPredicate = criteriaBuilder.equal(teacher.get(User_.gender), Gender.MALE);
				andPredicates.add(genderPredicate);
			} else if (gender.equals("FEMALE")) {
				Predicate genderPredicate = criteriaBuilder.equal(teacher.get(User_.gender), Gender.FEMALE);
				andPredicates.add(genderPredicate);
			}
		}
		
		if (teacherName != null && teacherName.length() != 0) {
			// 这个函数和Adam确认过，已经不再用，所以没有往里面加realName的search
			Predicate teacherNamePredicate = criteriaBuilder.like(teacher.get(User_.name), '%' + teacherName + '%');
			andPredicates.add(teacherNamePredicate);
		}
		
		Predicate teacherTypePredicate = criteriaBuilder.equal(teacher.get(Teacher_.type), Type.PART_TIME);
		andPredicates.add(teacherTypePredicate);
		
		Predicate teacherStatusPredicate = criteriaBuilder.equal(teacher.get(User_.status), Status.NORMAL);
		andPredicates.add(teacherStatusPredicate);
		
		Predicate hidePredicate = criteriaBuilder.notEqual(teacher.get(Teacher_.hide), hide);
		andPredicates.add(hidePredicate);
		
		criteriaQuery.where(criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
		
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
		
		List<Teacher> result = typedQuery.getResultList();

		Calendar beginWeekCalendar = Calendar.getInstance();
		beginWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (date != null) {
			beginWeekCalendar.setTime(date);
		} else {
			beginWeekCalendar.add(Calendar.DAY_OF_YEAR, 7);
		}
		beginWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		beginWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		beginWeekCalendar.set(Calendar.MINUTE, 0);
		beginWeekCalendar.set(Calendar.SECOND, 0);
		beginWeekCalendar.set(Calendar.MILLISECOND, 0);
		Calendar endWeekCalendar = Calendar.getInstance();
		endWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (date != null) {
			endWeekCalendar.setTime(date);
		} else {
			endWeekCalendar.add(Calendar.DAY_OF_YEAR, 7);
		}
		endWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		endWeekCalendar.set(Calendar.MINUTE, 0);
		endWeekCalendar.set(Calendar.SECOND, 0);
		endWeekCalendar.set(Calendar.MILLISECOND, 0);
		for (Teacher t : result) {
			t.setNoAvailable(OnlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(t.getId(), beginWeekCalendar.getTime(), endWeekCalendar.getTime()));
		}
		
		return typedQuery.getResultList();
	}

	public List<Teacher> QueryWidthConditions() {
		return null;
	}

	public List<TeacherView> query(long studentId, String teacherCondition,
							   String availableCondition, Hide hide, 
							   long amount, long start, 
							   Long couseId, Date startDate,
							   Date endDate, Date dateFilter,
							   Date timeFilter) {
		logger.debug("querying teacher list");
		int iter = (int) start;
		int stillNeed = (int) amount;
		List<Teacher> result = new ArrayList<Teacher>();
		
		if (dateFilter != null || timeFilter != null) {
			availableCondition = "AVAILABLE";
		}
		
		String dateFilterString = "";
		if (dateFilter != null) {
			dateFilterString = " AND SUBSTRING(o.scheduledDateTime, 1) like :dateFilterString ";
		}
		
		if (timeFilter != null) {
			dateFilterString += " AND SUBSTRING(o.scheduledDateTime, 12) like :timeFilterString ";
		}
		
		String sql = "SELECT DISTINCT t FROM Teacher t LEFT JOIN t.certificatedCourses tc "
				+ "WHERE t.type = :type AND :student MEMBER OF t.favoredByStudents "
				+ "AND EXISTS "
				+ "(SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate "
				+ dateFilterString + ")"
				+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("type", Type.PART_TIME);
		typedQuery.setParameter("student", studentRepository.find(studentId));
		typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
		typedQuery.setParameter("teacherStatus", Status.LOCKED);
		typedQuery.setParameter("certificatedCourseId", couseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		if (dateFilter != null) {
			SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
			typedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
		}
		if (timeFilter != null) {
			SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
			typedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
		}
		typedQuery.setFirstResult(iter);
		typedQuery.setMaxResults(stillNeed);
		List<Teacher> favoredAndAvailableTeachers = typedQuery.getResultList();
		result.addAll(favoredAndAvailableTeachers);
		
		if (result.size() < stillNeed) {
			String favoredAndAvailableCountSql = "SELECT COUNT(DISTINCT t) FROM Teacher t LEFT JOIN t.onlineClasses oc LEFT JOIN t.certificatedCourses tc "
					+ "WHERE t.type = :type AND :student MEMBER OF t.favoredByStudents "
					+ "AND EXISTS (SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString +") "
					+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
			TypedQuery<Long> favoredAndAvailableCountTypedQuery = entityManager.createQuery(favoredAndAvailableCountSql, Long.class);
			favoredAndAvailableCountTypedQuery.setParameter("type", Type.PART_TIME);
			favoredAndAvailableCountTypedQuery.setParameter("student", studentRepository.find(studentId));
			favoredAndAvailableCountTypedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
			favoredAndAvailableCountTypedQuery.setParameter("teacherStatus", Status.LOCKED);
			favoredAndAvailableCountTypedQuery.setParameter("certificatedCourseId", couseId);
			favoredAndAvailableCountTypedQuery.setParameter("startDate", startDate);
			favoredAndAvailableCountTypedQuery.setParameter("endDate", endDate);
			if (dateFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
				favoredAndAvailableCountTypedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
			}
			if (timeFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
				favoredAndAvailableCountTypedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
			}
			
			long favoredAndAvailableCount = favoredAndAvailableCountTypedQuery.getSingleResult();
			
			iter = (int) ((iter - favoredAndAvailableCount) > 0 ? iter - favoredAndAvailableCount : 0);
			stillNeed -= result.size();
		} else {
			List<TeacherView> teacherShortCutList = new ArrayList<TeacherView>();
			for (Teacher teacher : result) {
				TeacherView t = new TeacherView();
				t.setId(teacher.getId());
				t.setAvatar(teacher.getAvatar());
				t.setIntroduction(teacher.getIntroduction());
				t.setName(teacher.getName());
				t.setNoAvailable(!OnlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(teacher.getId(), startDate, endDate));
				teacherShortCutList.add(t);
			}
			
			return teacherShortCutList;
		}
		
		if (!"AVAILABLE".equals(availableCondition)) {
			// 收藏但没课的老师。
			sql = "SELECT DISTINCT t FROM Teacher t LEFT JOIN t.onlineClasses oc  LEFT JOIN t.certificatedCourses tc "
					+ "WHERE t.type = :type AND :student MEMBER OF t.favoredByStudents AND NOT EXISTS ("
					+ "SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString + " ) "
					+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
			typedQuery = entityManager.createQuery(sql, Teacher.class);
			typedQuery.setParameter("type", Type.PART_TIME);
			typedQuery.setParameter("student", studentRepository.find(studentId));
			typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
			typedQuery.setParameter("teacherStatus", Status.LOCKED);
			typedQuery.setParameter("certificatedCourseId", couseId);
			typedQuery.setParameter("startDate", startDate);
			typedQuery.setParameter("endDate", endDate);
			if (dateFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
				typedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
			}
			if (timeFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
				typedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
			}
			typedQuery.setFirstResult(iter);
			typedQuery.setMaxResults(stillNeed);
			List<Teacher> favoredButNotAvailableTeachers = typedQuery.getResultList();
			result.addAll(favoredButNotAvailableTeachers);
	
			if (favoredButNotAvailableTeachers.size() < stillNeed) {
				String favoredButNotAvailableCountSql = "SELECT COUNT(DISTINCT t) FROM Teacher t LEFT JOIN t.onlineClasses oc LEFT JOIN t.certificatedCourses tc "
						+ "WHERE t.type = :type AND :student MEMBER OF t.favoredByStudents AND NOT EXISTS ("
						+ "SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString + " ) "
						+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
				TypedQuery<Long> favoredAndAvailableCountTypedQuery = entityManager.createQuery(favoredButNotAvailableCountSql, Long.class);
				favoredAndAvailableCountTypedQuery.setParameter("type", Type.PART_TIME);
				favoredAndAvailableCountTypedQuery.setParameter("student", studentRepository.find(studentId));
				favoredAndAvailableCountTypedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
				favoredAndAvailableCountTypedQuery.setParameter("teacherStatus", Status.LOCKED);
				favoredAndAvailableCountTypedQuery.setParameter("certificatedCourseId", couseId);
				favoredAndAvailableCountTypedQuery.setParameter("startDate", startDate);
				favoredAndAvailableCountTypedQuery.setParameter("endDate", endDate);
				if (dateFilter != null) {
					SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
					favoredAndAvailableCountTypedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
				}
				if (timeFilter != null) {
					SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
					favoredAndAvailableCountTypedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
				}
	
				long favoredButNotAvailableCount = favoredAndAvailableCountTypedQuery.getSingleResult();
	
				iter = (int) ((iter - favoredButNotAvailableCount) > 0 ? iter - favoredButNotAvailableCount : 0);
				stillNeed -= favoredButNotAvailableTeachers.size();
			} else {
				List<TeacherView> teacherShortCutList = new ArrayList<TeacherView>();
				for (Teacher teacher : result) {
					TeacherView t = new TeacherView();
					t.setId(teacher.getId());
					t.setAvatar(teacher.getAvatar());
					t.setIntroduction(teacher.getIntroduction());
					t.setName(teacher.getName());
					t.setNoAvailable(!OnlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(teacher.getId(), startDate, endDate));
					teacherShortCutList.add(t);
				}
				
				return teacherShortCutList;
			}
		}
		
		// 没收藏但有课的老师。
		sql = "SELECT DISTINCT t FROM Teacher t LEFT JOIN t.onlineClasses oc LEFT JOIN t.certificatedCourses tc "
				+ "WHERE t.type = :type AND :student NOT MEMBER OF t.favoredByStudents AND EXISTS ("
				+ "SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString + ") "
				+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
		typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("type", Type.PART_TIME);
		typedQuery.setParameter("student", studentRepository.find(studentId));
		typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
		typedQuery.setParameter("teacherStatus", Status.LOCKED);
		typedQuery.setParameter("certificatedCourseId", couseId);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		if (dateFilter != null) {
			SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
			typedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
		}
		if (timeFilter != null) {
			SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
			typedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
		}
		typedQuery.setFirstResult(iter);
		typedQuery.setMaxResults(stillNeed);
		List<Teacher> notFavoredButAvailableTeachers = typedQuery.getResultList();
		result.addAll(notFavoredButAvailableTeachers);

		if (notFavoredButAvailableTeachers.size() < stillNeed) {
			String favoredButNotAvailableCountSql = "SELECT COUNT(DISTINCT t) FROM Teacher t LEFT JOIN t.onlineClasses oc LEFT JOIN t.certificatedCourses tc "
					+ "WHERE t.type = :type AND :student NOT MEMBER OF t.favoredByStudents AND EXISTS ("
					+ "SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString + " ) "
					+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
			TypedQuery<Long> notFavoredButAvailableCountTypedQuery = entityManager.createQuery(favoredButNotAvailableCountSql, Long.class);
			notFavoredButAvailableCountTypedQuery.setParameter("type", Type.PART_TIME);
			notFavoredButAvailableCountTypedQuery.setParameter("student", studentRepository.find(studentId));
			notFavoredButAvailableCountTypedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
			notFavoredButAvailableCountTypedQuery.setParameter("teacherStatus", Status.LOCKED);
			notFavoredButAvailableCountTypedQuery.setParameter("certificatedCourseId", couseId);
			notFavoredButAvailableCountTypedQuery.setParameter("startDate", startDate);
			notFavoredButAvailableCountTypedQuery.setParameter("endDate", endDate);
			if (dateFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
				notFavoredButAvailableCountTypedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
			}
			if (timeFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
				notFavoredButAvailableCountTypedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
			}

			long notFavoredButAvailableCount = notFavoredButAvailableCountTypedQuery.getSingleResult();

			iter = (int) ((iter - notFavoredButAvailableCount) > 0 ? iter - notFavoredButAvailableCount : 0);
			stillNeed -= notFavoredButAvailableTeachers.size();
		} else {
			List<TeacherView> teacherShortCutList = new ArrayList<TeacherView>();
			for (Teacher teacher : result) {
				TeacherView t = new TeacherView();
				t.setId(teacher.getId());
				t.setAvatar(teacher.getAvatar());
				t.setIntroduction(teacher.getIntroduction());
				t.setName(teacher.getName());
				t.setNoAvailable(!OnlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(teacher.getId(), startDate, endDate));
				teacherShortCutList.add(t);
			}
			
			return teacherShortCutList;
		}

		if (!"AVAILABLE".equals(availableCondition)) {
			//没收藏且没有课的老师
			sql = "SELECT DISTINCT t FROM Teacher t LEFT JOIN t.onlineClasses oc LEFT JOIN t.certificatedCourses tc "
					+ "WHERE t.type = :type AND :student NOT MEMBER OF t.favoredByStudents AND NOT EXISTS ("
					+ "SELECT o FROM t.onlineClasses o WHERE o.status = :status AND o.scheduledDateTime BETWEEN :startDate AND :endDate " + dateFilterString + " ) "
					+ "AND t.status != :teacherStatus AND tc.id = :certificatedCourseId";
			typedQuery = entityManager.createQuery(sql, Teacher.class);
			typedQuery.setParameter("type", Type.PART_TIME);
			typedQuery.setParameter("student", studentRepository.find(studentId));
			typedQuery.setParameter("status", OnlineClass.Status.AVAILABLE);
			typedQuery.setParameter("teacherStatus", Status.LOCKED);
			typedQuery.setParameter("certificatedCourseId", couseId);
			typedQuery.setParameter("startDate", startDate);
			typedQuery.setParameter("endDate", endDate);
			if (dateFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("yyyy-MM-dd");
				typedQuery.setParameter("dateFilterString", dateFilterFormat.format(dateFilter) + "%");
			}
			if (timeFilter != null) {
				SimpleDateFormat dateFilterFormat = new SimpleDateFormat("HH:mm");
				typedQuery.setParameter("timeFilterString", "%" + dateFilterFormat.format(timeFilter) + "%");
			}
			
			typedQuery.setFirstResult(iter);
			typedQuery.setMaxResults(stillNeed);
			List<Teacher> notFavoredAndNotAvailableTeachers = typedQuery.getResultList();
			result.addAll(notFavoredAndNotAvailableTeachers);
		}
		
		List<TeacherView> teacherShortCutList = new ArrayList<TeacherView>();
		for (Teacher teacher : result) {
			TeacherView t = new TeacherView();
			t.setId(teacher.getId());
			t.setAvatar(teacher.getAvatar());
			t.setIntroduction(teacher.getIntroduction());
			t.setName(teacher.getName());
			t.setNoAvailable(!OnlineClassRepository.findIsAvailableByTeacherIdAndStartDateAndEndDate(teacher.getId(), startDate, endDate));
			teacherShortCutList.add(t);
		}
		
		return teacherShortCutList;
	}

	public List<Teacher> getTopEvaluationTeachersByDate(Calendar cal) {
		//cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		String sqlMaxEvaluation = "SELECT MAX(p.evaluation) FROM Payroll p WHERE p.paidDateTime = :pay_date_time";
		Query query = entityManager.createQuery(sqlMaxEvaluation, Teacher.class);
		query.setParameter("pay_date_time", cal.getTime());
		long evaluation = (Long) query.getSingleResult();
		
		if (evaluation != 0) {
			String sqlMaxEvaluationTeacher = "SELECT t FROM Payroll p JOIN p.teacher t WHERE p.evaluation = :evaluation AND p.paidDateTime = :pay_date_time";
			TypedQuery<Teacher> typedQuery = entityManager.createQuery(sqlMaxEvaluationTeacher, Teacher.class);
			typedQuery.setParameter("evaluation", evaluation);
			typedQuery.setParameter("pay_date_time", cal.getTime());
			List<Teacher> teachers = typedQuery.getResultList();
	
			if (teachers != null && teachers.size() != 0) {
				return teachers;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public List<Teacher> findAll() {
		String sql = "SELECT t FROM Teacher t";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		
		try {
			return typedQuery.getResultList();
		} catch (NoResultException exception) {
			return null;
		}

	}
	
	public List<Teacher> findNotExistsOnlineClassByScheduledDate(Date scheduledDateTime, long courseId) {
		String sql = "SELECT DISTINCT t FROM Teacher t JOIN t.certificatedCourses tcs JOIN t.onlineClasses tos WHERE tcs.id = :courseId AND t.status =:status AND NOT EXISTS (SELECT o FROM t.onlineClasses o WHERE o.scheduledDateTime = :scheduledDateTime)";	
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("status", Status.NORMAL);
		
		return typedQuery.getResultList();
	}
	
	public List<Teacher> findNormal() {
		String sql = "SELECT t FROM Teacher t WHERE t.status = :status";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("status", Status.NORMAL);
		try {
			return typedQuery.getResultList();
		} catch (NoResultException exception) {
			return null;
		}

	}
	
	public List<Teacher> findNormalAndRegular() {
		String sql = "SELECT t FROM Teacher t WHERE t.status = :status AND t.lifeCycle = :lifeCycle";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("status", Status.NORMAL);
		typedQuery.setParameter("lifeCycle", Teacher.LifeCycle.REGULAR);
		try {
			return typedQuery.getResultList();
		} catch (NoResultException exception) {
			return null;
		}

	}
	
	public List<Teacher> findItTestTeachers() {
		String sql = "SELECT DISTINCT t FROM Teacher t JOIN t.certificatedCourses tcs WHERE tcs.type = :type AND t.status =:status";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("type", Course.Type.IT_TEST);
		typedQuery.setParameter("status", Status.NORMAL);
		
		return typedQuery.getResultList();
	}
	
	public List<Teacher> findDemoTeachers() {
		String sql = "SELECT DISTINCT t FROM Teacher t JOIN t.certificatedCourses tcs WHERE (tcs.type = :demo OR tcs.type = :trial) AND t.status =:status";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("demo", Course.Type.DEMO);
		typedQuery.setParameter("trial", Course.Type.TRIAL); // trial课从Type demo 中取出,所以新增此项
		typedQuery.setParameter("status", Status.NORMAL);
		
		return typedQuery.getResultList();
	}

	public LifeCycle findLifeCycleById(long id) {
		String sql = "SELECT t.lifeCycle FROM Teacher t WHERE t.id = :id";
		TypedQuery<LifeCycle> typedQuery = entityManager.createQuery(sql, LifeCycle.class);
		typedQuery.setParameter("id", id);
		LifeCycle result = typedQuery.getSingleResult();
		return result; 
	}

	public List<TeacherNameView> findRegularTeachers(){
		String sql = "SELECT DISTINCT NEW com.vipkid.service.pojo.TeacherNameView(t.id, t.realName) FROM Teacher t WHERE t.status = :status AND t.lifeCycle = :lifeCycle AND t.type = :type AND (t.realName not like :englishTest AND t.realName not like :chineseTest) ORDER BY t.realName";
		TypedQuery<TeacherNameView> typedQuery = entityManager.createQuery(sql, TeacherNameView.class);
		typedQuery.setParameter("status", Status.NORMAL);
		typedQuery.setParameter("lifeCycle", LifeCycle.REGULAR);
		typedQuery.setParameter("type", Teacher.Type.PART_TIME);
		typedQuery.setParameter("englishTest", "%test%");
		typedQuery.setParameter("chineseTest", "%测试%");
		
		return typedQuery.getResultList();
	}		

	public Teacher findByRecruitmentId(String recruitmentId) {
		String sql = "SELECT t FROM Teacher t WHERE t.recruitmentId = :recruitmentId";
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(sql, Teacher.class);
		typedQuery.setParameter("recruitmentId", recruitmentId);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}

	}
	
	public List<TeachersView>listTeachers(int tabSign,String courseType,Date weekStart,Date weekEnd,String teacherName,int seachSign,long studentId,int rowNum,int currNum){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.TeachersView(t.id,t.name,t.avatar,t.shortVideo,t.gender)FROM Teacher t");
		sql.append(" LEFT JOIN t.onlineClasses ol  LEFT JOIN t.certificatedCourses co"); 
		if(tabSign!=-1){
			sql.append(" LEFT JOIN t.favoredByStudents st");
		}
		if(seachSign==1){
			sql.append(" WHERE 1=1");
			if(teacherName!=null&&!teacherName.trim().equals("")){
				sql.append(" AND t.name like :name");
			}
		}else if(seachSign==0){
			sql.append(" WHERE 1=1");     
			if(courseType!=null&&!courseType.equals("All")){
				sql.append(" AND co.type= :courseType");
			}
			if(weekStart!=null&&weekEnd!=null){
				sql.append(" AND ol.scheduledDateTime>= :weekStart");
				sql.append(" AND ol.scheduledDateTime<= :weekEnd");
				sql.append(" AND ol.status = :status");
			}
		}else{
			sql.append(" WHERE 1=1");
		}
		if(tabSign!=-1){
			sql.append(" AND st.id = :studentId");
		}
		sql.append(" AND co.type in (:MAJOR)");
		sql.append(" AND t.type =:type");
		sql.append(" AND t.accountType=:accountType ");
		sql.append(" AND t.status =:teacherStatus");
		sql.append(" AND t.lifeCycle =:lifeCycle");
		sql.append(" GROUP BY t.id");
		
		TypedQuery<TeachersView> query = entityManager.createQuery(sql.toString(),TeachersView.class);
		if(seachSign==1){
			if(teacherName!=null&&!teacherName.trim().equals("")){
				query.setParameter("name", "%"+teacherName+"%");
			}
		}else if(seachSign==0){
			if(courseType!=null&&!courseType.equals("All")){
				query.setParameter("courseType", com.vipkid.model.Course.Type.valueOf(courseType));
			}
			if(weekStart!=null&&weekEnd!=null){
				query.setParameter("weekStart", weekStart);
				query.setParameter("weekEnd", weekEnd);
				query.setParameter("status", com.vipkid.model.OnlineClass.Status.AVAILABLE);
			}
		}
		if(tabSign!=-1){
			query.setParameter("studentId", studentId);
		}
		query.setParameter("MAJOR", com.vipkid.model.Course.Type.valueOf("MAJOR"));
		query.setParameter("type", Type.PART_TIME);
		query.setParameter("accountType", AccountType.NORMAL);
		query.setParameter("teacherStatus", Status.NORMAL);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		query.setFirstResult((currNum-1)*rowNum);
		query.setMaxResults(rowNum);
		List<TeachersView>list = query.getResultList();
		if(list!=null&&list.size()!=0){
			return list;
		}
		return new ArrayList<TeachersView>();
	}
	public long countTeachers(int tabSign,String courseType,Date weekStart,Date weekEnd,String teacherName,int seachSign,long studentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(distinct t) FROM Teacher t");
		sql.append(" LEFT JOIN t.onlineClasses ol LEFT JOIN t.certificatedCourses co"); 
		if(tabSign!=-1){
			sql.append(" LEFT JOIN t.favoredByStudents st");
		}
		if(seachSign==1){
			sql.append(" WHERE 1=1");
			if(teacherName!=null&&!teacherName.trim().equals("")){
				sql.append(" AND t.name like :name");
			}
		}else if(seachSign==0){
			sql.append(" WHERE 1=1");     
			if(courseType!=null&&!courseType.equals("All")){
				sql.append(" AND co.type= :courseType");
			}
			if(weekStart!=null&&weekEnd!=null){
				sql.append(" AND ol.scheduledDateTime>= :weekStart");
				sql.append(" AND ol.scheduledDateTime<= :weekEnd");
				sql.append(" AND ol.status = :status");
			}
		}else{
			sql.append(" WHERE 1=1");
		}
		if(tabSign!=-1){
			sql.append(" AND st.id = :studentId");
		}
		sql.append(" AND co.type in (:MAJOR)");
		sql.append(" AND t.type =:type");
		sql.append(" AND t.accountType=:accountType ");
		sql.append(" AND t.status =:teacherStatus");
		sql.append(" AND t.lifeCycle =:lifeCycle");
		
		TypedQuery<Long> query = entityManager.createQuery(sql.toString(),Long.class);
		if(seachSign==1){
			if(teacherName!=null&&!teacherName.trim().equals("")){
				query.setParameter("name", "%"+teacherName+"%");
			}
		}else if(seachSign==0){
			if(courseType!=null&&!courseType.equals("All")){
				query.setParameter("courseType", com.vipkid.model.Course.Type.valueOf(courseType));
			}
			if(weekStart!=null&&weekEnd!=null){
				query.setParameter("weekStart", weekStart);
				query.setParameter("weekEnd", weekEnd);
				query.setParameter("status", com.vipkid.model.OnlineClass.Status.AVAILABLE);
			}
		}
		if(tabSign!=-1){
			query.setParameter("studentId", studentId);
		}
		query.setParameter("MAJOR", com.vipkid.model.Course.Type.valueOf("MAJOR"));
		query.setParameter("type", Type.PART_TIME);
		query.setParameter("accountType", AccountType.NORMAL);
		query.setParameter("teacherStatus", Status.NORMAL);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		long count = query.getSingleResult();
		return count;
	}
	public boolean findHasAvailableByTeacherId(long teacherId,Date weekStart,Date weekEnd){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(t) FROM Teacher t");
		sql.append(" LEFT JOIN t.onlineClasses ol"); 
		sql.append(" WHERE t.id = :teacherId");
		sql.append(" AND t.status =:teacherStatus");
		sql.append(" AND t.lifeCycle =:lifeCycle");
		sql.append(" AND t.type = :type");
		sql.append(" AND t.accountType=:accountType ");
		sql.append(" AND ol.status = :status");
		sql.append(" AND ol.scheduledDateTime>= :weekStart");
		sql.append(" AND ol.scheduledDateTime<= :weekEnd");
		TypedQuery<Long> query = entityManager.createQuery(sql.toString(),Long.class);
		query.setParameter("teacherId", teacherId);
		query.setParameter("status", com.vipkid.model.OnlineClass.Status.AVAILABLE);
		query.setParameter("teacherStatus", Status.NORMAL);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		query.setParameter("type", Type.PART_TIME);
		query.setParameter("accountType", AccountType.NORMAL);
		query.setParameter("weekStart", weekStart);
		query.setParameter("weekEnd", weekEnd);
		
		long count = query.getSingleResult();
		if(count>0){
			return false;
		}
		return true;
	}
	
	public List<TeachersView> listTeachers(long studentId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.TeachersView(t.id,t.name,t.avatar,t.shortVideo,t.gender)FROM Teacher t");
		sql.append(" LEFT JOIN t.favoredByStudents st");
		sql.append(" WHERE st.id = :studentId");
		sql.append(" AND t.status =:teacherStatus");
		sql.append(" AND t.lifeCycle =:lifeCycle");
		sql.append(" AND t.type = :type");
		sql.append(" AND t.accountType=:accountType ");
		sql.append(" GROUP BY t.id");
		
		TypedQuery<TeachersView> query = entityManager.createQuery(sql.toString(),TeachersView.class);
		query.setParameter("studentId", studentId);
		query.setParameter("teacherStatus", Status.NORMAL);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		query.setParameter("type", Type.PART_TIME);
		query.setParameter("accountType", AccountType.NORMAL);
		query.setFirstResult(0);
		query.setMaxResults(5);
		
		List<TeachersView>list = query.getResultList();
		if(list!=null&&list.size()!=0){
			return list;
		}
		return new ArrayList<TeachersView>();
	}
	@SuppressWarnings("unchecked")
	public List<TeachersView>listTeachersForPreschedule(Integer seaType,
			Long teacherId,
			Date timeStart,
			Date timeEnd,
			String courseType,
			long studentId,
			Integer currNum,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ( ");
		sql.append("select * from ( ");
		sql.append("SELECT	t.id,u.name,t.avatar,t.teacher_tags,1 as student_id,u.gender FROM vipkid.teacher t ");
		sql.append("LEFT JOIN user u ON t.id = u.id ");
		sql.append("WHERE t.type = 'PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' GROUP BY t.id ");
		sql.append(")t1 where t1.id in (SELECT teacher_id FROM student_favorate_teacher WHERE student_id =  ?)  ");
		if(seaType!=2){
			sql.append("union ");
			sql.append("select * from ( ");
			sql.append("SELECT t.id,u.name,t.avatar,t.teacher_tags,-1 as student_id,u.gender	FROM vipkid.teacher t ");
			sql.append("LEFT JOIN user u ON t.id = u.id ");
			sql.append("LEFT JOIN student_favorate_teacher s ON t.id = s.teacher_id ");
			sql.append("WHERE t.type = 'PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' GROUP BY t.id ");
			sql.append(")t1 where t1.id  not in (SELECT teacher_id FROM student_favorate_teacher WHERE student_id =  ?) ");
		}
		sql.append(")t2 where 1=1 ");
		if(teacherId!=-1&&seaType==1){
			sql.append("and t2.id = ? ");
		}else if(!"".equals(teacherName)&&seaType==-1){
			sql.append(" and t2.name like ? ");
		}
		sql.append("and EXISTS ");
		sql.append("(select tc.teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id  where  t2.id=tc.teacher_id and co.type = ?) ");
		sql.append("and EXISTS(select o.id from online_class o where o.scheduled_date_time >= ? and o.scheduled_date_time <= ? and o.status = 'AVAILABLE' and o.teacher_id = t2.id)");
		
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, studentId);
		if(seaType!=2){
			query.setParameter(2, studentId);
			if(teacherId!=-1&&seaType==1){
				query.setParameter(3, teacherId);
				query.setParameter(4, courseType);
				query.setParameter(5, timeStart);
				query.setParameter(6, timeEnd);
			}else if(!"".equals(teacherName)&&seaType==-1){
				query.setParameter(3, "%"+teacherName+"%");
				query.setParameter(4, courseType);
				query.setParameter(5, timeStart);
				query.setParameter(6, timeEnd);
			}else{
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}
		}else{
			if(teacherId!=-1&&seaType==1){
				query.setParameter(2, teacherId);
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}else if(!"".equals(teacherName)&&seaType==-1){
				query.setParameter(2, "%"+teacherName+"%");
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}else{
				query.setParameter(2, courseType);
				query.setParameter(3, timeStart);
				query.setParameter(4, timeEnd);
			}
		}
		query.setFirstResult((currNum-1)*5);
		query.setMaxResults(5);
		List<Object> rows = query.getResultList();
		List<TeachersView> teachersViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return teachersViews;
        }
		for (Object row : rows) {
			TeachersView teachersView = new TeachersView();
			Object[] cells = (Object[])row;
			teachersView.setTeacherId((long) (cells[0]));
			teachersView.setName((String)cells[1]);
			teachersView.setAvatar((String)cells[2]);
			teachersView.setTag(getTag((String)cells[3]));
			teachersView.setStudentId(-1);
			if(cells[4]!=null){
				if((Integer) (cells[4])==1){
					teachersView.setStudentId(studentId);
				}else{
					teachersView.setStudentId(-1);
				}
			}
			if(StringUtils.isBlank(teachersView.getAvatar())){
				if(StringUtils.isBlank((String) (cells[5]))){
					teachersView.setAvatar("static/images/common/boyteacher.jpg");
				}else if(((String) (cells[5])).equals("FEMALE")){
					teachersView.setAvatar("static/images/common/girlteacher.jpg");
				}if(((String) (cells[5])).equals("MALE")){
					teachersView.setAvatar("static/images/common/boyteacher.jpg");
				}
			}
			teachersViews.add(teachersView);
		}
		return teachersViews;
		
	}	
	
	public long countTeachersForPreschedule(Integer seaType,
			Long teacherId,
			Date timeStart,
			Date timeEnd,
			String courseType,
			long studentId,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) from ( ");
		sql.append("select * from ( ");
		sql.append("SELECT	t.id,u.name,t.avatar,t.teacher_tags,1 as student_id FROM vipkid.teacher t ");
		sql.append("LEFT JOIN user u ON t.id = u.id ");
		sql.append("WHERE t.type = 'PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' GROUP BY t.id ");
		sql.append(")t1 where t1.id in (SELECT teacher_id FROM student_favorate_teacher WHERE student_id =  ?) ");
		if(seaType!=2){
			sql.append("union ");
			sql.append("select * from ( ");
			sql.append("SELECT t.id,u.name,t.avatar,t.teacher_tags,-1 as student_id	FROM vipkid.teacher t ");
			sql.append("LEFT JOIN user u ON t.id = u.id ");
			sql.append("WHERE t.type = 'PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' GROUP BY t.id ");
			sql.append(")t1 where t1.id not in (SELECT teacher_id FROM student_favorate_teacher WHERE student_id =  ?) ");
		}
		sql.append(")t2 where 1=1 ");
		if(teacherId!=-1&&seaType==1){
			sql.append("and t2.id = ? ");
		}else if(!"".equals(teacherName)&&seaType==-1){
			sql.append(" and t2.name like ? ");
		}
		sql.append("and EXISTS ");
		sql.append("(select tc.teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id  where  t2.id=tc.teacher_id and co.type = ?) ");
		sql.append("and EXISTS(select o.id from online_class o where o.scheduled_date_time >= ? and o.scheduled_date_time <= ? and o.status = 'AVAILABLE' and o.teacher_id = t2.id)");
		
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, studentId);
		if(seaType!=2){
			query.setParameter(2, studentId);
			if(teacherId!=-1&&seaType==1){
				query.setParameter(3, teacherId);
				query.setParameter(4, courseType);
				query.setParameter(5, timeStart);
				query.setParameter(6, timeEnd);
			}else if(!"".equals(teacherName)&&seaType==-1){
				query.setParameter(3, "%"+teacherName+"%");
				query.setParameter(4, courseType);
				query.setParameter(5, timeStart);
				query.setParameter(6, timeEnd);
			}else{
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}
		}else{
			if(teacherId!=-1&&seaType==1){
				query.setParameter(2, teacherId);
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}else if(!"".equals(teacherName)&&seaType==-1){
				query.setParameter(2, "%"+teacherName+"%");
				query.setParameter(3, courseType);
				query.setParameter(4, timeStart);
				query.setParameter(5, timeEnd);
			}else{
				query.setParameter(2, courseType);
				query.setParameter(3, timeStart);
				query.setParameter(4, timeEnd);
			}
		}
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	public List<OnlineClassesView> findOnliclassForTelMode(long teacherId,Date timeStart,Date timeEnd,String courseType){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.OnlineClassesView(o.id,o.status,o.scheduledDateTime,o.teacher.id,o.teacher.name)FROM OnlineClass o left join o.teacher.certificatedCourses cc");
		sql.append(" WHERE o.teacher.id = :teacherId");
		sql.append(" and o.teacher.type = :type");
		sql.append(" AND o.teacher.status =:teacherStatus");
		sql.append(" AND o.teacher.lifeCycle =:lifeCycle");
		sql.append(" AND o.teacher.accountType=:accountType ");
		sql.append(" and (o.status = :status1 or o.status = :status2) ");
		sql.append(" and o.scheduledDateTime>= :timeStart and  o.scheduledDateTime<= :timeEnd");
		sql.append(" and cc.type = :courseType");
		sql.append(" group by o.id ");
		TypedQuery<OnlineClassesView> query = entityManager.createQuery(sql.toString(),OnlineClassesView.class);
		query.setParameter("teacherId", teacherId);
		query.setParameter("status1", com.vipkid.model.OnlineClass.Status.AVAILABLE);
		query.setParameter("status2", com.vipkid.model.OnlineClass.Status.BOOKED);
		query.setParameter("type", Type.PART_TIME);
		query.setParameter("teacherStatus", Status.NORMAL);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		query.setParameter("accountType", AccountType.NORMAL);
		query.setParameter("timeStart", timeStart);
		query.setParameter("timeEnd", timeEnd);
		query.setParameter("courseType", com.vipkid.model.Course.Type.valueOf(courseType));
		
		return query.getResultList();
	}
	public List<OnlineClassesView> findOnliclassByStrudentAndTime(long studentId,Date timeStart,Date timeEnd,String courseType){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NEW com.vipkid.service.pojo.parent.OnlineClassesView(o.id,o.status,o.scheduledDateTime,o.teacher.id,o.teacher.name)FROM OnlineClass o left join o.students os left join o.teacher.certificatedCourses cc");
		sql.append(" WHERE (o.status = :status1 or o.status = :status2) ");
		sql.append(" and o.scheduledDateTime>= :timeStart and  o.scheduledDateTime<= :timeEnd");
		sql.append(" and os.id = :studentId ");
		sql.append(" and cc.type = :courseType");
		sql.append(" group by o.id ");
		
		TypedQuery<OnlineClassesView> query = entityManager.createQuery(sql.toString(),OnlineClassesView.class);
		query.setParameter("studentId", studentId);
		query.setParameter("status1", com.vipkid.model.OnlineClass.Status.BOOKED);
		query.setParameter("status2", com.vipkid.model.OnlineClass.Status.OPEN);
		query.setParameter("timeStart", timeStart);
		query.setParameter("timeEnd", timeEnd);
		query.setParameter("courseType", com.vipkid.model.Course.Type.valueOf(courseType));
		
		return query.getResultList();	
	}
	
	@SuppressWarnings("unchecked")
	public List<OnlineClassesView> findOnliclassForCalMode(int seaType,long teacherId,Date timeStart,Date timeEnd,String courseType,long studentId,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id,o.status,o.scheduled_date_time,o.teacher_id,u.name from online_class o join user u on o.teacher_id = u.id");
		sql.append(" join teacher t on o.teacher_id = t.id ");
		sql.append(" where t.type = 'PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' and o.status='AVAILABLE'");
		sql.append(" and o.teacher_id in (select teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id and co.type = ? )");
		sql.append(" and o.scheduled_date_time>=? and o.scheduled_date_time<=?");
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				sql.append(" and o.teacher_id = ?");
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				sql.append(" and u.name like ?");
			}
		}else{
			sql.append(" and o.teacher_id in");
			sql.append(" (select teacher_id from student_favorate_teacher st where st.student_id = ?  )");
		}
		sql.append(" GROUP BY o.id");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, courseType);
		query.setParameter(2, timeStart);
		query.setParameter(3, timeEnd);
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				query.setParameter(4, teacherId);
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				query.setParameter(4, "%"+teacherName+"%");
			}
		}else{
			query.setParameter(4, studentId);
		}
		List<Object> rows = query.getResultList();
		List<OnlineClassesView> onlineClassesViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return onlineClassesViews;
        }
		for (Object row : rows) {
			OnlineClassesView onlineClassesView = new OnlineClassesView();
			Object[] cells = (Object[])row;
			onlineClassesView.setId((long) (cells[0]));
			onlineClassesView.setStatus((String)(cells[1]));
			onlineClassesView.setScheduledDateTime(cells[2]==null?null:(Date)cells[2]);
			onlineClassesView.setTeacherId((long) (cells[3]));
			onlineClassesView.setTeacherName((String)(cells[4]));
			onlineClassesViews.add(onlineClassesView);
		}
		return onlineClassesViews;
	}
	
	@SuppressWarnings("unchecked")
	public List<TeachersView> listTeacherForCal(Date scheduledDateTime,int seaType,long teacherId,long studentId,String courseType,int currNum,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("select t.* from(");
		sql.append("select o.id,o.status,u.name,t.avatar,t.teacher_tags,t.id as teacherId,ot.num,u.gender from teacher t ");
		sql.append("join (select count(ol.id) num,ol.teacher_id from online_class ol where ol.status = 'AVAILABLE' GROUP BY ol.teacher_id) ot on t.id=ot.teacher_id ");
		sql.append("join online_class o on o.teacher_id=t.id join user u on t.id = u.id where o.status ='AVAILABLE' and o.scheduled_date_time = ? ");
		sql.append("and t.type='PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' and t.id in (select tc.teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id  where co.type = ?)");
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				sql.append(" and t.id = ?");
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				sql.append(" and u.name like ?");
			}
		}else{
			sql.append(" and t.id in (select teacher_id from student_favorate_teacher st where st.student_id = ?)");
		}
		sql.append(" group by o.id) t  order by t.num desc");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, scheduledDateTime);
		query.setParameter(2, courseType);
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				query.setParameter(3, teacherId);
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				query.setParameter(3, "%"+teacherName+"%");
			}
		}else{
			query.setParameter(3, studentId);
		}
		query.setFirstResult((currNum-1)*6);
		query.setMaxResults(6);
		List<Object> rows = query.getResultList();
		List<TeachersView> teachersViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return teachersViews;
        }
		for (Object row : rows) {
			TeachersView teachersView = new TeachersView();
			Object[] cells = (Object[])row;
			teachersView.setOnlineClassId((long) (cells[0]));
			teachersView.setStatus((String)(cells[1]));
			teachersView.setName((String) (cells[2]));
			teachersView.setAvatar((String) (cells[3]));
			teachersView.setTag(getTag((String) (cells[4])));
			teachersView.setTeacherId(cells[5]==null?-1:(long) (cells[5]));
			if(StringUtils.isBlank(teachersView.getAvatar())){
				if(StringUtils.isBlank((String) (cells[7]))){
					teachersView.setAvatar("static/images/common/boyteacher.jpg");
				}else if(((String) (cells[7])).equals("FEMALE")){
					teachersView.setAvatar("static/images/common/girlteacher.jpg");
				}if(((String) (cells[7])).equals("MALE")){
					teachersView.setAvatar("static/images/common/boyteacher.jpg");
				}
			}
			teachersViews.add(teachersView);
		}
		return teachersViews;
	}
	
	public long counrTeacherForCal(Date scheduledDateTime,int seaType,long teacherId,long studentId,String courseType,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) from (");
		sql.append("select o.id,o.status,u.name,t.avatar,t.teacher_tags from teacher t ");
		sql.append("join online_class o on o.teacher_id=t.id join user u on t.id = u.id where o.status ='AVAILABLE' and o.scheduled_date_time = ? ");
		sql.append("and t.type='PART_TIME' AND t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL' and t.id in (select tc.teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id  where co.type = ?)");
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				sql.append(" and t.id = ?");
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				sql.append(" and u.name like ?");
			}
		}else{
			sql.append(" and t.id in (select teacher_id from student_favorate_teacher st where st.student_id = ?)");
		}
		sql.append(" group by o.id) t");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, scheduledDateTime);
		query.setParameter(2, courseType);
		if(seaType!=2){
			if(teacherId!=-1&&seaType==1){
				query.setParameter(3, teacherId);
			}else if(!StringUtils.isBlank(teacherName)&&seaType==-1){
				query.setParameter(3, "%"+teacherName+"%");
			}
		}else{
			query.setParameter(3, studentId);
		}
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	/**
	 * 
	* @Title: listTeachersView 
	* @Description: 加载搜索老师下拉框数据 
	* @param parameter
	* @author zhangfeipeng 
	* @return List<TeView>
	* @throws
	 */
	@SuppressWarnings("unchecked")
	public List<TeView> listTeachersView(String courseType,String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	u.name,t.avatar,t.id,u.gender FROM teacher t ");
		sql.append("JOIN user u ON t.id = u.id ");
		sql.append("JOIN online_class o ON t.id = o.teacher_id ");
		sql.append("WHERE  t.life_cycle='REGULAR' and t.type = 'PART_TIME' AND u.status='NORMAL' AND u.account_type='NORMAL' AND o. STATUS = 'AVAILABLE' ");
		sql.append("AND t.id IN (	SELECT tc.teacher_id	FROM	teacher_certificated_course tc	JOIN course co ON tc.course_id = co.id	WHERE	co.type = ? ) ");
		sql.append("AND u.name like  ? ");
		sql.append("GROUP BY t.id order by u.name");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, courseType);
		query.setParameter(2, "%"+teacherName+"%");
		query.setMaxResults(5);
		List<Object> rows = query.getResultList();
		List<TeView> tViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return tViews;
        }
		for (Object row : rows) {
			TeView tView = new TeView();
			Object[] cells = (Object[])row;
			tView.setTitle((String) (cells[0]));
			tView.setAvatar((String) (cells[1]));
			tView.setId(cells[2]==null?-1:(long) (cells[2]));
			if(!(StringUtils.isNotBlank(tView.getAvatar()))){
				if(StringUtils.isBlank((String) (cells[3]))){
					tView.setAvatar("static/images/common/boyteacher.jpg");
				}else if(((String) (cells[3])).equals("FEMALE")){
					tView.setAvatar("static/images/common/girlteacher.jpg");
				}if(((String) (cells[3])).equals("MALE")){
					tView.setAvatar("static/images/common/boyteacher.jpg");
				}
			}
			tViews.add(tView);
		}
		return tViews;
	}
	
	
	@SuppressWarnings("unchecked")
	public TeacherDetailView findTeacherDetailById(long teacherId){
		String sql = "SELECT t.id,u.name,t.avatar,t.short_video,t.introduction_zh,graduated_from,vipkid_remarks,teacher_tags,u.gender FROM	teacher t JOIN user u ON t.id = u.id WHERE	t.id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setMaxResults(1);
		query.setParameter(1, teacherId);
		List<Object> rows = query.getResultList();
		List<TeacherDetailView> tViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
		for (Object row : rows) {
			TeacherDetailView tView = new TeacherDetailView();
			Object[] cells = (Object[])row;
			tView.setTeacherId(cells[0]==null?-1:(long) (cells[0]));
			tView.setName((String) (cells[1]));
			tView.setAvatar((String) (cells[2]));
			tView.setShortVideo((String) (cells[3]));
			tView.setIntroduction((String) (cells[4]));
			tView.setGraduatedFrom((String) (cells[5]));
			tView.setVipkidRemarks((String) (cells[6]));
			tView.setTag(getTag((String) (cells[7])));
			if(StringUtils.isBlank(tView.getAvatar())){
				if(StringUtils.isBlank((String) (cells[8]))){
					tView.setAvatar("static/images/common/boyteacher.jpg");
				}else if(((String) (cells[8])).equals("FEMALE")){
					tView.setAvatar("static/images/common/girlteacher.jpg");
				}if(((String) (cells[8])).equals("MALE")){
					tView.setAvatar("static/images/common/boyteacher.jpg");
				}
			}
			tViews.add(tView);
		}
		return tViews.get(0);
	}
	
	/**
	 * 
	* @Title: getTab 
	* @Description: 将从数据库中查询出来的teacherTabs翻译
	* @param parameter
	* @author zhangfeipeng 
	* @return String[]
	* @throws
	 */
	public String[] getTag(String tags){
		if(StringUtils.isBlank(tags)){
			return new String [0];
		}else{
			String tag[] = new String[7];
			String t[] = tags.split("，");
			if(t.length==7){
				if(t[0].equals("1")){
					tag[0]="Level 1";
				}
				if(t[1].equals("2")){
					tag[1]="Level 2";
				}
				if(t[2].equals("3")){
					tag[2]="Level 3";
				}
				if(t[3].equals("4")){
					tag[3]="Level 4";
				}
				if(t[4].equals("5")){
					tag[4]="Level 5";
				}
				if(t[5].equals("6")){
					tag[5]="Level 6";
				}
				if(t[6].equals("7")){
					tag[6]="Level 7";
				}
			}else{
				return new String [0];
			}
			return tag;
		}
	}

	// ======================================================== signup begin
	// 2015-08-15 signup 的teacher filter 和count
	public long countSignup(String search, String status, String recruitChannel) {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
			criteriaQuery.select(criteriaBuilder.countDistinct(teacher));

			// compose OR predicate
		    List<Predicate> orPredicates = new LinkedList<Predicate>();
			
		    // 2015-08-15  signup阶段 使用username和email
			if (search != null) {
				orPredicates.add(criteriaBuilder.like(teacher.get(User_.username), "%" + search + "%"));
				orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
			}
			
			Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
			
			// compose AND predicate
			List<Predicate> andPredicates = new LinkedList<Predicate>();
			
			// !! SIGNUP status设置
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), LifeCycle.SIGNUP));
			
			try {
				if (null != status) {
					Status statusV = Status.valueOf(status);
					andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status),statusV));
				} 
			} catch(Exception e) {
				logger.error("error status:"+e.getMessage());
			}
			
			try {
//				if (null != recruitChannel) {
//					RecruitmentChannel channelV = RecruitmentChannel.valueOf(recruitChannel);
//					andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.recruitmentChannel), channelV));
//				}
				//2015-08-17
				if (recruitChannel !=null) {
					Join<Teacher, Partner> partnerJoin = teacher.join(Teacher_.partner);
					andPredicates.add(criteriaBuilder.equal(partnerJoin.get(User_.id), recruitChannel));
				}
			} catch (Exception e) {
				logger.error("error recruitChannel:"+e.getMessage());
			}
			
			Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
			
			List<Predicate> finalPredicates = new LinkedList<Predicate>();
			if(andPredicates.size() > 0) {
				finalPredicates.add(andPredicate);
			}
			if(orPredicates.size() > 0) {
				finalPredicates.add(orPredicate);
			}
							
			Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
			criteriaQuery.where(finalPredicate);

			TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
			return typedQuery.getSingleResult();
		
	}

	public List<Teacher> listSignup(String search, Status status,
			String recruitChannel, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);

		// compose OR predicate
	    List<Predicate> orPredicates = new LinkedList<Predicate>();
		
	    // 2015-08-15  signup阶段 使用username和email
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(teacher.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// !! SIGNUP status设置
		andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), LifeCycle.SIGNUP));
		
		try {
			if (null != status) {
				Status statusV = status;//Status.valueOf(status);
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status),statusV));
			} 
		} catch(Exception e) {
			logger.error("error status:"+e.getMessage());
		}
		
		try {
//			if (null != recruitChannel) {
//				RecruitmentChannel channelV = RecruitmentChannel.valueOf(recruitChannel);
//				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.recruitmentChannel), channelV));
//			}
			if (recruitChannel !=null) {
				Join<Teacher, Partner> partnerJoin = teacher.join(Teacher_.partner);
				andPredicates.add(criteriaBuilder.equal(partnerJoin.get(User_.id), recruitChannel));
			}
		} catch (Exception e) {
			logger.error("error recruitChannel:"+e.getMessage());
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
						
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);

		criteriaQuery.orderBy(criteriaBuilder.desc(teacher.get(User_.lastEditDateTime)));
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
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
	// ======================================================== signup end
	
	
	// ======================================================== normal begin
	// TODO ... jiangsiyue: it Test,Level 
	// 2015-08-15 REGULAR and QUIT 的teacher filter 和count
	public long countNormal(LifeCycle lifeCycle, String search, String status,String[] certificatedCourseId, String[] managers, Long operatorId,
			Gender gender, Country country,Teacher.Type teacherType,
			// 2015-08-15 
			DateTimeParam operationStartDate,
			DateTimeParam operationEndDate,
			String[] contractEndDate,String strAccountType
			) {
		//
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
			criteriaQuery.select(criteriaBuilder.countDistinct(teacher));

			// compose OR predicate
		    List<Predicate> orPredicates = new LinkedList<Predicate>();
			
		    // 2015-08-15  非signup阶段 使用realName和email
			if (search != null) {
				orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
				orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
			}
			
			Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
			
			// compose AND predicate
			List<Predicate> andPredicates = new LinkedList<Predicate>();
			
			try {
				if (null != status) {
					Status statusV = Status.valueOf(status);
					andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status),statusV));
				} 
			} catch(Exception e) {
				logger.error("error status:"+e.getMessage());
			}
			
			try {
				if (null != lifeCycle) {
					andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), lifeCycle));
				}
			} catch (Exception e) {
				logger.error("error recruitChannel:"+e.getMessage());
			}
			
			// compose OR predicate
		    List<Predicate> orManagerPredicates = new LinkedList<Predicate>();
		    Join<Teacher, Staff> staffJoin = teacher.join(Teacher_.manager, JoinType.LEFT);
			if (null != managers && managers.length > 0) {
				for (String managerId : managers){
					orManagerPredicates.add(criteriaBuilder.equal(staffJoin.get(User_.id), Long.parseLong(managerId)));
				}
			}
			Predicate orManagerPredicate = criteriaBuilder.or(orManagerPredicates.toArray(new Predicate[orManagerPredicates.size()]));
			
			// opeator 
			if (null != operatorId ) {
				Join<Teacher, TeacherLifeCycleLog> teacherJoinTLC = teacher.join(Teacher_.teacherLifeCycleLogs, JoinType.LEFT);
				Join<TeacherLifeCycleLog,User> teacherTLCJoinUser = teacherJoinTLC.join(TeacherLifeCycleLog_.operator, JoinType.LEFT);
				andPredicates.add(criteriaBuilder.equal(teacherTLCJoinUser.get(User_.id), operatorId));
			}
			
			//operatorId,operationFrom/ToTime
			Join<Teacher, TeacherLifeCycleLog> teacherLCLog = teacher.join(Teacher_.teacherLifeCycleLogs, JoinType.LEFT);
			if (null != operationStartDate) {
			andPredicates.add( criteriaBuilder.greaterThanOrEqualTo(teacherLCLog.get(TeacherLifeCycleLog_.createDateTime), DateTimeUtils.getBeginningOfTheDay(operationStartDate.getValue())) );
			}
			if (operationEndDate != null) {
			andPredicates.add( criteriaBuilder.lessThan(teacherLCLog.get(TeacherLifeCycleLog_.createDateTime), DateTimeUtils.getNextDay(operationEndDate.getValue())));
			}
			
			if (!StringUtils.isEmpty(strAccountType)) {
				try {
					AccountType accountTypeValue = AccountType.valueOf(strAccountType);
					andPredicates.add(criteriaBuilder.equal(teacher.get(User_.accountType), accountTypeValue));
				
				} catch (Exception e) {
					logger.error("Invalid account type:"+strAccountType);
				}
			}
			
			if (gender != null) {
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.gender), gender));
			}
			
			if (country != null) {
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.country), country));
			}
			
			if (null != teacherType) {
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.type), teacherType));
			}
			
			List<Predicate> orContractEndDatePredicates = new LinkedList<Predicate>();
			if (contractEndDate != null && contractEndDate.length > 0) {
				for(String date:contractEndDate){
					Date leftDateTime = new Date();
					leftDateTime.setTime(Long.parseLong(date));
					Calendar rightDateTime = Calendar.getInstance();
					rightDateTime.setTime(leftDateTime);
					rightDateTime.set(Calendar.MONTH, rightDateTime.get(Calendar.MONTH)+1 );
					
					List<Predicate> andContractEndDatePredicates = new LinkedList<Predicate>();
					andContractEndDatePredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getBeginningOfTheDay(leftDateTime)));
					andContractEndDatePredicates.add(criteriaBuilder.lessThan(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getBeginningOfTheDay(rightDateTime.getTime())));
					
					Predicate andContractEndDatePredicate = criteriaBuilder.and(andContractEndDatePredicates.toArray(new Predicate[andContractEndDatePredicates.size()]));
					
					orContractEndDatePredicates.add(andContractEndDatePredicate);				
				}
			}
			Predicate orContractEndDatePredicate = criteriaBuilder.or(orContractEndDatePredicates.toArray(new Predicate[orContractEndDatePredicates.size()]));
			
			List<Predicate> orCoursesPredicates = new LinkedList<Predicate>();
			if (certificatedCourseId != null && certificatedCourseId.length > 0) {
				Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses);
				for(String courseId:certificatedCourseId){
					orCoursesPredicates.add(criteriaBuilder.equal(courses.get(Course_.id), Long.parseLong(courseId)));
				}
			}
			Predicate orCoursesPredicate = criteriaBuilder.or(orCoursesPredicates.toArray(new Predicate[orCoursesPredicates.size()]));
			
			Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

			List<Predicate> finalPredicates = new LinkedList<Predicate>();
			if(andPredicates.size() > 0) {
				finalPredicates.add(andPredicate);
			}
			if(orCoursesPredicates.size() > 0) {
				finalPredicates.add(orCoursesPredicate);
			}
			if(orPredicates.size() > 0) {
				finalPredicates.add(orPredicate);
			}
			if(orManagerPredicates.size() > 0) {
				finalPredicates.add(orManagerPredicate);
			}
			if(orContractEndDatePredicates.size() > 0){
				finalPredicates.add(orContractEndDatePredicate);
			}
							
			Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
			criteriaQuery.where(finalPredicate);

			TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
			return typedQuery.getSingleResult();
		
	}

	
	public List<Teacher> listNormal(LifeCycle lifeCycle, String search, String status,String[] certificatedCourseIds, String[] managers,
			Gender gender, Country country,Teacher.Type teacherType,
			Integer start, Integer length,String[] contractEndDate,
			Long operatorId, DateTimeParam operationStartDate, DateTimeParam operationEndDate,String strAccountType
			) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);

		// compose OR predicate
	    List<Predicate> orPredicates = new LinkedList<Predicate>();
		
	    // 2015-08-15  非signup阶段 使用realName和email
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		try {
			if (null != status) {
				Status statusV = Status.valueOf(status);
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.status),statusV));
			} 
		} catch(Exception e) {
			logger.error("error status:"+e.getMessage());
		}
		
		try {
			if (null != lifeCycle) {
				andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.lifeCycle), lifeCycle));
			}
		} catch (Exception e) {
			logger.error("error recruitChannel:"+e.getMessage());
		}
		
		// compose OR predicate
	    List<Predicate> orManagerPredicates = new LinkedList<Predicate>();
	    Join<Teacher, Staff> staffJoin = teacher.join(Teacher_.manager, JoinType.LEFT);
		if (null != managers && managers.length > 0) {
			for (String managerId : managers){
				orManagerPredicates.add(criteriaBuilder.equal(staffJoin.get(User_.id), Long.parseLong(managerId)));
			}
		}
		Predicate orManagerPredicate = criteriaBuilder.or(orManagerPredicates.toArray(new Predicate[orManagerPredicates.size()]));
		
		// opeator 
		if (null != operatorId ) {
			Join<Teacher, TeacherLifeCycleLog> teacherJoinTLC = teacher.join(Teacher_.teacherLifeCycleLogs, JoinType.LEFT);
			Join<TeacherLifeCycleLog,User> teacherTLCJoinUser = teacherJoinTLC.join(TeacherLifeCycleLog_.operator, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(teacherTLCJoinUser.get(User_.id), operatorId));
		}
		
		//operatorId,operationFrom/ToTime
		Join<Teacher, TeacherLifeCycleLog> teacherLCLog = teacher.join(Teacher_.teacherLifeCycleLogs, JoinType.LEFT);

		if (null != operationStartDate) {
		andPredicates.add( criteriaBuilder.greaterThanOrEqualTo(teacherLCLog.get(TeacherLifeCycleLog_.createDateTime), DateTimeUtils.getBeginningOfTheDay(operationStartDate.getValue())) );
		}
		if (operationEndDate != null) {
		andPredicates.add( criteriaBuilder.lessThan(teacherLCLog.get(TeacherLifeCycleLog_.createDateTime), DateTimeUtils.getNextDay(operationEndDate.getValue())));
		}
		
		if (gender != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(User_.gender), gender));
		}
		
		if (country != null) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.country), country));
		}
		
		if (null != teacherType) {
			andPredicates.add(criteriaBuilder.equal(teacher.get(Teacher_.type), teacherType));
		}
		
	    List<Predicate> orCoursesPredicates = new LinkedList<Predicate>();
		if (certificatedCourseIds != null && certificatedCourseIds.length > 0) {
			Join<Teacher, Course> courses = teacher.join(Teacher_.certificatedCourses);
			for(String courseId:certificatedCourseIds){
				orCoursesPredicates.add(criteriaBuilder.equal(courses.get(Course_.id), Long.parseLong(courseId)));
			}
		}
		Predicate orCoursesPredicate = criteriaBuilder.or(orCoursesPredicates.toArray(new Predicate[orCoursesPredicates.size()]));

		List<Predicate> orContractEndDatePredicates = new LinkedList<Predicate>();
		if (contractEndDate != null && contractEndDate.length > 0) {
			for(String date:contractEndDate){
				Date leftDateTime = new Date();
				leftDateTime.setTime(Long.parseLong(date));
				Calendar rightDateTime = Calendar.getInstance();
				rightDateTime.setTime(leftDateTime);
				int month = rightDateTime.get(Calendar.MONTH)+1;
				rightDateTime.set(Calendar.MONTH, month );
				
				List<Predicate> andContractEndDatePredicates = new LinkedList<Predicate>();
				andContractEndDatePredicates.add(criteriaBuilder.greaterThanOrEqualTo(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getBeginningOfTheDay(leftDateTime)));
				andContractEndDatePredicates.add(criteriaBuilder.lessThan(teacher.get(Teacher_.contractEndDate), DateTimeUtils.getBeginningOfTheDay(rightDateTime.getTime())));
				
				Predicate andContractEndDatePredicate = criteriaBuilder.and(andContractEndDatePredicates.toArray(new Predicate[andContractEndDatePredicates.size()]));
				
				orContractEndDatePredicates.add(andContractEndDatePredicate);				
			}
		}
		Predicate orContractEndDatePredicate = criteriaBuilder.or(orContractEndDatePredicates.toArray(new Predicate[orContractEndDatePredicates.size()]));

		
		if (!StringUtils.isEmpty(strAccountType)) {
			try {
				AccountType accountTypeValue = AccountType.valueOf(strAccountType);
				andPredicates.add(criteriaBuilder.equal(teacher.get(User_.accountType), accountTypeValue));
			
			} catch (Exception e) {
				logger.error("Invalid account type:"+strAccountType);
			}
		}		
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		if(orManagerPredicates.size() > 0) {
			finalPredicates.add(orManagerPredicate);
		}
		if(orCoursesPredicates.size() > 0) {
			finalPredicates.add(orCoursesPredicate);
		}
		if(orContractEndDatePredicates.size() > 0){
			finalPredicates.add(orContractEndDatePredicate);
		}
						
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);

		criteriaQuery.orderBy(criteriaBuilder.desc(teacher.get(User_.lastEditDateTime)));
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
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
	// ======================================================== normal end

	public List<Teacher> listAll(String search, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Teacher> criteriaQuery = criteriaBuilder.createQuery(Teacher.class).distinct(true);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);

		// compose OR predicate
	    List<Predicate> orPredicates = new LinkedList<Predicate>();
		
	    // 2015-08-15  非signup阶段 使用realName和email
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.realName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);

		criteriaQuery.orderBy(criteriaBuilder.desc(teacher.get(User_.lastEditDateTime)));
		TypedQuery<Teacher> typedQuery = entityManager.createQuery(criteriaQuery);
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

	public long countAll(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Teacher> teacher = criteriaQuery.from(Teacher.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(teacher));

	    List<Predicate> orPredicates = new LinkedList<Predicate>();
		
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(teacher.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(teacher.get(Teacher_.email), "%" + search + "%"));
		}
		
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
		
		
		
	}

	public List<Date> getRegularTeacherContractDate() {
		String sql = "SELECT distinct t.contractEndDate FROM Teacher t WHERE t.lifeCycle = :lifeCycle ORDER BY t.contractEndDate ASC";
		TypedQuery<Date> typedQuery = entityManager.createQuery(sql, Date.class);
		typedQuery.setParameter("lifeCycle", LifeCycle.REGULAR);
		
		List<Date> dateList = typedQuery.getResultList();
		return dateList;
	}
		
}
