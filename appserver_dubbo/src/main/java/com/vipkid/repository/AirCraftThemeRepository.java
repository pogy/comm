package com.vipkid.repository;

import com.vipkid.model.AirCraftTheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

@Repository
public class AirCraftThemeRepository extends BaseRepository<AirCraftTheme> {

	private Logger logger = LoggerFactory.getLogger(AirCraftThemeRepository.class);

	public AirCraftThemeRepository() {
		super(AirCraftTheme.class);
	}
	
	public List<AirCraftTheme> findByAirCraftIdAndLevel(long aircraftId,int level) {
		logger.debug("aircraftId=" + aircraftId + "level" + level);
		String sql = "SELECT ac FROM AirCraftTheme ac WHERE ac.airCraft.id = :aircraftId AND ac.level = :level";
		TypedQuery<AirCraftTheme> Query = entityManager.createQuery(sql, AirCraftTheme.class);
		Query.setParameter("aircraftId", aircraftId);
		Query.setParameter("level", level);
	    return Query.getResultList();
	}
	
	public List<AirCraftTheme> findByAirCraftId(long aircraftId) {
		logger.debug("aircraftId=" + aircraftId);
		String sql = "SELECT ac FROM AirCraftTheme ac WHERE ac.airCraft.id = :aircraftId";
		TypedQuery<AirCraftTheme> Query = entityManager.createQuery(sql, AirCraftTheme.class);
		Query.setParameter("aircraftId", aircraftId);
		List<AirCraftTheme> list = Query.getResultList();
	    return list;
	}
	
	public List<AirCraftTheme> findCurrentByAirCraftId(long aircraftId) {
		logger.debug("aircraftId=" + aircraftId);
		String sql = "SELECT ac FROM AirCraftTheme ac WHERE ac.airCraft.id = :aircraftId AND ac.current = true";
		TypedQuery<AirCraftTheme> Query = entityManager.createQuery(sql, AirCraftTheme.class);
		Query.setParameter("aircraftId", aircraftId);
		List<AirCraftTheme> list = Query.getResultList();
	    return list;
	}
	
	public List<AirCraftTheme> updateAirCraftCurrentByAirCraftId(long aircraftId,long level) {
		logger.debug("aircraftId=" + aircraftId + "level" + level);
		String sql = "UPDATE AirCraftTheme ac  SET ac.current = true WHERE ac.id = :aircraftId AND ac.level = :level";
		TypedQuery<AirCraftTheme> Query = entityManager.createQuery(sql, AirCraftTheme.class);
		Query.setParameter("aircraftId", aircraftId);
		Query.setParameter("level", level);
	    return Query.getResultList();
	}
	
	public long findStarsByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		String sql = "SELECT SUM(a.price) FROM AirCraftTheme a WHERE a.createDateTime BETWEEN :startDate AND :endDate AND a.airCraft.student.id = :studentId";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("studentId", studentId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.debug("no AirCraftTheme found");
			return 0;
		}
	}
}
