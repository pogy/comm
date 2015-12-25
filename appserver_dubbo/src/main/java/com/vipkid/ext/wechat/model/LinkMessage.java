package com.vipkid.ext.wechat.model;


public class LinkMessage extends AbstractMessage{
	private String title;
	private String description;
	private String url;
	private String msgId;
	
	public LinkMessage(){
		super();
	}
	
	public LinkMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public LinkMessage(MessageHeader msgHeader, String title, String description, String url, String msgId) {
		super(msgHeader);
		this.title = title;
		this.description = description;
		this.url = url;
		this.msgId = msgId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	
}
