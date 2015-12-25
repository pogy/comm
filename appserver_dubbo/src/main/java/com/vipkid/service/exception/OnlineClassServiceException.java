package com.vipkid.service.exception;

public class OnlineClassServiceException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlineClassServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_SERVICE_EXCEPTION, template, params);
	}

}
