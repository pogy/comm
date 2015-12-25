package com.vipkid.service.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public InternalServerErrorServiceException(String template, Object... params) {
		super(HttpStatus.INTERNAL_SERVER_ERROR.value(), template, params);
	}

}
