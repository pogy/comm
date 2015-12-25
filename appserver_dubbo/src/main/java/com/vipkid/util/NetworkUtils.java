package com.vipkid.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class.getSimpleName());

	public static boolean checkMacAddressIsValidated() { // 检查mac 地址是否在配置中，如果在，则返回true
		String currentMacAddress = getMacAddress();
		for(MacAddress macAddress : MacAddress.values()) {
			if(currentMacAddress.equals(macAddress.getAddress())) {
				return true;
			}
		}
		return false;
	}
	
	public static String getMacAddress() { // 返回的字母默认大写
		InetAddress ip;
	    try {
	        ip = InetAddress.getLocalHost();

	        NetworkInterface network = NetworkInterface.getByInetAddress(ip);

	        byte[] mac = network.getHardwareAddress();

	        StringBuilder macAddress = new StringBuilder();
	        for (int i = 0; i < mac.length; i++) { // 将10进制转换位16进制, 且字母为大写(X控制)
	        	macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));        
	        }
	        return macAddress.toString();

	    } catch (Throwable e) {
	    	logger.error("get macaddress failed");
	    	return null;
	    }
	}
	
	public static String getIPAddress() {
		InetAddress ip;
		try {				
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		}catch (Throwable t) {
			logger.error("get ip failed");
			return null;
		}
	}
}

