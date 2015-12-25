package com.vipkid.security;

public class VerificationCodeGeneratorTester {

	public static void main(String[] args) {
		String verificationCode = VerificationCodeGenerator.generate(4);
		System.out.println(verificationCode);
	}
}
