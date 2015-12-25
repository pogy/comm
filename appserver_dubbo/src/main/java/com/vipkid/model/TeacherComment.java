
package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 教师评语
 */
@Entity
@Table(name = "teacher_comment", schema = DBInfo.SCHEMA)
public class TeacherComment extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	//操作者id
	@Column(name = "operator_id")
	private long operatorId;
	

	// 在线教室
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
	// 老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// 能够跟随教师指导
	@Column(name = "ability_to_follow_instructions")
	private int abilityToFollowInstructions;
	
	// 能够正确复述
	@Column(name = "repetition")
	private int repetition;
	
	// 发音清晰
	@Column(name = "clear_pronunciation")
	private int clearPronunciation;
	
	// 阅读技巧
	@Column(name = "reading_skills")
	private int readingSkills;
	
	// 拼写正确
	@Column(name = "spelling_accuracy")
	private int spellingAccuracy;
	
	// 积极互动
	@Column(name = "actively_interaction")
	private int activelyInteraction;
	
	// 教师反馈
	@Lob
	@Column(name = "teacher_feedback")
	private String teacherFeedback;
	
	@Lob
	@Column(name = "feedback_translation")
	private String feedbackTranslation;
	
	// 给其它教师的小贴士
	@Lob
	@Column(name = "tips_for_other_teachers")
	private String tipsForOtherTeachers;
	
	// 报告问题
	@Lob
	@Column(name = "report_issues")
	private String reportIssues;
	
	// 标记是否紧急
	@Column(name = "urgent")
	private boolean urgent;
	
	// 标记教师评语是否为空
	@Column(name = "empty")
	private boolean empty;
	
	// 本节课获得星星币
    @Column(name="stars")
    private int stars;
	
    /**
     * 2015-06-29 该学生的performance表现：
     * 	计算得出： Above，OnTarget, Below
     */
    @Enumerated(EnumType.STRING)
    @Column(name="current_performance", insertable=false)
    private StudentPerformance currentPerformance;
    
    /**
     * 本次课程中的performance 得分
     * 0 - no comments	1 -very diff	2 - diff	3- average	4 - easy	5 - very easy
     */
    @Column(name="performance", insertable=false)
    private int performance;
    
    // 2015-08-29 trial 水平测试的结果
    @Column(name="trial_level_result", insertable=false)
    private String trialLevelExamResult;
    
 // 2015-08-29 comment对应的课类型：trial 类型需要显示 trial level result
    @Transient
    private Course.Type courseType;
    
    
    public Course.Type getCourseType() {
		return courseType;
	}

	public void setCourseType(Course.Type courseType) {
		this.courseType = courseType;
	}

	@PostLoad
    public void updateCommentAfterLoad() {
    	//
    	if (null != this.onlineClass && null != this.onlineClass.getCourseType()) {
    		Course.Type type = this.onlineClass.getCourseType();
    		this.setCourseType(type);
		}
    }
    
	public StudentPerformance getCurrentPerformance() {
		return currentPerformance;
	}

	public void setCurrentPerformance(StudentPerformance currentPerformance) {
		this.currentPerformance = currentPerformance;
	}

	public String getTrialLevelExamResult() {
		return trialLevelExamResult;
	}

	public void setTrialLevelExamResult(String trialLevelExamResult) {
		this.trialLevelExamResult = trialLevelExamResult;
	}

	@PrePersist
	public void prePersist() {
		this.createDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public OnlineClass getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		this.onlineClass = onlineClass;
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

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getTeacherFeedback() {
		return teacherFeedback;
	}

	public void setTeacherFeedback(String teacherFeedback) {
		this.teacherFeedback = teacherFeedback;
	}

	public String getFeedbackTranslation() {
		return feedbackTranslation;
	}

	public void setFeedbackTranslation(String feedbackTranslation) {
		this.feedbackTranslation = feedbackTranslation;
	}

	public String getTipsForOtherTeachers() {
		return tipsForOtherTeachers;
	}

	public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
		this.tipsForOtherTeachers = tipsForOtherTeachers;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public String getReportIssues() {
		return reportIssues;
	}

	public void setReportIssues(String reportIssues) {
		this.reportIssues = reportIssues;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public boolean isUrgent() {
		return urgent;
	}

	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}
	
	/**
	 * Why not select override toString() that because it will be invoked outof my control
	 * @return
	 */
	public String getTeacherCommentContent(){
		return  "Id:" + this.getId() +
				" AbilityToFollowInstructions:" + this.getAbilityToFollowInstructions() + 
				" Repetition:" + this.getRepetition() +
				" Pronucation:" + this.getClearPronunciation() + 
				" ReadingSkills:" + this.getReadingSkills() +
				" SpellingAccuracy:" + this.getSpellingAccuracy() + 
				" Interaction:" + this.getActivelyInteraction() + 
				" Feedback:" + this.getTeacherFeedback() +
				" Tips:" + this.getTipsForOtherTeachers() + 
				" ReportIssues:" + this.getReportIssues();
		       
	}

	public StudentPerformance getCurrentPerforance() {
		return currentPerformance;
	}

	public void setCurrentPerforance(StudentPerformance currentPerforance) {
		this.currentPerformance = currentPerforance;
	}

	public int getPerformance() {
		return performance;
	}

	public void setPerformance(int performance) {
		this.performance = performance;
	}

	public long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}

	
}
