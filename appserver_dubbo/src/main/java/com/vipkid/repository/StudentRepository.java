package com.vipkid.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.model.Channel;
import com.vipkid.model.Channel_;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.FollowUp.Category;
import com.vipkid.model.Course_;
import com.vipkid.model.Family;
import com.vipkid.model.Family_;
import com.vipkid.model.FollowUp;
import com.vipkid.model.FollowUp_;
import com.vipkid.model.Gender;
import com.vipkid.model.ItTest;
import com.vipkid.model.ItTest.FinalResult;
import com.vipkid.model.ItTest_;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.LearningCycle_;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.LearningProgress_;
import com.vipkid.model.Lesson;
import com.vipkid.model.Lesson_;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.OrderItem;
import com.vipkid.model.OrderItem_;
import com.vipkid.model.Order_;
import com.vipkid.model.Parent;
import com.vipkid.model.Parent_;
import com.vipkid.model.Product;
import com.vipkid.model.Product_;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Student.Source;
import com.vipkid.model.Student.StudentType;
import com.vipkid.model.StudentPerformance;
import com.vipkid.model.Student_;
import com.vipkid.model.Teacher;
import com.vipkid.model.Unit;
import com.vipkid.model.Unit_;
import com.vipkid.model.User.AccountType;
import com.vipkid.model.User.Status;
import com.vipkid.model.User_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.CltStudent;
import com.vipkid.service.pojo.leads.FollowUpVo;
import com.vipkid.service.pojo.leads.ParentVo;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;


@Repository
public class StudentRepository extends BaseRepository<Student> {
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	public StudentRepository(){
		super(Student.class);
	}
	
	public String findMaxStudentNumber(){
		//String sql = "SELECT MAX(s.username) FROM Student s";
        String sql = "SELECT s.username FROM Student s ORDER BY s.id desc";
		
		TypedQuery<String> typedQuery = entityManager.createQuery(sql, String.class);
		typedQuery.setMaxResults(1);
		return typedQuery.getSingleResult();
	}
	
