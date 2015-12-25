package com.vipkid.ext.youdao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.vipkid.util.CharSet;
import com.vipkid.util.Configurations;

public class YoudaoTranslateAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(YoudaoTranslateAPI.class);
	
	public static final Map<String,String> errorCodeMap;
	static {
		errorCodeMap = Maps.newHashMap();
		errorCodeMap.put("0", "正常");
		errorCodeMap.put("20", "要翻译的文本过长");
		errorCodeMap.put("30", "无法进行有效的翻译");
		errorCodeMap.put("40", "不支持的语言类型");
		errorCodeMap.put("50", "无效的key");
		errorCodeMap.put("60", "无词典结果，仅在获取词典结果生效");
	}
	
	
	public static String translate(String text) {
		logger.info("youdao translation source text = {}", text);

		if (StringUtils.isBlank(text)) {
			return null;
		}
		
		if (text.length() <200) {
			return translateInternal(text);
		}
		
		String[] textArray = (text + " ").split("\\.");
		String[] result = new String[textArray.length];
		for (int i=0; i< textArray.length; i++) {
			if (StringUtils.isNotBlank(textArray[i])) {
				result[i] = translateInternal(textArray[i]);
			} else {
				result[i] = "";
			}
		}
		String outText = String.join("。", result);
		logger.info("youdao translation translated outText = {}", outText);
		return outText;
	}
	
	private static String translateInternal(String text) {
		logger.info("youdao translation source text = {}", text);
		StringBuffer buffer = new StringBuffer();
		try {
			String json = sendGet(buildRequestUrl(text));
			logger.info("youdao translation response json = {}", json);
			if (StringUtils.isNotBlank(json)) {
					YouDaoTransResp resp =  new Gson().fromJson(json, YouDaoTransResp.class);
					if (resp != null) {
						if (resp.getErrorCode() != null && resp.getErrorCode() == 0
								&& resp.getTranslation() != null) {
							Iterator<String> iterator = resp.getTranslation().iterator();
							while (iterator.hasNext()) {
								buffer.append(iterator.next());
							}
						} else {
							logger.info("error when handle youdao translation errorCode = {},error message = {}",
									resp.getErrorCode(),errorCodeMap.get(resp.getErrorCode()));
						}
					}
				}
			} catch(Exception e) {
				logger.info("error when handle youdao translation", e);
			}
		
		String result = null;
		if (buffer.length() > 0) {
			result = buffer.toString();
		}
		
		logger.info("youdao translation translated text = {}", result);
		return result;
	}
	
	
	public static String sendGet(String url) {
		logger.info("youdao translation send url = {}", url);
		try {
			HttpGet requestGet = new HttpGet(url);
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(requestGet);
			if (response.getStatusLine() != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			} else {
				logger.info("error when handle youdao translation HttpStatus = {}", response.getStatusLine() != null ?
						response.getStatusLine().getStatusCode() : null );
			}
		} catch (ClientProtocolException e) {
			logger.info("error when handle youdao translation = {}", e);
		} catch (IOException e) {
			logger.info("error when handle youdao translation = {}", e);
		}
		
		return null;
	}
	
	
	private static String buildRequestUrl(String text) throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer(Configurations.YouDao.FANYI_URL);
		buffer.append("?")
			  .append("keyfrom").append("=").append(Configurations.YouDao.KEY_FROM).append("&")
			  .append("key").append("=").append(Configurations.YouDao.KEY).append("&")
			  .append("type").append("=").append(Configurations.YouDao.TYPE).append("&")
			  .append("doctype").append("=").append(Configurations.YouDao.DOCT_YPE).append("&")
			  .append("version").append("=").append(Configurations.YouDao.VERSION).append("&")
			  .append("only").append("=").append(Configurations.YouDao.ONLY).append("&")
			  .append("q").append("=").append(URLEncoder.encode(text,  CharSet.UTF_8));
		
		logger.info("youdao translation, buildRequestUrl = {} ", buffer.toString());
		return buffer.toString();
	}
}
