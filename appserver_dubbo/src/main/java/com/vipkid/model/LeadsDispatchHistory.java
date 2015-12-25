package com.vipkid.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vipkid.model.util.DBInfo;

@Entity
@Table(name = "leads_dispatch_history", schema = DBInfo.SCHEMA)
public class LeadsDispatchHistory extends Base {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "leads_id")
	private Long leadsId;
	
	@Column(name = "leads_status")
	private int leadsStatus;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "pre_user_id")
	private Long preUserId;
	
	@Column(name = "pre_user_name")
	private String preUserName;
	
	@Column(name = "assigner_id")
	private Long assignerId;
	
	@Column(name = "assign_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date assignTime;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLeadsId() {
		return leadsId;
	}
	public void setLeadsId(Long leadsId) {
		this.leadsId = leadsId;
	}
	public int getLeadsStatus() {
		return leadsStatus;
	}
	public void setLeadsStatus(int leadsStatus) {
		this.leadsStatus = leadsStatus;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getPreUserId() {
		return preUserId;
	}
	public void setPreUserId(Long preUserId) {
		this.preUserId = preUserId;
	}
	public String getPreUserName() {
		return preUserName;
	}
	public void setPreUserName(String preUserName) {
		this.preUserName = preUserName;
	}
	public Long getAssignerId() {
		return assignerId;
	}
	public void setAssignerId(Long assignerId) {
		this.assignerId = assignerId;
	}
	public Date getAssignTime() {
		return assignTime;
	}
	public void setAssignTime(Date assignTime) {
		this.assignTime = assignTime;
	}

}
