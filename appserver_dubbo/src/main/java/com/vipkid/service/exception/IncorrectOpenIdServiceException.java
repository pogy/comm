package com.vipkid.service.exception;

public class IncorrectOpenIdServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public IncorrectOpenIdServiceException(String template, Object... params) {
		super(ServiceExceptionCode.PARENT_WECHAT_OPENID_MISMATCH, template, params);
	}
}
