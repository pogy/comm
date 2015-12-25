package com.vipkid.service.exception;

public class ActivityIdIsNotInRegisterSourceListException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ActivityIdIsNotInRegisterSourceListException(String template, Object... params) {
		super(ServiceExceptionCode.ACTIVITY_IS_NOT_IN_REGISTER_SOURCE_LIST, template, params);
	}
}
