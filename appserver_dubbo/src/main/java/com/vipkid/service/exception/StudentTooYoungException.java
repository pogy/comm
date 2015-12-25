package com.vipkid.service.exception;

public class StudentTooYoungException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public StudentTooYoungException(String template, Object... params) {
		super(ServiceExceptionCode.STUDENT_TOO_YOUNG, template, params);
	}
}
