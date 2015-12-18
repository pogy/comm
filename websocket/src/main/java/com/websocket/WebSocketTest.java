package com.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketTest {

	@OnMessage
    public void onMessage(String message, Session session) 
    	throws IOException, InterruptedException {
		System.out.println("接收客户端消息: " + message);
		session.getBasicRemote().sendText("服务器接收到你的消息：" + message);
    }
	
	@OnOpen
    public void onOpen () {
        System.out.println("客户端连接");
    }

    @OnClose
    public void onClose () {
    	System.out.println("连接关闭");
    }
}
