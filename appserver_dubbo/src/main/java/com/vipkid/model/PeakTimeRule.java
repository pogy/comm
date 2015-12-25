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

import com.vipkid.model.PeakTime.Type;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 审计
 */
@Entity
@Table(name = "peak_time_rule", schema = DBInfo.SCHEMA)
public class PeakTimeRule extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		APPLIED,
		WORKING
	}
	
	public enum Category {
		LONGTIME,
		WEEKDAY,
		SPECIFICDAY 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "peak_time")
	private String peakTime; // time ranges in which, it is hot

	@Column(name = "time")
	private String time; // the key to find peak time
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private Category category;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "parent_rule_id", referencedColumnName = "id")
	private PeakTimeRule parentRule;

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	public String getPeakTime() {
		return peakTime;
	}

	public void setPeakTime(String peakTime) {
		this.peakTime = peakTime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public PeakTimeRule getParentRule() {
		return parentRule;
	}

	public void setParentRule(PeakTimeRule parentRule) {
		this.parentRule = parentRule;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
