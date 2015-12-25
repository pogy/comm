package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.FiremanLog.Event;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.json.moxy.DateTimeAdapter;

/**
 * @author Administrator
 *
 */
public class FiremanLogView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Event event;
	
	private long onlineClassId;
	
	private Status onlineClassStatus;
	
	private boolean helpYell;

	private long id;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date onlineClassScheduledDateTime;
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public long getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public Status getOnlineClassStatus() {
		return onlineClassStatus;
	}

	public void setOnlineClassStatus(Status onlineClassStatus) {
		this.onlineClassStatus = onlineClassStatus;
	}

	public boolean isHelpYell() {
		return helpYell;
	}

	public void setHelpYell(boolean helpYell) {
		this.helpYell = helpYell;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getOnlineClassScheduledDateTime() {
		return onlineClassScheduledDateTime;
	}

	public void setOnlineClassScheduledDateTime(Date onlineClassScheduledDateTime) {
		this.onlineClassScheduledDateTime = onlineClassScheduledDateTime;
	}
	

}
