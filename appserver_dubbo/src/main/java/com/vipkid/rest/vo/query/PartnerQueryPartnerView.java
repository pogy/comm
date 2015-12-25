package com.vipkid.rest.vo.query;

import java.util.Date;

import com.vipkid.model.Partner.Type;
import com.vipkid.model.User.Status;

public class PartnerQueryPartnerView {
	
	private Long id;
	private String name;
	private String email;
	private Type type;
	//2015-07-27 添加status
	private Status status;
	//2015-07-28 添加时间
	private Date createDatetime;
	private Date lastEditDatetime;
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Date getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	public Date getLastEditDatetime() {
		return lastEditDatetime;
	}
	public void setLastEditDatetime(Date lastEditDatetime) {
		this.lastEditDatetime = lastEditDatetime;
	}
	

}
