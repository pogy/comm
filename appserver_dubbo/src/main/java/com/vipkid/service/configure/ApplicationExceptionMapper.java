package com.vipkid.service.configure;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	private Logger logger = LoggerFactory.getLogger(ApplicationExceptionMapper.class.getSimpleName());
	
	@Override
	public Response toResponse(WebApplicationException webApplicationException) {
		logger.error(webApplicationException.getMessage(), webApplicationException);
		return webApplicationException.getResponse();
	}
}
