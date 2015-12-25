package com.vipkid.service.exception;

public class ManagerIsNotTMKServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ManagerIsNotTMKServiceException(String template, Object... params) {
		super(ServiceExceptionCode.MANAGER_MUST_BE_TMK_MANAGER, template, params);
	}
}
