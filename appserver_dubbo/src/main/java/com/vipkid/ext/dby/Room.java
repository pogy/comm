package com.vipkid.ext.dby;

import java.io.Serializable;
import java.util.Date;

public class Room implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String roomId;
	private String title;
	private Date startTime;
	private Date endTime;
//	private Date validEndTime;
	private boolean video;
	private String hostCode;
	
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
//	public Date getValidEndTime() {
//		return validEndTime;
//	}
//	public void setValidEndTime(Date validEndTime) {
//		this.validEndTime = validEndTime;
//	}
	public boolean isVideo() {
		return video;
	}
	public void setVideo(boolean video) {
		this.video = video;
	}
	public String getHostCode() {
		return hostCode;
	}
	public void setHostCode(String hostCode) {
		this.hostCode = hostCode;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", video=" + video +
                ", hostCode='" + hostCode + '\'' +
                '}';
    }
}
