package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * Moxtra用户
 */
@Entity
@Table(name = "moxtra_user", schema = DBInfo.SCHEMA)
public class MoxtraUser extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// Moxtra用户ID
	@Column(name = "moxtra_user_id")
	private String moxtraUserId;
	
	// VIPKID用户
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "vipkid_user_id", referencedColumnName = "id")
	private User vipkidUser;
	
	// 是否使用中
	@Column(name = "in_use")
	private boolean inUse;
	
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

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getMoxtraUserId() {
		return moxtraUserId;
	}

	public void setMoxtraUserId(String moxtraUserId) {
		this.moxtraUserId = moxtraUserId;
	}

	public User getVipkidUser() {
		return vipkidUser;
	}

	public void setVipkidUser(User vipkidUser) {
		this.vipkidUser = vipkidUser;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
}
