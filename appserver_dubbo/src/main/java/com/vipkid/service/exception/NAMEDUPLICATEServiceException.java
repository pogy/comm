package com.vipkid.service.exception;

public class NAMEDUPLICATEServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public NAMEDUPLICATEServiceException(String template, Object... params) {
		super(ServiceExceptionCode.DUPLICATE_NAME, template, params);
	}
}
