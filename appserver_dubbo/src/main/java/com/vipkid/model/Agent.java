package com.vipkid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 代理商
 */
@Entity
@Table(name = "agent", schema = DBInfo.SCHEMA)
public class Agent extends Base {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 电子邮箱，用户名
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Email(message = ValidateMessages.EMAIL)
	@Column(name = "email", nullable = false, unique = true)
	protected String email;

	// 密码
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "password", nullable = false)
	protected String password;
	
	// 姓名
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "name")
	protected String name;
	
    // 账户是否被锁定
    @Column(name = "locked")
    private boolean isLocked;
    
	// 令牌
	@Column(name = "token")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	
}
