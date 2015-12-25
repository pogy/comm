package com.vipkid.service.exception;

public class ChineseLeadTeacherAlreadyAssignedServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ChineseLeadTeacherAlreadyAssignedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.CHINESE_LEAD_TEACHER_ALREADY_ASSIGNED, template, params);
	}
}
