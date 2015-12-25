package com.vipkid.controller.util;

public class Page {	
	public static String redirectTo(String page) {
		return "redirect:" + page;
	}
	
	public static String forwardTo(String page) {
		return "forward:" + page;
	}
}
