package com.vipkid.service.pojo.parent;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass.FinishType;

/**
 * 
* @ClassName: LessonsView 
* @Description: 用于已上课程和已上课程页面详情展示
* @author zhangfeipeng 
* @date 2015年6月9日 上午11:21:11 
*
 */
public class LessonsView implements Serializable{
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 2593214290093144843L;
	
	public static final String feedback = "A great student and does a good job reading and following directions,Good job!";
	public static final String feedbackTranslation = "很棒的学生，在阅读和跟随方面做得很好，干得好！";
	
	private long onlineClassId;
	private long studentId;
	private long teacherId;
	
	private Date scheduledDateTime;
	private String level;
	private String unitName;
	private String learnCycleName;
	private String teacherName;
	private String finishType;
	private int stars;
	private long teacherCommentId;
	private long medalId;
	private String medalName;
	private String lessonName;
	private String teacherFeedback;
	
	
	//已上课程详情页增加字段;
	private int abilityToFollowInstructions;
	private int repetition;
	private int clearPronunciation;
	private int readingSkills;
	private int spellingAccuracy;
	private int activelyInteraction;
	private String objective;
	private String vocabularies;
	private String unitTestPath;
	
	private String onlineClassStatus;
	
	private String sentencePatterns;
	private String grammar;
	private boolean showFeedback;

	public boolean isShowFeedback() {
		return showFeedback;
	}

	public void setShowFeedback(boolean showFeedback) {
		this.showFeedback = showFeedback;
	}

