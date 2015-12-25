package com.vipkid.rest.vo.query;

import java.util.Date;

import com.vipkid.model.TeacherApplication;

public class TeacherQueryTApplicationView {
	private Long id;
	private TeacherQueryOnlineClassView onlineClass;
	private TeacherApplication.Result applicationResult;
	
	// 2015-08-13 application的status阶段
	private TeacherApplication.Status applicationStatus;
	private Date applyDateTime;
	private Date auditDateTime;
	private Date passedPreviousPhaseDateTime;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TeacherQueryOnlineClassView getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(TeacherQueryOnlineClassView onlineClass) {
		this.onlineClass = onlineClass;
	}

	public TeacherApplication.Result getApplicationResult() {
		return applicationResult;
	}

	public void setApplicationResult(TeacherApplication.Result applicationResult) {
		this.applicationResult = applicationResult;
	}

	public TeacherApplication.Status getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(TeacherApplication.Status applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	public Date getApplyDateTime() {
		return applyDateTime;
	}

	public void setApplyDateTime(Date applyDateTime) {
		this.applyDateTime = applyDateTime;
	}

	public Date getAuditDateTime() {
		return auditDateTime;
	}

	public void setAuditDateTime(Date auditDateTime) {
		this.auditDateTime = auditDateTime;
	}

	public Date getPassedPreviousPhaseDateTime() {
		return passedPreviousPhaseDateTime;
	}

	public void setPassedPreviousPhaseDateTime(Date passedPreviousPhaseDateTime) {
		this.passedPreviousPhaseDateTime = passedPreviousPhaseDateTime;
	}
}
