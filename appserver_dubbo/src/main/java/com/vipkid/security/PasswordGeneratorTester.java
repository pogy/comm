package com.vipkid.security;

public class PasswordGeneratorTester {

	public static void main(String[] args) {
		String password = PasswordGenerator.generate();
		System.out.println(password);
	}
}