	/**
	 * find student by username
	 * @param username
	 * @return
	 */
	public Student findByUsername(String username) {
		String sql = "SELECT s FROM Student s WHERE s.username = :username";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<Student> findByName(String name) {
		String sql = "SELECT s FROM Student s WHERE s.name = :name";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("name", name);
		
		return typedQuery.getResultList();
	}
	
	/**
	 * find student by username and password
	 * @param username
	 * @param password
	 * @return
	 */
	public Student findByUsernameAndPassword(String username, String password) {
		String sql = "SELECT s FROM Student s WHERE s.username = :username AND s.password = :password";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("username", username);
//		typedQuery.setParameter("password", PasswordEncryptor.encrypt(password));
		typedQuery.setParameter("password", password);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	/**
	 * find student by id and token
	 * @param id
	 * @param token
	 * @return
	 */
	public Student findByIdAndToken(long id, String token) {
		String sql = "SELECT t FROM Student t WHERE t.id = :id AND t.token = :token";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("id", id);
		typedQuery.setParameter("token", token);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	/**
	 * 获取学生列表
	 * @param search
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public List<Student> list(
			String gender, Integer age, String province, String city, String lifeCycle, String status, String source, DateTimeParam followUpTargetDateTimeFrom, DateTimeParam followUpTargetDateTimeTo, DateTimeParam followUpCreateDateTimeFrom, DateTimeParam followUpCreateDateTimeTo, // for General
			Long salesIdForPermission, Long chineseLeadTeacherId,
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo, Long courseId, DateTimeParam enrollmentDateFrom, DateTimeParam enrollmentDateTo, Integer customerStage, Long salesId, Integer leftClassHour, // TODO  DateTimeParam firstClassDateFrom, DateTimeParam firstClassDateTo, // for sale
			Long productId, String payBy, // for product
			Long lastOnlineClassCourseId, Long lastOnlineClassUnitId, Long lastOnlineClassLearningCycleId, Long lastOnlineClassLessonId,
			String search, Boolean forBooking, String finalResult, int start, int length,String channel, String currentPerformance, AccountType accountType) {		
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class).distinct(true);
		Root<Student> student = criteriaQuery.from(Student.class);
		// criteriaQuery.select(student); // add
		
		// 允许sales通过电话等搜索到非自己名下的学生
		if(salesIdForPermission != null && search != null) {
			salesIdForPermission = null;
		}
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.LEFT);
			Join<Family, Parent> parents = family.join(Family_.parents, JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// for General
		if(gender != null&& !gender.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(User_.gender), Gender.valueOf(gender)));
		}
		if(accountType != null){
			andPredicates.add(criteriaBuilder.equal(student.get(User_.accountType), accountType));
		}
		if(age!=null && age != 0){
			Calendar nowTime = Calendar.getInstance();
			int thisYear = nowTime.get(Calendar.YEAR);
			
			Calendar birthdayFrom = Calendar.getInstance();
			birthdayFrom.set(Calendar.YEAR, thisYear - age);
			Date birthdayFromDate = birthdayFrom.getTime();
			
			if(age < 5) {
				Calendar birthdayTo = Calendar.getInstance();
				birthdayTo.set(Calendar.YEAR, thisYear - age - 1);
				Date birthdayToDate = birthdayTo.getTime();
				andPredicates.add(criteriaBuilder.greaterThan(student.get(Student_.birthday), birthdayToDate));
			}else if(age >=5 && age <= 8){												
				Calendar birthdayTo = Calendar.getInstance();
				birthdayTo.set(Calendar.YEAR, thisYear - age - 1);
				Date birthdayToDate = birthdayTo.getTime();
				
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(Student_.birthday), birthdayFromDate));
				andPredicates.add(criteriaBuilder.greaterThan(student.get(Student_.birthday), birthdayToDate));
			}else {
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(Student_.birthday), birthdayFromDate));
			}
		}
		if (province != null) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.province), province));
		}
		if (city != null) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.city), city));
		}
		if (lifeCycle != null && !lifeCycle.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		
		if (status != null && !status.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(User_.status), Status.valueOf(status)));
		}else{
			andPredicates.add(criteriaBuilder.notEqual(student.get(User_.status), Status.TEST));
		}
		if (source != null && !source.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.valueOf(source)));
		}else if(channel != null && channel.length() > 0){
			Join<Student, Channel> marJoin = student.join(Student_.channel, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(marJoin.get(Channel_.sourceName), channel));
		}
		
		if(followUpTargetDateTimeFrom != null) {
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(followUps.get(FollowUp_.targetDateTime), followUpTargetDateTimeFrom.getValue()));
		}
		
		if(followUpTargetDateTimeTo != null) {
			Date actualToDate = DateTimeUtils.getNextDay(followUpTargetDateTimeTo.getValue());
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(followUps.get(FollowUp_.targetDateTime), actualToDate));
		}

		if(followUpCreateDateTimeFrom != null) {
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(followUps.get(FollowUp_.createDateTime), followUpCreateDateTimeFrom.getValue()));
		}

		if(followUpCreateDateTimeTo != null) {
			Date actualToDate = DateTimeUtils.getNextDay(followUpCreateDateTimeTo.getValue());
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(followUps.get(FollowUp_.createDateTime), actualToDate));
		}
		
		if (finalResult !=null && !finalResult.equals("")) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.INNER);
			Join<Family, ItTest> itTests = family.join(Family_.itTests, JoinType.LEFT);
			switch(FinalResult.valueOf(finalResult)) {
			case NORMAL:
			case ABNORMAL:
				andPredicates.add(criteriaBuilder.equal(family.get(Family_.hasTested), true));				
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.current), true));
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.finalResult), FinalResult.valueOf(finalResult)));
				break;
			case NONE:List<Predicate> hasTestedIsFalseOrNullPredicates = new LinkedList<Predicate>();
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.equal(family.get(Family_.hasTested), false));
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.isNull(family.get(Family_.hasTested)));
				Predicate hasTestedIsFalseOrNullPredicate = criteriaBuilder.or(hasTestedIsFalseOrNullPredicates.toArray(new Predicate[hasTestedIsFalseOrNullPredicates.size()]));
				andPredicates.add(hasTestedIsFalseOrNullPredicate);			
				break;
			}
		}
		
		
		if (salesIdForPermission != null) {
			Join<Student, Staff> sales = student.join(Student_.sales, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesIdForPermission));
		}
		
		if (chineseLeadTeacherId != null) {
			Join<Student, Staff> chineseLeadTeacher = student.join(Student_.chineseLeadTeacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(chineseLeadTeacher.get(User_.id), chineseLeadTeacherId));
		}
		
		// for sale
		if (registrationDateFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(student.get(User_.registerDateTime), registrationDateFrom.getValue()));
		}
		if (registrationDateTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(User_.registerDateTime), DateTimeUtils.getNextDay(registrationDateTo.getValue())));
		}
		if (customerStage != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.customerStage), customerStage));
		}
		if (salesId != null) {
			Join<Student, Staff> sales = student.join(Student_.sales, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesId));
		}
		// 2015-07-01 currentPerformance
		if (currentPerformance != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.currentPerformance), StudentPerformance.valueOf(currentPerformance)) );
		}
		if (leftClassHour != null) {
			Join<Student, LearningProgress> learningProgress = student.join(Student_.learningProgresses, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(learningProgress.get(LearningProgress_.leftClassHour), leftClassHour));
		}
		if (courseId != null){
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			
			if (forBooking != null && forBooking){
				andPredicates.add(criteriaBuilder.equal(learningProgresses.get(LearningProgress_.status), LearningProgress.Status.STARTED));
			}
			if (enrollmentDateFrom != null) {
				Join<Student, Order> orders = student.join(Student_.orders, JoinType.LEFT);
				andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(orders.get(Order_.paidDateTime), enrollmentDateFrom.getValue()));
			}
			if (enrollmentDateTo != null) {
				Join<Student, Order> orders = student.join(Student_.orders, JoinType.LEFT);
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(orders.get(Order_.paidDateTime), enrollmentDateTo.getValue()));
			}	
			/* //TODO
			String sql1 = "SELECT DISTINCT s FROM Student s JOIN s.learningProgresses slps JOIN slps.completedOnlineClasses slpscocs "
					+ "WHERE EXISTS(SELECT aa from slps.completedOnlineClasses aa "
					+ "WHERE aa.scheduledDateTime in (SELECT min(bb.scheduledDateTime) FROM slps.completedOnlineClasses bb) "
					+ "AND aa.scheduledDateTime >= :startDate and aa.scheduledDateTime <= :endDate)";
			if(firstClassDateFrom != null){
				Join<LearningProgress, OnlineClass> onlineClasses = learningProgresses.join(LearningProgress_.completedOnlineClasses, JoinType.LEFT);				
				// 一级子查询 
				Subquery<OnlineClass> subquery1 = criteriaQuery.subquery(OnlineClass.class); 
				Root<OnlineClass> onlineClassSub1 = subquery1.from(OnlineClass.class);  // 后面没有用到
				subquery1.select(onlineClassSub1); // ?
				
				// 二级子查询
				Subquery<Date> subquery2 = subquery1.subquery(Date.class);
				subquery2.from(OnlineClass.class);
				subquery2.select(criteriaBuilder.least(onlineClasses.get(OnlineClass_.scheduledDateTime)));
							
				// 一级子查询条件
				List<Predicate> subQueryPredicates1 = new ArrayList<Predicate>(); 
//				subQueryPredicates1.add(criteriaBuilder.in(subquery2));  // 二级查询加入到一级查询
				subQueryPredicates1.add(criteriaBuilder.greaterThanOrEqualTo(onlineClasses.get(OnlineClass_.scheduledDateTime), firstClassDateFrom.getValue()));			
				subquery1.where(criteriaBuilder.in(onlineClassSub1.get(OnlineClass_.scheduledDateTime)).value(subquery2));
				// 加入主查询
				andPredicates.add(criteriaBuilder.exists(subquery1)); 
			}
			if(firstClassDateTo != null){
				
			} */
		}	
		
		// for product 
		if (productId != null) {
			Join<Student, Order> orders = student.join(Student_.orders, JoinType.LEFT);
			Join<Order, OrderItem> orderItmes = orders.join(Order_.orderItems, JoinType.LEFT);
			Join<OrderItem, Product> product = orderItmes.join(OrderItem_.product, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(product.get(Product_.id), productId));
		}
		if (payBy != null) {
			Join<Student, Order> order = student.join(Student_.orders, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.payBy), payBy));
		}
		
