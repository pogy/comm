package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.CourseLevel;

/**
 * course level operation
 * @author wangbing 20150731
 *
 */
@Repository
public class LevelRepository extends BaseRepository<CourseLevel>{

	public LevelRepository() {
		super(CourseLevel.class);
	}
	
	/**
	 * 根据课程id查询level信息
	 * @param courseId
	 * @return
	 */
	public List<CourseLevel> findByCourseId(long courseId) {
		String sql = "SELECT l FROM CourseLevel l WHERE l.course.id = :courseId ORDER BY l.sequence";
		TypedQuery<CourseLevel> typedQuery = entityManager.createQuery(sql, CourseLevel.class);
		typedQuery.setParameter("courseId", courseId);
	    
	    return typedQuery.getResultList();
	}

	public CourseLevel findBySerialNumber(String serialNumber){
		String sql = "SELECT l FROM CourseLevel l WHERE l.serialNumber = :serialNumber";
		TypedQuery<CourseLevel> typedQuery = entityManager.createQuery(sql, CourseLevel.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<CourseLevel> CourseLevels = typedQuery.getResultList();
		if(CourseLevels.isEmpty()){
			return null;
		}else {
			return CourseLevels.get(0);
		}
	}
}
