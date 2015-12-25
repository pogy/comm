package com.vipkid.service.pojo;

import java.io.Serializable;

public class TeacherCommentView implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long id;
	
	private int abilityToFollowInstructions;
	
	private int repetition;
	
	private int clearPronunciation;
	
	private int readingSkills;
	
	private int spellingAccuracy;
	
	private int activelyInteraction;
	
	private String teacherFeedback;
	
	private String tipsForOtherTeachers;

	private String reportIssues;
	
	private int stars;
	
	// performance value -- 2015-07-06 classroomList获取teacherComment时，使用的teacher comment对象
	private int performance;

	public TeacherCommentView() {
		super();
	}

	public TeacherCommentView(long id, int abilityToFollowInstructions,
			int repetition, int clearPronunciation, int readingSkills,
			int spellingAccuracy, int activelyInteraction,
			String teacherFeedback, String tipsForOtherTeachers,
			String reportIssues, int stars, int performance) {
		super();
		this.id = id;
		this.abilityToFollowInstructions = abilityToFollowInstructions;
		this.repetition = repetition;
		this.clearPronunciation = clearPronunciation;
		this.readingSkills = readingSkills;
		this.spellingAccuracy = spellingAccuracy;
		this.activelyInteraction = activelyInteraction;
		this.teacherFeedback = teacherFeedback;
		this.tipsForOtherTeachers = tipsForOtherTeachers;
		this.reportIssues = reportIssues;
		this.stars = stars;
		//2015-07-06 performance
		this.performance = performance;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAbilityToFollowInstructions() {
		return abilityToFollowInstructions;
	}

	public void setAbilityToFollowInstructions(int abilityToFollowInstructions) {
		this.abilityToFollowInstructions = abilityToFollowInstructions;
	}

	public int getRepetition() {
		return repetition;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public int getClearPronunciation() {
		return clearPronunciation;
	}

	public void setClearPronunciation(int clearPronunciation) {
		this.clearPronunciation = clearPronunciation;
	}

	public int getReadingSkills() {
		return readingSkills;
	}

	public void setReadingSkills(int readingSkills) {
		this.readingSkills = readingSkills;
	}

	public int getSpellingAccuracy() {
		return spellingAccuracy;
	}

	public void setSpellingAccuracy(int spellingAccuracy) {
		this.spellingAccuracy = spellingAccuracy;
	}

	public int getActivelyInteraction() {
		return activelyInteraction;
	}

	public void setActivelyInteraction(int activelyInteraction) {
		this.activelyInteraction = activelyInteraction;
	}

	public String getTeacherFeedback() {
		return teacherFeedback;
	}

	public void setTeacherFeedback(String teacherFeedback) {
		this.teacherFeedback = teacherFeedback;
	}

	public String getTipsForOtherTeachers() {
		return tipsForOtherTeachers;
	}

	public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
		this.tipsForOtherTeachers = tipsForOtherTeachers;
	}

	public String getReportIssues() {
		return reportIssues;
	}

	public void setReportIssues(String reportIssues) {
		this.reportIssues = reportIssues;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public int getPerformance() {
		return performance;
	}

	public void setPerformance(int performance) {
		this.performance = performance;
	}
}
