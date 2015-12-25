package com.vipkid.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Broadcast;
import com.vipkid.model.Broadcast_;

@Repository
public class BroadcastRepository extends BaseRepository<Broadcast> {

	public BroadcastRepository() {
		super(Broadcast.class);
	}

	public List<Broadcast> list(String search, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Broadcast> criteriaQuery = criteriaBuilder.createQuery(Broadcast.class);
		Root<Broadcast> activity = criteriaQuery.from(Broadcast.class);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(activity.get(Broadcast_.title), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(activity.get(Broadcast_.message), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(activity.get(Broadcast_.archived));
		
		criteriaQuery.where(orPredicate, andPredicate, archivedPredicate);
		TypedQuery<Broadcast> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}

	public long count(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Broadcast> activity = criteriaQuery.from(Broadcast.class);
		criteriaQuery.select(criteriaBuilder.count(activity));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(activity.get(Broadcast_.title), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(activity.get(Broadcast_.message), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// archived predicate
		Predicate archivedPredicate = criteriaBuilder.isFalse(activity.get(Broadcast_.archived));

		criteriaQuery.where(orPredicate, andPredicate, archivedPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	public void archive(Broadcast activity) {
		activity.setArchived(true);
		entityManager.merge(activity);
	}

	public List<Broadcast> findByDate(Date date) {
		String sql = "SELECT b FROM Broadcast b WHERE b.sendDateTime = :date";
		TypedQuery<Broadcast> query = entityManager.createQuery(sql, Broadcast.class);
		query.setParameter("date", date);

		List<Broadcast> broadcastList = query.getResultList();
		if (broadcastList.isEmpty()) {
			return null;
		} else {
			return broadcastList;
		}
	}
	
}
