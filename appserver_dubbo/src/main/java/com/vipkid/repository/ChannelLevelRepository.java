package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.ChannelLevel;

@Repository
public class ChannelLevelRepository extends BaseRepository<ChannelLevel> {

	public ChannelLevelRepository() {
		super(ChannelLevel.class);
	}
	
	public List<ChannelLevel> findAll() {
		String sql = "SELECT cl FROM ChannelLevel cl ORDER BY cl.id";
		TypedQuery<ChannelLevel> typedQuery = entityManager.createQuery(sql, ChannelLevel.class);
		
		return typedQuery.getResultList();
	}
	
	public ChannelLevel findByLevel(String level) {
		String sql = "SELECT cl FROM ChannelLevel cl WHERE cl.level = :level";
		TypedQuery<ChannelLevel> typedQuery = entityManager.createQuery(sql, ChannelLevel.class);
		typedQuery.setParameter("level", level);
		List<ChannelLevel> channelLevels = typedQuery.getResultList();
		if(channelLevels.isEmpty()) {
			return null;
		}else {
			return channelLevels.get(0);
		}
	}
	
}
