package com.vipkid.service.exception;

public class ActivityIdInvalidServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ActivityIdInvalidServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ACTIVITY_ID_INVALID, template, params);
	}
}