	public String getGrammar() {
		return grammar;
	}

	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}

	public String getOnlineClassStatus() {
		return onlineClassStatus;
	}

	public void setOnlineClassStatus(String onlineClassStatus) {
		this.onlineClassStatus = onlineClassStatus;
	}

	public LessonsView(){
		
	}
	
	/**
	 * 
	* <p>Title: 已上课程详情页</p> 
	* <p>Description: </p> 
	* @param onlineClassId
	* @param scheduledDateTime
	* @param finishType
	* @param level
	* @param unitName
	* @param learnCycleName
	* @param lessonName
	* @param stars
	* @param teacherCommentId
	* @param teacherName
	* @param teacherId
	* @param medalId
	* @param studentId
	* @param teacherFeedback
	* @param abilityToFollowInstructions
	* @param repetition
	* @param clearPronunciation
	* @param readingSkills
	* @param spellingAccuracy
	* @param activelyInteraction
	 */
	public LessonsView(long onlineClassId,
			Date scheduledDateTime,
			FinishType finishType,
			Level level,
			String unitName,
			String learnCycleName,
			String lessonName,
			int stars,
			long teacherCommentId,
			String teacherName,
			long teacherId,
			/*long medalId,*/
			long studentId,
			String teacherFeedback,
			int abilityToFollowInstructions,
			int repetition,
			int clearPronunciation,
			int readingSkills,
			int spellingAccuracy,
			int activelyInteraction,
			String objective,
			String vocabularies,
			String sentencePatterns,
			String grammar){
		super();
		this.onlineClassId=onlineClassId;
		this.scheduledDateTime=scheduledDateTime;
		this.finishType=finishType==null?"":finishType.toString();
		this.level=level==null?"":level.toString();
		this.level=this.level.replace("LEVEL_", "Level ");
		this.unitName=unitName;
		this.learnCycleName=learnCycleName;
		this.lessonName=lessonName;
		this.stars=stars;
		this.teacherCommentId=teacherCommentId;
		this.teacherName=teacherName;
		this.teacherId=teacherId;
		/*this.medalId=medalId;*/
		this.studentId=studentId;
		this.teacherFeedback=teacherFeedback;
		this.abilityToFollowInstructions=abilityToFollowInstructions;
		this.repetition=repetition;
		this.clearPronunciation=clearPronunciation;
		this.readingSkills=readingSkills;
		this.spellingAccuracy=spellingAccuracy;
		this.activelyInteraction=activelyInteraction;
		this.objective=objective;
		this.vocabularies=vocabularies;
		this.sentencePatterns=sentencePatterns;
		this.grammar=grammar;
		changeObjective(scheduledDateTime, teacherFeedback);
		transFinishType();
		
	}

	private void changeObjective(Date scheduledDateTime, String teacherFeedback) {
		this.showFeedback = false;
		if(StringUtils.isEmpty(teacherFeedback)){
			if(scheduledDateTime!=null){
				if(new Date().getTime() -scheduledDateTime.getTime()>=325*60*1000){
					if(this.finishType.equals("AS_SCHEDULED")){
						this.teacherFeedback = feedback;
						this.showFeedback = true;
					}else if(this.finishType.equals("")){
						this.teacherFeedback="教师评语正在撰写中...";
					}else{
						this.teacherFeedback="异常结束的课程，没有教师评语";
					}
				}else{
					if(this.finishType.equals("AS_SCHEDULED")){
						this.teacherFeedback="教师评语正在撰写中...";
					}else if(this.finishType.equals("")){
						this.teacherFeedback="教师评语正在撰写中...";
					}else{
						this.teacherFeedback="异常结束的课程，没有教师评语";
					}
				}
			}
		} else {
			this.showFeedback = true;
		}
	}

	private void transFinishType() {
		if(this.finishType.equals("AS_SCHEDULED")){
			this.finishType="正常结束";
		}else if(this.finishType.equals("STUDENT_NO_SHOW")){
			this.finishType="24小时内取消或学生未出席（课时-1）";
		}else if(this.finishType.equals("TEACHER_NO_SHOW")){
			this.finishType="教师未出席或者网络问题（不扣课时）";
		}else if(this.finishType.equals("TEACHER_NO_SHOW_WITH_BACKUP")){
			this.finishType="TEACHER_NO_SHOW_WITH_BACKUP";
		}else if(this.finishType.equals("TEACHER_NO_SHOW_WITH_SHORTNOTICE")){
			this.finishType="教师未出席或者网络问题（不扣课时）";
		}else if(this.finishType.equals("STUDENT_IT_PROBLEM")){
			this.finishType="网络问题（不扣课时）";
		}else if(this.finishType.equals("TEACHER_IT_PROBLEM")){
			this.finishType="网络问题（不扣课时）";
		}else if(this.finishType.equals("TEACHER_IT_PROBLEM_WITH_BACKUP")){
			this.finishType="TEACHER_IT_PROBLEM_WITH_BACKUP";
		}else if(this.finishType.equals("TEACHER_IT_PROBLEM_WITH_SHORTNOTICE")){
			this.finishType="网络问题（不扣课时）";
		}else if(this.finishType.equals("TEACHER_CANCELLATION")){
			this.finishType="老师申请取消";
		}else if(this.finishType.equals("SYSTEM_PROBLEM")){
			this.finishType="系统问题";
		}else if(this.finishType.equals("")){
			this.finishType="生成中...";
		}
	}
	
	/**
	 * <p>Title: 已上课程列表页面</p> 
	* SELECT OL.id,OL.scheduledDateTime,OL.finishType,
	* OL.lesson.learningCycle.unit.level,
	* OL.lesson.learningCycle.unit.name,
	* OL.lesson.learningCycle.name,
	* OL.lesson.name,TC.stars,TC.id,OL.teacher.name,
	* OL.teacher.id,ST.id,TC.teacherFeedback
	 */
	public LessonsView(long onlineClassId,
			Date scheduledDateTime,
			FinishType finishType,
			Level level,
			String unitName,
			String learnCycleName,
			String lessonName,
			int stars,
			long teacherCommentId,
			String teacherName,
			long teacherId,
			long studentId,
			String teacherFeedback,
			String unitTestPath){
		super();
		this.onlineClassId=onlineClassId;
		this.scheduledDateTime=scheduledDateTime;
		this.finishType=finishType==null?"":finishType.toString();
		this.level=level==null?"":level.toString();
		this.level=this.level.replace("LEVEL_", "Level ");
		this.unitName=unitName;
		this.learnCycleName=learnCycleName;
		this.lessonName=lessonName;
		this.stars=stars;
		this.teacherCommentId=teacherCommentId;
		this.teacherName=teacherName;
		this.teacherId=teacherId;
		this.studentId=studentId;
		this.teacherFeedback=teacherFeedback;
		this.unitTestPath = unitTestPath;
		transFinishType();
	}
	
	public LessonsView(long onlineClassId,
			Date scheduledDateTime,
			FinishType finishType,
			Level level,
			String unitName,
			String learnCycleName,
			String lessonName,
			String teacherName,
			long teacherId,
			long studentId){
		super();
		this.onlineClassId=onlineClassId;
		this.scheduledDateTime=scheduledDateTime;
		this.finishType=finishType==null?"":finishType.toString();
		this.level=level==null?"":level.toString();
		this.level=this.level.replace("LEVEL_", "Level ");
		this.unitName=unitName;
		this.learnCycleName=learnCycleName;
		this.lessonName=lessonName;
		this.teacherName=teacherName;
		this.teacherId=teacherId;
		this.studentId=studentId;
		transFinishType();
	}
	public String getSentencePatterns() {
		return sentencePatterns;
	}

	public void setSentencePatterns(String sentencePatterns) {
		this.sentencePatterns = sentencePatterns;
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
	public String getLessonName() {
		return lessonName;
	}
	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}
	public long getOnlineClassId() {
		return onlineClassId;
	}
	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public long getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public Date getScheduledDateTime() {
		return scheduledDateTime;
	}
	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getLearnCycleName() {
		return learnCycleName;
	}
	public void setLearnCycleName(String learnCycleName) {
		this.learnCycleName = learnCycleName;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getFinishType() {
		return finishType;
	}
	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}
	public int getStars() {
		return stars;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public long getMedalId() {
		return medalId;
	}
	public void setMedalId(long medalId) {
		this.medalId = medalId;
	}

	public long getTeacherCommentId() {
		return teacherCommentId;
	}

	public void setTeacherCommentId(long teacherCommentId) {
		this.teacherCommentId = teacherCommentId;
	}
	
	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getVocabularies() {
		return vocabularies;
	}

	public void setVocabularies(String vocabularies) {
		this.vocabularies = vocabularies;
	}
	public String getMedalName() {
		return medalName;
	}

	public void setMedalName(String medalName) {
		this.medalName = medalName;
	}
	
	public String getUnitTestPath() {
		return unitTestPath;
	}

	public void setUnitTestPath(String unitTestPath) {
		this.unitTestPath = unitTestPath;
	}

}
