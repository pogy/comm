package com.mina.server;

import java.util.logging.Logger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ServerHandler extends IoHandlerAdapter {

	private static Logger logger = Logger.getLogger(ServerHandler.class.getSimpleName());

	/**
	 * 开启session前需要做的处理
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened");
	}

	/**
	 * 接收信息后要做的处理
	 */
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		System.out.println("服务器端接收命令：" + message);
		Thread.sleep(1000);
		// 反馈给客户端信息
		session.write("你好，我是服务器:" + message);
	}
	
	/**
	 * 发送信息后的处理
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.info("messageSent:消息发送完毕");
	}

	/**
	 * 异常后的处理
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable e)throws Exception {
		e.printStackTrace();
		logger.info("exceptionCaught：异常断开");
		session.close(true);
	}

	/**
	 * 关闭session时候要做的处理
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("sessionClosed");
		session.close(true);
	}
	
	 /**
	  * 超时断开
	  */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        // TODO Auto-generated method stub
    	logger.info("sessionIdle：超时断开");
        session.close(true);
    }
}