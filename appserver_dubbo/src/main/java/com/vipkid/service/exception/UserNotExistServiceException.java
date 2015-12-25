package com.vipkid.service.exception;

public class UserNotExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public UserNotExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.USER_NOT_EXSIT, template, params);
	}
}
