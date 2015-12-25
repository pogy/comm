package com.vipkid.service.param;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

public class DateParam extends AbstractParam<Date> {

	public DateParam(String param) throws WebApplicationException {
		super(param);
	}

	@Override
	protected Date parse(String param) throws Throwable {
		return new Date(Long.parseLong(param));
	}

	public static DateParam valueOf(String param) throws Throwable {
		if(param == null) {
			return null;
		}else {
			return new DateParam(param);
		}
	}
	
	/**
	 * 转日期String为Date对象
	 * @param param
	 * @return
	 * @throws Throwable
	 */
	public static Date dateValueOf(String param) throws Throwable {
		if(param == null){
			return null;
		}else{
			return new DateParam(param).getValue();
		}
	}
}