		// for education
		if (lastOnlineClassCourseId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), lastOnlineClassCourseId));
			andPredicates.add(criteriaBuilder.isNotEmpty(learningProgresses.get(LearningProgress_.completedOnlineClasses)));
		}
		
		/*if(lastOnlineClassUnitId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(units.get(Unit_.id), lastOnlineClassUnitId));
		}
		
		if(lastOnlineClassLearningCycleId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			Join<Unit, LearningCycle> learningCycles = units.join(Unit_.learningCycles, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(learningCycles.get(LearningCycle_.id), lastOnlineClassLearningCycleId));
		}

		if(lastOnlineClassLessonId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			Join<Unit, LearningCycle> learningCycles = units.join(Unit_.learningCycles, JoinType.LEFT);
			Join<LearningCycle, Lesson> lessons = learningCycles.join(LearningCycle_.lessons, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(lessons.get(Lesson_.id), lastOnlineClassLessonId));
		}*/
				
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(student.get(User_.registerDateTime)));
		TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		if(length != 0) { // for work arround
			typedQuery.setMaxResults(length);
		}		
		return typedQuery.getResultList(); 
	}
	
	public long count(
			String gender, Integer age, String province, String city, String lifeCycle, String status, String source, DateTimeParam followUpTargetDateTimeFrom, DateTimeParam followUpTargetDateTimeTo, DateTimeParam followUpCreateDateTimeFrom, DateTimeParam followUpCreateDateTimeTo, // for General
			Long salesIdForPermission, Long chineseLeadTeacherId,
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo, Long courseId, DateTimeParam enrollmentDateFrom, DateTimeParam enrollmentDateTo, Integer customerStage, Long salesId, Integer leftClassHour, // for sale
			Long productId, String payBy, // for product
			Long lastOnlineClassCourseId, Long lastOnlineClassUnitId, Long lastOnlineClassLearningCycleId, Long lastOnlineClassLessonId,
			String search, Boolean forBooking, String finalResult,String channel, String currentPerformance, AccountType accountType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Student> student = criteriaQuery.from(Student.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(student));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			Join<Student, Family> family = student.join(Student_.family,JoinType.LEFT);
			Join<Family, Parent> parents = family.join(Family_.parents,JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parents.get(User_.name),  "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parents.get(Parent_.mobile), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		// for General
		if(gender != null && !gender.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(User_.gender), Gender.valueOf(gender)));
		}
		if(age!=null && age != 0){
			Calendar nowTime = Calendar.getInstance();
			int thisYear = nowTime.get(Calendar.YEAR);
			
			Calendar birthdayFrom = Calendar.getInstance();
			birthdayFrom.set(Calendar.YEAR, thisYear - age);
			Date birthdayFromDate = birthdayFrom.getTime();
			
			if(age < 5) {
				Calendar birthdayTo = Calendar.getInstance();
				birthdayTo.set(Calendar.YEAR, thisYear - age - 1);
				Date birthdayToDate = birthdayTo.getTime();
				andPredicates.add(criteriaBuilder.greaterThan(student.get(Student_.birthday), birthdayToDate));
			}else if(age >=5 && age <= 8){												
				Calendar birthdayTo = Calendar.getInstance();
				birthdayTo.set(Calendar.YEAR, thisYear - age - 1);
				Date birthdayToDate = birthdayTo.getTime();
				
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(Student_.birthday), birthdayFromDate));
				andPredicates.add(criteriaBuilder.greaterThan(student.get(Student_.birthday), birthdayToDate));
			}else {
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(Student_.birthday), birthdayFromDate));
			}
		}
		if (province != null) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.province), province));
		}
		if (city != null) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.city), city));
		}
		if (lifeCycle != null && !lifeCycle.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		if (status != null && !status.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(User_.status), Status.valueOf(status)));
		}else{
			andPredicates.add(criteriaBuilder.notEqual(student.get(User_.status), Status.TEST));
		}		
		if (source != null && !source.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.valueOf(source)));
		}else if(channel!=null && channel.length()>0){
			Join<Student, Channel> marJoin = student.join(Student_.channel, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(marJoin.get(Channel_.sourceName), channel));
		}
		
		if(followUpTargetDateTimeFrom != null) {
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(followUps.get(FollowUp_.targetDateTime), followUpTargetDateTimeFrom.getValue()));
		}
		
		if(followUpTargetDateTimeTo != null) {
			Date actualToDate = DateTimeUtils.getNextDay(followUpTargetDateTimeTo.getValue());
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(followUps.get(FollowUp_.targetDateTime), actualToDate));
		}

		if(followUpCreateDateTimeFrom != null) {
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(followUps.get(FollowUp_.createDateTime), followUpCreateDateTimeFrom.getValue()));
		}

		if(followUpCreateDateTimeTo != null) {
			Date actualToDate = DateTimeUtils.getNextDay(followUpCreateDateTimeTo.getValue());
			Join<Student, FollowUp> followUps = student.join(Student_.followUps, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(followUps.get(FollowUp_.current), true));
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(followUps.get(FollowUp_.createDateTime), actualToDate));
		}
		
		if (finalResult !=null && !finalResult.equals("")) {
			Join<Student, Family> family = student.join(Student_.family, JoinType.INNER);
			Join<Family, ItTest> itTests = family.join(Family_.itTests, JoinType.LEFT);
			switch(FinalResult.valueOf(finalResult)) {
			case NORMAL:
			case ABNORMAL:
				andPredicates.add(criteriaBuilder.equal(family.get(Family_.hasTested), true));				
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.current), true));
				andPredicates.add(criteriaBuilder.equal(itTests.get(ItTest_.finalResult), FinalResult.valueOf(finalResult)));
				break;
			case NONE:List<Predicate> hasTestedIsFalseOrNullPredicates = new LinkedList<Predicate>();
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.equal(family.get(Family_.hasTested), false));
				hasTestedIsFalseOrNullPredicates.add(criteriaBuilder.isNull(family.get(Family_.hasTested)));
				Predicate hasTestedIsFalseOrNullPredicate = criteriaBuilder.or(hasTestedIsFalseOrNullPredicates.toArray(new Predicate[hasTestedIsFalseOrNullPredicates.size()]));
				andPredicates.add(hasTestedIsFalseOrNullPredicate);			
				break;
			}
		}
		
		
		if (salesIdForPermission != null) {
			Join<Student, Staff> sales = student.join(Student_.sales, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesIdForPermission));
		}
		
		if (chineseLeadTeacherId != null) {
			Join<Student, Staff> chineseLeadTeacher = student.join(Student_.chineseLeadTeacher, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(chineseLeadTeacher.get(User_.id), chineseLeadTeacherId));
		}
		// 2015-07-01 currentPerformance
		if (currentPerformance != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.currentPerformance),  StudentPerformance.valueOf(currentPerformance) ));
		}
		// for sale
		if (registrationDateFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(student.get(User_.registerDateTime), registrationDateFrom.getValue()));
		}
		if (registrationDateTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(User_.registerDateTime), DateTimeUtils.getNextDay(registrationDateTo.getValue())));
		}
		if (customerStage != null) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.customerStage), customerStage));
		}
		if (accountType != null){
			andPredicates.add(criteriaBuilder.equal(student.get(User_.accountType), accountType));
		}
		if (salesId != null) {
			Join<Student, Staff> sales = student.join(Student_.sales, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id), salesId));
		}
		if (leftClassHour != null) {
			Join<Student, LearningProgress> learningProgress = student.join(Student_.learningProgresses, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(learningProgress.get(LearningProgress_.leftClassHour), leftClassHour));
		}
		if (courseId != null){
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), courseId));
			
			if (forBooking != null && forBooking){
				andPredicates.add(criteriaBuilder.equal(learningProgresses.get(LearningProgress_.status), LearningProgress.Status.STARTED));
			}
			
			if (enrollmentDateFrom != null) {
				Join<Student, Order> orders = student.join(Student_.orders, JoinType.LEFT);
				andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(orders.get(Order_.paidDateTime), enrollmentDateFrom.getValue()));
			}
			if (enrollmentDateTo != null) {
				Join<Student, Order> order = student.join(Student_.orders, JoinType.LEFT);
				andPredicates.add(criteriaBuilder.lessThanOrEqualTo(order.get(Order_.paidDateTime), enrollmentDateTo.getValue()));
			}	
			/* //TODO
			String sql1 = "SELECT DISTINCT s FROM Student s JOIN s.learningProgresses slps JOIN slps.completedOnlineClasses slpscocs "
					+ "WHERE EXISTS(SELECT aa from slps.completedOnlineClasses aa "
					+ "WHERE aa.scheduledDateTime in (SELECT min(bb.scheduledDateTime) FROM slps.completedOnlineClasses bb) "
					+ "AND aa.scheduledDateTime >= :startDate and aa.scheduledDateTime <= :endDate)";
			if(firstClassDateFrom != null){
				Join<LearningProgress, OnlineClass> onlineClasses = learningProgresses.join(LearningProgress_.completedOnlineClasses, JoinType.LEFT);				
				// 一级子查询 
				Subquery<OnlineClass> subquery1 = criteriaQuery.subquery(OnlineClass.class); 
				Root<OnlineClass> onlineClassSub1 = subquery1.from(OnlineClass.class);  // 后面没有用到
				subquery1.select(onlineClassSub1); // ?
				
				// 二级子查询
				Subquery<Date> subquery2 = subquery1.subquery(Date.class);
				subquery2.from(OnlineClass.class);
				subquery2.select(criteriaBuilder.least(onlineClasses.get(OnlineClass_.scheduledDateTime)));
							
				// 一级子查询条件
				List<Predicate> subQueryPredicates1 = new ArrayList<Predicate>(); 
//				subQueryPredicates1.add(criteriaBuilder.in(subquery2));  // 二级查询加入到一级查询
				subQueryPredicates1.add(criteriaBuilder.greaterThanOrEqualTo(onlineClasses.get(OnlineClass_.scheduledDateTime), firstClassDateFrom.getValue()));			
				subquery1.where(criteriaBuilder.in(onlineClassSub1.get(OnlineClass_.scheduledDateTime)).value(subquery2));
				// 加入主查询
				andPredicates.add(criteriaBuilder.exists(subquery1)); 
			}
			if(firstClassDateTo != null){
				
			} */
		}	
		
		// for product 
