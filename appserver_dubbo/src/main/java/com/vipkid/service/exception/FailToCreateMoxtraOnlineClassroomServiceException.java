package com.vipkid.service.exception;

public class FailToCreateMoxtraOnlineClassroomServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public FailToCreateMoxtraOnlineClassroomServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FAIL_TO_CREATE_MOXTRA_ONLINE_CLASSROOM, template, params);
	}
}
