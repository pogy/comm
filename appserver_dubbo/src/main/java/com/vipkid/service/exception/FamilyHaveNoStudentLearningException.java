package com.vipkid.service.exception;

public class FamilyHaveNoStudentLearningException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FamilyHaveNoStudentLearningException() {
		super(ServiceExceptionCode.FAMILY_NO_LEARNING_STUDENT, "", 0);
	}
}
