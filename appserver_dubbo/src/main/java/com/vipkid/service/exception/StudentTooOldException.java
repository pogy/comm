package com.vipkid.service.exception;

public class StudentTooOldException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public StudentTooOldException(String template, Object... params) {
		super(ServiceExceptionCode.STUDENT_TOO_OLD, template, params);
	}
}
