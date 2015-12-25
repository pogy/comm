package com.vipkid.service.pojo;

import com.vipkid.model.Parent;

public class Resetter {
	private Parent parent;
	private String mobile;
	private String email;
	private String verify;

	public String getVerify() {
		return verify;
	}
	public void setVerify(String verify) {
		this.verify = verify;
	}
	public Parent getParent() {
		return parent;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setParent(Parent parent) {
		this.parent = parent;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}