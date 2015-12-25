package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.CourseLevel;

/**
 * CourseLevel 序列化 及 反序列化
 * @author wangbing 20150731
 *
 */
public class CourseLevelAdapter extends XmlAdapter<CourseLevel, CourseLevel> {

	@Override
	public CourseLevel unmarshal(CourseLevel courseLevel) throws Exception {
		return courseLevel;
	}

	@Override
	public CourseLevel marshal(CourseLevel courseLevel) throws Exception {
		if(courseLevel == null) {
			return null;
		}else {
			CourseLevel simplifiedCourseLevel = new CourseLevel();
			simplifiedCourseLevel.setId(courseLevel.getId());
			simplifiedCourseLevel.setName(courseLevel.getName());
			simplifiedCourseLevel.setSerialNumber(courseLevel.getSerialNumber());
			simplifiedCourseLevel.setSequence(courseLevel.getSequence());
			simplifiedCourseLevel.setCourse(courseLevel.getCourse());
			return simplifiedCourseLevel;
		}	
	}
}
