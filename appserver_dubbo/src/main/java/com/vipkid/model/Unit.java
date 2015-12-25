package com.vipkid.model;

import com.vipkid.model.json.moxy.CourseAdapter;
import com.vipkid.model.json.moxy.CourseLevelAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.LearningCycleAdapter;
import com.vipkid.model.json.moxy.ProductAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.model.validation.group.AcadmicCourse;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;
import java.util.List;

/**
 * 单元
 */
@Entity
@Table(name = "unit", schema = DBInfo.SCHEMA)
public class Unit extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 编号，如C1-U1
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;
	
	// 学习循环
	@XmlJavaTypeAdapter(LearningCycleAdapter.class)
	@OneToMany(mappedBy="unit")
	private List<LearningCycle> learningCycles;
	
	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// unit编号
	@Column(name = "number")
	private String number;
	
	// 课程级别
	@Enumerated(EnumType.STRING)
	@Column(name = "level")
	private Level level;
	
	// 在当前level中的名称
	@Column(name = "name_in_level")
	private String nameInLevel;
	
	// 领域
	@Column(name = "domain")
	private String domain;	

	// 主题
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "topic")
	private String topic;
	
	// 教学目标
	@Lob
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "objective")
	private String objective;
	
	// 顺序
	@Column(name = "sequence", nullable = false)
	private int sequence;
	
	// 发音说明
	@Column(name = "phonemic_awareness")
	private String phonemicAwareness;
	
	// 字母
	@Column(name = "letters")
	private String letters;
	
	// 常用单词
	@Column(name = "high_frenquncy_words")
	private String highFrenquncyWords;
	
	// 单词
	@Lob
	@Column(name = "vocabularies")
	private String vocabularies;
	
	// 语法
	@Lob
	@Column(name = "grammar")
	private String grammar;
	
	// 句型
	@Lob
	@Column(name = "sentence_patterns")
	private String sentencePatterns;
	
	// CCSS语言标准
	@Lob
	@Column(name = "ccss_language_art")
	private String ccssLanguageArt;
	
	// 数学主题
	@Column(name = "math_topic")
	private String mathTopic;
	
	// CCSS数学标准
	@Lob
	@Column(name = "ccss_math")
	private String ccssMath;
	
	// 准入标准
	@Lob
	@Column(name = "entry_point_criteria")
	private String entryPointCriteria;
	
	//单元测评
	@Column(name = "unit_test_path")
	private String unitTestPath;

	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;	
	
	// 教学方案
	@XmlJavaTypeAdapter(CourseAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
	private Course course;
	
	//水平划分
	@XmlJavaTypeAdapter(CourseLevelAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "level_id", referencedColumnName = "id")
	private CourseLevel courseLevel;
	
	@XmlJavaTypeAdapter(ProductAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH, mappedBy="units")
	private List<Product> products;
	
	@Transient
	private long learningCycleCount;
	
	@Transient
	private long lessonCount;
	
	@PrePersist
	public void prePersist(){
		this.createDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPhonemicAwareness() {
		return phonemicAwareness;
	}

	public void setPhonemicAwareness(String phonemicAwareness) {
		this.phonemicAwareness = phonemicAwareness;
	}

	public String getLetters() {
		return letters;
	}

	public void setLetters(String letters) {
		this.letters = letters;
	}

	public String getHighFrenquncyWords() {
		return highFrenquncyWords;
	}

	public void setHighFrenquncyWords(String highFrenquncyWords) {
		this.highFrenquncyWords = highFrenquncyWords;
	}

	public String getVocabularies() {
		return vocabularies;
	}

	public void setVocabularies(String vocabularies) {
		this.vocabularies = vocabularies;
	}

	public String getGrammar() {
		return grammar;
	}

	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}

	public String getSentencePatterns() {
		return sentencePatterns;
	}

	public void setSentencePatterns(String sentencePatterns) {
		this.sentencePatterns = sentencePatterns;
	}

	public String getCcssLanguageArt() {
		return ccssLanguageArt;
	}

	public void setCcssLanguageArt(String ccssLanguageArt) {
		this.ccssLanguageArt = ccssLanguageArt;
	}

	public String getMathTopic() {
		return mathTopic;
	}

	public void setMathTopic(String mathTopic) {
		this.mathTopic = mathTopic;
	}

	public String getCcssMath() {
		return ccssMath;
	}

	public void setCcssMath(String ccssMath) {
		this.ccssMath = ccssMath;
	}

	public String getEntryPointCriteria() {
		return entryPointCriteria;
	}

	public void setEntryPointCriteria(String entryPointCriteria) {
		this.entryPointCriteria = entryPointCriteria;
	}

	public List<LearningCycle> getLearningCycles() {
		return learningCycles;
	}

	public void setLearningCycles(List<LearningCycle> learningCycles) {
		this.learningCycles = learningCycles;
	}

	public long getLearningCycleCount() {
		return learningCycleCount;
	}

	public void setLearningCycleCount(long learningCycleCount) {
		this.learningCycleCount = learningCycleCount;
	}

	public long getLessonCount() {
		return lessonCount;
	}

	public void setLessonCount(long lessonCount) {
		this.lessonCount = lessonCount;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getNameInLevel() {
		return nameInLevel;
	}

	public void setNameInLevel(String nameInLevel) {
		this.nameInLevel = nameInLevel;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUnitTestPath() {
		return unitTestPath;
	}

	public void setUnitTestPath(String unitTestPath) {
		this.unitTestPath = unitTestPath;
	}

	public CourseLevel getCourseLevel() {
		return courseLevel;
	}

	public void setCourseLevel(CourseLevel courseLevel) {
		this.courseLevel = courseLevel;
	}

}
