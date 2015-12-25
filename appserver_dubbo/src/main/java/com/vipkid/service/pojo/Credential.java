package com.vipkid.service.pojo;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.model.validation.ValidateRegularExpression;

public class Credential implements Serializable {
	private static final long serialVersionUID = 1L;
	
//	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Pattern(regexp = ValidateRegularExpression.MOBILE, message = ValidateMessages.MOBILE)
	private String username;
	
//	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Size(min=6, max=20)
	private String password;
	private String newPassword;
	
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

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	
}
