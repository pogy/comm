package com.dubbo.utils.json;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

/**
 * Json 转化工具抽象
 * @author VIPKID
 *
 */
public interface JsonConver {
	/**
	 * 对象初始化
	 * 默认时间格式为yyyy-MM-dd 如需修改 通过setDateFormate()方法修改
	 * @Author:VIPKID-ZengWeiLong
	 * @param dateFormat 2015年9月15日
	 */
	public JsonConver init();
	
	/**
	 * 重置时间格式
	 * @Author:VIPKID-ZengWeiLong
	 * @param dateFormat
	 * @return 2015年9月15日
	 */
	public JsonConver setDateFormate(DateFormat dateFormat);
	
    /**
     * json --> List<Map<String, Object>> 
     * @Author:VIPKID-ZengWeiLong
     * @param json
     * @return 2015年9月15日
     */
	public List<Map<String,Object>> renderJson2List(String json);
	
	/**
	 * 将Json转化为对象
	 * @Author:VIPKID-ZengWeiLong
	 * @param json
	 * @param clazz
	 * @return 2015年9月15日
	 */
	public <T> T renderJson2Obj(String json, Class<T> clazz);
	
	/**
	 * 将对象转化为Json
	 * @Author:VIPKID-ZengWeiLong
	 * @param obj
	 * @return 2015年9月16日
	 */
	public String renderObj2Json(Object obj);
	
}
