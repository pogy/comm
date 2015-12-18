package com.dubbo.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dubbo.bean.TestBeans;

@Service
/**
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class TestService {

	@Reference(version="0.0.0")
	private DemoService demoService;
	
	@Reference(version="0.0.1")
	private DemoService demoService2;	
	/**
	 * @author:VIPKID ZengWeiLong 2015-9-7
	 * void
	 */
	public void initService(){
		int i = 1;
		while( i <= 100){
			TestBeans bean = new TestBeans();
			bean.setDubbos(1.11);
			System.out.println("客户端接收消息:" + demoService2.sayHello("请求 "+i+"次，时间:"+new Date(),bean));
			i++;
		}
	}
}
