package com.vipkid.service.exception;


public class PeakTimeApplyTimeoutException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public PeakTimeApplyTimeoutException(String template, Object... params) {
		super(ServiceExceptionCode.PEAK_TIME_APPLY_TIMEOUT, template, params);
	}

}
