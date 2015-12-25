package com.vipkid.service.exception;


public class WrongTrialThresholdFormatException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public WrongTrialThresholdFormatException(String template, Object... params) {
		super(ServiceExceptionCode.WRONG_TRIAL_THRESHOLD_FORMAT, template, params);
	}

}
