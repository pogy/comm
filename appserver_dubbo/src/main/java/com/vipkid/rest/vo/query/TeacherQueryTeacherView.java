package com.vipkid.rest.vo.query;

import java.util.ArrayList;
import java.util.List;

import com.vipkid.model.Country;
import com.vipkid.model.Gender;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.Type;
import com.vipkid.model.User.Status;

public class TeacherQueryTeacherView {
	
	private Long id;
	private String realName;
	private String name;
	private Status status;
	private Type type;
	private Gender gender;
	private Country country;
	private LifeCycle lifeCycle;
	private String email;
	private Long contractStartDate;
	private Long contractEndDate;
	private TeacherQueryPartnerView partner;
	private TeacherQueryTApplicationView currentTeacherApplication;
	private Long signUpDateTime;
	private List<TeacherQueryCourseView> certificatedCourses = new ArrayList<TeacherQueryCourseView>();
	//2015-07-27 refer -- if not recruit channel, then refer.
	private String referee;
	private String managerName;
	private Long operatorId;
	private String operatorName;
	private Long operationTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}
	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getContractStartDate() {
		return contractStartDate;
	}
	public void setContractStartDate(Long contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	public Long getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(Long contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	public TeacherQueryPartnerView getPartner() {
		return partner;
	}
	public void setPartner(TeacherQueryPartnerView partner) {
		this.partner = partner;
	}
	public List<TeacherQueryCourseView> getCertificatedCourses() {
		return certificatedCourses;
	}
	public void setCertificatedCourses(
			List<TeacherQueryCourseView> certificatedCourses) {
		this.certificatedCourses = certificatedCourses;
	}
	public TeacherQueryTApplicationView getCurrentTeacherApplication() {
		return currentTeacherApplication;
	}
	public void setCurrentTeacherApplication(
			TeacherQueryTApplicationView currentTeacherApplication) {
		this.currentTeacherApplication = currentTeacherApplication;
	}
	public String getReferee() {
		return referee;
	}
	public void setReferee(String refer) {
		this.referee = refer;
	}
	public Long getSignUpDateTime() {
		return signUpDateTime;
	}
	public void setSignUpDateTime(Long signUpDateTime) {
		this.signUpDateTime = signUpDateTime;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public Long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Long getOperationTime() {
		return operationTime;
	}
	public void setOperationTime(Long operationTime) {
		this.operationTime = operationTime;
	}
	
	
	

}
