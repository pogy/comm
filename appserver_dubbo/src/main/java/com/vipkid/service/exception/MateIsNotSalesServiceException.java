package com.vipkid.service.exception;

public class MateIsNotSalesServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public MateIsNotSalesServiceException(String template, Object... params) {
		super(ServiceExceptionCode.MATE_MUST_BE_SALES, template, params);
	}
}
