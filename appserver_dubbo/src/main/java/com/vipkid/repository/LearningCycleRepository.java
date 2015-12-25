package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.LearningCycle;

@Repository
public class LearningCycleRepository extends BaseRepository<LearningCycle> {

	public LearningCycleRepository() {
		super(LearningCycle.class);
	}
	
	public List<LearningCycle> findByUnitId(long unitId) {
		String sql = "SELECT lc FROM LearningCycle lc WHERE lc.unit.id = :unitId ORDER BY lc.sequence";
		TypedQuery<LearningCycle> typedQuery = entityManager.createQuery(sql, LearningCycle.class);
		typedQuery.setParameter("unitId", unitId);
	    
	    return typedQuery.getResultList();
	}
	
	public LearningCycle findBySerialNumber(String serialNumber){
		String sql = "SELECT lc FROM LearningCycle lc WHERE lc.serialNumber = :serialNumber";
		TypedQuery<LearningCycle> typedQuery = entityManager.createQuery(sql, LearningCycle.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<LearningCycle> learningCycles = typedQuery.getResultList();
		if(learningCycles.isEmpty()){
			return null;
		}else {
			return learningCycles.get(0);
		}
	}

}
