package com.vipkid.model.json.moxy;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter extends XmlAdapter<Long, Date> {

	@Override
	public Date unmarshal(Long milliseconds) throws Exception {
		return new Date(milliseconds);
	}

	@Override
	public Long marshal(Date date) throws Exception {
		if(date == null) {
			return null;
		}else {
			return date.getTime();
		}
	}

}
