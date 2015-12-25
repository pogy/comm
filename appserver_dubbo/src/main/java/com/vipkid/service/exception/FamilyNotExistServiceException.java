package com.vipkid.service.exception;

public class FamilyNotExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FamilyNotExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FAMILY_NOT_EXSIT, template, params);
	}
}
