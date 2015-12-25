package com.vipkid.repository;


import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.InventionCode;

@Repository
public class InventionCodeRepository extends BaseRepository<InventionCode> {
	private Logger logger = LoggerFactory.getLogger(InventionCodeRepository.class);
	
	public InventionCodeRepository(){
		super(InventionCode.class);
	}
	
	public List<InventionCode> findByMarketingActivityId(long marketingActivityId){
		String sql = "SELECT ic FROM InventionCode ic  WHERE ic.marketingActivity.id = :marketingActivityId";
		TypedQuery<InventionCode> typedQuery = entityManager.createQuery(sql.toString(), InventionCode.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		return typedQuery.getResultList();
	}
	
	public List<InventionCode> findByMarketingActivityIdAndStatus(long marketingActivityId, boolean status){
		String sql = "SELECT ic FROM InventionCode ic  WHERE ic.marketingActivity.id = :marketingActivityId AND ic.hasUsed = :hasUsed";
		TypedQuery<InventionCode> typedQuery = entityManager.createQuery(sql.toString(), InventionCode.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		typedQuery.setParameter("hasUsed", status);
		return typedQuery.getResultList();
	}
	
	public InventionCode findByCode(String code){
		String sql = "SELECT ic FROM InventionCode ic  WHERE ic.code = :code";
		TypedQuery<InventionCode> typedQuery = entityManager.createQuery(sql.toString(), InventionCode.class);
		typedQuery.setParameter("code", code);
		
		InventionCode inventionCode = null;
		List<InventionCode> inventionCodes = typedQuery.getResultList();
		if(!inventionCodes.isEmpty()){
			inventionCode = inventionCodes.get(0);
		}
		logger.info("The inventionCode is ={}", inventionCode);
		return inventionCode;
	}
	
	public List<InventionCode> list(String hasUsed,long marketingActivityId, int start, int length) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a FROM InventionCode a  WHERE a.marketingActivity.id = :marketingActivityId");
		if(hasUsed!=null){
			sql.append(" AND a.hasUsed = :hasUsed");
		}
		TypedQuery<InventionCode> typedQuery = entityManager.createQuery(sql.toString(), InventionCode.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		if(hasUsed!=null){
			if(hasUsed.equals("false"))
				typedQuery.setParameter("hasUsed", false);
			if(hasUsed.equals("true"))
				typedQuery.setParameter("hasUsed", true);
		}
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(String hasUsed,long marketingActivityId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(a.id) FROM InventionCode a WHERE a.marketingActivity.id = :marketingActivityId");
		if(hasUsed!=null){
			sql.append(" AND a.hasUsed = :hasUsed");
		}
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql.toString(),Long.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		if(hasUsed!=null){
			if(hasUsed.equals("false"))
				typedQuery.setParameter("hasUsed", false);
			if(hasUsed.equals("true"))
				typedQuery.setParameter("hasUsed", true);
		}
		return typedQuery.getSingleResult();
	}
	public List<InventionCode> listForExcel(String hasUsed,long marketingActivityId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a FROM InventionCode a  WHERE a.marketingActivity.id = :marketingActivityId");
		if(hasUsed!=null){
			sql.append(" AND a.hasUsed = :hasUsed");
		}
		TypedQuery<InventionCode> typedQuery = entityManager.createQuery(sql.toString(), InventionCode.class);
		typedQuery.setParameter("marketingActivityId", marketingActivityId);
		if(hasUsed!=null){
			if(hasUsed.equals("false"))
				typedQuery.setParameter("hasUsed", false);
			if(hasUsed.equals("true"))
				typedQuery.setParameter("hasUsed", true);
		}
		return typedQuery.getResultList();
		
	}
	
//	public InventionCode create(MarketingActivity activity, long number){
//		String uuid = UUID.randomUUID().toString();
//        String result = uuid.substring(0, 8);
//        InventionCode code = new InventionCode();
//        code.setCode(result);
//        code.setHasUsed(false);
//        code.setMarketingActivity(activity);
//        return this.create(code);
//	}
	
}
