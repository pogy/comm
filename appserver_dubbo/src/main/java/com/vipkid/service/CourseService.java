package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Course;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.CourseRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.parent.CourseView;

@Service
public class CourseService {
	private Logger logger = LoggerFactory.getLogger(CourseService.class.getSimpleName());
	
	@Resource
	private CourseRepository courseRepository;
	
	@Resource
	private SecurityService securityService;

	public Course find(long id) {
		logger.debug("find course for id = {}", id);
		return courseRepository.find(id);
	}

	public List<Course> findAll(){
		logger.debug("find all course");
		return courseRepository.findAll();
	}
	
	public List<Course> list(String searchCourseName, Integer start, Integer length) {
		logger.debug("list Student with params: search = {}, start = {}, length = {}.", searchCourseName, start, length);
		return courseRepository.list(searchCourseName, start, length);
	}

	public Course update(Course course) {
		logger.debug("update course: {}", course);
		courseRepository.update(course);
		StringBuffer strbuf = new StringBuffer(course.getSerialNumber());
		securityService.logAudit(Level.INFO, Category.COURSE_BASIC_INFO_UPDATE, "Update: The "+strbuf.toString()+" Course has been updated！" );
		
		return course;
	}
	
	public Course findByCourseType(Course.Type type) {
		logger.debug("find course by type: {}", type);
		
		return courseRepository.findByCourseType(type);
	}
	
	/**
	 * 
	* @Title: findCourses 
	* @Description: 获取家长端我的老师页面筛选课程下拉框
	* @param parameter
	* @author zhangfeipeng 
	* @return List<CourseView>
	* @throws
	 */
	public List<CourseView> findCourses(){
		return courseRepository.findCourses();
	}
}
