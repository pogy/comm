package com.vipkid.service.exception;

public class OnlineClassAlreadyBookedByOthersServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public OnlineClassAlreadyBookedByOthersServiceException(String template, Object... params) {
		super(ServiceExceptionCode.ONLINE_CLASS_ALREADY_BOOKED_BY_OTHERS, template, params);
	}
}
