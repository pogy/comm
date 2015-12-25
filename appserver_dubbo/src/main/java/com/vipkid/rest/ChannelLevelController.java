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

import com.vipkid.model.ChannelLevel;
import com.vipkid.service.ChannelLevelService;
import com.vipkid.service.pojo.Option;

@RestController
@RequestMapping("/api/service/private/channelLevels")
public class ChannelLevelController {
	private Logger logger = LoggerFactory.getLogger(ChannelLevelController.class.getSimpleName());
	
	@Resource
	private ChannelLevelService channelLevelService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public ChannelLevel find(@RequestParam("id") long id) {
		logger.info("find channelLevel for id = {}", id);
		return channelLevelService.find(id);
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public List<Option> findAll() {
		logger.info("List all channelLevel.");
		return channelLevelService.findAll();
	}
	

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ChannelLevel create(@RequestBody ChannelLevel channelLevel) {
		logger.info("create channelLevel for level = {}", channelLevel.getLevel());
		return channelLevelService.create(channelLevel);
	}
}
