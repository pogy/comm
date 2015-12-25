package com.vipkid.service.exception;


public class TrialThresholdApplyTimeoutException extends ServiceException{
	
private static final long serialVersionUID = 1L;
	
	public TrialThresholdApplyTimeoutException(String template, Object... params) {
		super(ServiceExceptionCode.TRIAL_THRESHOLD_APPLY_TIMEOUT, template, params);
	}

}
