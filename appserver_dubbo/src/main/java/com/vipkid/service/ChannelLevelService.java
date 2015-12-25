package com.vipkid.service;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.ChannelLevel;
import com.vipkid.repository.ChannelLevelRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.ChannelLevelAlreadyExistServiceException;
import com.vipkid.service.pojo.Option;

@Service
public class ChannelLevelService {
	private Logger logger = LoggerFactory.getLogger(ChannelLevelService.class.getSimpleName());

	@Resource
	private ChannelLevelRepository channelLevelRepository;
	
	@Resource
	private SecurityService securityService;

	public ChannelLevel find(long id) {
		logger.info("find channel level for id = {}", id);
		return channelLevelRepository.find(id);
	}

	public ChannelLevel create(ChannelLevel channelLevel) {
		logger.info("create channelLevel: {}", channelLevel);
		ChannelLevel findChannelLevel = channelLevelRepository.findByLevel(channelLevel.getLevel());
		if(findChannelLevel == null) {
			channelLevelRepository.create(channelLevel);
			securityService.logAudit(Level.INFO, Category.CHANNEL_LEVEL_CREATE, "Create channel level: " + channelLevel.getLevel());
			
			return channelLevel;
		}else{
			throw new ChannelLevelAlreadyExistServiceException("channel level already exist.");
		}
	}
	
	public List<Option> findAll() {
		logger.info("find all channelLevel");
		List<ChannelLevel> channelLevels = channelLevelRepository.findAll();
		if(channelLevels.isEmpty()) {
			return null;
		}else {
			List<Option> options = new LinkedList<Option>();
			for (ChannelLevel channelLevel :channelLevels) {
				Option option = new Option(channelLevel.getLevel(), channelLevel.getLevel());
				options.add(option);
			}
			return options;
		}
	}
	
}
