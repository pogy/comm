package com.test;

public class InitTest {

	public static void main(String[] args) {
		DisruptorUtils.initAndStart();
		for(int i = 0 ; i < 100 ; i++){
			LongEvent doj = new LongEvent();
			doj.setMessage("你好：" + i);
			DisruptorUtils.produce(doj);
		}
		DisruptorUtils.shutdown();
		
	}
}
