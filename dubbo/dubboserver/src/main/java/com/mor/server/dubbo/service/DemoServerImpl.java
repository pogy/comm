
package com.mor.server.dubbo.service;

import java.util.Date;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class DemoServerImpl implements DemoServer {

	public String sayHello(String str) {
		str = "1.我是服务器端： " + str + "  2:" + new Date();
		System.err.println("server:" + str);
		return str;
	}

}
