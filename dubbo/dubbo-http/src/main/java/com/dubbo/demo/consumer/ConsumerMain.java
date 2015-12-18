package com.dubbo.demo.consumer;

import com.dubbo.demo.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Jerry Lee
 */
public class ConsumerMain {
	/**
	 * 
	 * @author:VIPKID ZengWeiLong 2015-9-7
	 * @param args
	 * void
	 */
	public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("application-context-consumer.xml");
        classPathXmlApplicationContext.start();
        HelloService helloService = (HelloService) classPathXmlApplicationContext.getBean("helloService");
        
        for(int i = 1 ; i <= 10; i++){
        	String world = helloService.hello("World"+i);
	        System.out.println(i+"=====================================");
	        System.out.println(world);
        }
        System.out.println("休息一下");
        helloService = (HelloService) classPathXmlApplicationContext.getBean("helloService");
        Thread.sleep(2000);
        for (int i = 1; i <= 10; i++) {
        	String world = helloService.hello("World"+i);
	        System.out.println(i+"=====================================");
	        System.out.println(world);
		}
    }
}
