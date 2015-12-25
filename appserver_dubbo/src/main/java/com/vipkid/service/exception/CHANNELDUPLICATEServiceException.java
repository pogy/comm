package com.vipkid.service.exception;

public class CHANNELDUPLICATEServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public CHANNELDUPLICATEServiceException(String template, Object... params) {
		super(ServiceExceptionCode.DUPLICATE_CHANNEL, template, params);
	}
}
