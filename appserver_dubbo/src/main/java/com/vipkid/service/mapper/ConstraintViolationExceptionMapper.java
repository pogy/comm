package com.vipkid.service.mapper;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException constraintViolationException) {
		Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        StringBuilder stringBuilder = new StringBuilder();
        for(ConstraintViolation<?> constraintViolation : constraintViolations) {
        	stringBuilder.append(constraintViolation.getMessage()).append("; ");
        }
        return Response.status(Status.BAD_REQUEST).entity(stringBuilder.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
