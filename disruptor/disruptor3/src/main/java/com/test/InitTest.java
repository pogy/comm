package com.test;

public class InitTest {
	
	public static void main(String[] args) throws Exception {
		DisruptorUtils.start();
		for(int i= 0;i<100;i++){
			Thread.sleep(100);
			DisruptorUtils.produce2("你好!"+i);
			System.out.println("生产完毕:你好!"+i);
		}
		DisruptorUtils.shutdown();
	}
}
