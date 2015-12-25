package com.vipkid.ext.sms.yunpian;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SendSMSResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private transient boolean success;
	
	@SerializedName("code")
	private int code;
	
	@SerializedName("msg")
	private String message;
	
	@SerializedName("detail")
	private String detail;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
