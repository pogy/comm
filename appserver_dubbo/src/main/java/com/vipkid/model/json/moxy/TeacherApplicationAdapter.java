package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.TeacherApplication;

public class TeacherApplicationAdapter extends XmlAdapter<TeacherApplication, TeacherApplication> {

	@Override
	public TeacherApplication unmarshal(TeacherApplication teacherApplication) throws Exception {
		return teacherApplication;
	}

	@Override
	public TeacherApplication marshal(TeacherApplication teacherApplication) throws Exception {
		if(teacherApplication == null) {
			return null;
		}else {
			TeacherApplication simplifiedTeacherApplication = new TeacherApplication();
			simplifiedTeacherApplication.setId(teacherApplication.getId());
			simplifiedTeacherApplication.setCurrent(teacherApplication.isCurrent());
			simplifiedTeacherApplication.setResult(teacherApplication.getResult());
			simplifiedTeacherApplication.setOnlineClass(teacherApplication.getOnlineClass());
			
			return simplifiedTeacherApplication;
		}	
	}
}
