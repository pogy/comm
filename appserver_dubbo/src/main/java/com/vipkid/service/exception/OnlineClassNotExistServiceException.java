package com.vipkid.service.exception;

public class OnlineClassNotExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public OnlineClassNotExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_NOT_EXSIT, template, params);
	}
}
