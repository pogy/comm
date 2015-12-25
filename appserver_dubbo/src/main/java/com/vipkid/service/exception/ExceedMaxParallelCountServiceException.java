package com.vipkid.service.exception;

public class ExceedMaxParallelCountServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ExceedMaxParallelCountServiceException(String template, Object... params) {
		super(ServiceExceptionCode.EXCEED_MAX_PARALLEL_COUNT, template, params);
	}
}
