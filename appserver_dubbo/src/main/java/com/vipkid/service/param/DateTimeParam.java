package com.vipkid.service.param;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

public class DateTimeParam extends AbstractParam<Date> {

	public DateTimeParam(String param) throws WebApplicationException {
		super(param);
	}

	@Override
	protected Date parse(String param) throws Throwable {
		return new Date(Long.parseLong(param));
	}

}
