package com.vipkid.rest;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.service.pojo.ServerDateTime;
import com.vipkid.util.DateTimeUtils;

@RestController
@RequestMapping(value="/api/service/private/servers")
public class DateTimeController {

	private Logger logger = LoggerFactory.getLogger(DateTimeController.class.getSimpleName());

	@RequestMapping(value="/datetime",method = RequestMethod.GET)
	public ServerDateTime getServerDateTime() {
		long currentDateTime = System.currentTimeMillis();
		logger.info("Current datetime = {}, format = {}", currentDateTime, DateTimeUtils.format(new Date(currentDateTime), DateTimeUtils.DATETIME_FORMAT));
		return new ServerDateTime(currentDateTime);
	}
}
