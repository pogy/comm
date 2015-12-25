package com.vipkid.ext.dby;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean success;
	private String error;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
