package com.vipkid.service.pojo;

public class CountOnlineClassByCourseView {
	private String CourseName;
	private long num;
	
	public CountOnlineClassByCourseView(){
		
	}
	
	public CountOnlineClassByCourseView(String CourseName,
			long num
			){
		this.CourseName = CourseName;
		this.num = num;
	}
	
	public String getCourseName() {
		return CourseName;
	}
	public void setCourseName(String courseName) {
		CourseName = courseName;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
	}
	
	

}
