package com.vipkid.service.exception;

public class HaveSalesTeamMateServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public HaveSalesTeamMateServiceException(String template, Object... params) {
		super(ServiceExceptionCode.HAVE_SALES_TEAM_MATE, template, params);
	}
}
