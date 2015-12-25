package com.vipkid.service.pojo.leads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vipkid.model.Student.LifeCycle;

public class LeadsVo {

	private Long id;
	private Long stuId;
	private String name;
	private String englishName;
	private LifeCycle lifeCycle;
	private Long salesId;
	private Long salesAssignTime;
	private String salesName;
	private Long tmkId;
	private String tmkName;
	private Long tmkAssignTime;
	private Long registerTime;
	private List<ParentVo> parents = new ArrayList<ParentVo>();
	private FollowUpVo lastFollowUp;
	private String channel;
	private int status;
	private int customerStage;
	private boolean locked;
	private Date birthday;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStuId() {
		return stuId;
	}
	public void setStuId(Long stuId) {
		this.stuId = stuId;
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
	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}
	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}
	public Long getSalesId() {
		return salesId;
	}
	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}
	public Long getSalesAssignTime() {
		return salesAssignTime;
	}
	public void setSalesAssignTime(Long salesAssignTime) {
		this.salesAssignTime = salesAssignTime;
	}
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	public Long getTmkId() {
		return tmkId;
	}
	public void setTmkId(Long tmkId) {
		this.tmkId = tmkId;
	}
	public String getTmkName() {
		return tmkName;
	}
	public void setTmkName(String tmkName) {
		this.tmkName = tmkName;
	}
	public Long getTmkAssignTime() {
		return tmkAssignTime;
	}
	public void setTmkAssignTime(Long tmkAssignTime) {
		this.tmkAssignTime = tmkAssignTime;
	}
	public Long getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}
	public List<ParentVo> getParents() {
		return parents;
	}
	public void setParents(List<ParentVo> parents) {
		this.parents = parents;
	}
	public FollowUpVo getLastFollowUp() {
		return lastFollowUp;
	}
	public void setLastFollowUp(FollowUpVo lastFollowUp) {
		this.lastFollowUp = lastFollowUp;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCustomerStage() {
		return customerStage;
	}
	public void setCustomerStage(int customerStage) {
		this.customerStage = customerStage;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
}
