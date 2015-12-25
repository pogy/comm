package com.vipkid.service.pojo;

import java.io.Serializable;

public class FiremanLogRedisView implements Serializable{
	
	static final long serialVersionUID = 1L;
	
	private long onlineClassId;

	private boolean isStudentInTheClassroom;
	
	private boolean isTeacherInTheClassroom;
	
	private boolean isStudentHavingProblem;
	
	private boolean isTeacherHavingProblem;
	
	public long getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public boolean isStudentInTheClassroom() {
		return isStudentInTheClassroom;
	}

	public void setStudentInTheClassroom(boolean isStudentInTheClassroom) {
		this.isStudentInTheClassroom = isStudentInTheClassroom;
	}

	public boolean isTeacherInTheClassroom() {
		return isTeacherInTheClassroom;
	}

	public void setTeacherInTheClassroom(boolean isTeacherInTheClassroom) {
		this.isTeacherInTheClassroom = isTeacherInTheClassroom;
	}

	public boolean isStudentHavingProblem() {
		return isStudentHavingProblem;
	}

	public void setStudentHavingProblem(boolean isStudentHavingProblem) {
		this.isStudentHavingProblem = isStudentHavingProblem;
	}

	public boolean isTeacherHavingProblem() {
		return isTeacherHavingProblem;
	}

	public void setTeacherHavingProblem(boolean isTeacherHavingProblem) {
		this.isTeacherHavingProblem = isTeacherHavingProblem;
	}

}
