package com.vipkid.rest.vo.query;

public class TeacherQueryOnlineClassView {
	
	private Long id;
	private Long scheduledDateTime;
	private String teacherName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScheduledDateTime() {
		return scheduledDateTime;
	}

	public void setScheduledDateTime(Long scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	
	

}
