package com.vipkid.service;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Agent;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.AgentAuthRepository;
import com.vipkid.security.TokenGenerator;
import com.vipkid.service.exception.UserLockedServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Credential;

@Service
public class AgentAuthService {
	
	private Logger logger = LoggerFactory.getLogger(AgentService.class.getSimpleName());
	
	@Resource
	private AgentAuthRepository agentAuthRepository;
	
	public Agent login(Credential credential) {
		String username = credential.getUsername();
		String password = credential.getPassword();
		
		Agent agent=agentAuthRepository.findByUsernameAndPassword(username, password);
		if(agent == null) {
			throw new UserNotExistServiceException("Agent[username: {}] is not exist.", username);
		}else {
			if(agent.isLocked()) {
				throw new UserLockedServiceException("Agent[username: {}] is locked.", username);
			}
			
			if(TextUtils.isEmpty(agent.getToken())) {
				agent.setToken(TokenGenerator.generate());
			}
			agentAuthRepository.update(agent);
            String redisKey = KeyGenerator.generateKey(String.valueOf(agent.getId()),agent.getToken());
            RedisClient.getInstance().setObject(redisKey,agent);
			logger.info("Staff[username: {}] is login.", agent.getEmail());
		}
		return agent;
	}
}
