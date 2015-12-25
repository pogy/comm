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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 勋章
 */
@Entity
@Table(name = "medal", schema = DBInfo.SCHEMA)
public class Medal extends Base {
	
	private static final long serialVersionUID = -2257011196672972292L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	
	// 勋章名称
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name", nullable = false)
	private String name;
	
	// 勋章描述
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "description", nullable = false)
	private String description;
	
	// 勋章对应的单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "unit_id", referencedColumnName = "id")
	private Unit unit;
	
	//勋章对应的活动
	@Enumerated(EnumType.STRING)
	@Column(name = "activity")
	private MarketActivity activity;
	
	// 获得勋章学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;
	
	// 勋章获得时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gain_time")
	private Date gainTime;

	//是否是新获得的勋章
	@Column(name = "pristine")
	private boolean pristine;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getGainTime() {
		return gainTime;
	}

	public void setGainTime(Date gainTime) {
		this.gainTime = gainTime;
	}

	public boolean isPristine() {
		return pristine;
	}

	public void setPristine(boolean pristine) {
		this.pristine = pristine;
	}

	public MarketActivity getActivity() {
		return activity;
	}

	public void setActivity(MarketActivity activity) {
		this.activity = activity;
	}


}
