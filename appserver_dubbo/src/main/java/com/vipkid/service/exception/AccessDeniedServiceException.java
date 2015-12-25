package com.vipkid.service.exception;

import javax.ws.rs.core.Response.Status;

public class AccessDeniedServiceException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public AccessDeniedServiceException(String template, Object... params) {
		super(Status.FORBIDDEN.getStatusCode(), template, params);
	}

}
