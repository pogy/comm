package com.vipkid.service.exception;

public class OpenIdAlreadyUsedServiceException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public OpenIdAlreadyUsedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.PARENT_WECHAT_OPENID_OCCUPIED, template, params);
	}

}
