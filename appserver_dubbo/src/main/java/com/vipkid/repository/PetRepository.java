package com.vipkid.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Pet;

@Repository
public class PetRepository extends BaseRepository<Pet> {

	private Logger logger = LoggerFactory.getLogger(PetRepository.class);
	
	public PetRepository() {
		super(Pet.class);
	}
	
	public List<Pet> findByStudentId(long studentId) {
		String sql = "SELECT p FROM Pet p WHERE p.student.id = :studentId";
		TypedQuery<Pet> typedQuery = entityManager.createQuery(sql, Pet.class);
		typedQuery.setParameter("studentId", studentId);
	    
	    return typedQuery.getResultList();
	}
	
	public Pet findCurrentByStudentId(long studentId) {
		String sql = "SELECT p FROM Pet p WHERE p.student.id = :studentId AND p.current = :current";
		TypedQuery<Pet> typedQuery = entityManager.createQuery(sql, Pet.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("current", true);
	    
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<Pet> findPetByStudentIdAndSequence(long studentId,int sequence) {
		String sql = "SELECT p FROM Pet p WHERE p.student.id = :studentId AND p.sequence = :sequence";
		TypedQuery<Pet> typedQuery = entityManager.createQuery(sql, Pet.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("sequence", sequence);

		List<Pet> list = typedQuery.getResultList();
	    return list;
	}

	public long findStarsByStudentIdAndTimeRange(long studentId,
			Date startDate, Date endDate) {
		String sql = "SELECT SUM(p.price) FROM Pet p WHERE p.createDateTime BETWEEN :startDate AND :endDate AND p.student.id = :studentId";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("startDate", startDate);
		typedQuery.setParameter("endDate", endDate);
		typedQuery.setParameter("studentId", studentId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.debug("no pet found");
			return 0;
		}
	}
	
	
}
