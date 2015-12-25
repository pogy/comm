package com.vipkid.service.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vipkid.model.Student.LifeCycle;
import com.vipkid.service.pojo.leads.FollowUpVo;
import com.vipkid.service.pojo.leads.ParentVo;

/**
 * clt列表展示
 * @author wangbing
 * @date 20150821
 */
public class CltStudent {
	
	public static class DashType{
		public static String NeverContact = "0";
		public static String NeedContact = "1";
		public static String AlreadyContact = "2";
	}

	/**
	 * 学生id
	 */
	private Long id;
	
	/**
	 * 学生Name
	 */
	private String name;
	
	/**
	 * 学生英文Name
	 */
	private String englishName;
	
	/**
	 * 性别
	 */
	private String gender;
	
	/**
	 * 年龄
	 */
	private Date birthday;

	/**
	 * 状态
	 */
	private LifeCycle lifeCycle;

	/**
	 * 家长
	 */
	private List<ParentVo> parents = new ArrayList<ParentVo>();
	
	/**
	 * clt名
	 */
	private String cltName;
	
	/**
	 * 分配当前clt日期
	 */
	private Date cltAssignDate;
	
	/**
	 * 销售名
	 */
	private String salesName;
	
	/**
	 * tmk名
	 */
	private String tmkName;
	
	/**
	 * 来源
	 */
	private String channelName;
	
	/**
	 * 最后一次跟进
	 */
	private FollowUpVo lastFollowUp;
	
	/**
	 * 剩余主修课课时
	 */
	private String remainMajorClass;

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

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public List<ParentVo> getParents() {
		return parents;
	}

	public void setParents(List<ParentVo> parents) {
		this.parents = parents;
	}

	public String getCltName() {
		return cltName;
	}

	public void setCltName(String cltName) {
		this.cltName = cltName;
	}

	public Date getCltAssignDate() {
		return cltAssignDate;
	}

	public void setCltAssignDate(Date cltAssignDate) {
		this.cltAssignDate = cltAssignDate;
	}

	public String getSalesName() {
		return salesName;
	}

	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}

	public String getTmkName() {
		return tmkName;
	}

	public void setTmkName(String tmkName) {
		this.tmkName = tmkName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public FollowUpVo getLastFollowUp() {
		return lastFollowUp;
	}

	public void setLastFollowUp(FollowUpVo lastFollowUp) {
		this.lastFollowUp = lastFollowUp;
	}

	public String getRemainMajorClass() {
		return remainMajorClass;
	}

	public void setRemainMajorClass(String remainMajorClass) {
		this.remainMajorClass = remainMajorClass;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
}
