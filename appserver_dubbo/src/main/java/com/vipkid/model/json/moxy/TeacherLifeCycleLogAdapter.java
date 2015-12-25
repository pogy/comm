package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.TeacherLifeCycleLog;

public class TeacherLifeCycleLogAdapter  extends XmlAdapter<TeacherLifeCycleLog, TeacherLifeCycleLog> {

	@Override
	public TeacherLifeCycleLog unmarshal(TeacherLifeCycleLog teacherLifeCycleLog)
			throws Exception {
		return teacherLifeCycleLog;
	}

	@Override
	public TeacherLifeCycleLog marshal(TeacherLifeCycleLog teacherLifeCycleLog) throws Exception {
		//
		if (null == teacherLifeCycleLog) {
			return null;
		}
		TeacherLifeCycleLog simpleTeacherLifeCycleLog = new TeacherLifeCycleLog();
		simpleTeacherLifeCycleLog.setCreateDateTime(teacherLifeCycleLog.getCreateDateTime());
		simpleTeacherLifeCycleLog.setFromStatus(teacherLifeCycleLog.getFromStatus());
		simpleTeacherLifeCycleLog.setId(teacherLifeCycleLog.getId());
		simpleTeacherLifeCycleLog.setOperator(teacherLifeCycleLog.getOperator());
		simpleTeacherLifeCycleLog.setToStatus(teacherLifeCycleLog.getToStatus());
		
		return simpleTeacherLifeCycleLog;
	}

}
