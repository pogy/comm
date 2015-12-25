package com.vipkid.service.exception;

public class NoMoreLessonForBookingServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public NoMoreLessonForBookingServiceException(String template, Object... params) {
		super(ServiceExceptionCode.NO_MORE_LESSON_FOR_BOOKING, template, params);
	}
}
