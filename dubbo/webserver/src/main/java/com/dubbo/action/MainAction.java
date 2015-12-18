/**
 * 
 */
package com.dubbo.action;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class MainAction {

	/**
	 * 
	 * @author:VIPKID ZengWeiLong 2015-9-7
	 * @param args
	 * @throws IOException
	 * void
	 */
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-provider.xml" });
		context.start();
		System.out.println("按任意键退出");
		System.in.read();
	}

}
