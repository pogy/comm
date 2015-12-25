package com.vipkid.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.PeakTimeRule;
import com.vipkid.model.PeakTimeRule.Category;
import com.vipkid.model.PeakTimeRule.Status;

@Repository
public class PeakTimeRuleRepository extends BaseRepository<PeakTimeRule> {

	public PeakTimeRuleRepository() {
		super(PeakTimeRule.class);
	}

	public List<PeakTimeRule> findAllRules() {
		String sql = "SELECT p FROM PeakTimeRule p";
		TypedQuery<PeakTimeRule> typedQuery = entityManager.createQuery(sql, PeakTimeRule.class);

        return typedQuery.getResultList();
	}

	public List<PeakTimeRule> findRecentYearRules() {
		String sql = "SELECT p FROM PeakTimeRule p WHERE p.createDateTime > :aYearAgo";
		TypedQuery<PeakTimeRule> typedQuery = entityManager.createQuery(sql, PeakTimeRule.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		typedQuery.setParameter("aYearAgo", cal.getTime());

        return typedQuery.getResultList();
	}

	public List<PeakTimeRule> findByCategoryAndStatus(Category category, Status status) {
		String sql = "SELECT p FROM PeakTimeRule p WHERE p.status = :status AND p.category = :category ORDER BY p.createDateTime";
		TypedQuery<PeakTimeRule> typedQuery = entityManager.createQuery(sql, PeakTimeRule.class);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("category", category);
		
        return typedQuery.getResultList();
	}

	public List<PeakTimeRule> findWorkingByCategoryAndParentRuleId(Category category, long parentRuleId) {
		String sql = "SELECT p FROM PeakTimeRule p WHERE p.status = :status AND p.category = :category AND p.parentRule.id = :parentRuleId";
		TypedQuery<PeakTimeRule> typedQuery = entityManager.createQuery(sql, PeakTimeRule.class);
		typedQuery.setParameter("status", PeakTimeRule.Status.WORKING);
		typedQuery.setParameter("category", category);
		typedQuery.setParameter("parentRuleId", parentRuleId);
		
        return typedQuery.getResultList();
	}
	
}
