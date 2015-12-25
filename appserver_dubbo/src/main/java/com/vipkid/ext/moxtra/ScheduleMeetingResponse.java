package com.vipkid.ext.moxtra;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class ScheduleMeetingResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	
	@SerializedName("schedule_binder_id")
	private String scheduleBinderId;
	
	@SerializedName("binder_name")
	private String binderName;
	
	@SerializedName("revision")
	private int revision;
	
	@SerializedName("session_key")
	private String sessionKey;

	@SerializedName("startmeet_url")
	private String startMeetURL;
	
	@SerializedName("created_time")
	private Date createdDateTime;
	
	@SerializedName("updated_time")
	private Date updatedDateTime;

	public String getScheduleBinderId() {
		return scheduleBinderId;
	}

	public void setScheduleBinderId(String scheduleBinderId) {
		this.scheduleBinderId = scheduleBinderId;
	}

	public String getBinderName() {
		return binderName;
	}

	public void setBinderName(String binderName) {
		this.binderName = binderName;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getStartMeetURL() {
		return startMeetURL;
	}

	public void setStartMeetURL(String startMeetURL) {
		this.startMeetURL = startMeetURL;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}
	
}
