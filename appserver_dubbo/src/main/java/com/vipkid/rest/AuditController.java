package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Audit;
import com.vipkid.service.AuditService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping(value="/api/service/private/audits")
public class AuditController {
	private Logger logger = LoggerFactory.getLogger(AuditController.class.getSimpleName());
	
	@Resource
	private AuditService auditService;
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<Audit> list(@RequestParam(value="search",required=false) String search, @RequestParam(value ="executeDateTimeFrom",required=false) String executeDateTimeFrom, @RequestParam(value="executeDateTimeTo",required=false) String executeDateTimeTo, @RequestParam(value="level",required=false) String level, @RequestParam(value="category",required=false) String category, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.debug("count audit with params: search = {}, executeDateTimeFrom = {}, executeDateTimeTo = {}, level = {}, category = {}.", search, executeDateTimeFrom, executeDateTimeTo, level, category);
		
		return auditService.list(search, executeDateTimeFrom==null?null:new DateTimeParam(executeDateTimeFrom), executeDateTimeTo==null?null:new DateTimeParam(executeDateTimeTo), level, category, start, length);
	}

	@RequestMapping(value="/count", method=RequestMethod.GET)
	public Count count(@RequestParam(value="search",required=false) String search, @RequestParam(value ="executeDateTimeFrom",required=false) String executeDateTimeFrom, @RequestParam(value="executeDateTimeTo",required=false) String executeDateTimeTo, @RequestParam(value="level",required=false) String level, @RequestParam(value="category",required=false) String category) {
		logger.debug("count audit with params: search = {}, executeDateTimeFrom = {}, executeDateTimeTo = {}, level = {}, category = {}.", search, executeDateTimeFrom, executeDateTimeTo, level, category);
		
		return auditService.count(search, executeDateTimeFrom==null?null:new DateTimeParam(executeDateTimeFrom), executeDateTimeTo==null?null:new DateTimeParam(executeDateTimeTo), level, category);
	}
	
}
