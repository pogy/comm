package com.vipkid.service.exception;


public class FileUploadServiceException extends ServiceException {
	
private static final long serialVersionUID = 1L;
	
	public FileUploadServiceException(String template, Object... params) {
		super(ServiceExceptionCode.FAIL_TO_UPLOAD_FILE, template, params);
	}

}
