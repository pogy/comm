package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Agent;
import com.vipkid.service.AgentAuthService;
import com.vipkid.service.pojo.Credential;

@RestController
@RequestMapping(value="/api/service/public/agentAuth")
public class AgentAuthController {

	private Logger logger = LoggerFactory.getLogger(AgentAuthController.class);
	@Resource
	private AgentAuthService agentAuthService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Agent login(@RequestBody Credential credential) {
		logger.info("param={} ", credential);
		return agentAuthService.login(credential);
	}

}
