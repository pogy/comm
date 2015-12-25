package com.vipkid.service.exception;

public class FailToGetMoxtraAccessTokenServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FailToGetMoxtraAccessTokenServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FAIL_TO_GET_MOXTRA_ACCESS_TOKEN, template, params);
	}
}
