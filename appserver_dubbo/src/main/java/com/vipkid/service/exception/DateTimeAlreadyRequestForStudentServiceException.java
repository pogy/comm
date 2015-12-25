package com.vipkid.service.exception;

public class DateTimeAlreadyRequestForStudentServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public DateTimeAlreadyRequestForStudentServiceException(String template, Object... params) {
		super(ServiceExceptionCode.DATETIME_ALREADY_REQUESTED_FOR_STUDENT, template, params);
	}
}
