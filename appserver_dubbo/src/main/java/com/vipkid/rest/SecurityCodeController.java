package com.vipkid.rest;

import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.ext.sms.yunpian.SendSMSResponse;
import com.vipkid.model.Parent;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.UserRepository;
import com.vipkid.service.ParentService;
import com.vipkid.service.exception.FailToSendSMSServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.util.TextUtils;

@RestController
@RequestMapping("/api/service/public/securityCode")
public class SecurityCodeController {
	private Logger logger = LoggerFactory.getLogger(SecurityCodeController.class.getSimpleName());
	@Resource
	private ParentService parentService;
	@Resource
	private UserRepository userRepository;

	private RedisClient redisClient = RedisClient.getInstance();
	
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public String sendSecurityCode(@RequestParam("username") final String username) {
		logger.info("Enter getSecurityCode()");
		final String randomNumber = TextUtils.generateRandomNumber(4);
		logger.info("mobile: " + username + "   " + "Random number:" + randomNumber);

		SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(username, randomNumber);
		if (sendSMSResponse.isSuccess()) {
			System.out.println(sendSMSResponse.getCode());
			return randomNumber;
		} else {
			System.out.println(sendSMSResponse.getCode());
			return null;
		}
	}

	@RequestMapping(value = "/sendForForgetPassword", method = RequestMethod.GET)
	public String sendSecurityCodeForForgetPassword(@RequestParam("username") final String username) {
		logger.info("Enter getSecurityCode()");
		final String randomNumber = TextUtils.generateRandomNumber(4);
		logger.info("mobile: " + username + "   " + "Random number:" + randomNumber);

		Parent parent = parentService.findByUsername(username);

		if (parent != null) {
			SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(username, randomNumber);
			if (sendSMSResponse.isSuccess()) {
				System.out.println(sendSMSResponse.getCode());
				parent.setVerifyCode(randomNumber);
				parentService.update(parent);

				return randomNumber;
			} else {
				System.out.println(sendSMSResponse.getCode());
				throw new FailToSendSMSServiceException("Failed to send forget password verify sms");
			}
		} else {
			logger.debug("User not find");
			throw new UserNotExistServiceException("Parent not find");
		}

	}
	
	
	// 2015-09-26
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendSecurityCode1(@RequestParam("mobile") final String username,@RequestParam(value="token", required=true)String token,@RequestParam(value="key", required=true)String key) {
		logger.info("Enter getSecurityCode1()");
		
		// 2015-09-26 -- security
		if (null == key) {
			return "";
		}
		
		String keyV = key.replace("\"", "");
		String tokenV = redisClient.get(keyV);
		
		if (null == tokenV) {
			return "";
		}
		
		String token1 = token.replace("\"", "");
		if (!tokenV.equals(token1)) {
			return "";
		}
		
		redisClient.del(key);
		
		final String randomNumber = TextUtils.generateRandomNumber(4);
		logger.info("mobile: " + username + "   " + "Random number:" + randomNumber);

		SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(username, randomNumber);
		if (sendSMSResponse.isSuccess()) {
			System.out.println(sendSMSResponse.getCode());
			return randomNumber;
		} else {
			System.out.println(sendSMSResponse.getCode());
			return null;
		}
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
		
		String keyV = key.replace("\"", "");
		
		redisClient.set(keyV,strToken);
		redisClient.expire(keyV, 1200);
	
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
