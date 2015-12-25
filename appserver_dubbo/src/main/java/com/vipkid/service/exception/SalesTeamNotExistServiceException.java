package com.vipkid.service.exception;

public class SalesTeamNotExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public SalesTeamNotExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.TEAM_NOT_EXSIT, template, params);
	}
}
