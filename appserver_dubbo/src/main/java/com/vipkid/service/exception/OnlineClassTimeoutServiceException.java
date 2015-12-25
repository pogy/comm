package com.vipkid.service.exception;

public class OnlineClassTimeoutServiceException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlineClassTimeoutServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_REQUEST_TIMEOUT, template, params);
	}
}

