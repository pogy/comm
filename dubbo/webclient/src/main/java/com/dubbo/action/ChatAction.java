package com.dubbo.action;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dubbo.service.TestService;

/**
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class ChatAction {
	/**
	 * @author:VIPKID ZengWeiLong 2015-9-7
	 * @param args
	 * @throws Exception
	 * void
	 */
	public static void main(String[] args) throws Exception{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-consumer.xml" });
		context.start();
		/*
		DemoService demoService = (DemoService)context.getBean("demoService2");
		for(int i = 0;i< 100; i++){
			TestBeans bean = new TestBeans();
			bean.setDubbos(1.11);
			System.out.println(i+":" + demoService.sayHello("我是服务器", bean));
		}
		*/
		TestService testService = (TestService) context.getBean("testService");
		testService.initService();
    }
}
