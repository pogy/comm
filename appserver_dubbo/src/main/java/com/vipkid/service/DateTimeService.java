package com.vipkid.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.service.pojo.ServerDateTime;
import com.vipkid.util.DateTimeUtils;

@Service
public class DateTimeService {

	private Logger logger = LoggerFactory.getLogger(DateTimeService.class.getSimpleName());

	public ServerDateTime getServerDateTime() {
		long currentDateTime = System.currentTimeMillis();
		logger.debug("Current datetime = {}, format = {}", currentDateTime, DateTimeUtils.format(new Date(currentDateTime), DateTimeUtils.DATETIME_FORMAT));
		return new ServerDateTime(currentDateTime);
	}
}
