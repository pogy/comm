package com.vipkid.ext.wechat.model;

import com.google.gson.annotations.SerializedName;


public class VoiceMessage extends AbstractMessage{
	
	@SerializedName("media_id")
	private String mediaId;
	
	private String format;
	private String msgId;
	
	public VoiceMessage(){
		super();
	}
	
	public VoiceMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public VoiceMessage(MessageHeader msgHeader, String mediaId, String format, String msgId) {
		super(msgHeader);
		this.mediaId = mediaId;
		this.format = format;
		this.msgId = msgId;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	
}
