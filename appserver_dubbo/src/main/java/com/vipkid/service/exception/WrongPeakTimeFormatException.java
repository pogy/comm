package com.vipkid.service.exception;


public class WrongPeakTimeFormatException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public WrongPeakTimeFormatException(String template, Object... params) {
		super(ServiceExceptionCode.WRONG_PEAK_TIME_FORMAT, template, params);
	}

}
