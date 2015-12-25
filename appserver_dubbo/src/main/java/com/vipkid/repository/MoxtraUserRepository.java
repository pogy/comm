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

import com.vipkid.model.MoxtraUser;
import com.vipkid.model.MoxtraUser_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.util.DateTimeUtils;

@Repository
public class MoxtraUserRepository extends BaseRepository<MoxtraUser> {
	
	private Logger logger = LoggerFactory.getLogger(MoxtraUser.class);
	
	public MoxtraUserRepository(){
		super(MoxtraUser.class);
	}
	
	public List<MoxtraUser> findByUsage(boolean inUse) {
		String sql = "SELECT mu FROM MoxtraUser mu WHERE mu.inUse = :inUse";
		logger.debug("The sql = {}", sql);
		TypedQuery<MoxtraUser> typedQuery = entityManager.createQuery(sql, MoxtraUser.class);
		typedQuery.setParameter("inUse", inUse);
	    return typedQuery.getResultList();
	}
	
	public MoxtraUser findByVIPKIDUserId(long vipkidUserId) {
		String sql = "SELECT mu FROM MoxtraUser mu WHERE mu.vipkidUser.id = :vipkidUserId";
		logger.debug("The sql = {}", sql);
		TypedQuery<MoxtraUser> typedQuery = entityManager.createQuery(sql, MoxtraUser.class);
		typedQuery.setParameter("vipkidUserId", vipkidUserId);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public long totalCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<MoxtraUser> moxtraUser = criteriaQuery.from(MoxtraUser.class);
		criteriaQuery.select(criteriaBuilder.count(moxtraUser));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public List<MoxtraUser> list(Boolean inUse, DateTimeParam fromDate, DateTimeParam toDate, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MoxtraUser> criteriaQuery = criteriaBuilder.createQuery(MoxtraUser.class);
		Root<MoxtraUser> moxtraUser = criteriaQuery.from(MoxtraUser.class);

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (inUse != null) {
			andPredicates.add(criteriaBuilder.equal(moxtraUser.get(MoxtraUser_.inUse), inUse));
		}
		if (fromDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(moxtraUser.get(MoxtraUser_.createDateTime), fromDate.getValue()));
		}
		if (toDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(moxtraUser.get(MoxtraUser_.createDateTime), DateTimeUtils.getNextDay(toDate.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		criteriaQuery.where(andPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(moxtraUser.get(MoxtraUser_.createDateTime)));
		TypedQuery<MoxtraUser> typedQuery = entityManager.createQuery(criteriaQuery);
		if (start != null) {
			typedQuery.setFirstResult(start);
		}
		if (length != null) {
			typedQuery.setMaxResults(length);
		}
		return typedQuery.getResultList();
	}
	
	public long count(Boolean inUse, DateTimeParam fromDate, DateTimeParam toDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<MoxtraUser> moxtraUser = criteriaQuery.from(MoxtraUser.class);
		criteriaQuery.select(criteriaBuilder.count(moxtraUser)); 

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (inUse != null) {
			andPredicates.add(criteriaBuilder.equal(moxtraUser.get(MoxtraUser_.inUse), inUse));
		}
		if (fromDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(moxtraUser.get(MoxtraUser_.createDateTime), fromDate.getValue()));
		}
		if (toDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(moxtraUser.get(MoxtraUser_.createDateTime), DateTimeUtils.getNextDay(toDate.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		criteriaQuery.where(andPredicate);

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
}
