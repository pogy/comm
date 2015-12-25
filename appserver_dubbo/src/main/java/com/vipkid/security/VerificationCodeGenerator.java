package com.vipkid.security;

import java.util.Random;

public class VerificationCodeGenerator {
	
	public static String generate(int length) {
		Random random = new Random();
		StringBuilder sbVerificationCode = new StringBuilder();
		for(int i = 0; i < length; i++) {
			sbVerificationCode.append(random.nextInt(10));
		}
		
		return sbVerificationCode.toString();
	}
	
	public static String generate() {
		return generate(4);
	}
}
