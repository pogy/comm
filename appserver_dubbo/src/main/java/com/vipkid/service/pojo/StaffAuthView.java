package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.List;

public class StaffAuthView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;	
	private String token;
	private String roles;
	private String permissions;
	private List<String> permissionCodes;
	private Long salesTeamId;
	private String isFirstLogin;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public List<String> getPermissionCodes() {
		return permissionCodes;
	}
	public void setPermissionCodes(List<String> permissionCodes) {
		this.permissionCodes = permissionCodes;
	}
	public Long getSalesTeamId() {
		return salesTeamId;
	}
	public void setSalesTeamId(Long salesTeamId) {
		this.salesTeamId = salesTeamId;
	}
	public String getIsFirstLogin() {
		return isFirstLogin;
	}
	public void setIsFirstLogin(String isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

}
