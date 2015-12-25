package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Teacher;

public class TeacherAdapter extends XmlAdapter<Teacher, Teacher> {

	@Override
	public Teacher unmarshal(Teacher teacher) throws Exception {
		return teacher;
	}

	@Override
	public Teacher marshal(Teacher teacher) throws Exception {
		if (teacher == null) {
			return null;
		} else {
			Teacher simplifiedTeacher = new Teacher();
			simplifiedTeacher.setId(teacher.getId());
			simplifiedTeacher.setSkype(teacher.getSkype());
			simplifiedTeacher.setEmail(teacher.getEmail());
			simplifiedTeacher.setName(teacher.getName());
			simplifiedTeacher.setRealName(teacher.getRealName());
			simplifiedTeacher.setMobile(teacher.getMobile());
			simplifiedTeacher.setQq(teacher.getQq());
			simplifiedTeacher.setCertificatedCourses(teacher.getCertificatedCourses());
			simplifiedTeacher.setPartner(teacher.getPartner());
			simplifiedTeacher.setTimezone(teacher.getTimezone());
			simplifiedTeacher.setLifeCycle(teacher.getLifeCycle());
			simplifiedTeacher.setRecruitmentChannel(teacher.getRecruitmentChannel());
			simplifiedTeacher.setReferee(teacher.getReferee());
			return simplifiedTeacher;
		}
	}

}
