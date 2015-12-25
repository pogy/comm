package com.vipkid.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HMACSHA256Signature {
	private static Logger logger = LoggerFactory.getLogger(HMACSHA256Signature.class.getSimpleName());
	
	public static String sign(String key, String message) {
		String hash = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			mac.init(secretKeySpec);
			
			hash = encodeUrlSafe(mac.doFinal(message.getBytes()));
		} catch (Exception e) {
			logger.error("Exception when sign message = {} with key = {}: {}", message, key, e);
		}

		return hash;
	}
	
	private static String encodeUrlSafe(byte[] data) {
	    String strcode = Base64.encode(data);
	    byte[] encode = strcode.getBytes(); 
	    for (int i = 0; i < encode.length; i++) {
	        if (encode[i] == '+') {
	            encode[i] = '-';
	        } else if (encode[i] == '/') {
	            encode[i] = '_';
	        } else if (encode[i] == '=') {
	        	encode[i] = ' ';
	        }
	    }
	    return new String(encode).trim();
	}
}
