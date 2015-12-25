package com.vipkid.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.security.HEXSHA256Signature;
import com.vipkid.security.VerificationCodeGenerator;

@Service
public class VerificationCodeService {
	private Logger logger = LoggerFactory.getLogger(VerificationCodeService.class.getSimpleName());

	public String send(String mobile) {
		String verificationCode = VerificationCodeGenerator.generate();	
		String signedVerificationCode = HEXSHA256Signature.sign(verificationCode);
		
		logger.info("verificationCode = {}, signedVerificationCode = {}", verificationCode, signedVerificationCode);
		
		SMS.sendVerificationCodeSMS(mobile, verificationCode);
		
		return signedVerificationCode;
	}
}
