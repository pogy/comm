package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Lesson;
import com.vipkid.model.Level;

@Repository
public class LessonRepository extends BaseRepository<Lesson> {

	private Logger logger = LoggerFactory.getLogger(LessonRepository.class);
	
	public LessonRepository() {
		super(Lesson.class);
	}

	public List<Lesson> findByLearningCycleId(long learningCycleId) {
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.id = :learningCycleId ORDER BY l.sequence";
		TypedQuery<Lesson> typedQuery = entityManager.createQuery(sql, Lesson.class);
		typedQuery.setParameter("learningCycleId", learningCycleId);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Lesson> findByUnitId(long unitId) {
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.id = :unitId ORDER BY l.sequence";
		TypedQuery<Lesson> typedQuery = entityManager.createQuery(sql, Lesson.class);
		typedQuery.setParameter("unitId", unitId);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Lesson> findByCourseId(long courseId) {
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.course.id = :courseId ORDER BY l.sequence";
		logger.debug("Sql query={}", sql);
		TypedQuery<Lesson> typedQuery = entityManager.createQuery(sql, Lesson.class);
		typedQuery.setParameter("courseId", courseId);
	    
	    return typedQuery.getResultList();
	}
	
	public Long countByCourseId(long courseId) {
		String sql = "SELECT COUNT(l) FROM Lesson l WHERE l.learningCycle.unit.course.id = :courseId";
		logger.debug("Sql query={}", sql);
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("courseId", courseId);
	    
	    return typedQuery.getSingleResult();
	}
	
	public Lesson findByCourseIdAndSequence(long courseId, int sequence) {
		String sql = "SELECT l FROM Lesson l WHERE l.sequence = :sequence AND l.learningCycle.unit.course.id = :courseId";
		TypedQuery<Lesson> query = entityManager.createQuery(sql, Lesson.class);
		query.setParameter("courseId", courseId);
		query.setParameter("sequence", sequence);
		
		List<Lesson> lessons = query.getResultList();
		if(lessons.isEmpty()) {
			return null;
		}else{
			return lessons.get(0);
		}
	}
	
	public Lesson findNextByCourseIdAndSequence(long courseId, int sequence) {
		return findByCourseIdAndSequence(courseId, sequence + 1);
	}
	
	public Lesson findPrevByCourseIdAndSequence(long courseId, int sequence) {
		return findByCourseIdAndSequence(courseId, sequence - 1);
	}

	public List<Lesson> findLessonAndClassByUnitId(long studentId, long unitId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public long findLessonCountByUnitId(long unitId){
		String sql = "SELECT COUNT(l) FROM Lesson l WHERE l.learningCycle.unit.id = :unitId";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("unitId", unitId);
	    return typedQuery.getSingleResult();
	}
	
	public Lesson findBySerialNumber(String serialNumber){
		String sql = "SELECT l FROM Lesson l WHERE l.serialNumber = :serialNumber";
		TypedQuery<Lesson> typedQuery = entityManager.createQuery(sql, Lesson.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<Lesson> lessons = typedQuery.getResultList();
		if(lessons.isEmpty()){
			return null;
		}else {
			return lessons.get(0);
		}
	}
	
	public Lesson findFirstByCourseId(long courseId){
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.course.id = :courseId  ORDER BY l.sequence";
		TypedQuery<Lesson> query = entityManager.createQuery(sql, Lesson.class);
		query.setParameter("courseId", courseId);
		
		List<Lesson> lessons = query.getResultList();
		if(lessons.isEmpty()) {
			return null;
		}else{
			return lessons.get(0);
		}
	}
	
	public Lesson findFirstByUnitId(long unitId){
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.id = :unitId  ORDER BY l.sequence";
		TypedQuery<Lesson> query = entityManager.createQuery(sql, Lesson.class);
		query.setParameter("unitId", unitId);
		
		List<Lesson> lessons = query.getResultList();
		if(lessons.isEmpty()) {
			return null;
		}else{
			return lessons.get(0);
		}
	}
	
	public Lesson findFirstByCourseIdAndLevel(long courseId, Level level){
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.course.id = :courseId AND l.learningCycle.unit.level = :level ORDER BY l.sequence";
		TypedQuery<Lesson> query = entityManager.createQuery(sql, Lesson.class);
		query.setParameter("courseId", courseId);
		query.setParameter("level", level);
		
		List<Lesson> lessons = query.getResultList();
		if(lessons.isEmpty()) {
			return null;
		}else{
			return lessons.get(0);
		}
	}
	
	public Lesson findEndByCourseId(long courseId){
		String sql = "SELECT l FROM Lesson l WHERE l.learningCycle.unit.course.id = :courseId AND l.sequence IN (SELECT MAX(subl.sequence) FROM Lesson subl WHERE subl.learningCycle.unit.course.id = :courseId)";
		TypedQuery<Lesson> query = entityManager.createQuery(sql, Lesson.class);
		query.setParameter("courseId", courseId);
		
		List<Lesson> lessons = query.getResultList();
		if(lessons.isEmpty()) {
			return null;
		}else{
			return lessons.get(0);
		}
	}

}
