package com.vipkid.service.exception;

public class ParentMobileUsedServiceException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public ParentMobileUsedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.PARENT_MOBILE_OCCUPIED, template, params);
	}

}
