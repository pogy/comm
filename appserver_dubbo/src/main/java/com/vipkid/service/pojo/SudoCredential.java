package com.vipkid.service.pojo;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.model.validation.ValidateRegularExpression;

public class SudoCredential implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Pattern(regexp = ValidateRegularExpression.MOBILE, message = ValidateMessages.MOBILE)
	private String userName;
	
	private String password;
	
	private String adminName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
}
