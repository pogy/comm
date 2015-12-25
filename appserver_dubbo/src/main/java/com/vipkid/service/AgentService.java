package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Agent;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.AgentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;

@Service
public class AgentService {
	
	private Logger logger = LoggerFactory.getLogger(AgentService.class.getSimpleName());
	
	@Resource
	private AgentRepository agentRepository;
	
	@Resource
	private SecurityService securityService;

	public List<Agent> list(String search, String lock, Integer start, Integer length) {
		logger.debug("list agent with params: search = {}, lock={}, start = {}, length = {}.", search,lock, start, length);
		return agentRepository.list(search,lock,start, length);
	}

	public Count count(String search, String lock) {
		logger.debug("count agent with params: search = {},lock={}, start = {}, length = {}.", search,lock);
		return new Count(agentRepository.count(search,lock));
	}
	
	public Agent create(Agent agent) {
		logger.debug("create agent: {}", agent);
		
		Agent findAgent =agentRepository.findByUsername(agent.getEmail());
		if(findAgent == null) {
			agentRepository.create(agent);
			securityService.logAudit(Level.INFO, Category.PARENT_CREATE, "Create agent: " + agent.getEmail());
			return agent;
		}else {
			throw new UserAlreadyExistServiceException("Agent already exist.");
		}
	}

	public Agent find(long id) {
		logger.debug("find agent with params: id = {}.", id);
		return agentRepository.find(id);
	}

	public Agent changeStatus(long id, boolean isLocked) {
		logger.debug("update agent with params: id = {}, isLocked = {}.", id,isLocked);
		
		Agent agent =agentRepository.find(id);
		if(agent != null) {
			agent.setLocked(isLocked);
			agentRepository.update(agent);
			return agent;
		}else {
			throw new UserNotExistServiceException("agent[id: {}] is not exist.", id);
		}
	}
	
}
