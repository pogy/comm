package com.vipkid.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class UrlUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    public static Map<String, String> getRequestMapfromUrl(String url) {
        Map<String, String> resultMap = Maps.newHashMap();
        try {
            for (String pairs : url.split("&")) {
                String[] pair = pairs.split("=");
                if (pair.length == 2) {
                    resultMap.put(pair[0], pair[1]);
                }
            }
            return resultMap;
        } catch (Exception e) {
            logger.error("parse url error ", e);
            return null;
        }
    }

    public static String decodeURL(String url) {
        if (StringUtils.isNotBlank(url)) {
            try {
                url = URLDecoder.decode(url,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("URL decode error,url={}",url);
            }
        }
        return url;
    }

}
