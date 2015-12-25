package com.vipkid.ext.moxtra;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class ScheduleMeetingRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("start_time")
	private Date startDateTime;
	
	@SerializedName("end_time")
	private Date endDateTime;
	
	@SerializedName("auto_recording")
	private boolean autoRecording = true;
	
	@SerializedName("join_before_minutes")
	private int joinBeforeMinutes = 30;

	public ScheduleMeetingRequest(String name, Date startDateTime, Date endDateTime) {
		this.name = name;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isAutoRecording() {
		return autoRecording;
	}

	public void setAutoRecording(boolean autoRecording) {
		this.autoRecording = autoRecording;
	}

	public int getJoinBeforeMinutes() {
		return joinBeforeMinutes;
	}

	public void setJoinBeforeMinutes(int joinBeforeMinutes) {
		this.joinBeforeMinutes = joinBeforeMinutes;
	}
	
	
}
