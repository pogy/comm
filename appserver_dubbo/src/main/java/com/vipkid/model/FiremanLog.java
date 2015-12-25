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

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.StaffAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 审计
 */
@Entity
@Table(name = "fireman_log", schema = DBInfo.SCHEMA)
public class FiremanLog extends Base {
	private static final long serialVersionUID = 1L;

	public enum Event {
		TEACHER_NEED_HELP,
		STUDENT_NEED_HELP,
		BOTH_NEED_HELP,
		STUDENT_NOT_ENTER,
		TEACHER_NOT_ENTER
	}
	
	public enum Status {
		RESOLVING,
		RESOLVED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;

	@XmlJavaTypeAdapter(StaffAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "resolved_by_staff_id", referencedColumnName = "id")
	private Staff resolvedBy;
	
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "need_help_user", referencedColumnName = "id")
	private User needHelpUser;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date_time", nullable = false)
	private Date createdDateTime;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "resolve_date_time")
	private Date resolveDateTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "event", nullable = false)
	private Event event;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;
	
	@Column(name = "help_yell", columnDefinition="bit default 0")
	private boolean helpYell;
	
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

	public Staff getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(Staff resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public User getNeedHelpUser() {
		return needHelpUser;
	}

	public void setNeedHelpUser(User needHelpUser) {
		this.needHelpUser = needHelpUser;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getResolveDateTime() {
		return resolveDateTime;
	}

	public void setResolveDateTime(Date resolveDateTime) {
		this.resolveDateTime = resolveDateTime;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isHelpYell() {
		return helpYell;
	}

	public void setHelpYell(boolean helpYell) {
		this.helpYell = helpYell;
	}

	
	
}
