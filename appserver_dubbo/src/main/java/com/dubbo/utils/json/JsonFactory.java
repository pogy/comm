package com.dubbo.utils.json;

import com.dubbo.utils.json.support.JacksonImpl;

/**
 * Json转化工具类对像工厂
 * @author VIPKID
 *
 */
public class JsonFactory {

	
	public enum  ConverType{
		JACKSON
	}
	
	public static JsonConver getConver(ConverType type){
		if(ConverType.JACKSON.equals(type)){
			return new JacksonImpl().init();
		}
		return null;
	}
}
