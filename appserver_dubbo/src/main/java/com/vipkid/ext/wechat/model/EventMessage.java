package com.vipkid.ext.wechat.model;


public class EventMessage extends AbstractMessage {
	
	public static final String SUBSCRIBE = "subscribe";
	public static final String UNSUBSCRIBE = "unsubscribe";

	public static final String CLICK = "CLICK";
	public static final String SCAN = "scan";
	public static final String LOCATION = "LOCATION";

	private String event;
	private String eventKey;
	private String ticket;

	public EventMessage(MessageHeader msgHeader) {
		super(msgHeader);
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	


}
