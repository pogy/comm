package com.vipkid.ext.moxtra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.model.json.gson.GsonManager;
import com.vipkid.security.HMACSHA256Signature;
import com.vipkid.util.CharSet;
import com.vipkid.util.Configurations.Moxtra;

public class MoxtraAPI {
	private static Logger logger = LoggerFactory.getLogger(MoxtraAPI.class.getSimpleName());
	
	public static GetAccessTokenResponse getAccessToken() { 
		return getAccessToken("VIPKID");
	}
	public static GetAccessTokenResponse getAccessToken(String uniqueId) {
		String timestamp = Long.toString(System.currentTimeMillis());
		
		StringBuffer sbMessage = new StringBuffer();
		sbMessage.append(Moxtra.CLIENT_ID);
		sbMessage.append(uniqueId);
		sbMessage.append(timestamp);
		
		String signature = HMACSHA256Signature.sign(Moxtra.CLIENT_SECRET, sbMessage.toString()).trim();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(Moxtra.Service.GET_ACCESS_TOKEN);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", Moxtra.CLIENT_ID));
		params.add(new BasicNameValuePair("client_secret", Moxtra.CLIENT_SECRET));
		params.add(new BasicNameValuePair("grant_type", Moxtra.UNIQUEID_GRANT_TYPE));
		params.add(new BasicNameValuePair("uniqueid", uniqueId));
		params.add(new BasicNameValuePair("timestamp", timestamp));
		params.add(new BasicNameValuePair("signature", signature));
		
		GetAccessTokenResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, CharSet.UTF_8));
			response = httpClient.execute(httpPost, new GetAccessTokenResponseHandler());
			
			if (response.isSuccess()) {
				logger.info("success to get access token");
			} else {
				logger.info("fail to get access token, error code is {}", response.getErrorCode());
			}
			
		} catch (Exception e) {
			logger.error("exception when get access token: {}", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when get access token: {}", e);
			}
		}
		
		return response;
	}
	
	public static ScheduleMeetingResponse scheduleMeeting(String accessToken, String name, Date startDateTime, Date endDateTime) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(Moxtra.Service.SCHEDULE_MEETING);
		
		ScheduleMeetingResponse response = null;
		try {
			ScheduleMeetingRequest scheduleMeetingRequest = new ScheduleMeetingRequest(name, startDateTime, endDateTime);
			String json = GsonManager.getInstance().getGson().toJson(scheduleMeetingRequest);
			StringEntity stringEntity = new StringEntity(json, CharSet.UTF_8);
			httpPost.setHeader("content-type", "application/json");
			httpPost.setHeader("Authorization", "Bearer " + accessToken);
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost, new ScheduleMeetingResponseHandler());
			
			if (response.isSuccess()) {
				logger.info("success to schedule meeting");
			} else {
				logger.info("fail to schedule meeting, error code is {}", response.getErrorCode());
			}
			
		} catch (Exception e) {
			logger.error("exception when schedule meeting: {}", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("exception when schedule meeting: {}", e);
			}
		}
		
		return response;
	}
}
