package com.vipkid.service.exception;

public class ProductNotExistServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public ProductNotExistServiceException(String template, Object... params) {
		super(ServiceExceptionCode.PRODUCT_NOT_EXSIT, template, params);
	}
}
