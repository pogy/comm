package com.vipkid.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Course.Type;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Unit;

@Repository
public class UnitRepository extends BaseRepository<Unit> {

	private Logger logger = LoggerFactory.getLogger(UnitRepository.class);
	
	public UnitRepository() {
		super(Unit.class);
	}
	
	public List<Unit> findByCourseId(long courseId) {
		String sql = "SELECT u FROM Unit u WHERE u.course.id = :courseId ORDER BY u.sequence";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("courseId", courseId);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Unit> findByLevelId(long levelId) {
		String sql = "SELECT u FROM Unit u WHERE u.courseLevel.id = :levelId ORDER BY u.sequence";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("levelId", levelId);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Unit> findByCourseType(Type type) {
		String sql = "SELECT u FROM Unit u WHERE u.course.type = :type";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("type", type);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Unit> findByCourseIdAndLevel(long courseId, Level level) {
		String sql = "SELECT u FROM Unit u WHERE u.course.id = :courseId AND u.level = :level ORDER BY u.sequence";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("level", level);
	    return typedQuery.getResultList();
	}
	
	
	public List<Unit> findBySequenceRangeAndCourseId(int firstSequence, int lastSequence, long courseId) {
		String sql = "SELECT u FROM Unit u WHERE u.sequence >= :firstSequence AND u.sequence <= :lastSequence AND u.course.id = :courseId";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("firstSequence", firstSequence);
		typedQuery.setParameter("lastSequence", lastSequence);
		typedQuery.setParameter("courseId", courseId);
		
	    return typedQuery.getResultList();
	}
	
	public List<Unit> findByFirstSequenceAndCourseId(int firstSequence, long courseId) {
		String sql = "SELECT u FROM Unit u WHERE u.sequence >= :firstSequence AND u.course.id = :courseId";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("firstSequence", firstSequence);
		typedQuery.setParameter("courseId", courseId);
		
	    return typedQuery.getResultList();
	}
	
	/**
	 * 根据状态和课程ID获取单元信息
	 * @param status
	 * @param courseId
	 * @return
	 */
	public List<Unit> findUnitsByStatus(String status, long courseId){
		logger.debug("status = " + status + "  courseId=" + courseId);
		List<Unit> list = new ArrayList<Unit>();
		if(status == OnlineClass.Status.BOOKED.toString()){
			Unit unit1 = new Unit();
			unit1.setId(518);
			unit1.setName("U2");
			unit1.setSequence(222);
			unit1.setTopic("ABC");
			list.add(unit1);
		}else if(status == OnlineClass.Status.FINISHED.toString()){
			Unit unit2 = new Unit();
			unit2.setId(503);
			unit2.setName("U1");
			unit2.setSequence(111);
			unit2.setTopic("DEF");
			list.add(unit2);
		}
		
		return list;
	}
	
	public Unit findBySerialNumber(String serialNumber){
		String sql = "SELECT u FROM Unit u WHERE u.serialNumber = :serialNumber";
		TypedQuery<Unit> typedQuery = entityManager.createQuery(sql, Unit.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<Unit> units = typedQuery.getResultList();
		if(units.isEmpty()){
			return null;
		}else {
			return units.get(0);
		}
	}

}
