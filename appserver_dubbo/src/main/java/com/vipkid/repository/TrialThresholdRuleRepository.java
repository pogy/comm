package com.vipkid.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.TrialThresholdRule.Category;
import com.vipkid.model.TrialThresholdRule.Status;
import com.vipkid.model.TrialThresholdRule;

@Repository
public class TrialThresholdRuleRepository extends BaseRepository<TrialThresholdRule> {

	public TrialThresholdRuleRepository() {
		super(TrialThresholdRule.class);
	}

	public List<TrialThresholdRule> findAllRules() {
		String sql = "SELECT r FROM TrialThresholdRule r";
		TypedQuery<TrialThresholdRule> typedQuery = entityManager.createQuery(sql, TrialThresholdRule.class);

        return typedQuery.getResultList();
	}

	public List<TrialThresholdRule> findRecentYearRules() {
		String sql = "SELECT r FROM TrialThresholdRule r WHERE r.createDateTime > :aYearAgo";
		TypedQuery<TrialThresholdRule> typedQuery = entityManager.createQuery(sql, TrialThresholdRule.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		typedQuery.setParameter("aYearAgo", cal.getTime());

        return typedQuery.getResultList();
	}

	public List<TrialThresholdRule> findByCategoryAndStatus(Category category, Status status) {
		String sql = "SELECT r FROM TrialThresholdRule r WHERE r.status = :status AND r.category = :category ORDER BY r.createDateTime";
		TypedQuery<TrialThresholdRule> typedQuery = entityManager.createQuery(sql, TrialThresholdRule.class);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("category", category);
		
        return typedQuery.getResultList();
	}

	public List<TrialThresholdRule> findWorkingByCategoryAndParentRuleId(Category category, long parentRuleId) {
		String sql = "SELECT r FROM TrialThresholdRule r WHERE r.status = :status AND r.category = :category AND r.parentRule.id = :parentRuleId";
		TypedQuery<TrialThresholdRule> typedQuery = entityManager.createQuery(sql, TrialThresholdRule.class);
		typedQuery.setParameter("status", TrialThresholdRule.Status.WORKING);
		typedQuery.setParameter("category", category);
		typedQuery.setParameter("parentRuleId", parentRuleId);
		
        return typedQuery.getResultList();
	}
	
}
