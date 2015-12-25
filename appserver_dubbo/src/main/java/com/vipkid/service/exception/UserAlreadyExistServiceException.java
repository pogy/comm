package com.vipkid.service.exception;

public class UserAlreadyExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public UserAlreadyExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.USER_ALREADY_EXIST, template, params);
	}
}
