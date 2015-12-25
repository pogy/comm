/**
 * 
 */
package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.StudentExam;
import com.vipkid.model.StudentExam_;
import com.vipkid.model.Student_;
import com.vipkid.model.User_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.util.DateTimeUtils;

/**
 * @author vipkid
 *
 */
@Repository
public class StudentExamRepository extends BaseRepository<StudentExam> {

	// log
	private Logger logger = LoggerFactory.getLogger(StudentExamRepository.class);

	public StudentExamRepository() {
		super(StudentExam.class);
	}

	//
	// 根据StudentExam id获取记录
	public StudentExam findById(long id) {
		logger.info("StudentExam findById for {}", id);
		String sql = "SELECT a FROM StudentExam a WHERE a.id = :id  ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);
		typedQuery.setParameter("id", id);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults(1);

		List<StudentExam> examList = typedQuery.getResultList();
		if (examList.size()>0) {
			return examList.get(0);
		}
		return null;
	}

	// 根据StudentExam id获取记录
	public StudentExam findByUUId(String uuid) {
		logger.info("StudentExam findByUUId for {}", uuid);
		String sql = "SELECT a FROM StudentExam a WHERE a.recordUuid = :uuid  ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);
		typedQuery.setParameter("uuid", uuid);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults(1);

		List<StudentExam> examList = typedQuery.getResultList();
		if (examList.size()>0) {
			return examList.get(0);
		}
		return null;
	}

	public List<StudentExam> findByStudentId(long studentId) {
		String sql = "SELECT a FROM StudentExam a WHERE a.student.id = :studentId  and a.status = 1 ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);
		typedQuery.setParameter("studentId", studentId);

		return typedQuery.getResultList();
	}

	//
	public List<StudentExam> findByFamilyId(long familyId) {
		String sql = "SELECT a FROM StudentExam a WHERE a.familyId = :familyId  and a.status = 1 ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);
		typedQuery.setParameter("familyId", familyId);

		return typedQuery.getResultList();
	}

	public StudentExam findLastByStudentId(long studentId) {
		String sql = "SELECT a FROM StudentExam a WHERE a.student.id = :studentId  and a.status = 1 ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class).setMaxResults(1);
		typedQuery.setParameter("studentId", studentId);

		List<StudentExam> examList = typedQuery.getResultList();
		if (examList.size()>0) {
			return examList.get(0);
		}
		return null;
	}

	/**
	 * 
	 * @param studentId
	 * @return
	 */
	public long count(long studentId) {
		//
		long nCnt = 0;
		String sql = "SELECT count(1) FROM StudentExam a WHERE a.student.id = :studentId  and a.status = 1";
		TypedQuery<Long> typedQuery = entityManager
				.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);

		nCnt = typedQuery.getSingleResult();
		return nCnt;
	}

	/**
	 * 
	 * @param studentId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<StudentExam> list(Long studentId, int start, int length) {
		//
		String sql = "SELECT a FROM StudentExam a WHERE a.student.id = :studentId  and a.status = 1 ORDER BY a.id desc"; 
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);

		typedQuery.setParameter("studentId", studentId);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);

		List<StudentExam> studentExamList = typedQuery.getResultList();
		return studentExamList;
	}

	/**
	 * 
	 * @param familyId
	 * @return
	 */
	public long countByFamily(long familyId) {
		//
		long nCnt = 0;
		String sql = "SELECT count(1) FROM StudentExam a WHERE a.familyId = :familyId and a.status = 1";
		TypedQuery<Long> typedQuery = entityManager
				.createQuery(sql, Long.class);
		typedQuery.setParameter("familyId", familyId);

		nCnt = typedQuery.getSingleResult();
		return nCnt;
	}

	public List<StudentExam> listByFamily(Long familyId, int start, int length) {
		//
		String sql = "SELECT a FROM StudentExam a WHERE a.familyId = :familyId  and a.status = 1 ORDER BY a.id desc";
		TypedQuery<StudentExam> typedQuery = entityManager.createQuery(sql,
				StudentExam.class);

		typedQuery.setParameter("familyId", familyId);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);

		List<StudentExam> studentExamList = typedQuery.getResultList();
		return studentExamList;
	}

	public long countBySearch(String search, DateTimeParam executeDateTimeFrom,
			DateTimeParam executeDateTimeTo,long salesId, long cltId) {
		//
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder
				.createQuery(Long.class);

		Root<StudentExam> studentExam = criteriaQuery.from(StudentExam.class);
		Join<StudentExam, Student> student = studentExam.join(
				StudentExam_.student, JoinType.LEFT);

		criteriaQuery.select(criteriaBuilder.count(studentExam));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(
					student.get(Student_.englishName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%"
					+ search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates
				.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (executeDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
					studentExam.get(StudentExam_.createDatetime),
					executeDateTimeFrom.getValue()));
		}
		if (executeDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(
					studentExam.get(StudentExam_.createDatetime),
					DateTimeUtils.getNextDay(executeDateTimeTo.getValue())));
		}
//		andPredicates.add(criteriaBuilder.isNotNull(studentExam
//				.get(StudentExam_.endDatetime)));
		andPredicates.add(criteriaBuilder.equal(studentExam
				.get(StudentExam_.status),1));
		// sales and clt join
		if (salesId>0) {
			Join<Student, Staff> sales = student.join(Student_.sales,JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id),salesId));
		}
		if (cltId>0) {
			Join<Student, Staff> chineseLeadTeacher = student.join(Student_.chineseLeadTeacher, JoinType.LEFT);	
			andPredicates.add(criteriaBuilder.equal(chineseLeadTeacher.get(User_.id),cltId));
		}
		
		// 
		Predicate andPredicate = criteriaBuilder.and(andPredicates
				.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if (!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		} else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	public List<StudentExam> listBySearch(String search,
			DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo,long salesId, long cltId,
			int start, int length) {
		//
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<StudentExam> criteriaQuery = criteriaBuilder
				.createQuery(StudentExam.class);

		Root<StudentExam> studentExam = criteriaQuery.from(StudentExam.class);
		Join<StudentExam, Student> student = studentExam.join(
				StudentExam_.student, JoinType.LEFT);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(
					student.get(Student_.englishName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%"
					+ search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates
				.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (executeDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
					studentExam.get(StudentExam_.createDatetime),
					executeDateTimeFrom.getValue()));
		}
		if (executeDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(
					studentExam.get(StudentExam_.createDatetime),
					DateTimeUtils.getNextDay(executeDateTimeTo.getValue())));
		}
//		andPredicates.add(criteriaBuilder.isNotNull(studentExam
//				.get(StudentExam_.endDatetime)));

		andPredicates.add(criteriaBuilder.equal(studentExam
				.get(StudentExam_.status),1));
		
		// sales and clt join
		if (salesId>0) {
			Join<Student, Staff> sales = student.join(Student_.sales,JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(sales.get(User_.id),salesId));
		}
		if (cltId>0) {
			Join<Student, Staff> chineseLeadTeacher = student.join(Student_.chineseLeadTeacher, JoinType.LEFT);	
			andPredicates.add(criteriaBuilder.equal(chineseLeadTeacher.get(User_.id),cltId));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates
				.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if (!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		} else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(studentExam
				.get(StudentExam_.id)));

		TypedQuery<StudentExam> typedQuery = entityManager
				.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);

		List<StudentExam> studentExamList = typedQuery.getResultList();
		return studentExamList;
	}

	/**
	 * 清理指定学生的未完成记录
	 * 
	 * @param studentId
	 */
	public void clearForStudent(long studentId) {
		return;
	}

}
