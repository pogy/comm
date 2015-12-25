package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.FollowUp;
import com.vipkid.model.FollowUp.Category;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FollowUpService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping(value="/api/service/private/followUps")
public class FollowUpController {
	private Logger logger = LoggerFactory.getLogger(FollowUpController.class.getSimpleName());

	@Resource
	private FollowUpService followUpService;
	
	@Resource
	private SecurityService securityService;

	@RequestMapping(value="/find",method = RequestMethod.GET)
	public FollowUp find(@RequestParam("id") long id) {
		logger.info("find follow up for id = {}", id);
		return followUpService.find(id);
	}

	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<FollowUp> list(@RequestParam("studentId") long studentId, @RequestParam(value="category", required=false) Category category, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list followUp with params: studentId = {}, category = {}, start = {}, length = {}.", studentId, category, start, length);
		return followUpService.list(studentId, category, start, length);
	}

	@RequestMapping(value="/count",method = RequestMethod.GET)
	public Count count(@RequestParam("studentId") long studentId, @RequestParam(value="category", required=false) Category category) {
		logger.info("count followUp with params: studentId = {}, category = {}.", studentId, category);
		return followUpService.count(studentId, category);
	}

	@RequestMapping(method = RequestMethod.POST)
	public FollowUp create(@RequestBody FollowUp followUp) {
		logger.info("create followUp: {}", followUp);
		return followUpService.create(followUp);
	}
}
