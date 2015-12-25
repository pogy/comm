package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Resource.Type;
import com.vipkid.model.Slide;

@Repository
public class SlideRepository extends BaseRepository<Slide> {

	private Logger logger = LoggerFactory.getLogger(SlideRepository.class);
	
	public SlideRepository() {
		super(Slide.class);
	}
	
	public List<Slide> findByResourceNameAndType(String resourceName, Type resourceType){
		String sql = "SELECT s FROM Slide s WHERE s.ppt.resource.name = :resourceName and s.ppt.resource.type = :resourceType";
		logger.debug("JPQL={}", sql);
		TypedQuery<Slide> typedQuery = entityManager.createQuery(sql, Slide.class);
		typedQuery.setParameter("resourceName", resourceName);
		typedQuery.setParameter("resourceType", resourceType);			
		return typedQuery.getResultList();
	}
	
	public List<Slide> findByPPTId(long pptId){
		String sql = "SELECT s FROM Slide s WHERE s.ppt.id = :pptId ORDER BY s.page ASC";
		logger.debug("JPQL={}", sql);
		TypedQuery<Slide> typedQuery = entityManager.createQuery(sql, Slide.class);
		typedQuery.setParameter("pptId", pptId);				
		return typedQuery.getResultList();
	}
	
	public Slide findByPPTIdAndPage(long pptId, int page){
		String sql = "SELECT s FROM Slide s WHERE s.ppt.id = :pptId AND s.page = :page";
		logger.debug("JPQL={}", sql);
		TypedQuery<Slide> typedQuery = entityManager.createQuery(sql, Slide.class);
		typedQuery.setParameter("pptId", pptId);	
		typedQuery.setParameter("page", page);	
		return typedQuery.getSingleResult();
	}
	
	public Slide findByLessonSerialNumberAndSlidePage(String lessonSerialNumber, int page){
		String sql = "SELECT s FROM Slide s JOIN s.ppt sp JOIN sp.resource spr JOIN spr.activities spras WHERE spras.lesson.serialNumber = :lessonSerialNumber AND s.page = :page";
		logger.debug("JPQL={}", sql);
		TypedQuery<Slide> typedQuery = entityManager.createQuery(sql, Slide.class);
		typedQuery.setParameter("lessonSerialNumber", lessonSerialNumber);
		typedQuery.setParameter("page", page);
		List<Slide> slides = typedQuery.getResultList();
		if(slides.isEmpty()){
			return null;
		}else{
			return slides.get(0);
		}
	}

	/*
	public Slide findByLessonSerialNumberrAndSlidePage(String lessonSerialNumber, int page){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Slide> criteriaQuery = criteriaBuilder.createQuery(Slide.class);
		Root<Slide> slide = criteriaQuery.from(Slide.class);
		
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (lessonSerialNumber != null) {
			Join<Slide, PPT> ppt = slide.join(Slide_.ppt, JoinType.LEFT);
			Join<PPT, com.vipkid.pojo.Resource> resource = ppt.join(PPT_.resource, JoinType.LEFT);
			ListJoin<com.vipkid.pojo.Resource, Activity> activities = (ListJoin<com.vipkid.pojo.Resource, Activity>) resource.join(Resource_.activities, JoinType.LEFT);
			Join<Activity, Lesson> lesson = activities.join(Activity_.lesson, JoinType.LEFT);
			andPredicates.add(criteriaBuilder.equal(lesson.get(Lesson_.serialNumber), lessonSerialNumber));
		}else {
			return null;
		}
		
		if (page != 0) {
			andPredicates.add(criteriaBuilder.equal(slide.get(Slide_.page), page));
		}else {
			return null;
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!andPredicates.isEmpty()) {
			criteriaQuery.where(andPredicate);
		}
		
		TypedQuery<Slide> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Slide> slides = typedQuery.getResultList();
		if(!slides.isEmpty()){
			return slides.get(0);
		}else {
			return null;
		}
	}*/
}
