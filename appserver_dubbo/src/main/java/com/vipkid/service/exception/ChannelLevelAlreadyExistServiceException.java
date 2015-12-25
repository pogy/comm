package com.vipkid.service.exception;

public class ChannelLevelAlreadyExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ChannelLevelAlreadyExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.CHANNEL_LEVEL_ALREADY_EXIST, template, params);
	}
}
