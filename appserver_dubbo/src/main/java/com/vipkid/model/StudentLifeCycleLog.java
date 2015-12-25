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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;

@Entity
@Table(name = "student_life_cycle_log", schema = DBInfo.SCHEMA)
public class StudentLifeCycleLog extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date_time", nullable = false)
	private Date createdDateTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "from_student_lifecycle")
	private LifeCycle fromStudentLifeCycle;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "to_student_lifecycle")
	private LifeCycle toStudentLifeCycle;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public LifeCycle getFromStudentLifeCycle() {
		return fromStudentLifeCycle;
	}

	public void setFromStudentLifeCycle(LifeCycle fromStudentLifeCycle) {
		this.fromStudentLifeCycle = fromStudentLifeCycle;
	}

	public LifeCycle getToStudentLifeCycle() {
		return toStudentLifeCycle;
	}

	public void setToStudentLifeCycle(LifeCycle toStudentLifeCycle) {
		this.toStudentLifeCycle = toStudentLifeCycle;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}
