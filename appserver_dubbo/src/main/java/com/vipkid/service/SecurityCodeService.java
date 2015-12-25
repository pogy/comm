package com.vipkid.service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.ext.sms.yunpian.SendSMSResponse;
import com.vipkid.model.Parent;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.UserRepository;
import com.vipkid.service.exception.FailToSendSMSServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.util.TextUtils;

@Service
public class SecurityCodeService {
	private Logger logger = LoggerFactory.getLogger(SecurityCodeService.class.getSimpleName());
	@Context
	private ServletContext servletContext;
	@Resource
	private ParentRepository parentRepository;
	@Resource
	private UserRepository userRepository;

	public String sendSecurityCode(final String username){
		logger.debug("Enter getSecurityCode()");
		final String randomNumber = TextUtils.generateRandomNumber(4);
		logger.debug("Random number:" + randomNumber);
		
		SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(username, randomNumber);
		if(sendSMSResponse.isSuccess()) {
			System.out.println(sendSMSResponse.getCode());
			return randomNumber;
		} else {
			System.out.println(sendSMSResponse.getCode());
			return null;
		}
	}
	
	public String sendSecurityCodeForForgetPassword(final String username){
		logger.debug("Enter getSecurityCode()");
		final String randomNumber = TextUtils.generateRandomNumber(4);
		logger.debug("Random number:" + randomNumber);
		
		Parent parent = parentRepository.findByUsername(username);
		
		if (parent != null) {
			SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(username, randomNumber);
			if (sendSMSResponse.isSuccess()) {
				System.out.println(sendSMSResponse.getCode());
				parent.setVerifyCode(randomNumber);
				parentRepository.update(parent);
				
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
}
