package com.vipkid.service.pojo;

import java.io.Serializable;

public class TeacherNameView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String realName;

	
	public TeacherNameView() {
		super();
	}

	public TeacherNameView(long id, String realName) {
		super();
		this.id = id;
		this.realName = realName;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	
}
