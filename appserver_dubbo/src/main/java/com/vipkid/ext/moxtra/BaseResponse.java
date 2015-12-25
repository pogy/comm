package com.vipkid.ext.moxtra;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean success;
	private int errorCode;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
