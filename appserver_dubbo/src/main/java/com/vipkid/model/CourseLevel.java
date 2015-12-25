package com.vipkid.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.CourseAdapter;
import com.vipkid.model.json.moxy.CourseLevelAdapter;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * Curriculum中的Level，连接Course和Unit
 * @author wangbing 20150731
 *
 */
@Entity
@Table(name = "level", schema = DBInfo.SCHEMA)
public class CourseLevel extends Base{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	//序列编号，如C1-L2
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "serial_number", nullable = false, unique = true)
	private String serialNumber;

	//名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;

	//顺序
	@Column(name = "sequence", nullable = false)
	private int sequence;

	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;	
	
	//课程
	@XmlJavaTypeAdapter(CourseAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
	private Course course;

	//单元
	@XmlJavaTypeAdapter(CourseLevelAdapter.class)
	@OneToMany(mappedBy="courseLevel")
	private List<Unit> units;

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
}
