package com.vipkid.ext.wechat.model;


public class LocationMessage extends AbstractMessage{
	private String location_X;
	private String location_Y;
	private String scale;
	private String label;
	private String msgId;
	
	public LocationMessage(){
		super();
	}
	
	public LocationMessage(MessageHeader msgHeader){
		super(msgHeader);
	}

	public LocationMessage(MessageHeader msgHeader, String location_X, String location_Y, String scale, String label, String msgId) {
		super(msgHeader);
		this.location_X = location_X;
		this.location_Y = location_Y;
		this.scale = scale;
		this.label = label;
		this.msgId = msgId;
	}

	public String getLocation_X() {
		return location_X;
	}

	public void setLocation_X(String location_X) {
		this.location_X = location_X;
	}

	public String getLocation_Y() {
		return location_Y;
	}

	public void setLocation_Y(String location_Y) {
		this.location_Y = location_Y;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	
}
