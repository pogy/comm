package com.vipkid.ext.youdao;

import java.util.List;

public class YouDaoTransResp {
	
	private Integer errorCode; 
	private List<String> translation ;
	
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	public List<String> getTranslation() {
		return translation;
	}
	public void setTranslation(List<String> translation) {
		this.translation = translation;
	}
	
	

}
