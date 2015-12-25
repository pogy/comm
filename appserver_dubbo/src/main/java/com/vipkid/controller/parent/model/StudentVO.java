package com.vipkid.controller.parent.model;

import java.io.Serializable;

public class StudentVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String englishName;
	private int stars;
	private String name;
	private String token;
	private String avatar;
	private long familyId;
	private int targetClassesPerWeek;
	
	// 2015-09-01 max-time-level-exam
	private int maxTimesLevelExam;
	// 2015-09-01 学生的channel
	private long channelId;
	
	//是否引导水平测试 2015-09-14
	private int guideToLevelExam;

	public int getGuideToLevelExam() {
		return guideToLevelExam;
	}

	public void setGuideToLevelExam(int guideToLevelExam) {
		this.guideToLevelExam = guideToLevelExam;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public long getFamilyId() {
		return familyId;
	}
	public void setFamilyId(long familyId) {
		this.familyId = familyId;
	}
	public int getTargetClassesPerWeek() {
		return targetClassesPerWeek;
	}
	public void setTargetClassesPerWeek(int targetClassesPerWeek) {
		this.targetClassesPerWeek = targetClassesPerWeek;
	}
	public int getMaxTimesLevelExam() {
		return maxTimesLevelExam;
	}
	public void setMaxTimesLevelExam(int maxTimesLevelExam) {
		this.maxTimesLevelExam = maxTimesLevelExam;
	}
	public long getChannelId() {
		return channelId;
	}
	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}
	
}
