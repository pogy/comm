package com.vipkid.service.exception;

public class OpenClassHasBeenSignUpException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public OpenClassHasBeenSignUpException(String template, Object... params) {
		super(ServiceExceptionCode.OPEN_CLASS_HASBEEN_SIGN_UP, template, params);
	}

}
