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

import com.vipkid.service.SettingService;
import com.vipkid.service.pojo.KeyValue;

@RestController
@RequestMapping("/api/service/private/settings")
public class SettingController {
	private Logger logger = LoggerFactory.getLogger(SettingController.class.getSimpleName());
	
	@Resource
	private SettingService settingService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public String find(@RequestParam("key") String key) {
		logger.info("Get redis data for key = {}", key);
		return settingService.find(key);
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public List<KeyValue> findAll() {
		logger.info("List all redis keys.");
		return settingService.findAll();
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public KeyValue update(@RequestBody KeyValue keyValue) {
		logger.info("update redis data for key = {}, and value = {}", keyValue.getKey(), keyValue.getValue());
		return settingService.update(keyValue);
	}
	

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public KeyValue create(@RequestBody KeyValue keyValue) {
		logger.info("create redis data for key = {}, and value = {}", keyValue.getKey(), keyValue.getValue());
		return settingService.create(keyValue);
	}
	
	@RequestMapping(value = "/createChannelLevel", method = RequestMethod.POST)
	public KeyValue createChannelLevel(@RequestBody KeyValue keyValue) {
		return settingService.createChannelLevel(keyValue);
	}
	
	@RequestMapping(value = "/findChannelLevel", method = RequestMethod.GET)
	public List<KeyValue> findChannelLevel() {
		return settingService.findChannelLevel();
	}
}
