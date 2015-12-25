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

import com.vipkid.model.PeakTime;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.PeakTimeService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.pojo.PeakTimePerWeek;

@RestController
@RequestMapping(value="/api/service/private/peakTime")
public class PeakTimeController {
	private Logger logger = LoggerFactory.getLogger(PeakTimeController.class.getSimpleName());
	
	@Resource
	private PeakTimeService peakTimeService;
	
	@Resource
	private OnlineClassService onlineClassService;
		
	@Context
	private ServletContext servletContext;
	
	@RequestMapping(value="/getDefault",method = RequestMethod.GET)
	public PeakTimePerWeek getDefaultPeakTime() {
		logger.info("find peakTime ");
		return peakTimeService.getDefaultPeak();		
	}

	@RequestMapping(value="/getByTimeRange", method = RequestMethod.GET)
	public List<PeakTime> getByTimeRange(@RequestParam("startDate") DateParam start, @RequestParam("endDate") DateParam end) {
		logger.debug("find peak time within range: {} - {}", start.getValue(), end.getValue());
		return peakTimeService.getByTimeRange(start.getValue(), end.getValue());
	}
	
}
