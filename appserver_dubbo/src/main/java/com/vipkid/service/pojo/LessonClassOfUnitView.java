package com.vipkid.service.pojo;

public class LessonClassOfUnitView {
	
    private String imgUrl;
    private boolean isFinished;
	private String name;
	private int sequence;
	private long onlineClassId;
	private int stars;
	private String number;
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public long getOnlineClassId() {
		return onlineClassId;
	}
	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
}
