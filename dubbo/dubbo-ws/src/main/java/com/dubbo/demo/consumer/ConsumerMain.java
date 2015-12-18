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
    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("application-context-consumer.xml");
        classPathXmlApplicationContext.start();
        //此方式仅仅支持Servlet2.5版本
        HelloService helloService = (HelloService) classPathXmlApplicationContext.getBean("helloService");
        for(int i = 1 ; i <= 100; i++){
        	String world = helloService.hello("World");
	        System.out.println(i+"=====================================");
	        System.out.println(world);
        }
    }
}
