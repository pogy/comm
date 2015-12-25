package com.vipkid.util;

import javax.servlet.http.Cookie;

public class CookieUtils {
	public static final String HTTP_COOKIE_AUTHENTICATION = "Authorization";
	public static final String DOMAIN = ".vipkid.com.cn";
	
	public static String get(Cookie [] cookies, String key) {
		String result = null;
    	if (cookies != null) {
    		for (Cookie cookie : cookies) {
    			if (cookie.getName().equals(HTTP_COOKIE_AUTHENTICATION)) {
    				result = cookie.getValue();
    			}
    		}
    	}
    	
    	return result;
	}

    public static Cookie createVIPKIDCookie(String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(DOMAIN);
        cookie.setPath("/");                                      
        return cookie;
    }
    
    public static Cookie delVIPKIDCookie(String name){
    	Cookie cookie = new Cookie(name, null);
        cookie.setDomain(DOMAIN);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
