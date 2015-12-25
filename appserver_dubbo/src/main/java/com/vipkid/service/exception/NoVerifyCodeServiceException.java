package com.vipkid.service.exception;

public class NoVerifyCodeServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public NoVerifyCodeServiceException(String template, Object... params) {
		super(ServiceExceptionCode.USER_NO_VERIFY_CODE, template, params);
	}
}
