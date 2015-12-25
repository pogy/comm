package com.vipkid.service.pojo;

import java.io.Serializable;

public class AccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

//	"access_token":"ACCESS_TOKEN",
//	   "expires_in":7200,
//	   "refresh_token":"REFRESH_TOKEN",
//	   "openid":"OPENID",
//	   "scope":"SCOPE",
//	   "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
	
	private String access_token;
	
	private String refresh_token;
	
	private String openid;
	
	private String scope;
	
	private String unionid;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
	

}
