package com.vipkid.service.exception;

public class MateIsNotTMKServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public MateIsNotTMKServiceException(String template, Object... params) {
		super(ServiceExceptionCode.MATE_MUST_BE_TMK, template, params);
	}
}
