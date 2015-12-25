package com.vipkid.repository;

import java.util.Date;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.model.LeadsDispatchHistory;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;

@Repository
public class LeadsDispatchHistoryRepository  extends BaseRepository<LeadsDispatchHistory>{
	
	public LeadsDispatchHistoryRepository() {
		super(LeadsDispatchHistory.class);
	}
	public long findDispatchHistorySize(Long assignerId,Long userId,Date assignTimeFrom,Date assignTimeTo) {
		
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		
		jpql.append("select count(leadsHistory) from LeadsDispatchHistory leadsHistory where 1=1");
		if (assignerId != null) {
			jpql.append(" and leadsHistory.assignerId = :assignerId ");
			params.put("assignerId", assignerId);
		}
		if (userId != null) {
			jpql.append(" and leadsHistory.userId = :userId ");
			params.put("userId", userId);
		}
		
		// assignTimeFrom/assignTimeTo
		if (assignTimeFrom != null) {
			jpql.append(" and leadsHistory.assignTime >= :assignTimeFrom");
			params.put("assignTimeFrom", assignTimeFrom);
		}
		if (assignTimeTo != null) {
			assignTimeTo = DateTimeUtils.getNextDay(assignTimeTo);
			jpql.append(" and leadsHistory.assignTime < :assignTimeTo");
			params.put("assignTimeTo", assignTimeTo);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}

}
