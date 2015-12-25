package com.vipkid.service.pojo;

import java.util.List;

public class ParentPortalTeacherQueryResult {
	
	private List<TeacherView> teachers;
	
	private long amount;
	
	private TeacherCondition teacherCondition;
	
	private AvailableCondition availableCondition;


	public TeacherCondition getTeacherCondition() {
		return teacherCondition;
	}

	public void setTeacherCondition(TeacherCondition teacherCondition) {
		this.teacherCondition = teacherCondition;
	}

	public AvailableCondition getAvailableCondition() {
		return availableCondition;
	}

	public void setAvailableCondition(AvailableCondition availableCondition) {
		this.availableCondition = availableCondition;
	}

	public List<TeacherView> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<TeacherView> teachers) {
		this.teachers = teachers;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}
	
}
