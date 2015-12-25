package com.vipkid.service.pojo;

import java.io.Serializable;

import com.vipkid.model.Parent;
import com.vipkid.model.Student;

public class SignupFromInvitation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Parent parent;
	private Student student;
	private String invitationId;
	
	public String getInvitationId() {
		return invitationId;
	}
	public void setInvitationId(String invitationId) {
		this.invitationId = invitationId;
	}
	public Parent getParent() {
		return parent;
	}
	public void setParent(Parent parent) {
		this.parent = parent;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

}
