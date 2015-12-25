package com.vipkid.service.exception;

public class NoMoreClassHourForBookingServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public NoMoreClassHourForBookingServiceException(String template, Object... params) {
		super(ServiceExceptionCode.NO_MORE_CLASS_HOUR_FOR_BOOKING, template, params);
	}
}
