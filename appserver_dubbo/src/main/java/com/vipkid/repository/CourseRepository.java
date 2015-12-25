package com.vipkid.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Course_;
import com.vipkid.service.pojo.parent.CourseView;

@Repository
public class CourseRepository extends BaseRepository<Course> {

	private Logger logger = LoggerFactory.getLogger(CourseRepository.class);
	
	public CourseRepository() {
		super(Course.class);
	}
	
	public List<Course> findNeedBackupTeacher(){
		String sql = "SELECT c FROM Course c WHERE c.needBackupTeacher = :needBackupTeacher";
		logger.debug("The sql={}", sql);
		TypedQuery<Course> typedQuery = entityManager.createQuery(sql, Course.class);
		typedQuery.setParameter("needBackupTeacher", true);
	    return typedQuery.getResultList();
	}
	
	public Course findByCourseType(Type type) {
		String sql = "SELECT c FROM Course c WHERE c.type = :type";
		logger.debug("The sql = {}", sql);
		TypedQuery<Course> typedQuery = entityManager.createQuery(sql, Course.class);
		typedQuery.setParameter("type", type);
		List<Course> courses = typedQuery.getResultList();
		if(courses.isEmpty()) {
			return null;
		}else {
			return courses.get(0);
		}
	}
	
	public List<Course> findAll() {
		String sql = "SELECT c FROM Course c";
		TypedQuery<Course> typedQuery = entityManager.createQuery(sql, Course.class);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Course> list(String searchCourseName, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
		Root<Course> course = criteriaQuery.from(Course.class);
		
		Predicate cousePredicate = criteriaBuilder.and();
		if (searchCourseName != null){
			cousePredicate = criteriaBuilder.and(criteriaBuilder.equal(course.get(Course_.name), searchCourseName));
		}
		criteriaQuery.where(cousePredicate);
		TypedQuery<Course> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start==null?0:start);
		typedQuery.setMaxResults(length==null?0:length);
	    return typedQuery.getResultList();
	}
	
	public Course findBySerialNumber(String serialNumber){
		String sql = "SELECT c FROM Course c WHERE c.serialNumber = :serialNumber";
		TypedQuery<Course> typedQuery = entityManager.createQuery(sql, Course.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<Course> courses = typedQuery.getResultList();
		if(courses.isEmpty()){
			return null;
		}else {
			return courses.get(0);
		}	
	}
	
	public List<CourseView> findCourses(){
		String sql="SELECT NEW com.vipkid.service.pojo.parent.CourseView(c.id,c.name) FROM Course c";
		TypedQuery<CourseView> typedQuery = entityManager.createQuery(sql, CourseView.class);
		List<CourseView> courseViews = typedQuery.getResultList();
		if(courseViews==null||courseViews.isEmpty()){
			return new ArrayList<CourseView>();
		}else {
			return courseViews;
		}
	}
}
