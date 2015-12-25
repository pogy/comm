package com.vipkid.service.exception;

public class StudentAlreadyAttenedCurrentActivityServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public StudentAlreadyAttenedCurrentActivityServiceException(String template, Object... params) {
		super(ServiceExceptionCode.STUDENT_ALREADY_ATTENDED_CURRENT_ACTIVITY, template, params);
	}
}
