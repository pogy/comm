package com.vipkid.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.CourseAdapter;
import com.vipkid.model.json.moxy.LessonAdapter;
import com.vipkid.model.json.moxy.OnlineClassesAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 学习进度
 */
@Entity
@Table(name = "learning_progress", schema = DBInfo.SCHEMA)
public class LearningProgress extends Base {
	private static final long serialVersionUID = 1L;
	
	/** 状态 */
	public enum Status {
		STARTED,	//已开始
		FINISHED	//已结束
	}
	
	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;	
	
	// 课程
	@XmlJavaTypeAdapter(CourseAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private Course course;
	
	// 产品id
	@Column(name = "product_id")
	private long productId;

	// 开始单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "start_unit_id", referencedColumnName = "id")
	private Unit startUnit;
	
	// 在线课程
	@XmlJavaTypeAdapter(OnlineClassesAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "learning_progress_completed_online_class", inverseJoinColumns = @JoinColumn(name = "online_class_id"), joinColumns = @JoinColumn(name = "learning_progress_id"))
	private List<OnlineClass> completedOnlineClasses = new LinkedList<OnlineClass>();
	
	@Transient
	private OnlineClass firstCompletedOnlineClass;
	
	@Transient
	private OnlineClass lastCompletedOnlineClass;
		
	// 下节应上课程
	@XmlJavaTypeAdapter(LessonAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "next_should_take_lesson_id", referencedColumnName = "id")
	private Lesson nextShouldTakeLesson;
		
	// 最近预约课程
	@XmlJavaTypeAdapter(LessonAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "last_scheduled_lesson_id", referencedColumnName = "id")
	private Lesson lastScheduledLesson;
	
	// 当前剩余可预约课时
	@Column(name = "left_avaiable_class_hour", nullable = false)
	private int leftAvaiableClassHour;
	
	// 当前剩余课时
	@Column(name = "left_class_hour", nullable = false)
	private int leftClassHour;
	
	// 总课时
	@Column(name = "total_class_hour", nullable = false)
	private int totalClassHour;
	
	@Transient
	private Lesson reScheduledStartLesson;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
		if(this.student != null && !this.student.getLearningProgresses().contains(this)) {
			this.student.getLearningProgresses().add(this);
		}
	}
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public Lesson getNextShouldTakeLesson() {
		return nextShouldTakeLesson;
	}

	public void setNextShouldTakeLesson(Lesson nextShouldTakeLesson) {
		this.nextShouldTakeLesson = nextShouldTakeLesson;
	}

	public Lesson getLastScheduledLesson() {
		return lastScheduledLesson;
	}

	public void setLastScheduledLesson(Lesson lastScheduledLesson) {
		this.lastScheduledLesson = lastScheduledLesson;
	}

	public int getLeftClassHour() {
		return leftClassHour;
	}

	public void setLeftClassHour(int leftClassHour) {
		this.leftClassHour = leftClassHour;
	}
	
	public List<OnlineClass> getCompletedOnlineClasses() {
		return completedOnlineClasses;
	}

	public void setCompletedOnlineClasses(List<OnlineClass> completedOnlineClasses) {
		this.completedOnlineClasses = completedOnlineClasses;
	}

	public void addCompletedOnlineClass(OnlineClass onlineClass){
		this.completedOnlineClasses.add(onlineClass);
	}
	
	public void removeCompletedOnlineClass(OnlineClass onlineClass){
		this.completedOnlineClasses.remove(onlineClass);
	}

	public int getTotalClassHour() {
		return totalClassHour;
	}

	public void setTotalClassHour(int totalClassHour) {
		this.totalClassHour = totalClassHour;
	}

	public OnlineClass getFirstCompletedOnlineClass() {
		return firstCompletedOnlineClass;
	}

	public void setFirstCompletedOnlineClass(OnlineClass firstCompletedOnlineClass) {
		this.firstCompletedOnlineClass = firstCompletedOnlineClass;
	}

	public OnlineClass getLastCompletedOnlineClass() {
		return lastCompletedOnlineClass;
	}

	public void setLastCompletedOnlineClass(OnlineClass lastCompletedOnlineClass) {
		this.lastCompletedOnlineClass = lastCompletedOnlineClass;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getLeftAvaiableClassHour() {
		return leftAvaiableClassHour;
	}

	public void setLeftAvaiableClassHour(int leftAvaiableClassHour) {
		this.leftAvaiableClassHour = leftAvaiableClassHour;
	}

	public Unit getStartUnit() {
		return startUnit;
	}

	public void setStartUnit(Unit startUnit) {
		this.startUnit = startUnit;
	}

	public Lesson getReScheduledStartLesson() {
		return reScheduledStartLesson;
	}

	public void setReScheduledStartLesson(Lesson reScheduledStartLesson) {
		this.reScheduledStartLesson = reScheduledStartLesson;
	}
	
	

}
