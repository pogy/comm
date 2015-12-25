package com.vipkid.repository;

import com.vipkid.model.AirCraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AirCraftRepository extends BaseRepository<AirCraft> {

	private Logger logger = LoggerFactory.getLogger(AirCraftRepository.class);

	public AirCraftRepository() {
		super(AirCraft.class);
	}
	
	public List<AirCraft> findByStudentId(long studentId) {
		logger.debug("studentId=" + studentId);
		String sql = "SELECT ac FROM AirCraft ac WHERE ac.student.id = :studentId";
		TypedQuery<AirCraft> Query = entityManager.createQuery(sql, AirCraft.class);
		Query.setParameter("studentId", studentId);
	    return Query.getResultList();
	}
	
	public List<AirCraft> findAircraftBySequenceAndStudentId(long studentId,int sequence) {
		logger.debug("studentId=" + studentId +"sequence=" + sequence);
		String sql = "SELECT ac FROM AirCraft ac WHERE ac.student.id = :studentId AND ac.sequence = :sequence";
		TypedQuery<AirCraft> Query = entityManager.createQuery(sql, AirCraft.class);
		Query.setParameter("studentId", studentId);
		Query.setParameter("sequence", sequence);
	    return Query.getResultList();
	}
	
	public List<AirCraft> findCurrentByStudentId(long studentId) {
		logger.debug("studentId=" + studentId);
		String sql = "SELECT ac.id FROM AirCraft ac WHERE ac.student.id = :studentId AND ac.current = true";
		TypedQuery<AirCraft> Query = entityManager.createQuery(sql, AirCraft.class);
		Query.setParameter("studentId", studentId);
	    return Query.getResultList();
	}
	
	public List<AirCraft> updateAirCraftCurrentByStudentId(long studentId,long aircraftId) {
		logger.debug("courseId=" + studentId + "aircraftId" + aircraftId);
		String sql = "UPDATE AirCraft ac SET ac.current = true WHERE ac.name = :aircraftId AND ac.student.id = :studentId";
		TypedQuery<AirCraft> Query = entityManager.createQuery(sql, AirCraft.class);
		Query.setParameter("aircraftId", aircraftId);
		Query.setParameter("studentId", studentId);
	    return Query.getResultList();
	}
}