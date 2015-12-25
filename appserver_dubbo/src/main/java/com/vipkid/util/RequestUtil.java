package com.vipkid.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zfl on 2015/5/19.
 */
public class RequestUtil {
    public static SerializableRequest buildRequest(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>(request.getParameterMap().size());
        params.put("url", request.getRequestURI());
        Map<String, String> cookie = getCookies(request);
        Map<String, Serializable> attributes = new HashMap<String, Serializable>();
        Map<String, Serializable> sessionAttributes = new HashMap<String, Serializable>();
        return new SerializableRequest(params, cookie, attributes, sessionAttributes);
    }

    private static Map<String, String> getCookies(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : request.getCookies()) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
        return map;
    }

    private static boolean isBlank(char c) {
        return Character.isWhitespace(c) || c == 14;
    }

    public static String getURI(String URI) {
        if (URI != null && URI.length() > 0) {
            try {
                URI = URLDecoder.decode(URI, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            URI = URI.toLowerCase();
            StringBuilder stringBuilder = new StringBuilder(URI.length());
            char[] chars = URI.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (stringBuilder.length() != 0 && stringBuilder.charAt(stringBuilder.length() - 1) == '/' &&
                        (chars[i] == '/' || isBlank(chars[i]) ||
                                (chars[i] == '.' && (chars[i + 1] == '.' || chars[i + 1] == '/')))) {
                    continue;
                }
                stringBuilder.append(chars[i]);
            }
            URI = stringBuilder.toString();
        }
        return URI;
    }

    public static String getRequestURI(HttpServletRequest request) {
        String URI = request.getRequestURI();

        return getURI(URI);
    }


    public static class SerializableRequest implements Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Map<String, String> params;
        private Map<String, String> cookies;
        private Map<String, Serializable> attributes;
        private Map<String, Serializable> sessionAttributes;

        private SerializableRequest(Map<String, String> params, Map<String, String> cookies, Map<String, Serializable> attributes, Map<String, Serializable> sessionAttributes) {
            this.params = params;
            this.cookies = cookies;
            this.attributes = attributes;
            this.sessionAttributes = sessionAttributes;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public Map<String, String> getCookies() {
            return cookies;
        }

        public Map<String, Serializable> getAttributes() {
            return attributes;
        }

        public Map<String, Serializable> getSessionAttributes() {
            return sessionAttributes;
        }
    }

    public static void main(String... args) {
        String URI = "//item////path1//path2";
        System.out.println("1:" + getURI(URI));

    }
}
