package com.mina;

import com.mina.client.MinaClient;

public class ClientTest {
		
	public static void main(String[] args) throws Exception {
		MinaClient client = new MinaClient();
		for(int i = 1; i <= 10; i++){
			client.sendMessage("你好，我是客户端 " + i);
		}
	}
	
}
