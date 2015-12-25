package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Partner;
import com.vipkid.model.Partner.Type;
import com.vipkid.model.Partner_;
import com.vipkid.model.User.Status;
import com.vipkid.model.User_;

@Repository
public class PartnerRepository extends BaseRepository<Partner> {
	


	public PartnerRepository() {
		super(Partner.class);
	}

	public List<Partner>list( String search,Status status,String email,String username, Integer start, Integer length, Type type){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Partner> criteriaQuery = criteriaBuilder.createQuery(Partner.class);
		Root<Partner> partner = criteriaQuery.from(Partner.class);
		
		
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		
		if (email != null) {
			andPredicates.add(criteriaBuilder.like(partner.get(Partner_.email), "%" + email + "%"));
		}
		if (search != null) {
			andPredicates.add(criteriaBuilder.like(partner.get(User_.name), "%" + search + "%"));
		}
		if (type != null) {
			andPredicates.add(criteriaBuilder.equal(partner.get(Partner_.type), type));
		}
		if (username != null) {
			andPredicates.add(criteriaBuilder.like(partner.get(User_.username), "%" + username + "%"));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(partner.get(User_.status),  status ));
		}
		
		
        Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		criteriaQuery.where(andPredicate);
		
		//criteriaQuery.orderBy(criteriaBuilder.desc(partner.get(User_.registerDateTime)));
		TypedQuery<Partner> typedQuery = entityManager.createQuery(criteriaQuery);
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

	public long count( String search,Status status,String email,String username, int start, int length) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Partner> partner = criteriaQuery.from(Partner.class);
		criteriaQuery.select(criteriaBuilder.count(partner));
		
		
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		
		if (email != null) {
			andPredicates.add(criteriaBuilder.like(partner.get(Partner_.email), "%" + email + "%"));
		}
		if (username != null) {
			andPredicates.add(criteriaBuilder.like(partner.get(User_.username), "%" + username + "%"));
		}
		if (search != null) {	//name
			andPredicates.add(criteriaBuilder.like(partner.get(User_.name), "%" + search + "%"));
		}
		
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(partner.get(User_.status), status));
		}
		
        Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		criteriaQuery.where(andPredicate);
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();	
		
	
	}
	
	public long totalCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Partner> partner = criteriaQuery.from(Partner.class);
		criteriaQuery.select(criteriaBuilder.count(partner));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	
	




}
