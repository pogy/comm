package com.vipkid.ext.wechat.model;

import com.vipkid.model.json.gson.GsonManager;



public abstract class AbstractMessage {
	
	public static final String MSG_TYPE_TEXT = "text";
	public static final String MSG_TYPE_IMAGE = "image";
	public static final String MSG_TYPE_MUSIC = "music";
	public static final String MSG_TYPE_LOCATION = "location";
	public static final String MSG_TYPE_LINK = "link";
	public static final String MSG_TYPE_IMAGE_TEXT = "news";
	public static final String MSG_TYPE_EVENT = "event";
	public static final String MSG_TYPE_VOICE = "voice";
	public static final String MSG_TYPE_VIDEO = "video";

	private MessageHeader msgHeader;
	
	public AbstractMessage(){
		super();
	}

	public AbstractMessage(MessageHeader msgHeader) {
		super();
		this.msgHeader = msgHeader;
	}


	public MessageHeader getMsgHeader() {
		return msgHeader;
	}


	public void setMsgHeader(MessageHeader msgHeader) {
		this.msgHeader = msgHeader;
	}
	
	@Override
	public String toString() {
		return GsonManager.getInstance().getGson().toJson(this);
	}
	
	
	
}
