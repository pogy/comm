package com.vipkid.rest.vo.query;

import com.vipkid.model.Channel;
import com.vipkid.model.Gender;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Student.Source;

public class StudentQueryStudentView {
	
	
	private Long id ;
	private String name;
	private String englishName;
	private Gender gender;
	private Integer age;
	private Long registerDateTime;
	private LifeCycle lifeCycle;
	private Channel channel;
	private Source source;
	private StudentQueryFamilyView  family;
	private StudentQueryStaffView chineseLeadTeacher;
	private StudentQueryStaffView sales;
	private StudentQueryMarketingActivityView marketingActivity;
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public StudentQueryFamilyView getFamily() {
		return family;
	}

	public void setFamily(StudentQueryFamilyView family) {
		this.family = family;
	}


	public StudentQueryStaffView getChineseLeadTeacher() {
		return chineseLeadTeacher;
	}

	public void setChineseLeadTeacher(StudentQueryStaffView chineseLeadTeacher) {
		this.chineseLeadTeacher = chineseLeadTeacher;
	}

	public StudentQueryStaffView getSales() {
		return sales;
	}

	public void setSales(StudentQueryStaffView sales) {
		this.sales = sales;
	}

	public StudentQueryMarketingActivityView getMarketingActivity() {
		return marketingActivity;
	}

	public void setMarketingActivity(
			StudentQueryMarketingActivityView marketingActivity) {
		this.marketingActivity = marketingActivity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Long getRegisterDateTime() {
		return registerDateTime;
	}

	public void setRegisterDateTime(Long registerDateTime) {
		this.registerDateTime = registerDateTime;
	}

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

}
