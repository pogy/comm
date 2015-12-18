package com.websocket;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/game")
public class ScoketServer {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	//定义session缓存
	static Map<String, Session> sessionMap = new Hashtable<String, Session>();

	@OnOpen
	/**
	 * 创建一个新的Session 并将该Session保存到缓存中
	 * @param session
	 */
	public void onOpen(Session session) {
		sessionMap.put(session.getId(), session);
	}

	@OnMessage
	/**
	 * 服务器接收到消息 然后调用广播发给所有人
	 * @param unscrambledWord
	 * @param session
	 */
	public void onMessage(String unscrambledWord, Session session) {
		broadcastAll("message", unscrambledWord);
	}

	/**
	 * 广播给所有人
	 * 这里也可设置广播给指定人
	 * @param message
	 */
	public static void broadcastAll(String type, String message) {
		Set<Map.Entry<String, Session>> set = sessionMap.entrySet();
		for (Map.Entry<String, Session> i : set) {
			try {
				i.getValue().getBasicRemote().sendText("{type:'" + type + "',text:'" + message + "'}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@OnClose
	/**
	 * 如果客户端关闭Session 则移除Session
	 * @param session
	 * @param closeReason 关闭原因
	 */
	public void onClose(Session session, CloseReason closeReason) {
		sessionMap.remove(session.getId());
		logger.info(String.format("Session %s closed because of %s",session.getId(), closeReason));
	}

	@OnError
	/**
	 * 如果某个客户端异常，则移除该会话
	 * @param session
	 * @param throwable 异常信息
	 */
	public void error(Session session, java.lang.Throwable throwable) {
		sessionMap.remove(session.getId());
		System.err.println("session " + session.getId() + " error:" + throwable);
	}
}