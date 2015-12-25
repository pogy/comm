package com.vipkid.service.exception;

public class FamilyDoNotHaveEnoughInvitationNumberException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FamilyDoNotHaveEnoughInvitationNumberException(String template, Object... params) {
		super(ServiceExceptionCode.FAMILY_INVITATION_NOT_ENOUGH, template, params);
	}
}
