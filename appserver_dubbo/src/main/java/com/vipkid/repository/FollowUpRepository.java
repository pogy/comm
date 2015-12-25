package com.vipkid.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.model.FollowUp;
import com.vipkid.model.FollowUp.Category;
import com.vipkid.model.FollowUp_;
import com.vipkid.model.Leads;
import com.vipkid.model.Student;
import com.vipkid.model.User_;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;

@Repository
public class FollowUpRepository extends BaseRepository<FollowUp> {
	
	public List<FollowUp> findByStudentId(long studentId){
		String sql = "SELECT f FROM FollowUp f WHERE f.stakeholder.id = :studentId";
		TypedQuery<FollowUp> typedQuery = entityManager.createQuery(sql, FollowUp.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}

	public FollowUpRepository() {
		super(FollowUp.class);
	}
	
	public List<FollowUp> list(long studentId, Category category, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FollowUp> criteriaQuery = criteriaBuilder.createQuery(FollowUp.class);
		Root<FollowUp> followUp = criteriaQuery.from(FollowUp.class);

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (studentId != 0){
			Join<FollowUp, Student> student = followUp.join(FollowUp_.stakeholder, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
		}
		if (category != null) {
			andPredicates.add(criteriaBuilder.equal(followUp.get(FollowUp_.category), category));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!andPredicates.isEmpty()) {
			criteriaQuery.where(andPredicate);
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(followUp.get(FollowUp_.createDateTime)));
		TypedQuery<FollowUp> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(long studentId, Category category) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<FollowUp> followUp = criteriaQuery.from(FollowUp.class);
		criteriaQuery.select(criteriaBuilder.count(followUp));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (studentId != 0){
			Join<FollowUp, Student> student = followUp.join(FollowUp_.stakeholder, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(student.get(User_.id), studentId));
		}
		if (category != null) {
			andPredicates.add(criteriaBuilder.equal(followUp.get(FollowUp_.category), category));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!andPredicates.isEmpty()) {
			criteriaQuery.where(andPredicate);
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
//	public long countForLeads(Leads.OwnerType ownerType, List<Long> staffIds, Date targetDateTimeFrom, Date targetDateTimeTo,Date createDateTimeFrom, Date createDateTimeTo, boolean isFollowed) {
//		long count = 0;
//		StringBuffer jpql = new StringBuffer();
//		Map<String,Object> params = Maps.newHashMap();
//		jpql.append("select count(distinct followUp.stakeholder.id) from FollowUp followUp,Leads leads where followUp.stakeholder.id = leads.studentId ");
//		
//		//staffIds
//		if (CollectionUtils.isNotEmpty(staffIds)) {
//			if (staffIds.size() > 1) {
//				if (ownerType == Leads.OwnerType.STAFF_SALES) {
//					jpql.append(" and leads.salesId in :staffIds");
//				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
//					jpql.append(" and leads.tmkId in :staffIds");
//					
//				}
//				jpql.append(" and followUp.creater.id in :staffIds");
//				params.put("staffIds", staffIds);
//			} else {
//				if (ownerType == Leads.OwnerType.STAFF_SALES) {
//					jpql.append(" and leads.salesId = :staffId");
//				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
//					jpql.append(" and leads.tmkId = :staffId");
//					
//				}
//				jpql.append(" and followUp.creater.id = :staffId");
//				params.put("staffId", staffIds.get(0));
//				
//			}
//		}
//		
//		// createDateTimeFrom/createDateTimeTo
//		if (createDateTimeFrom != null) {
//			jpql.append(" and followUp.createDateTime >= :createDateTimeFrom");
//			params.put("createDateTimeFrom", createDateTimeFrom);
//		}
//		if (createDateTimeTo != null) {
//			createDateTimeTo = DateTimeUtils.getNextDay(createDateTimeTo);
//			jpql.append(" and followUp.createDateTime < :createDateTimeTo");
//			params.put("createDateTimeTo", createDateTimeTo);
//		}
//		
//		// targetDateTimeFrom/targetDateTimeTo
//		if (targetDateTimeFrom != null) {
//			jpql.append(" and followUp.targetDateTime >= :targetDateTimeFrom");
//			params.put("targetDateTimeFrom", targetDateTimeFrom);
//		}
//		if (targetDateTimeTo != null) {
//			targetDateTimeTo = DateTimeUtils.getNextDay(targetDateTimeTo);
//			jpql.append(" and followUp.targetDateTime < :targetDateTimeTo");
//			params.put("targetDateTimeTo", targetDateTimeTo);
//		}
//		
//		Query query = entityManager.createQuery(jpql.toString());
//		DaoUtils.setQueryParameters(query, params);
//		count = (Long)query.getSingleResult();
//		return count;
//	}
	
	
	public long countForLeads(Leads.OwnerType ownerType, List<Long> staffIds, Date targetDateTimeFrom, Date targetDateTimeTo,Date createDateTimeFrom, Date createDateTimeTo,Boolean needFollowup) {
		long totalCount = 0;
		if (CollectionUtils.isNotEmpty(staffIds)) {
			for (Long staffId : staffIds) {
				long count = this.countForLeads(ownerType, staffId, targetDateTimeFrom, targetDateTimeTo, createDateTimeFrom, createDateTimeTo, needFollowup);
				totalCount += count;
			}
		}
		return totalCount;
	}
	
	private long countForLeads(Leads.OwnerType ownerType,Long staffId, Date targetDateTimeFrom, Date targetDateTimeTo,Date createDateTimeFrom, Date createDateTimeTo, Boolean needFollowup) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(distinct followUp.stakeholder.id) from FollowUp followUp,Leads leads where followUp.stakeholder.id = leads.studentId ");
		
		//staffId
		if (staffId != null) {
			if (ownerType == Leads.OwnerType.STAFF_SALES) {
				jpql.append(" and leads.salesId = :staffId");
			} else if (ownerType == Leads.OwnerType.STAFF_TMK){
				jpql.append(" and leads.tmkId = :staffId");
				
			}
			jpql.append(" and followUp.creater.id = :staffId");
			params.put("staffId", staffId);
			
		
		}
		
		// createDateTimeFrom/createDateTimeTo
		if (createDateTimeFrom != null) {
			jpql.append(" and followUp.createDateTime >= :createDateTimeFrom");
			params.put("createDateTimeFrom", createDateTimeFrom);
		}
		if (createDateTimeTo != null) {
			createDateTimeTo = DateTimeUtils.getNextDay(createDateTimeTo);
			jpql.append(" and followUp.createDateTime < :createDateTimeTo");
			params.put("createDateTimeTo", createDateTimeTo);
		}
		
		// targetDateTimeFrom/targetDateTimeTo
		if (targetDateTimeFrom != null) {
			jpql.append(" and followUp.targetDateTime >= :targetDateTimeFrom");
			params.put("targetDateTimeFrom", targetDateTimeFrom);
		}
		if (targetDateTimeTo != null) {
			targetDateTimeTo = DateTimeUtils.getNextDay(targetDateTimeTo);
			jpql.append(" and followUp.targetDateTime < :targetDateTimeTo");
			params.put("targetDateTimeTo", targetDateTimeTo);
		}
		
		//needFollowup
		if (needFollowup != null && needFollowup) {
			jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id = :createrId) ");
			params.put("createrId", staffId);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}
}
