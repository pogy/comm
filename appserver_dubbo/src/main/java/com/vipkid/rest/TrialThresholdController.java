package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.TrialThreshold;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.TrialThresholdService;
import com.vipkid.service.param.DateParam;

@RestController
@RequestMapping(value="/api/service/private/trialThreshold")
public class TrialThresholdController {
	private Logger logger = LoggerFactory.getLogger(TrialThresholdController.class.getSimpleName());
	
	@Resource
	private TrialThresholdService trialThresholdService;
	
	@Resource
	private OnlineClassService onlineClassService;
		
	@Context
	private ServletContext servletContext;

	@RequestMapping(value="/getByTimeRange", method = RequestMethod.GET)
	public List<TrialThreshold> getByTimeRange(@RequestParam("startDate") DateParam start, @RequestParam("endDate") DateParam end) {
		logger.debug("find peak time within range: {} - {}", start.getValue(), end.getValue());
		return trialThresholdService.getByTimeRange(start.getValue(), end.getValue());
	}
	
}
