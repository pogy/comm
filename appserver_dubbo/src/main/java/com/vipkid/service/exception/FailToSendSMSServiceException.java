package com.vipkid.service.exception;

public class FailToSendSMSServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FailToSendSMSServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FAIL_TO_SEND_SMS, template, params);
	}
}
