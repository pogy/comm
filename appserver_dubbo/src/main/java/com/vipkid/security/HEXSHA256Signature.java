package com.vipkid.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HEXSHA256Signature {
     private static Logger logger = LoggerFactory.getLogger(HEXSHA256Signature.class.getSimpleName());
	
	public static String sign(String message) {
		String value = null;
		if (message != null){
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				byte[] bytes = messageDigest.digest(message.getBytes());
				value = Hex.encodeHexString(bytes);
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage());
			} 
		}	
		return value;
	}
}
