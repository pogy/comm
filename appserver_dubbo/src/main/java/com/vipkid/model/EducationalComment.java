package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
 * 学术督导评语
 */
@Entity
@Table(name = "educational_comment", schema = DBInfo.SCHEMA)
public class EducationalComment extends Base {
	private static final long serialVersionUID = 1L;
	
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
	
	// 内容
	@Lob
	@Column(name = "content")
	private String content;
	
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
