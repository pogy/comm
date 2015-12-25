package com.vipkid.service.exception;

public class ForeignLeadTeacherAlreadyAssignedServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ForeignLeadTeacherAlreadyAssignedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FOREIGN_LEAD_TEACHER_ALREADY_ASSIGNED, template, params);
	}
}
