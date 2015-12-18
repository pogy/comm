package server;

import java.util.Map;

/**
 * Web Service 服务器端类
 * @author zengwl
 *
 */
public class AddService {
	
	/**
	 * 给参数a b做加法
	 * @param a
	 * @param b
	 * @return
	 */
	public int add(int a, int b) {
		System.out.println("客户端传入："+a+"+"+b);
		return a+b;
	}
	
	public Map<String,Object> getMaps(String json,Map<String,Object> map){
		System.out.println("===="+json);
		map.put("content","{\"body\":\"bodys\"}");
		return map;
	}
	
}
