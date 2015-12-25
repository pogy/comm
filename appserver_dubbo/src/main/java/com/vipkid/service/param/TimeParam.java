package com.vipkid.service.param;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

import com.vipkid.util.DateTimeUtils;

public class TimeParam extends AbstractParam<Date> {

	public TimeParam(String param) throws WebApplicationException {
		super(param);
	}

	@Override
	protected Date parse(String param) throws Throwable {
		return DateTimeUtils.parse(param, DateTimeUtils.TIME_FORMAT);
	}

}
