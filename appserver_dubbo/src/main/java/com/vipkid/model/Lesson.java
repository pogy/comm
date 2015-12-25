package com.vipkid.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.ActivityAdapter;
import com.vipkid.model.json.moxy.LearningCycleAdapter;
import com.vipkid.model.json.moxy.PPTAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.model.validation.group.AcadmicCourse;
import com.vipkid.service.pojo.LessonClassOfUnitView;

/**
 * 课程
 */
@Entity
@Table(name = "lesson", schema = DBInfo.SCHEMA)
public class Lesson extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 编号，如C1-U1-LC1-L1
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;
	
	// 活动
	@XmlJavaTypeAdapter(ActivityAdapter.class)
	@OneToMany(mappedBy="lesson")
	private List<Activity> activities;
	
	// 顺序
	@Column(name = "sequence", nullable = false)
	private int sequence;
	
	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// lesson编号
	@Column(name = "number")
	private String number;
	
	// 主题
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "topic")
	private String topic;
	
	// 领域
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "domain")
	private String domain;
	
	// 教学目标(老师)
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "objective")
	private String objective;
	
	// 教学目标(学生)
	@Lob
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "goal")
	private String goal;
	
	// 学习循环
	@XmlJavaTypeAdapter(LearningCycleAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "learning_cycle_id", referencedColumnName = "id", nullable = false)
	private LearningCycle learningCycle;
	
	// 单词
	@Column(name = "vocabularies")
	private String vocabularies;

	// 句型
	@Column(name = "sentence_patterns")
	private String sentencePatterns;
	
	// 语言，科学，社会目标
	@Column(name = "lss_target")
	private String lssTarget;
	
	// 数学目标
	@Column(name = "math_target")
	private String mathTarget;
	
	// 复习目标
	@Column(name = "review_target")
	private String reviewTarget;
	
	// 多贝云文档ID
	@Column(name = "dby_document")
	private String dbyDocument;
	
	//当前飞机的所有皮肤
	@Transient
	private LessonClassOfUnitView lessonClassOfUnitView;
	
	// PPT
	@Transient
	@XmlJavaTypeAdapter(PPTAdapter.class)
	private PPT ppt;
	
	@PrePersist
	public void prePersist() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public LearningCycle getLearningCycle() {
		return learningCycle;
	}

	public void setLearningCycle(LearningCycle learningCycle) {
		this.learningCycle = learningCycle;
	}

	public String getVocabularies() {
		return vocabularies;
	}

	public void setVocabularies(String vocabularies) {
		this.vocabularies = vocabularies;
	}

	public String getSentencePatterns() {
		return sentencePatterns;
	}

	public void setSentencePatterns(String sentencePatterns) {
		this.sentencePatterns = sentencePatterns;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getMathTarget() {
		return mathTarget;
	}

	public void setMathTarget(String mathTarget) {
		this.mathTarget = mathTarget;
	}

	public String getLssTarget() {
		return lssTarget;
	}

	public void setLssTarget(String lssTarget) {
		this.lssTarget = lssTarget;
	}

	public String getReviewTarget() {
		return reviewTarget;
	}

	public void setReviewTarget(String reviewTarget) {
		this.reviewTarget = reviewTarget;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	public LessonClassOfUnitView getLessonClassOfUnitView() {
		return lessonClassOfUnitView;
	}

	public void setLessonClassOfUnitView(LessonClassOfUnitView lessonClassOfUnitView) {
		this.lessonClassOfUnitView = lessonClassOfUnitView;
	}

	public PPT getPpt() {
		return ppt;
	}

	public void setPpt(PPT ppt) {
		this.ppt = ppt;
	}

	public String getDbyDocument() {
		return dbyDocument;
	}

	public void setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
	}
	
	public String getSafeName() {
		String name = this.name;
		return name.replace('&', ' ');
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	// 2015-09-01 为Trail lesson, 支持多个课件。
	public void addTrialDBYDocument(String strDocumentId) {
		//
		synchronized (this) {
			StringBuffer strDocuments = new StringBuffer(this.dbyDocument);
			
			if (strDocuments.length()>1) {
				strDocuments.append(",");
			}
			strDocuments.append(strDocumentId);
			
			//
			setDbyDocument(strDocuments.toString());
		}
		
	}
	
}
