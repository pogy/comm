package com.vipkid.ext.wechat.model;

import org.w3c.dom.Node;


public class PictureTextMessage extends AbstractMessage{
	private Node articlesElement;
	private String msgId;
	
	public PictureTextMessage(){
		super();
	}
	
	public PictureTextMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public Node getArticlesElement() {
		return articlesElement;
	}

	public void setArticlesElement(Node articlesElement) {
		this.articlesElement = articlesElement;
	}

	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
}
