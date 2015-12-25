package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Course;

public class CourseAdapter extends XmlAdapter<Course, Course> {

	@Override
	public Course unmarshal(Course course) throws Exception {
		return course;
	}

	@Override
	public Course marshal(Course course) throws Exception {
		if(course == null) {
			return null;
		}else {
			Course simplifiedCourse = new Course();
			simplifiedCourse.setId(course.getId());
			simplifiedCourse.setName(course.getName());
			simplifiedCourse.setMode(course.getMode());
			simplifiedCourse.setSequential(course.isSequential());
			simplifiedCourse.setType(course.getType());
			simplifiedCourse.setSerialNumber(course.getSerialNumber());
			simplifiedCourse.setBaseClassSalary(course.getBaseClassSalary());
			simplifiedCourse.setChildType(course.getChildType());
			return simplifiedCourse;
		}	
	}

}
