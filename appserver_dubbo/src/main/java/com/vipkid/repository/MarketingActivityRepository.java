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

import com.vipkid.model.Channel;
import com.vipkid.model.MarketingActivity;
import com.vipkid.model.MarketingActivity.Type;
import com.vipkid.model.MarketingActivity_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.util.DateTimeUtils;

@Repository
public class MarketingActivityRepository extends BaseRepository<MarketingActivity> {
	//private Logger logger = LoggerFactory.getLogger(MarketingActivityRepository.class);
	
	public MarketingActivityRepository(){
		super(MarketingActivity.class);
	}
	
	public List<MarketingActivity> list(String search, Type type, DateTimeParam fromCreateDate, DateTimeParam toCreateDate, int start, int length){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MarketingActivity> criteriaQuery = criteriaBuilder.createQuery(MarketingActivity.class).distinct(true);
		Root<MarketingActivity> marketingActivity = criteriaQuery.from(MarketingActivity.class);
		
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(marketingActivity.get(MarketingActivity_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if(type != null){
			andPredicates.add(criteriaBuilder.equal(marketingActivity.get(MarketingActivity_.type), type));
		}
		
		if (fromCreateDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(marketingActivity.get(MarketingActivity_.createDateTime), fromCreateDate.getValue()));
		}
		
		if (toCreateDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(marketingActivity.get(MarketingActivity_.createDateTime), DateTimeUtils.getNextDay(toCreateDate.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
		if(andPredicates.size() > 0) {
			finalPredicates.add(andPredicate);
		}
		if(orPredicates.size() > 0) {
			finalPredicates.add(orPredicate);
		}
		Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
		criteriaQuery.where(finalPredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(marketingActivity.get(MarketingActivity_.createDateTime)));
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		if(length != 0) { 
			typedQuery.setMaxResults(length);
		}		
		return typedQuery.getResultList(); 
	}
	
	public long count(String search, Type type, DateTimeParam fromCreateDate, DateTimeParam toCreateDate){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class).distinct(true);
		Root<MarketingActivity> marketingActivity = criteriaQuery.from(MarketingActivity.class);
		criteriaQuery.select(criteriaBuilder.count(marketingActivity));
		
		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(marketingActivity.get(MarketingActivity_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		
		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		
		if(type != null){
			andPredicates.add(criteriaBuilder.equal(marketingActivity.get(MarketingActivity_.type), type));
		}
		
		if (fromCreateDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(marketingActivity.get(MarketingActivity_.createDateTime), fromCreateDate.getValue()));
		}
		
		if (toCreateDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(marketingActivity.get(MarketingActivity_.createDateTime), DateTimeUtils.getNextDay(toCreateDate.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
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
	
	public long totalCount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<MarketingActivity> marketingActivity = criteriaQuery.from(MarketingActivity.class);
		criteriaQuery.select(criteriaBuilder.count(marketingActivity));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public MarketingActivity findByName(String name){
		String sql = "SELECT m FROM MarketingActivity m  WHERE m.name = :name";
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(sql.toString(), MarketingActivity.class);
		typedQuery.setParameter("name", name);
		List<MarketingActivity> list = typedQuery.getResultList();
		if(list==null||list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public List<MarketingActivity> findListByAgentId(long agentId, int start, int length) {
		String sql = "SELECT m FROM MarketingActivity m  WHERE m.agent.id = :agentId";
		
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(sql.toString(), MarketingActivity.class);
		typedQuery.setParameter("agentId", agentId);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		
		return typedQuery.getResultList();
		
	}
	
	public long countByAgentId(long agentId) {
		String sql = "SELECT count(m.name) FROM MarketingActivity m  WHERE m.agent.id = :agentId";
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql.toString(), Long.class);
		typedQuery.setParameter("agentId", agentId);
		return typedQuery.getSingleResult();
	}
	
	public Channel findByChannel(Channel channel){
		String sql = "SELECT c FROM Channel c  WHERE c.sourceName = :sourceName";
		TypedQuery<Channel> typedQuery = entityManager.createQuery(sql.toString(), Channel.class);
		typedQuery.setParameter("sourceName", channel.getSourceName());
		List<Channel> list = typedQuery.getResultList();
		if(list==null||list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public MarketingActivity findByChannel(String channel){
		String sql = "SELECT m FROM MarketingActivity m  WHERE m.channel.sourceName = :sourceName";
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(sql.toString(), MarketingActivity.class);
		typedQuery.setParameter("sourceName", channel);
		List<MarketingActivity> list = typedQuery.getResultList();
		if(list==null||list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public List<MarketingActivity> listForStudentSelect(){
		String sql = "SELECT m FROM MarketingActivity m WHERE m.hasReleased = 1";
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(sql.toString(), MarketingActivity.class);
		return typedQuery.getResultList();
	}
	
	public MarketingActivity findByChannelId(long id){
		String sql = "SELECT m FROM MarketingActivity m  WHERE m.channel.id = :id";
		TypedQuery<MarketingActivity> typedQuery = entityManager.createQuery(sql.toString(), MarketingActivity.class);
		typedQuery.setParameter("id", id);
		List<MarketingActivity> list = typedQuery.getResultList();
		if(list==null||list.size()==0){
			return null;
		}
		return list.get(0);
	}
}
