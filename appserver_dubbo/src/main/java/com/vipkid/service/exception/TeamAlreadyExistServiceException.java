package com.vipkid.service.exception;

public class TeamAlreadyExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public TeamAlreadyExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.TEAM_ALREADY_EXIST, template, params);
	}
}
