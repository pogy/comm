package com.vipkid.service.exception;


public class BadRequestServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public BadRequestServiceException(String template, Object... params) {
		super(ServiceExceptionCode.BAD_REQUEST, template, params);
	}
}
