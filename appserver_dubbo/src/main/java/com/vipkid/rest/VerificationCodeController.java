package com.vipkid.rest;

import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.redis.RedisClient;
import com.vipkid.service.VerificationCodeService;

@RestController
@RequestMapping("/api/service/public/verificationCode")
public class VerificationCodeController {
	private Logger logger = LoggerFactory.getLogger(VerificationCodeController.class.getSimpleName());
	
	private RedisClient redisClient = RedisClient.getInstance();
	
	@Resource
	private VerificationCodeService verificationCodeService;

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String send(@RequestParam(value="mobile", required=true) String mobile,@RequestParam(value="token", required=true)String token,@RequestParam(value="key", required=true)String key) {
		logger.info("send verification code, mobile = {} token:{}", mobile, token);
		if (null == key) {
			return "";
		}
		
		String tokenV = redisClient.get(key);
		
		if (null == tokenV) {
			return "";
		}
		
		if (!tokenV.equals(token)) {
			return "";
		}
		
		redisClient.del(key);
		
		return verificationCodeService.send(mobile);
	}
	
	/**
	 * 获取token
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/getSecToken", method = RequestMethod.GET)
	public String getSecToken(@RequestParam(value="key", required=true) String key) {
		UUID uuid = UUID.randomUUID();
		String strToken = uuid.toString();
		
		redisClient.set(key,strToken);
		redisClient.expire(key, 1200);
	
		return strToken;
	}
	
	/**
	 * 获取token
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/getSecTokenKey", method = RequestMethod.GET)
	public String getSecTokenKey() {
		UUID uuid = UUID.randomUUID();
		String strToken = uuid.toString();
		
		return strToken;
	}
}
