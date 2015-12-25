package com.vipkid.service.exception;

public class ManagerIsNotSalesServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ManagerIsNotSalesServiceException(String template, Object... params) {
		super(ServiceExceptionCode.MANAGER_MUST_BE_SALES_MANAGER, template, params);
	}
}
