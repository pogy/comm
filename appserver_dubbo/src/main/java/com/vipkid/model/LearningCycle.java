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
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.LessonAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.model.validation.group.AcadmicCourse;

/**
 * 学习循环
 */
@Entity
@Table(name = "learning_cycle", schema = DBInfo.SCHEMA)
public class LearningCycle extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 编号，如C1-U1-LC1
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;
	
	// 学习循环
	@XmlJavaTypeAdapter(LessonAdapter.class)
	@OneToMany(mappedBy="learningCycle")
	private List<Lesson> lessons;
	
	// 顺序
	@Column(name = "sequence", nullable = false)
	private int sequence;

	// 名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// learning cycle编号
	@Column(name = "number")
	private String number;

	// 主题
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "topic")
	private String topic;
	
	// 教学目标
	@Lob
	@NotEmpty(message = ValidateMessages.NOT_EMPTY, groups = AcadmicCourse.class)
	@Column(name = "objective")
	private String objective;
	
	// 单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "unit_id", referencedColumnName = "id", nullable = false)
	private Unit unit;
	
	// 发音说明
	@Lob
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
	
	// 复习目标
	@Lob
	@Column(name = "review_target")
	private String reviewTarget;

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

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
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

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
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

	public String getReviewTarget() {
		return reviewTarget;
	}

	public void setReviewTarget(String reviewTarget) {
		this.reviewTarget = reviewTarget;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
