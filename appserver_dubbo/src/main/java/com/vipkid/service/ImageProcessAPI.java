package com.vipkid.service;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.vipkid.service.exception.InternalServerErrorServiceException;
import com.vipkid.util.Configurations;

public class ImageProcessAPI {
	private static Logger logger = LoggerFactory.getLogger(ImageProcessAPI.class.getSimpleName());
	private static final Pattern pattern = Pattern.compile("(http://.*)/(.*)/(.*)\\.(.*)");
	
	/**
	 * 
	 * @param url, can not be null, it points out where the image store in aliYun OSS. For example http://domain/image.jpg
	 * @param height, can not be null.
	 * @param width, can not be null.
	 * @param type, can not be null.
	 * @return true means succeed, otherwise failed
	 */
	public static boolean shrink(final String url, final String height, final String width, final String type){
		if (!checkUrl(url)){
			throw new IllegalStateException("url of you image is invalidate.");
		}
		if (height == null){
			throw new IllegalStateException("height can not be null.");
		}
		if (width == null){
			throw new IllegalStateException("width can not be null.");
		}
		if (type == null){
			throw new IllegalStateException("type can not be null.");
		}
		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url + "@" + width + "w_" + height + "h_" + "." + type);
	    try{
	        result = httpClient.execute(httpGet, new ResponseHandler<Boolean>(){
	 			@Override
	 			public Boolean handleResponse(HttpResponse response)
	 					throws ClientProtocolException, IOException {
	 				StatusLine statusLine = response.getStatusLine();
	 				HttpEntity entity = response.getEntity();
	 				OSSClient client = new OSSClient(Configurations.OSS.ENDPOINT, Configurations.OSS.KEY_ID, Configurations.OSS.KEY_SECRET);
	 			    ObjectMetadata metaData = new ObjectMetadata();
	 				
	 			    if (entity != null){
	 				    metaData.setContentLength(entity.getContentLength());
	 				    Matcher matcher = pattern.matcher(url);
	 				    if (matcher.matches()){
	 				    	String dir = matcher.group(2);
	 				    	String name = matcher.group(3);
	 				    	//as our style defined in aliyun, all will be changed to png type
	 				    	String key = dir + "/" + name + "." + type; 
	 				    	client.putObject(Configurations.OSS.BUCKET, key , entity.getContent(), metaData);
	 				    }
	 			    	
	 			    }
	 				logger.debug("Response status ＝ {}" + statusLine.getStatusCode());
	 				if (statusLine.getStatusCode() == 200){
	 					return true;
	 				}
	 				return false;
	 			}
	 		});
	    httpClient.close();
	    }catch (Exception e) {
	    	logger.error("exception when processing image: %s", e.getMessage());
			throw new InternalServerErrorServiceException("exception when processing image: %s", e.getMessage());
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				//Just log 
				logger.error("exception when processing image: %s", e.getMessage());
			}
		} 
	    return result;
	}
	
	/**
	 * 
	 * @param url, can not be null, the url contains style of image processing. For example http://domain.com/image.jpg@!style
	 * @return true means succeed, otherwise failed
	 */
	public static boolean shrink(final String url, final String style){
		if (!checkUrl(url)){
			throw new IllegalStateException("url of you image is invalidate.");
		}
		if (style == null){
			throw new IllegalStateException("style can not be null.");
		}
		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url + "@!" + style);
	    try{
	        result = httpClient.execute(httpGet, new ResponseHandler<Boolean>(){
	 			@Override
	 			public Boolean handleResponse(HttpResponse response)
	 					throws ClientProtocolException, IOException {
	 				
	 				StatusLine statusLine = response.getStatusLine();
	 				if (statusLine.getStatusCode() != 200){	 				
	 					return false;
	 				}
	 				HttpEntity entity = response.getEntity();
	 				OSSClient client = new OSSClient(Configurations.OSS.ENDPOINT, Configurations.OSS.KEY_ID, Configurations.OSS.KEY_SECRET);
	 			    ObjectMetadata metaData = new ObjectMetadata();
	 				
	 			    if (entity != null){
	 				    metaData.setContentLength(entity.getContentLength());
	 				    Matcher matcher = pattern.matcher(url);
	 				    if (matcher.matches()){
	 				    	String dir = matcher.group(2);
	 				    	String name = matcher.group(3);
	 				    	//name = name.substring(0, name.indexOf(Configurations.OSS.SUFFIX));
	 				    	String type = matcher.group(4);
//
//	 				    	if("avatar-large".equals(style)){
//	 				    		name = name + "_large";
//	 				    	} 
//	 				    	if("avatar-small".equals(style)){
//	 				    		name = name + "_small";
//	 				    	} 
	 				    	//as our style defined in aliyun
	 				    	String key = dir + "/" + name + "." + type; 
	 				    	client.putObject(Configurations.OSS.BUCKET, key , entity.getContent(), metaData);
	 				    }
	 			    	
	 			    }
	 				logger.info("Image process response status ＝ {}" + statusLine.getStatusCode());
	 				if (statusLine.getStatusCode() == 200){
	 					return true;
	 				}
	 				return false;
	 			}
	 		});
	    httpClient.close();
	    }catch (Exception e) {
	    	logger.error("exception when processing image: %s", e.getMessage());
			throw new InternalServerErrorServiceException("exception when processing image: %s", e.getMessage());
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				//Just log 
				logger.error("exception when processing image: %s", e.getMessage());
			}
		} 
	    return result;
	}
	
	private static boolean checkUrl(String url){
		if (url == null){
			return false;
		}
		Matcher matcher = pattern.matcher(url);
		 if (matcher.matches()){
		    	return true;
		}  
		return false;
	}

}
