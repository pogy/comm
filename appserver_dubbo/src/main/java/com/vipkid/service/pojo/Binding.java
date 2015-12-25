package com.vipkid.service.pojo;

import java.io.Serializable;

public class Binding implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String wechatOpenId;
	
	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
