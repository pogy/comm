package com.vipkid.model;



public class ItTestInfo extends Base {
	private static final long serialVersionUID = 1L;
	
	// 具体信息
	private String value;
	
	// 返回结果，1为通过，2为不通过
	private int returnCode;
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
}
