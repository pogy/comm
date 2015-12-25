package com.vipkid.service.exception;

public class UserLockedServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public UserLockedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.USER_LOCKED, template, params);
	}
}
