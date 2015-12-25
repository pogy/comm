package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.rest.vo.query.CourseQueryCourseView;
import com.vipkid.service.CourseService;

@RestController
@RequestMapping(value="/api/service/private/courses")
public class CourseController {
	private Logger logger = LoggerFactory.getLogger(CourseController.class.getSimpleName());
	
	@Resource
	private CourseService courseService;
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public Course find(@RequestParam("id") long id) {
		logger.info("find course for id = {}", id);
		return courseService.find(id);
	}
	
	@RequestMapping(value="/findAll",method = RequestMethod.GET)
	public List<Course> findAll(){
		logger.info("find all course");
		return courseService.findAll();
	}
	
	@RequestMapping(value="/findByCourseType",method = RequestMethod.GET)
	public Course findByCourseType(@RequestParam("type") Type type) {
		return courseService.findByCourseType(type);
	}
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<Course> list(@RequestParam(value="searchCourseName",required=false) String searchCourseName, @RequestParam(value="start",required=false) Integer start, @RequestParam(value="length",required=false) Integer length) {
		logger.info("list Student with params: search = {}, start = {}, length = {}.", searchCourseName, start, length);
		return courseService.list(searchCourseName, start, length);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Course update(@RequestBody Course course) {
		logger.info("update course: {}", course);
		courseService.update(course);
		
		return course;
	}
	
	@RequestMapping(value="/filter",method = RequestMethod.GET)
	public List<CourseQueryCourseView> filter(@RequestParam(value="searchCourseName",required=false) String searchCourseName, @RequestParam(value="start",required=false) Integer start, @RequestParam(value="length",required=false) Integer length) {
		logger.info("list Student with params: search = {}, start = {}, length = {}.", searchCourseName, start, length);
		List<Course> courseList =  courseService.list(searchCourseName, start, length);
		return this.getCourseQueryResultView(courseList);
	}

	private List<CourseQueryCourseView> getCourseQueryResultView(List<Course> courseList) {
		List<CourseQueryCourseView> courseViews = new ArrayList<CourseQueryCourseView>();
		if (courseList != null && courseList.size()  > 0) {
			Iterator<Course> iterator = courseList.iterator();
			while(iterator.hasNext()) {
				Course course = iterator.next();
				CourseQueryCourseView courseView = new CourseQueryCourseView();
				courseView.setId(course.getId());
				courseView.setName(course.getName());
				courseView.setType(course.getType());
				courseView.setSerialNumber(course.getSerialNumber());
				courseView.setMode(course.getMode());
				courseView.setNeedBackupTeacher(course.isNeedBackupTeacher());
				courseView.setSequential(course.isSequential());
				courseView.setFree(course.isFree());
				if(!courseView.getName().equalsIgnoreCase("DEMO") && !courseView.getName().equalsIgnoreCase("GA")){
					courseViews.add(courseView);
				}
			}
		}
		return courseViews;
	}
}
