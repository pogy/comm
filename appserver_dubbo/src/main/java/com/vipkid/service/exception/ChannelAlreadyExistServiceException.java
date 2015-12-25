package com.vipkid.service.exception;

public class ChannelAlreadyExistServiceException extends ServiceException {
	
	private static final long serialVersionUID = 1L;

	public ChannelAlreadyExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.CHANNEL_ALREADY_EXIST, template, params);
	}

}
