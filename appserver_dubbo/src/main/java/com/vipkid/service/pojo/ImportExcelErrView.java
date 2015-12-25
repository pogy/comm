package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;

public class ImportExcelErrView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1034558538459355862L;
	
	private String parentName;
	private String studentName;
	private String englishName;
	private String age;
	private Date birDate;
	private String gender;
	private String phone;
	private String city;
	private String email;
	private String notes;
	private String source;
	private String channel;
	
	public ImportExcelErrView(){
		
	}
	
	public ImportExcelErrView(String parentName,
							String studentName,
							String englishName,
							String age,
							Date birDate,
							String gender,
							String phone,
							String city,
							String email,
							String notes,
							String source,
							String channel){
		this.parentName = parentName;
		this.studentName= studentName;
		this.englishName = englishName;
		this.age = age;
		this.birDate = birDate;
		this.gender = gender;
		this.phone = phone;
		this.city = city;
		this.email = email;
		this.notes = notes;
		this.source = source;
		this.channel = channel;
	}
	
	
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public Date getBirDate() {
		return birDate;
	}
	public void setBirDate(Date birDate) {
		this.birDate = birDate;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	

}
