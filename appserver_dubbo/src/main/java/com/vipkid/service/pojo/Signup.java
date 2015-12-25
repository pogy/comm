package com.vipkid.service.pojo;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.model.validation.ValidateMessages;

public class Signup implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Parent parent;
	private Student student;
	private String url;

	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	private String inventionCode;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public String getInventionCode() {
		return inventionCode;
	}
	public void setInventionCode(String inventionCode) {
		this.inventionCode = inventionCode;
	}

}
