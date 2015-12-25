package com.vipkid.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * Fireman对学生评语
 */
@Entity
@Table(name = "fireman_to_student_comment", schema = DBInfo.SCHEMA)
public class FiremanToStudentComment extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum StudentBehaviorProblem {
		LEAVE_FROM_SITE, // 频繁离开座位
		LEAVE_FROM_CAMERA, // 离开摄像头视野
		NO_INTRESTING, // 没有兴趣
		PLAY_OTHER_THINGS, // 玩与上课无关的事情
		PLAY_MOUSE_OR_KEYBOARD, // 玩鼠标或键盘
		CRY, // 哭了
		DISTRACTED, // 走神
		NEED_PARENT_HELP, // 需要家长帮助回答问题
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 在线教室
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
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
	
	// 学生行为问题
	@ElementCollection(targetClass = StudentBehaviorProblem.class) 
	@CollectionTable(name = "fireman_comment_student_behavior_problem", joinColumns = @JoinColumn(name = "fireman_comment_id"))
	@Column(name = "student_behavior_problem_id")
	private Set<StudentBehaviorProblem> studentBehaviorProblem; 
	
	// 学生IT问题
	@ElementCollection(targetClass = ITProblem.class) 
	@CollectionTable(name = "fireman_comment_student_it_problem", joinColumns = @JoinColumn(name = "fireman_comment_id"))
	@Column(name = "student_it_problem_id")
	private Set<ITProblem> studentITProblem; 
	
	// 补充说明
	@Lob
	@Column(name = "supplement")
	private String supplement;
	
	// 标记是否为空
	@Column(name = "empty")
	private boolean empty;
	
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

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getSupplement() {
		return supplement;
	}

	public void setSupplement(String supplement) {
		this.supplement = supplement;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Set<StudentBehaviorProblem> getStudentBehaviorProblem() {
		return studentBehaviorProblem;
	}

	public void setStudentBehaviorProblem(Set<StudentBehaviorProblem> studentBehaviorProblem) {
		this.studentBehaviorProblem = studentBehaviorProblem;
	}

	public Set<ITProblem> getStudentITProblem() {
		return studentITProblem;
	}

	public void setStudentITProblem(Set<ITProblem> studentITProblem) {
		this.studentITProblem = studentITProblem;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
