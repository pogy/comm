package com.dubbo.handle;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class ThreadTests implements Runnable {

	public String str = "";
	
	public ThreadTests(String str){
		this.str = str;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.err.println("服器端接收到消息： " + str);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
