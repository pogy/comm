package com.vipkid.service.pojo;

import java.io.Serializable;

import com.vipkid.model.Parent;
import com.vipkid.model.Student;

public class SignupAndBinding implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Parent parent;
	private Student student;
	private String wechatOpenId;

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
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
