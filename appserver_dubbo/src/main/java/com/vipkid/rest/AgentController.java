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

import com.alibaba.fastjson.JSONObject;
import com.vipkid.model.Agent;
import com.vipkid.security.SecurityService;
import com.vipkid.service.AgentService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping(value="/api/service/private/agent")
public class AgentController {
	
	private Logger logger = LoggerFactory.getLogger(AgentController.class.getSimpleName());
	
	@Resource
	private AgentService agentService;
	
	@Resource
	private SecurityService securityService;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Agent> list(@RequestParam(value = "search",required = false) String search, @RequestParam(value = "lock",required = false) String lock,@RequestParam(value = "start",required = false) Integer start, @RequestParam(value = "length",required = false) Integer length) {
		logger.debug("list agent with params: search = {}, lock={}, start = {}, length = {}.", search,lock, start, length);
        if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
		return agentService.list(search,lock,start, length);
	}

	@RequestMapping(value="/count", method=RequestMethod.GET)
	public Count count(@RequestParam(value = "search",required = false) String search, @RequestParam(value = "lock",required = false) String lock) {
		logger.debug("count agent with params: search = {},lock={}, start = {}, length = {}.", search,lock);
		return agentService.count(search, lock);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public Agent create(@RequestBody JSONObject agent) {
		
		logger.debug("create agent: {}", agent);
		Agent agentCreate = new Agent();
		agentCreate.setEmail(agent.getString("email"));
		agentCreate.setName(agent.getString("name"));
		agentCreate.setPassword(agent.getString("password"));
		agentCreate.setLocked(false);
		
		return agentService.create(agentCreate);
	}
	
	@RequestMapping(value="/find", method=RequestMethod.GET)
	public Agent find(@RequestParam("id") long id) {
		logger.debug("find agent with params: id = {}.", id);
		return agentService.find(id);
	}
	
	@RequestMapping(value="/changeStatus", method=RequestMethod.GET)
	public Agent changeStatus(@RequestParam("id") long id,@RequestParam("isLocked") boolean isLocked) {
		logger.debug("update agent with params: id = {}, isLocked = {}.", id,isLocked);
		
		return agentService.changeStatus(id, isLocked);
	}
	

}
