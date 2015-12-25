package com.vipkid.ext.wechat.model;

import com.google.gson.annotations.SerializedName;


public class VideoMessage extends AbstractMessage{
	
	@SerializedName("media_id")
	private String mediaId;
	
	@SerializedName("thumb_media_id")
	private String thumbMediaId;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("description")
	private String description;
	private String msgId;
	
	public VideoMessage(){
		super();
	}
	
	public VideoMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public VideoMessage(MessageHeader msgHeader, String mediaId, String thumbMediaId, String msgId) {
		super(msgHeader);
		this.mediaId = mediaId;
		this.thumbMediaId = thumbMediaId;
		this.msgId = msgId;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
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
	
	
}
