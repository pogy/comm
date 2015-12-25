package com.vipkid.service.pojo.leads;

public class ResponseVo {
	
	private RespStatus status;
	private String message;
	
	public ResponseVo() {
		
	}
	
	public ResponseVo(RespStatus status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public RespStatus getStatus() {
		return status;
	}
	public void setStatus(RespStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public enum RespStatus {
		SUCCESS,FAIL;
	}
	
	

}
