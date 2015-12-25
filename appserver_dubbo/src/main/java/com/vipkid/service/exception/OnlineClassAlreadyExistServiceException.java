package com.vipkid.service.exception;

public class OnlineClassAlreadyExistServiceException extends ServiceException {
	
	private static final long serialVersionUID = 1L;

	public OnlineClassAlreadyExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_ALREADY_EXIST, template, params);
	}

}
