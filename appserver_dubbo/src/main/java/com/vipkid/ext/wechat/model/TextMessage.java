package com.vipkid.ext.wechat.model;


public class TextMessage extends AbstractMessage{
	private String content;
	private String msgId;
	
	public TextMessage(){
		super();
	}
	
	public TextMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public TextMessage(MessageHeader msgHeader, String content, String msgId) {
		super(msgHeader);
		this.content = content;
		this.msgId = msgId;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
}
