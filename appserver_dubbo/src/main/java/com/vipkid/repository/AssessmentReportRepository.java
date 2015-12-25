package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.vipkid.model.AssessmentReport;
import com.vipkid.model.AssessmentReport_;
import com.vipkid.model.Student;
import com.vipkid.model.User_;

@Repository
public class AssessmentReportRepository extends BaseRepository<AssessmentReport> {

	public AssessmentReportRepository() {
		super(AssessmentReport.class);
	}
	
	public List<AssessmentReport> list(String search, Long studentId, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AssessmentReport> criteriaQuery = criteriaBuilder.createQuery(AssessmentReport.class);
		Root<AssessmentReport> assessmentReport = criteriaQuery.from(AssessmentReport.class);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			Join<AssessmentReport, Student> student = assessmentReport.join(AssessmentReport_.student, JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (studentId != null) {
			Join<AssessmentReport, Student> student = assessmentReport.join(AssessmentReport_.student, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
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
		
		criteriaQuery.orderBy(criteriaBuilder.desc(assessmentReport.get(AssessmentReport_.createDateTime)));
		TypedQuery<AssessmentReport> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(String search, Long studentId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<AssessmentReport> assessmentReport = criteriaQuery.from(AssessmentReport.class);
		criteriaQuery.select(criteriaBuilder.count(assessmentReport));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
		Join<AssessmentReport, Student> student = assessmentReport.join(AssessmentReport_.student, JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (studentId != null) {
			Join<AssessmentReport, Student> student = assessmentReport.join(AssessmentReport_.student, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
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

	public List<AssessmentReport> findByStudentId(long findByStudentId) {
		String sql = "SELECT ar FROM AssessmentReport ar WHERE ar.student.id = :findByStudentId ORDER BY ar.createDateTime DESC";
		TypedQuery<AssessmentReport> typedQuery = entityManager.createQuery(sql, AssessmentReport.class);
		typedQuery.setParameter("findByStudentId", findByStudentId);
	    
		try {
			return typedQuery.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}
