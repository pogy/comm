package com.vipkid.security;

import java.util.Random;

public class PasswordGenerator {
	
	public static String generate(int length) {
		Random random = new Random();
		StringBuilder sbPassword = new StringBuilder();
		for(int i = 0; i < length; i++) {
			sbPassword.append(random.nextInt(10));
		}
		
		return sbPassword.toString();
	}
	
	public static String generate() {
		return generate(6);
	}
}
