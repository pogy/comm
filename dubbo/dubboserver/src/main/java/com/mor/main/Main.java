
package com.mor.main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class Main {

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationProvider.xml" });
		context.start();
		System.out.println("按任意键退出");
		System.in.read();
	}

}
