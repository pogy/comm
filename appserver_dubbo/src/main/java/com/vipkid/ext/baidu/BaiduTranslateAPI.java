package com.vipkid.ext.baidu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vipkid.util.Configurations;

public class BaiduTranslateAPI {

	
	private static final Logger logger = LoggerFactory.getLogger(BaiduTranslateAPI.class);
	
	public static String translate(String text) {
		logger.info("baidu translation source text = {}", text);
		StringBuffer buffer = new StringBuffer();
		try {
			String json = sendPost(buildRequestUrl(text));
			logger.info("baidu translation response json = {}", json);
			if (StringUtils.isNotBlank(json)) {
				BaiduTransResp resp =  new Gson().fromJson(json, BaiduTransResp.class);
					if (resp != null) {
						if (resp.getError_code() == null) {
							if (CollectionUtils.isNotEmpty(resp.getTrans_result())) {
								Iterator<ResultItem> iterator = resp.getTrans_result().iterator();
								while (iterator.hasNext()) {
									buffer.append(iterator.next().getDst());
								}
							} else {
								logger.info(" baidu translation,translate result is empty");
							}
							
						} else {
							logger.info("error when handle baidu translation errorCode = {},error message = {}",
									resp.getError_code(),resp.getError_msg());
						}
					}
				}
			} catch(Exception e) {
				logger.info("error when handle baidu translation", e);
			}
		
		String outText = null;
		if (buffer.length() > 0) {
			outText = buffer.toString();
		}
		
		logger.info("baidu translation translated outText = {}", outText);
		return outText;
	}
	
	
	public static String sendPost(String url) {
		logger.info("baidu translation send url = {}", url);
		try {
			HttpPost requestPost = new HttpPost(url);
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(requestPost);
			if (response.getStatusLine() != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			} else {
				logger.info("error when handle baidu translation HttpStatus = {}", response.getStatusLine() != null ?
						response.getStatusLine().getStatusCode() : null );
			}
		} catch (ClientProtocolException e) {
			logger.info("error when handle baidu translation = {}", e);
		} catch (IOException e) {
			logger.info("error when handle baidu translation = {}", e);
		}
		
		return null;
	}
	
	
	private static String buildRequestUrl(String text) throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer(Configurations.BAIDU.FanYi.URL);
		buffer.append("?")
			  .append("client_id").append("=").append(Configurations.BAIDU.FanYi.CLIENT_ID).append("&")
			  .append("q").append("=").append(URLEncoder.encode(text, Configurations.BAIDU.FanYi.CHARSET)).append("&")
			  .append("from").append("=").append(Configurations.BAIDU.FanYi.FROM).append("&")
			  .append("to").append("=").append(Configurations.BAIDU.FanYi.TO);
		
		logger.info("baidu translation, buildRequestUrl = {} ", buffer.toString());
		return buffer.toString();
	}
}
