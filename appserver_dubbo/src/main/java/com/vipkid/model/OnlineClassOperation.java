package com.vipkid.model;

import java.util.Date;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

@Entity
@Table(name = "online_class_operation", schema = DBInfo.SCHEMA)
public class OnlineClassOperation extends Base {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 操作员
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "operator", nullable = false)
	private String operator;
	
	//操作员Id
	@Column(name = "operator_id", nullable = false)
	private long operatorId;

	// 类别
	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private Category category;

	// 执行时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execute_date_time", nullable = false)
	private Date executeDateTime;

	// 操作对象
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "online_class_operation_student", inverseJoinColumns = @JoinColumn(name = "student_id"), joinColumns = @JoinColumn(name = "online_class_operation_id"))
	private List<Student> students;
		
	// 在线class
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
	@PrePersist
	public void prePersist() {
		this.executeDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Date getExecuteDateTime() {
		return executeDateTime;
	}

	public void setExecuteDateTime(Date executeDateTime) {
		this.executeDateTime = executeDateTime;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public OnlineClass getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		this.onlineClass = onlineClass;
	}
	
	
}
