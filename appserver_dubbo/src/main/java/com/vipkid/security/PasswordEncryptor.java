package com.vipkid.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordEncryptor {
	private static final Logger logger = LoggerFactory.getLogger(PasswordEncryptor.class.getSimpleName());
	
	private class MessageDigestAlgorithm {
		private static final String SHA256 = "SHA-256";
	}
	
	public static String encrypt(String password) {
		String encryptPassword = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(MessageDigestAlgorithm.SHA256);
			byte[] bytes = messageDigest.digest(password.getBytes());
			String hex = Hex.encodeHexString(bytes);
			encryptPassword = Base64.encodeBase64URLSafeString(hex.getBytes());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		} 
		return encryptPassword;
	}
	
	
	
}
