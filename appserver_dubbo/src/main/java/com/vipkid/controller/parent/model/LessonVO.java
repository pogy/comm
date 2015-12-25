package com.vipkid.controller.parent.model;

import java.io.Serializable;

public class LessonVO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String time;
	private String name;
	private boolean learned;
	private String className;
	private String fullTime;
	
	
	public String getFullTime() {
		return fullTime;
	}
	public void setFullTime(String fullTime) {
		this.fullTime = fullTime;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isLearned() {
		return learned;
	}
	public void setLearned(boolean learned) {
		this.learned = learned;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
