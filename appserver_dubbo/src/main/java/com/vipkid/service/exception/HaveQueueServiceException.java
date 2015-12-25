package com.vipkid.service.exception;

public class HaveQueueServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public HaveQueueServiceException(String template, Object... params) {
		super(ServiceExceptionCode.HAVE_QUEUE, template, params);
	}
}
