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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Family;
import com.vipkid.model.Family_;
import com.vipkid.model.Parent;
import com.vipkid.model.Parent_;
import com.vipkid.model.Student;
import com.vipkid.model.Student_;
import com.vipkid.model.User_;

@Repository
public class FamilyRepository extends BaseRepository<Family> {

	private Logger logger = LoggerFactory.getLogger(FamilyRepository.class);
	
	public FamilyRepository() {
		super(Family.class);
	}

	public List<Family> list(String search, String province, String city, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Family> criteriaQuery = criteriaBuilder.createQuery(Family.class).distinct(true);
		Root<Family> familyRoot = criteriaQuery.from(Family.class);
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			Join<Family, Student> student = familyRoot.join(Family_.students, JoinType.LEFT);
			Join<Family, Parent> parent = familyRoot.join(Family_.parents, JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parent.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parent.get(Parent_.mobile), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (province != null) {
			andPredicates.add(criteriaBuilder.equal(familyRoot.get(Family_.province), province));
		}
		if (city != null) {
			andPredicates.add(criteriaBuilder.equal(familyRoot.get(Family_.city), city));
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
		criteriaQuery.orderBy(criteriaBuilder.desc(familyRoot.get(Family_.createDateTime)));
		
		criteriaQuery.orderBy(criteriaBuilder.desc(familyRoot.get(Family_.createDateTime)));
		TypedQuery<Family> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(String search, String province, String city) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Family> family = criteriaQuery.from(Family.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(family));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			Join<Family, Student> student = family.join(Family_.students, JoinType.LEFT);
			Join<Family, Parent> parent = family.join(Family_.parents, JoinType.LEFT);
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(parent.get(User_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (province != null) {
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.province), province));
		}
		if (city != null) {
			andPredicates.add(criteriaBuilder.equal(family.get(Family_.city), city));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!orPredicates.isEmpty() || !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		}
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public Family findByStudentId(long studentId) {	
		String sql = "SELECT f FROM Family f JOIN f.students fs WHERE fs.id = :studentId";
		TypedQuery<Family> typedQuery = entityManager.createQuery(sql, Family.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getSingleResult();
	}
	
	public Family findByInvitationId(String invitationId) {
		String sql = "SELECT f FROM Family f WHERE f.invitationId = :invitationId";
		logger.info("Execute findByInvitationId sql={}", sql);
		TypedQuery<Family> typedQuery = entityManager.createQuery(sql, Family.class);
		typedQuery.setParameter("invitationId", invitationId);
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
