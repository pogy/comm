package com.vipkid.service.exception;

public class RedisIOFailedException extends ServiceException {
	private static final long serialVersionUID = 1L;
	
	public RedisIOFailedException() {
		super(ServiceExceptionCode.REDIS_IO_FAILED, "", 0);
	}
}
