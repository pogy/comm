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
import javax.persistence.OneToOne;
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
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;

@Entity
@Table(name = "teacher_application", schema = DBInfo.SCHEMA)
public class TeacherApplication extends Base {
	
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		SIGNUP, // 新申请
		BASIC_INFO,	// 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW, //面试
		SIGN_CONTRACT, //签合同
		TRAINING, // 教师培训
		PRACTICUM,//试讲 
		CANCELED, //已取消
		FINISHED // 已结束
		;
		
		// 2015-08-22 添加获取前一个的值
		public static TeacherApplication.Status prevStatus(Status status) {
			TeacherApplication.Status [] arr = {Status.SIGNUP, Status.BASIC_INFO, Status.INTERVIEW, Status.SIGN_CONTRACT, Status.TRAINING, Status.PRACTICUM};
			for (int n = 1; n<arr.length; n++) {
				//
				if (status == arr[n]) {
					return arr[n-1];
				}
			}
			
			return TeacherApplication.Status.SIGNUP;
		};
	}
	
	public enum Result {
        PASS, // 通过
        FAIL, // 失败
        REAPPLY, //重新申请,继续上PRACTICUM1（由于客观原因没能完成面试）
        PRACTICUM2 //第一次面试没通过，上PRACTICUM2
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 申请者 
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// 关联一个系统测试学生，用于约课
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "stduent_id", referencedColumnName = "id")
	private Student student;
	
    // 关联一个在教室
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
	// 审核人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "auditor_id", referencedColumnName = "id")
	private User auditor;
	
	// 申请时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_date_time")
	private Date applyDateTime;
	
	// 延迟申请时间
	@Column(name = "delay_days")
	private int delayDays = 0;
	
	// 审核最终成功/失败时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "audit_date_time")
	private Date auditDateTime;
	
	//是否为当前申请
	@Column(name = "current")
	private boolean current;
	
	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	// 审核结果
	@Enumerated(EnumType.STRING)
	@Column(name = "result")
	private Result result; 
	
	// contract
	@Column(name = "contract_url")
	private String contractURL;

	// 失败原因
	@Lob
	@Column(name = "failed_reason")
	private String failedReason;
	
	@Column(name = "grade6_teaching_experience")
	private int grade6TeachingExperience = -1;
	
	@Column(name = "high_school_teaching_experience")
	private int highSchoolTeachingExperience = -1;
	
	@Column(name = "online_teaching_experience")
	private int onlineTeachingExperience = -1;
	
	@Column(name = "kid_teaching_experience")
	private int kidTeachingExperience = -1;
	
	@Column(name = "teaching_certificate")
	private int teachingCertificate = -1;
	
	//ESL
	
	@Column(name = "abroad_teaching_experience")
	private int abroadTeachingExperience = -1;
	
	@Column(name = "home_country_teaching_experience")
	private int homeCountryTeachingExperience = -1;
	
	@Column(name = "kid_under12_teaching_experience")
	private int kidUnder12TeachingExperience = -1;
	
	@Column(name = "teenager_teaching_experience")
	private int teenagerTeachingExperience = -1;
	
	@Column(name = "tefl_or_tosel_certificate")
	private int teflOrToselCertificate = -1;
	
	@Column(name = "base_pay")
	private float basePay = 0;
	
	@Column(name = "interaction_rapport_score")
	private int interactionRapportScore = -1;
	
	@Column(name = "teaching_method_score")
	private int teachingMethod = -1;
	
	@Column(name = "student_output_score")
	private int studentOutputScore = -1;
	
	@Column(name = "preparation_planning_score")
	private int preparationPlanningScore = -1;
	
	@Column(name = "english_language_score")
	private int englishLanguageScore = -1;
	
	@Column(name = "lesson_objectives_score")
	private int lessonObjectivesScore = -1;
	
	@Column(name = "time_management_score")
	private int timeManagementScore = -1;
	
	@Column(name = "appearance_score")
	private int appearanceScore = -1;
	
	// 2015-06-29 == 添加5新的评价类别
	@Column(name = "accent",insertable=false)
	private int accent = 1;
	
	@Column(name = "phonics",insertable=false)
	private int phonics = 1;
	
	@Column(name = "positive",insertable=false)
	private int positive = 1;
	
	@Column(name = "engaged",insertable=false)
	private int engaged = 1;
	
	@Column(name = "appearance",insertable=false)
	private int appearance = 1;
	
	//总得分
	@Transient
	private int totalScore;	
	
	// 标记第五步是否reapply过(页面展示不同)
	@Transient
	private boolean step5hasBeenReapply;
		
	// 备注
	@Lob
	@Column(name = "comments")
	private String comments;
	
	@PrePersist
	public void PrePersist(){
		this.applyDateTime = new Date();
	}
	
	public String getContractURL() {
		return contractURL;
	}

	public void setContractURL(String contractURL) {
		this.contractURL = contractURL;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public Date getApplyDateTime() {
		return applyDateTime;
	}

	public void setApplyDateTime(Date applyDateTime) {
		this.applyDateTime = applyDateTime;
	}

	public Date getAuditDateTime() {
		return auditDateTime;
	}

	public void setAuditDateTime(Date auditDateTime) {
		this.auditDateTime = auditDateTime;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getGrade6TeachingExperience() {
		return grade6TeachingExperience;
	}

	public void setGrade6TeachingExperience(int grade6TeachingExperience) {
		this.grade6TeachingExperience = grade6TeachingExperience;
	}

	public int getHighSchoolTeachingExperience() {
		return highSchoolTeachingExperience;
	}

	public void setHighSchoolTeachingExperience(int highSchoolTeachingExperience) {
		this.highSchoolTeachingExperience = highSchoolTeachingExperience;
	}

	public int getOnlineTeachingExperience() {
		return onlineTeachingExperience;
	}

	public void setOnlineTeachingExperience(int onlineTeachingExperience) {
		this.onlineTeachingExperience = onlineTeachingExperience;
	}

	public int getKidTeachingExperience() {
		return kidTeachingExperience;
	}

	public void setKidTeachingExperience(int kidTeachingExperience) {
		this.kidTeachingExperience = kidTeachingExperience;
	}

	public int getTeachingCertificate() {
		return teachingCertificate;
	}

	public void setTeachingCertificate(int teachingCertificate) {
		this.teachingCertificate = teachingCertificate;
	}

	public int getAbroadTeachingExperience() {
		return abroadTeachingExperience;
	}

	public void setAbroadTeachingExperience(int abroadTeachingExperience) {
		this.abroadTeachingExperience = abroadTeachingExperience;
	}

	public int getHomeCountryTeachingExperience() {
		return homeCountryTeachingExperience;
	}

	public void setHomeCountryTeachingExperience(int homeCountryTeachingExperience) {
		this.homeCountryTeachingExperience = homeCountryTeachingExperience;
	}

	public int getKidUnder12TeachingExperience() {
		return kidUnder12TeachingExperience;
	}

	public void setKidUnder12TeachingExperience(int kidUnder12TeachingExperience) {
		this.kidUnder12TeachingExperience = kidUnder12TeachingExperience;
	}

	public int getTeenagerTeachingExperience() {
		return teenagerTeachingExperience;
	}

	public void setTeenagerTeachingExperience(int teenagerTeachingExperience) {
		this.teenagerTeachingExperience = teenagerTeachingExperience;
	}

	public int getTeflOrToselCertificate() {
		return teflOrToselCertificate;
	}

	public void setTeflOrToselCertificate(int teflOrToselCertificate) {
		this.teflOrToselCertificate = teflOrToselCertificate;
	}
	
	public int getInterviewScores(){
		int result = 0;
		if (interactionRapportScore > 0){
			 result += interactionRapportScore;
		}
		if (teachingMethod > 0){
			result += teachingMethod;
		}
		if (studentOutputScore > 0){
			result += studentOutputScore;
		}
		if (preparationPlanningScore > 0){
			result += preparationPlanningScore;
		}
		if (englishLanguageScore > 0){
			result += englishLanguageScore;
		}
		if (lessonObjectivesScore > 0){
			result += lessonObjectivesScore;
		}
		if (timeManagementScore	 > 0){
			result += timeManagementScore;
		}
		if (appearanceScore > 0){
			result += appearanceScore;
		}
		return result;
	}

	public int getTotalScore() {
		int result = 0;
		if (grade6TeachingExperience > 0){
			 result += grade6TeachingExperience;
		}
		if (highSchoolTeachingExperience > 0){
			result += highSchoolTeachingExperience;
		}
		if (onlineTeachingExperience > 0){
			result += onlineTeachingExperience;
		}
		if (kidTeachingExperience > 0){
			result += kidTeachingExperience;
		}
		if (teachingCertificate > 0){
			result += teachingCertificate;
		}
		if (abroadTeachingExperience > 0){
			result += abroadTeachingExperience;
		}
		if (homeCountryTeachingExperience > 0){
			result += homeCountryTeachingExperience;
		}
		if (kidUnder12TeachingExperience > 0){
			result += kidUnder12TeachingExperience;
		}
		if (teenagerTeachingExperience > 0){
			result += teenagerTeachingExperience;
		}
		if (teflOrToselCertificate > 0){
			result += teflOrToselCertificate;
		}
		return result;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public OnlineClass getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		this.onlineClass = onlineClass;
	}

	public float getBasePay() {
		return basePay;
	}

	public void setBasePay(float basePay) {
		this.basePay = basePay;
	}

	public int getInteractionRapportScore() {
		return interactionRapportScore;
	}

	public void setInteractionRapportScore(int interactionRapportScore) {
		this.interactionRapportScore = interactionRapportScore;
	}

	public int getTeachingMethod() {
		return teachingMethod;
	}

	public void setTeachingMethod(int teachingMethod) {
		this.teachingMethod = teachingMethod;
	}

	public int getStudentOutputScore() {
		return studentOutputScore;
	}

	public void setStudentOutputScore(int studentOutputScore) {
		this.studentOutputScore = studentOutputScore;
	}

	public int getPreparationPlanningScore() {
		return preparationPlanningScore;
	}

	public void setPreparationPlanningScore(int preparationPlanningScore) {
		this.preparationPlanningScore = preparationPlanningScore;
	}

	public int getEnglishLanguageScore() {
		return englishLanguageScore;
	}

	public void setEnglishLanguageScore(int englishLanguageScore) {
		this.englishLanguageScore = englishLanguageScore;
	}

	public int getLessonObjectivesScore() {
		return lessonObjectivesScore;
	}

	public void setLessonObjectivesScore(int lessonObjectivesScore) {
		this.lessonObjectivesScore = lessonObjectivesScore;
	}

	public int getTimeManagementScore() {
		return timeManagementScore;
	}

	public void setTimeManagementScore(int timeManagementScore) {
		this.timeManagementScore = timeManagementScore;
	}

	public int getAppearanceScore() {
		return appearanceScore;
	}

	public void setAppearanceScore(int appearanceScore) {
		this.appearanceScore = appearanceScore;
	}

	public int getDelayDays() {
		return delayDays;
	}

	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}

	public boolean isStep5hasBeenReapply() {
		return step5hasBeenReapply;
	}

	public void setStep5hasBeenReapply(boolean step5hasBeenReapply) {
		this.step5hasBeenReapply = step5hasBeenReapply;
	}

	public int getAccent() {
		return accent;
	}

	public void setAccent(int accent) {
		this.accent = accent;
	}

	public int getPhonics() {
		return phonics;
	}

	public void setPhonics(int phonics) {
		this.phonics = phonics;
	}

	public int getPositive() {
		return positive;
	}

	public void setPositive(int positive) {
		this.positive = positive;
	}

	public int getEngaged() {
		return engaged;
	}

	public void setEngaged(int engaged) {
		this.engaged = engaged;
	}

	public int getAppearance() {
		return appearance;
	}

	public void setAppearance(int appearance) {
		this.appearance = appearance;
	}

}
