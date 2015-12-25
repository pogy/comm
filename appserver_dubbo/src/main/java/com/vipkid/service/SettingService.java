package com.vipkid.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.redis.RedisClient;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.KeyValue;
import com.vipkid.util.Redis;

@Service
public class SettingService {
	private Logger logger = LoggerFactory.getLogger(SettingService.class.getSimpleName());
	
	@Resource
	private SecurityService securityService;

	public String find(String key) {
		logger.info("Get redis data for key = {}", key);
		return RedisClient.getInstance().get(key);
	}
	
	public List<KeyValue> findAll() {
		logger.info("List all redis keys.");
		/*Set<String> keys = RedisClient.getInstance().getKeys(Redis.GlobalSetting.DEFAULT_KEY + "*");
		List<KeyValue> keyValues = new LinkedList<KeyValue>();
		String value = null;
		for(String key : keys) {
			value = RedisClient.getInstance().get(key);
			KeyValue keyValue = new KeyValue(key, value);
			keyValues.add(keyValue);
		}*/
		
		// 修改: 不列出所有key，改为列出所需key
		List<KeyValue> keyValues = new LinkedList<KeyValue>();
		String value = null;
		for(Redis redis : Redis.values()) {
			value = RedisClient.getInstance().get(redis.getKey());
			KeyValue keyValue = new KeyValue(redis.getKey(), value);
			keyValues.add(keyValue);
		}
		return keyValues;
	}

	public KeyValue update(KeyValue keyValue) {
		logger.info("update redis data for key = {}, and value = {}", keyValue.getKey(), keyValue.getValue());
		securityService.logAudit(Level.WARNING, Category.SETTINGS_UPDATE, "Update redis data: key = " + keyValue.getKey() + "value = " + keyValue.getValue());
		
		RedisClient.getInstance().set(keyValue.getKey(), keyValue.getValue());
		
		return keyValue;
	}
	
	public KeyValue create(KeyValue keyValue) {
		logger.info("create redis data for key = {}, and value = {}", keyValue.getKey(), keyValue.getValue());
		securityService.logAudit(Level.WARNING, Category.SETTINGS_CREATE, "Create redis data: key = " + keyValue.getKey() + "value = " + keyValue.getValue());
		
		RedisClient.getInstance().set(keyValue.getKey(), keyValue.getValue());
		
		return keyValue;
	}
	
	public KeyValue createChannelLevel(KeyValue keyValue) {
		keyValue.setKey(Redis.MarketingSetting.CHANNEL_LEVEL_KEY + keyValue.getValue());
		logger.info("create redis data for key = {}, and value = {}", keyValue.getKey(), keyValue.getValue());
		securityService.logAudit(Level.WARNING, Category.SETTINGS_CREATE, "Create redis data: key = " + keyValue.getKey() + "value = " + keyValue.getValue());
		
		RedisClient.getInstance().set(keyValue.getKey(), keyValue.getValue());
		
		return keyValue;
	}
	
	public List<KeyValue> findChannelLevel() {
		Set<String> keys = RedisClient.getInstance().getKeys(Redis.MarketingSetting.DEFAULT_KEY + "*");
		List<KeyValue> keyValues = new LinkedList<KeyValue>();
		String value = null;
		for(String key : keys) {
			value = RedisClient.getInstance().get(key);
			KeyValue keyValue = new KeyValue(key, value);
			keyValues.add(keyValue);
		}
		
		return keyValues;
	}
}
