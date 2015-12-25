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

import com.vipkid.model.Channel;
import com.vipkid.service.ChannelService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping(value = "/api/service/private/channel")
public class ChannelController {
	private Logger logger = LoggerFactory.getLogger(AgentController.class.getSimpleName());

	@Resource
	private ChannelService channelService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Channel> list(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length) {
		logger.debug("list agent with params: search = {}, start = {}, length = {}.", search, start, length);
		if (null == start) {
			start = 0;
		}
		if (null == length) {
			length = 0;
		}
		return channelService.list(search, start, length);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Channel update(@RequestBody Channel channnel) {
		logger.info("Update channel ", channnel.getId());
		return channelService.update(channnel);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value = "search", required = false) String search) {
		logger.debug("count agent with params: search = {},start = {}, length = {}.", search);
		return channelService.count(search);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Channel create(@RequestBody Channel channel) {
		logger.info("create channel: {}", channel);
		return channelService.create(channel);
	}

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Channel find(@RequestParam(value = "id", required = true) long channelId) {
		logger.info("find channel with params: search = {},start = {}, length = {}.", channelId);
		return channelService.find(channelId);
	}
	
	@RequestMapping(value = "/getChannelList", method = RequestMethod.GET)
	public List<String> getChannelList() {
		logger.info("find channel names");
		return channelService.getChannelList();
	}
	@RequestMapping(value = "/findBySourceName", method = RequestMethod.GET)	
	public Channel findBySourceName(String channelName){
		logger.info("find channel by name");
		return channelService.findBySourceName(channelName);
	}

}
