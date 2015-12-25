package com.vipkid.ext.wechatpay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.util.CharSet;

public class WeChatPaySignUtil {

	private static Logger logger = LoggerFactory.getLogger(WeChatPaySignUtil.class.getSimpleName());
	/**
	 * 生成签名
	 * @param paramMap 参与生成签名的 key=value对
	 * @param input_charset 编码
	 * @return 返回签名
	 * @throws java.io.UnsupportedEncodingException
	 */
    public static String sign(Map<String,String> paramMap, String key, String input_charset,boolean isEncode) throws UnsupportedEncodingException {
    	String linkStr = createLinkStr(paramMap,key,isEncode);
    	String sign = DigestUtils.md5Hex(getContentBytes(linkStr, input_charset));
    	logger.info("get sign :" + sign);
    	sign = sign != null ? sign.toUpperCase() : sign;
    	return sign;
    }
    
    /**
     * 签名字符串
     * @param paramMap 参与生成签名的 key=value对
     * @param sign 签名结果
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名验证结果
     * @throws java.io.UnsupportedEncodingException
     */
    public static boolean verifySign(Map<String,String> paramMap, String sign, String key, String input_charset) throws UnsupportedEncodingException {
		boolean result = false;
		String mySign = WeChatPaySignUtil.sign(paramMap, key, input_charset,false);
		logger.info("wechatpay sign verify :  mySign = " + mySign + ", sign = " + sign);
		if (mySign != null && mySign.equals(sign)) {
			result = true;
		}
		return result;
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws java.security.SignatureException
     * @throws java.io.UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }
    
    /** 
     * 把params中key从小到大排序，并按照"key=value(对value进行urlencode)"的模式用"&"字符拼接成字符串,过滤掉值为空的key
     * @param params 需要排序并参与字符拼接的"key=value" map
     * @return 拼接后字符串
     * @throws java.io.UnsupportedEncodingException
     */
    private  static String createLinkStr(Map<String, String> params,String key,boolean isEncode) throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer strBuffer = new StringBuffer();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()) {
        	String key1 = iterator.next();
            String value = params.get(key1);
            if (value != null && !"".equals(value.trim())) {
            	strBuffer.append(key1).append("=").append( isEncode ? URLEncoder.encode(value, CharSet.UTF_8) : value).append("&");
            }
        }
        logger.info("WeChatPay sign  except key, linkStr : " + strBuffer.toString());
        strBuffer.append("key").append("=").append(key);
        return strBuffer.toString();
    }

}
