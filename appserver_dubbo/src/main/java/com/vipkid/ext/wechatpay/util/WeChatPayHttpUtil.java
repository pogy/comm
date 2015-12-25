package com.vipkid.ext.wechatpay.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.util.CharSet;

public class WeChatPayHttpUtil {
	private static Logger logger = LoggerFactory.getLogger(WeChatPayHttpUtil.class.getSimpleName());
	
	private static final String CONTENT_TYPE = "text/xml;charset=utf-8";
	
	public static <T> T post(String text,String uri,ResponseHandler<T> responseHandler) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(text));
	        httpPost.setHeader("Content-Type", CONTENT_TYPE);
			return httpClient.execute(httpPost, responseHandler);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when close httpClient: {}", e);
			}
		}
	}
	
	public static String post(String text,String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(text));
	        httpPost.setHeader("Content-Type", CONTENT_TYPE);
			return httpClient.execute(httpPost, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse httpResponse)
						throws ClientProtocolException, IOException {
					String returnText = ""; 
					if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity httpEntity = httpResponse.getEntity();
						returnText = EntityUtils.toString(httpEntity, CharSet.UTF_8);
					}
					return returnText;
				}
				
			});
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when close httpClient: {}", e);
			}
		}
	}
	
	public static String get(String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String returnText = "";
		HttpGet httpGet = new HttpGet(url);
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				returnText = EntityUtils.toString(httpEntity, CharSet.UTF_8);
			}
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return returnText;
		
	}

}
