package com.dubbo.demo.impl;

import com.dubbo.demo.HelloService;

/**
 * @author Jerry Lee
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        System.out.println("received " + name);
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "Hello, " + name + "!";
    }
}