//		if (productId != null) {
//			Join<Student, LearningProgress> learningProgress = student.join(Student_.learningProgresses, JoinType.LEFT);
//			Join<LearningProgress, Product> product = learningProgress.join(LearningProgress_.product, JoinType.LEFT);
//			andPredicates.add(criteriaBuilder.equal(product.get(Product_.id), productId));
//		}
		if (payBy != null) {
			Join<Student, Order> order = student.join(Student_.orders, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.payBy), payBy));
		}
		
		// for education
		if (lastOnlineClassCourseId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.id), lastOnlineClassCourseId));
		}
		
		if(lastOnlineClassUnitId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(units.get(Unit_.id), lastOnlineClassUnitId));
		}
		
		if(lastOnlineClassLearningCycleId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			Join<Unit, LearningCycle> learningCycles = units.join(Unit_.learningCycles, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(learningCycles.get(LearningCycle_.id), lastOnlineClassLearningCycleId));
		}

		if(lastOnlineClassLessonId != null) {
			Join<Student, LearningProgress> learningProgresses = student.join(Student_.learningProgresses, JoinType.LEFT);
			Join<LearningProgress, Course> course = learningProgresses.join(LearningProgress_.course, JoinType.LEFT);
			Join<Course, Unit> units = course.join(Course_.units, JoinType.LEFT);
			Join<Unit, LearningCycle> learningCycles = units.join(Unit_.learningCycles, JoinType.LEFT);
			Join<LearningCycle, Lesson> lessons = learningCycles.join(LearningCycle_.lessons, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(lessons.get(Lesson_.id), lastOnlineClassLessonId));
		}
				
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public long totalCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Student> student = criteriaQuery.from(Student.class);
		criteriaQuery.select(criteriaBuilder.count(student));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public List<Student> findByFamilyId(long familyId) {
		String sql = "SELECT s FROM Student s WHERE s.family.id = :familyId";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("familyId", familyId);
		
		return typedQuery.getResultList();
	}
	
	public List<Student> findByUserId(long userId) {
		String sql = "SELECT s FROM Student s WHERE s.creater.id = :userId OR s.lastEditor.id = :userId";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("userId", userId);
		
		return typedQuery.getResultList();
	}

	public List<Student> findByParentId(long parentId) {
		String sql = "SELECT s FROM Student s JOIN s.family f JOIN f.parents p WHERE p.id = :parentId";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("parentId", parentId);
		
		return typedQuery.getResultList();
	}
	
	public Student findAvaiableTestStudentByScheduledDateTime(Date scheduledDateTime) {
		String sql = "SELECT DISTINCT s FROM Student s WHERE s.status = :status AND s.id NOT IN (SELECT os.id FROM OnlineClass o JOIN o.students os WHERE o.status = :bookedStatus AND o.scheduledDateTime = :scheduledDateTime)";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("status", Status.TEST);
		typedQuery.setParameter("bookedStatus", OnlineClass.Status.BOOKED);
		typedQuery.setParameter("scheduledDateTime", scheduledDateTime);
		
		if (!typedQuery.getResultList().isEmpty()){
			return typedQuery.getResultList().get(0);
		} 
		
		return null;
	}
	
	public Student findAvailablePracticumStudent(long practicumCourseId) {
		String sql = "SELECT DISTINCT s FROM Student s JOIN s.learningProgresses slps WHERE s.status = :status AND slps.status = :learningProgressStatus AND slps.course.id = :practicumCourseId AND slps.lastScheduledLesson IS NULL AND slps.nextShouldTakeLesson IS NULL";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("status", Status.TEST);
		typedQuery.setParameter("practicumCourseId", practicumCourseId);
		typedQuery.setParameter("learningProgressStatus", LearningProgress.Status.STARTED);
		
		if (!typedQuery.getResultList().isEmpty()){
			return typedQuery.getResultList().get(0);
		} 
		
		return null;
	}
	
	public Student upateFavoredTeachers(Student student) {
		List<Teacher> favoredTeachers = student.getFavorTeachers();
		Student persistedStudent = this.find(student.getId());

		for (Teacher t : favoredTeachers) {
			Teacher teacherInDB = teacherRepository.find(t.getId());
			persistedStudent.addFavoredTeacher(teacherInDB);
		}

		List<Teacher> theFavorTeachers = new ArrayList<Teacher>(persistedStudent.getFavorTeachers());
		for (Teacher t : theFavorTeachers) {
			if (!student.getFavorTeachers().contains(t)) {
				persistedStudent.removeFavorTeacher(t);
			}
		}

		this.update(persistedStudent);

		return persistedStudent;
	}
	
	public Student findLastByRegisterDateTime() {
		String sql = "SELECT s FROM Student s ORDER BY s.registerDateTime DESC";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Student> findAll() {
		String sql = "SELECT s FROM Student s";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
	    
	    return typedQuery.getResultList();
	}
	
	/** wait for testing */
	public List<Student> findLearningStudentNotExistsBookedOnlineClassInNextWeek() {
		String sql = "SELECT DISTINCT s FROM Student s JOIN s.onlineClasses sos WHERE s.lifeCycle = :lifeCycle AND NOT EXISTS (SELECT o FROM s.onlineClasses o WHERE (o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime) AND o.status = :status)";	
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("lifeCycle", LifeCycle.LEARNING);
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
		typedQuery.setParameter("status", OnlineClass.Status.BOOKED);
		
		return typedQuery.getResultList();
	}
	
	/** wait for testing  end*/
	
	
	/**
	 * 代理商获取学生列表
	 * @param search
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public List<Student> listForAgent(
			String lifeCycle, String source, 
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo,
			String search, int start, int length) {	
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class).distinct(true);
		Root<Student> student = criteriaQuery.from(Student.class);

		//过滤出haosaishi
		List<Predicate> hPredicates = new LinkedList<Predicate>();
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi1));
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi2));
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi1a));
		Predicate hPredicate = criteriaBuilder.or(hPredicates.toArray(new Predicate[hPredicates.size()]));
		
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			//orPredicates.add(criteriaBuilder.like(student.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (lifeCycle != null&&!lifeCycle.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		if (source != null&&!source.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.valueOf(source)));
		}
		
		if (registrationDateFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(student.get(User_.registerDateTime), registrationDateFrom.getValue()));
		}
		if (registrationDateTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(User_.registerDateTime), DateTimeUtils.getNextDay(registrationDateTo.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate,hPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate,hPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate,hPredicate);
		}else{
			criteriaQuery.where(hPredicate);
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(student.get(User_.registerDateTime)));
		TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		if(length != 0) { // for work arround
			typedQuery.setMaxResults(length);
		}	
		return typedQuery.getResultList(); 
	}
	
	public long countForAgent(
			String lifeCycle, String source, 
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo, 
			String search) {
		
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Student> student = criteriaQuery.from(Student.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(student));

		//过滤出haosaishi
		List<Predicate> hPredicates = new LinkedList<Predicate>();
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi1));
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi2));
		hPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.haosaishi1a));
		Predicate hPredicate = criteriaBuilder.or(hPredicates.toArray(new Predicate[hPredicates.size()]));
		
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			//orPredicates.add(criteriaBuilder.like(student.get(User_.username), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if (lifeCycle != null&&!lifeCycle.equals("")){
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.lifeCycle), LifeCycle.valueOf(lifeCycle)));
		}
		if (source != null&&!source.equals("")) {
			andPredicates.add(criteriaBuilder.equal(student.get(Student_.source), Source.valueOf(source)));
		}
		
		if (registrationDateFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(student.get(User_.registerDateTime), registrationDateFrom.getValue()));
		}
		if (registrationDateTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(student.get(User_.registerDateTime), DateTimeUtils.getNextDay(registrationDateTo.getValue())));
		}
				
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate,hPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate,hPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate,hPredicate);
		}else{
			criteriaQuery.where(hPredicate);
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public List<Student> findByMarketingActivityId(long marketingActivityId) {
		String sql = "SELECT s FROM Student s WHERE s.marketingActivity.id = :marketingActivityId";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		
		return typedQuery.getResultList();
	}
	
	public List<Student> findByLifeCycleAndCourseTyeAndTotalClassHour(LifeCycle lifeCycle, com.vipkid.model.Course.Type type, int totalClassHour) {
		String sql = "SELECT DISTINCT s FROM Student s JOIN s.learningProgresses sls WHERE s.lifeCycle = :lifeCycle AND sls.course.type = :type AND sls.totalClassHour >= :totalClassHour";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("lifeCycle", lifeCycle);
		typedQuery.setParameter("type", type);
		typedQuery.setParameter("totalClassHour", totalClassHour);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Student> findByNameOrEnglishName(String name, int start,
			int length) {
		String sql = "SELECT s FROM Student s WHERE s.name like :name or s.englishName like :name";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql,
				Student.class);
		typedQuery.setParameter("name", "%" + name + "%");
		typedQuery.setFirstResult(start);
		if (length > 0) {
			typedQuery.setMaxResults(length);
		}
		return typedQuery.getResultList();
	}
	
	public long countFavorateTeacher(long teacherId,long studentId){
		String sql = "select count(*) from vipkid.student_favorate_teacher stf where stf.student_id=? and stf.teacher_id=? ";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, studentId);
		query.setParameter(2, teacherId);
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	public void doCollectAdd(long teacherId,long studentId){
		String sql = "INSERT vipkid.student_favorate_teacher(student_id,teacher_id) values (?,?)";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, studentId);
		query.setParameter(2, teacherId);
		query.executeUpdate();
	}
	public void doCollectRemove(long teacherId,long studentId){
		String sql = "DELETE FROM	vipkid.student_favorate_teacher WHERE	student_id =? AND teacher_id =?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, studentId);
		query.setParameter(2, teacherId);
		query.executeUpdate();
	}
	
	public void doTakeStarStudentTable(long id){
		String sql = "update student set stars = stars+5  where id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, id);
		query.executeUpdate();
	}
	
	public void doTakeStarTeacherCommentTable(long studentId,long teacherId,long onlineClassId){
		String sql = "update teacher_comment set stars = stars + 5 where teacher_id = ? and student_id = ? and online_class_id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, teacherId);
		query.setParameter(2, studentId);
		query.setParameter(3, onlineClassId);
		query.executeUpdate();
	}
	
	public Student findRecentResisteringByParentId(long parentId) {
		String sql = "SELECT s FROM Student s JOIN s.family f JOIN f.parents p WHERE p.id = :parentId ORDER BY s.registerDateTime DESC";
		TypedQuery<Student> typedQuery = entityManager.createQuery(sql, Student.class);
		typedQuery.setParameter("parentId", parentId);
		List<Student> students = typedQuery.getResultList();
		if(students.isEmpty()) {
			return null;
		}else {
			return students.get(0);
		}
	}
	
	/**
	 * CLT查看学生信息
	 * @param assignDateFrom 分配日期开始
	 * @param assignDateTo 分配日期截止
	 * @param followUpTargetDateFrom FollowUp计划日期开始  
	 * @param followUpTargetDateTo FollowUp计划日期截止
	 * @param followUpDashType 未跟过，今日待跟，今日已跟
	 * @param channel 渠道
	 * @param lifeCycle 学生的状态
	 * @param cltId CLTId
	 * @param searchStudentText 学生或家长信息
	 * @param start 开始条数
	 * @param length 选取数量
	 * @return
	 */
	public List<CltStudent> selectCltStudents(Date assignDateFrom, Date assignDateTo, Date followUpCreateDateFrom, Date followUpCreateDateTo, Date followUpTargetDateFrom, Date followUpTargetDateTo, String followUpDashType, String channel, String lifeCycle, Long cltId, String searchStudentText, String studentLevel, Integer start, Integer length){
		Map<String,Object> params = Maps.newHashMap();
		String sql = "SELECT s.id, s.name, s.englishName, s.lifeCycle, s.family, clt.name, s.assignCltTime, l.salesName,  "
				+ "l.tmkName, s.channel.sourceName, s.gender, s.birthday "
				+ "FROM Student s LEFT JOIN Leads l on s.id = l.studentId "
				+ "LEFT JOIN s.chineseLeadTeacher clt ";
		sql += "WHERE s.studentType = :studentType ";
		params.put("studentType", StudentType.NORMAL);
		Map<String, Date> conditionDateMap = new HashMap<String, Date>(); 
		if(assignDateFrom != null){
			sql += "AND s.assignCltTime >= :assignDateFrom ";
			params.put("assignDateFrom", assignDateFrom);
		}
		if(assignDateTo != null){
			sql += "AND s.assignCltTime < :assignDateTo ";
			params.put("assignDateTo", DateTimeUtils.getNextDay(assignDateTo));
		}
		if(followUpTargetDateFrom != null || followUpTargetDateTo != null){
			sql += "AND exists ("
					+ "SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory ";
			params.put("cltCategory", Category.EDUCATION);
			if(followUpTargetDateFrom != null){
				sql += "AND f.targetDateTime >= :followUpTargetDateFrom ";
				params.put("followUpTargetDateFrom", followUpTargetDateFrom);
			}
			if(followUpTargetDateTo != null){
				sql += "AND f.targetDateTime < :followUpTargetDateTo ";
				params.put("followUpTargetDateTo", followUpTargetDateTo);
			}
			sql += ")";
		}
		if(StringUtils.isNotEmpty(followUpDashType)){
			//FollowUp类型比较特殊，根据各类型会有条件拼接，是与业务需求直接挂钩的。从未跟过，意味着没有任何人创建followUp，但clt接收其他clt的学生时又只检查自己是否跟过，因为要提醒他去联系学生。今日已跟和今日未跟则只关心当前clt的学生和属于学生的FollowUp，不管是否是其他clt留下的FollowUp。 By wangbing 20150826
			if(followUpDashType.equals(CltStudent.DashType.NeverContact)){
				sql += "AND NOT EXISTS(";
				sql += "	SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory ";
				params.put("cltCategory", Category.EDUCATION);
				if(cltId != null){
					sql += "AND f.creater.id = :cltId ";
					params.put("cltId", cltId);
				}
				sql += ") ";
			}else if(followUpDashType.equals(CltStudent.DashType.NeedContact)){
				sql += "AND EXISTS(";
				sql += "    SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory and f.targetDateTime = :todayTarget and f.id= (SELECT MAX(ff.id) FROM FollowUp ff WHERE ff.stakeholder.id = s.id and ff.category = :cltCategory and ff.targetDateTime = :todayTarget) ";
				params.put("cltCategory", Category.EDUCATION);
				conditionDateMap.put("todayTarget", DateTimeUtils.getToday(0));
				sql += ") ";
			}else if(followUpDashType.equals(CltStudent.DashType.AlreadyContact)){
				sql += "AND EXISTS(";
				sql += "    SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id AND f.category = :cltCategory and f.createDateTime >= :todayCreateBegin and f.createDateTime < :todayCreateEnd ";
				params.put("cltCategory", Category.EDUCATION);
				conditionDateMap.put("todayCreateBegin", DateTimeUtils.getToday(0));
				conditionDateMap.put("todayCreateEnd", DateTimeUtils.getTomorrow(0));
				sql += ") ";
			}
		}
		if(StringUtils.isNotBlank(channel)){
			sql += "AND s.channel.sourceName = :channel ";
			params.put("channel", channel);
		}
		if(StringUtils.isNotBlank(lifeCycle)){
			sql += "AND s.lifeCycle = :lifeCycle ";
			params.put("lifeCycle", LifeCycle.valueOf(lifeCycle));
		}
		if(cltId != null){
			sql += "AND s.chineseLeadTeacher.id = :cltId ";
			params.put("cltId", cltId);
		}
		if(StringUtils.isNotBlank(searchStudentText)){
			sql += "AND (s.name like :searchStudentText or s.englishName like :searchStudentText or EXISTS (SELECT 1 FROM s.family.parents p WHERE p.name like :searchStudentText OR p.mobile like :searchStudentText))";
			params.put("searchStudentText", searchStudentText);
		}
		if(StringUtils.isNotBlank(studentLevel)){
			if(studentLevel.equals("0")){
				sql += "AND EXISTS("
					+ "		SELECT 1 FROM LearningProgress lpt WHERE lpt.student.id = s.id AND lpt.course.type = :majorType AND (lpt.totalClassHour - lpt.leftClassHour) <= 12 "
					+ ")";
				params.put("majorType", Type.MAJOR);
			}else if(studentLevel.equals("1")){
				sql += "AND EXISTS("
						+ "		SELECT 1 FROM LearningProgress lpt WHERE lpt.student.id = s.id AND lpt.course.type = :majorType AND (lpt.totalClassHour - lpt.leftClassHour) > 12 "
						+ ")";
					params.put("majorType", Type.MAJOR);
			}
		}
		sql	+= "ORDER BY s.registerDateTime DESC";
		Query query = entityManager.createQuery(sql.toString());
		DaoUtils.setQueryParameters(query, params);
		for(String key:conditionDateMap.keySet()){
			query.setParameter(key, conditionDateMap.get(key), TemporalType.DATE);
		}
		if (start != null) {
			query.setFirstResult(start);
		}
		if (length != null) {
			query.setMaxResults(length);
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		List<CltStudent> students = Lists.newArrayList();
		CltStudent student = new CltStudent();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] row : resultList) {
				student = new CltStudent();
				Long stuId = (Long) row[0];
				String stuName = (String)row[1];
				String stuEnName = (String)row[2];
				LifeCycle stuLifeCycle = (LifeCycle)row[3];
				Family stuFamily = (Family)row[4];
				List<ParentVo> parentList = Lists.newArrayList();
				if (stuFamily != null && CollectionUtils.isNotEmpty(stuFamily.getParents())) {
					for(Parent parent:stuFamily.getParents()){
						ParentVo aParent = new ParentVo();
						aParent.setId(parent.getId());
						aParent.setMobile(parent.getMobile());
						aParent.setName(parent.getName());
						parentList.add(aParent);
					}
				}
				String cltName = (String)row[5];
				Date cltAssignDate = (Date)row[6];
				String salesName = (String)row[7];
				String tmkName = (String)row[8];
				String sourceName = (String)row[9];
				String majorRemain = selectLearningProgessRemainMajor(stuId+"");
				String gender = row[10]==null?"":row[10].toString();
				Date birthday = (Date)row[11];
				student.setId(stuId);
				student.setName(stuName);
				student.setEnglishName(stuEnName);
				student.setGender(gender);
				student.setBirthday(birthday);
				student.setLifeCycle(stuLifeCycle);
				student.setParents(parentList);
				student.setCltName(cltName);
				student.setCltAssignDate(cltAssignDate);
				student.setSalesName(salesName);
				student.setTmkName(tmkName);
				student.setChannelName(sourceName);
				student.setRemainMajorClass(majorRemain);
				student.setLastFollowUp(this.selectLatestCLTFollowUp(student.getId()+""));
				students.add(student);
			}
		}
		return students;
	}

	/**
	 * CLT查看学生信息数目
	 * @param assignDateFrom 分配日期开始
	 * @param assignDateTo 分配日期截止
	 * @param followUpTargetDateFrom FollowUp计划日期开始  
	 * @param followUpTargetDateTo FollowUp计划日期截止
	 * @param followUpDashType 未跟过，今日待跟，今日已跟
	 * @param channel 渠道
	 * @param lifeCycle 学生的状态
	 * @param cltId CLTId
	 * @param searchStudentText 学生或家长信息
	 * @param start 开始条数
	 * @param length 选取数量
	 * @return
	 */
	public long selectCltStudentsCount(Date assignDateFrom, Date assignDateTo, Date followUpCreateDateFrom, Date followUpCreateDateTo, Date followUpTargetDateFrom, Date followUpTargetDateTo, String followUpDashType, String channel, String lifeCycle, Long cltId, String searchStudentText, String studentLevel){
		Map<String,Object> params = Maps.newHashMap();
		String sql = "SELECT count(1) "
				+ "FROM Student s LEFT JOIN Leads l on s.id = l.studentId "
				+ "LEFT JOIN s.chineseLeadTeacher clt ";
		sql += "WHERE s.studentType = :studentType ";
		params.put("studentType", StudentType.NORMAL);
		Map<String, Date> conditionDateMap = new HashMap<String, Date>(); 
		if(assignDateFrom != null){
			sql += "AND s.assignCltTime >= :assignDateFrom ";
			params.put("assignDateFrom", assignDateFrom);
		}
		if(assignDateTo != null){
			sql += "AND s.assignCltTime < :assignDateTo ";
			params.put("assignDateTo", DateTimeUtils.getNextDay(assignDateTo));
		}
		if(followUpTargetDateFrom != null || followUpTargetDateTo != null){
			sql += "AND exists ("
					+ "SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory ";
			params.put("cltCategory", Category.EDUCATION);
			if(followUpTargetDateFrom != null){
				sql += "AND f.targetDateTime >= :followUpTargetDateFrom ";
				params.put("followUpTargetDateFrom", followUpTargetDateFrom);
			}
			if(followUpTargetDateTo != null){
				sql += "AND f.targetDateTime < :followUpTargetDateTo ";
				params.put("followUpTargetDateTo", followUpTargetDateTo);
			}
			sql += ")";
		}
		if(StringUtils.isNotEmpty(followUpDashType)){
			//FollowUp类型比较特殊，根据各类型会有条件拼接，是与业务需求直接挂钩的。从未跟过，意味着没有任何人创建followUp，但clt接收其他clt的学生时又只检查自己是否跟过，因为要提醒他去联系学生。今日已跟和今日未跟则只关心当前clt的学生和属于学生的FollowUp，不管是否是其他clt留下的FollowUp。 By wangbing 20150826
			if(followUpDashType.equals(CltStudent.DashType.NeverContact)){
				sql += "AND NOT EXISTS(";
				sql += "	SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory ";
				params.put("cltCategory", Category.EDUCATION);
				if(cltId != null){
					sql += "AND f.creater.id = :cltId ";
					params.put("cltId", cltId);
				}
				sql += ") ";
			}else if(followUpDashType.equals(CltStudent.DashType.NeedContact)){
				sql += "AND EXISTS(";
				sql += "    SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id and f.category = :cltCategory and f.targetDateTime = :todayTarget and f.id= (SELECT MAX(ff.id) FROM FollowUp ff WHERE ff.stakeholder.id = s.id and ff.category = :cltCategory and ff.targetDateTime = :todayTarget) ";
				params.put("cltCategory", Category.EDUCATION);
				conditionDateMap.put("todayTarget", DateTimeUtils.getToday(0));
				sql += ") ";
			}else if(followUpDashType.equals(CltStudent.DashType.AlreadyContact)){
				sql += "AND EXISTS(";
				sql += "    SELECT 1 FROM FollowUp f WHERE f.stakeholder.id = s.id AND f.category = :cltCategory and f.createDateTime >= :todayCreateBegin and f.createDateTime < :todayCreateEnd ";
				params.put("cltCategory", Category.EDUCATION);
				conditionDateMap.put("todayCreateBegin", DateTimeUtils.getToday(0));
				conditionDateMap.put("todayCreateEnd", DateTimeUtils.getTomorrow(0));
				sql += ") ";
			}
		}
		if(StringUtils.isNotBlank(channel)){
			sql += "AND s.channel.sourceName = :channel ";
			params.put("channel", channel);
		}
		if(StringUtils.isNotBlank(lifeCycle)){
			sql += "AND s.lifeCycle = :lifeCycle ";
			params.put("lifeCycle", LifeCycle.valueOf(lifeCycle));
		}
		if(cltId != null){
			sql += "AND s.chineseLeadTeacher.id = :cltId ";
			params.put("cltId", cltId);
		}
		if(StringUtils.isNotBlank(searchStudentText)){
			sql += "AND (s.name like :searchStudentText or s.englishName like :searchStudentText or EXISTS (SELECT 1 FROM s.family.parents p WHERE p.name like :searchStudentText OR p.mobile like :searchStudentText))";
			params.put("searchStudentText", searchStudentText);
		}
		if(StringUtils.isNotBlank(studentLevel)){
			if(studentLevel.equals("0")){
				sql += "AND EXISTS("
					+ "		SELECT 1 FROM LearningProgress lpt WHERE lpt.student.id = s.id AND lpt.course.type = :majorType AND (lpt.totalClassHour - lpt.leftClassHour) <= 12 "
					+ ")";
				params.put("majorType", Type.MAJOR);
			}else if(studentLevel.equals("1")){
				sql += "AND EXISTS("
						+ "		SELECT 1 FROM LearningProgress lpt WHERE lpt.student.id = s.id AND lpt.course.type = :majorType AND (lpt.totalClassHour - lpt.leftClassHour) > 12 "
						+ ")";
					params.put("majorType", Type.MAJOR);
			}
		}
		Query query = entityManager.createQuery(sql.toString());
		DaoUtils.setQueryParameters(query, params);
		for(String key:conditionDateMap.keySet()){
			query.setParameter(key, conditionDateMap.get(key), TemporalType.DATE);
		}
		return (long)query.getSingleResult();
	}
	
	/**
	 * 根据学生Id查询CLT留下的最新FollowUp
	 * @param stuId
	 * @return
	 */
	public FollowUpVo selectLatestCLTFollowUp(String stuId){
		String sql = "SELECT f FROM FollowUp f WHERE f.id = (SELECT max(f2.id) FROM FollowUp f2 WHERE f2.category =:category AND f2.stakeholder.id =:stuId ) ";
		TypedQuery<FollowUp> typedQuery = entityManager.createQuery(sql, FollowUp.class);
		typedQuery.setParameter("category", Category.EDUCATION);
		typedQuery.setParameter("stuId", new Integer(stuId));
		List<FollowUp> students = typedQuery.getResultList();
		if(students.isEmpty()) {
			return null;
		}else {
			FollowUp aF = students.get(0);
			FollowUpVo aVo = new FollowUpVo();
			aVo.setId(aF.getId());
			aVo.setCreateDateTime(aF.getCreateDateTime());
			aVo.setCreatorName(aF.getCreater().getName());
			aVo.setTargetDateTime(aF.getTargetDateTime());
			aVo.setContent(aF.getContent());
			return aVo;
		}
	}
	
	public String selectLearningProgessRemainMajor(String stuId){
		String sql = "SELECT f FROM LearningProgress f WHERE f.student.id =:stuId AND f.course.type = :majorType ";
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("majorType", Type.MAJOR);
		typedQuery.setParameter("stuId", new Integer(stuId));
		List<LearningProgress> students = typedQuery.getResultList();
		if(students.isEmpty()) {
			return "";
		}else {
			return students.get(0).getLeftClassHour()+"";
		}
	}
}
