package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.PPT;
import com.vipkid.model.Resource;

@Repository
public class PPTRepository extends BaseRepository<PPT> {

	private Logger logger = LoggerFactory.getLogger(PPTRepository.class);
	
	public PPTRepository() {
		super(PPT.class);
	}
	
	public PPT findByLessonIdAndType(long lessonId, Resource.Type type){
		String sql = "SELECT p FROM PPT p JOIN p.resource pr JOIN pr.activities pras JOIN pras.lesson prasl WHERE prasl.id = :lessonId and pr.type = :type";
		logger.debug("JPQL={}", sql);
		TypedQuery<PPT> typedQuery = entityManager.createQuery(sql, PPT.class);
		typedQuery.setParameter("lessonId", lessonId);
		typedQuery.setParameter("type", type);
        List<PPT> PPTs = typedQuery.getResultList();
        if(CollectionUtils.isEmpty(PPTs)) {
            return null;
        }else{
            return PPTs.get(0);
        }
	}
	
	public PPT findByLessonSerialNumberAndType(String lessonSerialNumber, Resource.Type type){
		String sql = "SELECT p FROM PPT p JOIN p.resource pr JOIN pr.activities pras JOIN pras.lesson prasl WHERE prasl.serialNumber = :lessonSerialNumber and pr.type = :type";
		logger.debug("JPQL={}", sql);
		TypedQuery<PPT> typedQuery = entityManager.createQuery(sql, PPT.class);
		typedQuery.setParameter("lessonSerialNumber", lessonSerialNumber);
		typedQuery.setParameter("type", type);
		
		List<PPT> PPTs = typedQuery.getResultList();
		if(PPTs.isEmpty()) {
			return null;
		}else{
			return PPTs.get(0);
		}
	}
}
