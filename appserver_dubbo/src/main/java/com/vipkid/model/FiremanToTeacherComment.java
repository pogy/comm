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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * Fireman对老师评语
 */
@Entity
@Table(name = "fireman_to_teacher_comment", schema = DBInfo.SCHEMA)
public class FiremanToTeacherComment extends Base {
	private static final long serialVersionUID = 1L;
		
	public enum TeacherBehaviorProblem {
		UNREAY, // 没有备课
		BAD_ATTITUDE, // 态度不好
		CANNOT_FINISH_CLASS, // 自身原因无法完成课程
		LEAVE_EARLY // 早退
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 在线教室
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
	// 老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// 教师行为问题
	@ElementCollection(targetClass = TeacherBehaviorProblem.class) 
	@CollectionTable(name = "fireman_comment_teacher_behavior_problem", joinColumns = @JoinColumn(name = "fireman_comment_id"))
	@Column(name = "teacher_behavior_problem_id")
	private Set<TeacherBehaviorProblem> teacherBehaviorProblem; 
	
	// 教师IT问题
	@ElementCollection(targetClass = ITProblem.class) 
	@CollectionTable(name = "fireman_comment_teacher_it_problem", joinColumns = @JoinColumn(name = "fireman_comment_id"))
	@Column(name = "teacher_it_problem_id")
	private Set<ITProblem> teacherITProblem;
	
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

	public Set<TeacherBehaviorProblem> getTeacherBehaviorProblem() {
		return teacherBehaviorProblem;
	}

	public void setTeacherBehaviorProblem(Set<TeacherBehaviorProblem> teacherBehaviorProblem) {
		this.teacherBehaviorProblem = teacherBehaviorProblem;
	}

	public Set<ITProblem> getTeacherITProblem() {
		return teacherITProblem;
	}

	public void setTeacherITProblem(Set<ITProblem> teacherITProblem) {
		this.teacherITProblem = teacherITProblem;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
