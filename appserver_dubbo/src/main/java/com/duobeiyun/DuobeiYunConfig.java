package com.duobeiyun;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DuobeiYunConfig {

	private static final String CONFIG_FILE = "duobeiyun-api-client.properties";
	
	private String serverAddress;
	private String partnerId;
	private String appKey;
	
	private static DuobeiYunConfig _instance;

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppKey() {
		return appKey;
	}

	public static DuobeiYunConfig getInstance() {
		if (_instance == null) {
			
			Properties prop = new Properties();
			InputStream input = null;
		 
			try {
				input = DuobeiYunConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
				prop.load(input);
				
				_instance = new DuobeiYunConfig();
				_instance.setServerAddress(prop.getProperty("serverAddress"));
				_instance.setPartnerId(prop.getProperty("partnerId"));
				_instance.setAppKey(prop.getProperty("appKey"));
		 
			} catch (IOException ex) {
				throw new RuntimeException("Failed to load config file", ex);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		return _instance;
	}
}
