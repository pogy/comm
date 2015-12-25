package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Audit;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Audit_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.util.DateTimeUtils;

@Repository
public class AuditRepository extends BaseRepository<Audit> {
	private Logger logger = LoggerFactory.getLogger(AuditRepository.class);
	
	public AuditRepository() {
		super(Audit.class);
	}
	
	public Audit findByUndoId(long undoId){
		String sql = "SELECT a FROM Audit a WHERE a.undoId = :undoId ORDER BY a.executeDateTime desc";
		logger.info("findByUndoId sql={}", sql);
		TypedQuery<Audit> typedQuery = entityManager.createQuery(sql, Audit.class);
		typedQuery.setParameter("undoId", undoId);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults(1);
	    return typedQuery.getSingleResult();
	}
	
	public List<Audit> list(String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo, String level, String category, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Audit> criteriaQuery = criteriaBuilder.createQuery(Audit.class);
		Root<Audit> audit = criteriaQuery.from(Audit.class);
		
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(audit.get(Audit_.operator), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(audit.get(Audit_.operation), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (level != null && !level.equals("")) {
			andPredicates.add(criteriaBuilder.equal(audit.get(Audit_.level), Level.valueOf(level)));
		}
		if (category != null && !category.equals("")) {
			andPredicates.add(criteriaBuilder.equal(audit.get(Audit_.category), Category.valueOf(category)));
		}
		if (executeDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(audit.get(Audit_.executeDateTime), executeDateTimeFrom.getValue()));
		}
		if (executeDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(audit.get(Audit_.executeDateTime), DateTimeUtils.getNextDay(executeDateTimeTo.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!orPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate);
		}
        if ( !andPredicates.isEmpty()) {
            criteriaQuery.where(andPredicate);
        }

		criteriaQuery.orderBy(criteriaBuilder.desc(audit.get(Audit_.executeDateTime)));
		TypedQuery<Audit> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}

	public long count(String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo, String level, String category) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Audit> audit = criteriaQuery.from(Audit.class);
		criteriaQuery.select(criteriaBuilder.count(audit));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(audit.get(Audit_.operator), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(audit.get(Audit_.operation), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (level != null && !level.equals("")) {
			andPredicates.add(criteriaBuilder.equal(audit.get(Audit_.level), Level.valueOf(level)));
		}
		if (category != null && !category.equals("")) {
			andPredicates.add(criteriaBuilder.equal(audit.get(Audit_.category), Category.valueOf(category)));
		}
		if (executeDateTimeFrom != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(audit.get(Audit_.executeDateTime), executeDateTimeFrom.getValue()));
		}
		if (executeDateTimeTo != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(audit.get(Audit_.executeDateTime), DateTimeUtils.getNextDay(executeDateTimeTo.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
        if(!orPredicates.isEmpty()) {
            criteriaQuery.where(orPredicate);
        }
        if ( !andPredicates.isEmpty()) {
            criteriaQuery.where(andPredicate);
        }
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

}
