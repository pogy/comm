package com.mor.client.dubbo.action;

import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mor.server.dubbo.service.DemoServer;
/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class ChatAction {
	/**
	 * 
	 * @author:VIPKID ZengWeiLong
	 * @date 2015-9-7 下午8:25:21
	 * @param args
	 */
	public static void main(String[] args){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationConsumer.xml" });
		context.start();
		DemoServer demoServer = (DemoServer) context.getBean("demoService");
		int i = 1;
		while( i <= 100){
			System.out.println("客户端接收消息:" + demoServer.sayHello("请求 "+i+"次，时间:"+new Date()));
			i++;
		}
    }
}
