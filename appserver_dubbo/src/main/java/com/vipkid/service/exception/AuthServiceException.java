package com.vipkid.service.exception;

import javax.ws.rs.core.Response.Status;

public class AuthServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public AuthServiceException(String template, Object... params) {
		super(Status.UNAUTHORIZED.getStatusCode(), template, params);
	}
}
