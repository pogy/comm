package com.vipkid.ext.wechat.model;

import com.google.gson.annotations.SerializedName;


public class ImageMessage extends AbstractMessage{
	private String picUrl;
	
	@SerializedName("media_id")
	private String mediaId;
	private String msgId;
	
	public ImageMessage(){
		super();
	}
	
	public ImageMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public ImageMessage(MessageHeader msgHeader, String picUrl, String mediaId, String msgId) {
		super(msgHeader);
		this.picUrl = picUrl;
		this.mediaId = mediaId;
		this.msgId = msgId;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
}
