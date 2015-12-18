package com.mina.client;

import java.util.logging.Logger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {

	private static Logger logger = Logger.getLogger(ClientHandler.class.getSimpleName());

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// 每次session 关闭都清空缓存
		logger.info("sessionIdle:客户端超时断开");
		session.close(true);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable e) throws Exception {
		logger.info("exceptionCaught:客户端异常断开");
		e.printStackTrace();
		session.close(true);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened");
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.info("messageSent：消息发送完毕");
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("客户端接收到服务信息：[" + message + "]");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("sessionClosed");
		session.close(true);
	}

}