package com.vipkid.service.exception;

public class OnlineClassAlreadyRequestedServiceException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlineClassAlreadyRequestedServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_ALREADY_REQUESTED, template, params);
	}
}
