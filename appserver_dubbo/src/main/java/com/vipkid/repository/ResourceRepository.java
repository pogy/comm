package com.vipkid.repository;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Resource;

@Repository
public class ResourceRepository extends BaseRepository<Resource> {

	private Logger logger = LoggerFactory.getLogger(ResourceRepository.class);
	
	public ResourceRepository() {
		super(Resource.class);
	}
	
	public Resource findByNameAndType(String name, Resource.Type type){
		String sql = "SELECT r FROM Resource r WHERE r.name = :name and r.type = :type";
		logger.debug("JPQL={}", sql);
		TypedQuery<Resource> typedQuery = entityManager.createQuery(sql, Resource.class);
		typedQuery.setParameter("name", name);
		typedQuery.setParameter("type", type);	
		return typedQuery.getSingleResult();
	}
	
	public Resource findByLessonIdAndType(long lessonId, Resource.Type type){
		String sql = "SELECT r FROM Resource r JOIN r.activities ras JOIN WHERE ras.id = :lessonId and r.type = :type";
		logger.debug("JPQL={}", sql);
		TypedQuery<Resource> typedQuery = entityManager.createQuery(sql, Resource.class);
		typedQuery.setParameter("lessonId", lessonId);
		typedQuery.setParameter("type", type);	
		return typedQuery.getSingleResult();
	}
	
	
}
