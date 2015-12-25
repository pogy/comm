package com.vipkid.service.exception;

public class DateTimeAlreadyScheduledServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public DateTimeAlreadyScheduledServiceException(String template, Object... params) {
		super(ServiceExceptionCode.DATETIME_ALREADY_SCHEDULED, template, params);
	}
}
