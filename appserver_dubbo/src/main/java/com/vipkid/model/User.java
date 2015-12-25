package com.vipkid.model;

import com.google.common.collect.Sets;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.UserAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.util.TextUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED) 
@Table(name = "user", schema = DBInfo.SCHEMA, indexes = {@Index(name="index_user_username", columnList = "username")})
public class User extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Status {
		NORMAL, // 正常
		LOCKED, // 冻结
		TEST //测试账号，用于教师招聘约课程
	}
	
	public enum AccountType {
		NORMAL, // 正式账号
		TEST // 员工创建的测试账号，用于测试流程
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 用户名
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	// 密码
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "password", nullable = false)
	private String password;
	
	// 密码
	@Column(name = "init_password")
	private String initPassword;
	
	// 角色
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "roles", nullable = false)
	protected String roles;
	
	// 姓名
	@Column(name = "name")
	protected String name;
	
	// 性别
	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;
	
	// 状态
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	// 账户类型
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", nullable = false)
	private AccountType accountType;
	
	// 注册时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "register_date_time")
	private Date registerDateTime;
	
	// 最近登录时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_date_time")
	private Date lastLoginDateTime;
	
	// 创建人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "creater_id", referencedColumnName = "id")
	private User creater;
	
	// 创建时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// 最后编辑人
	@XmlJavaTypeAdapter(UserAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "last_editor_id", referencedColumnName = "id")
	private User lastEditor;
	
	// 最后编辑时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_edit_date_time")
	private Date lastEditDateTime;
	
	// 令牌
	@Column(name = "token")
	private String token;

	//是否第一次登陆
	@Column(name = "isFirstLogin")
	private String isFirstLogin;
	
	@Transient
	@XmlTransient
	private transient Set<String> roleList = new HashSet<String>();
	public Set<Role> getRoleSet(){
		return new HashSet<Role>();
	}

	@XmlTransient
	public Set<String> getRoleList() {
		String[] strings = StringUtils.split(roles,TextUtils.SPACE);
        if (CollectionUtils.isEmpty(roleList)) {
            roleList = Sets.newHashSet();
        }
		for(String string : strings) {
			roleList.add(string);
		}
		return roleList;
	}
	
	@PrePersist
	public void prePersist() {
		this.registerDateTime = new Date();
		if (this.status == null){
			this.status = Status.NORMAL;
		}
		if (this.accountType == null){
			this.accountType = AccountType.NORMAL;
		}
		this.createDateTime = new Date();
		this.lastEditDateTime = new Date();
		this.lastEditor = this.creater;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.lastEditDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	public String getSafeName() {
		if(TextUtils.isEmpty(name)) {
			return username;
		}else {
			return name;
		}
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getRegisterDateTime() {
		return registerDateTime;
	}

	public void setRegisterDateTime(Date registerDateTime) {
		this.registerDateTime = registerDateTime;
	}

	public Date getLastLoginDateTime() {
		return lastLoginDateTime;
	}

	public void setLastLoginDateTime(Date lastLoginDateTime) {
		this.lastLoginDateTime = lastLoginDateTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getRoles() {
		return roles;
	}
	
	public void setRoles(String roles) {
		this.roles = roles;
	}

	public User getCreater() {
		return creater;
	}

	public void setCreater(User creater) {
		this.creater = creater;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public User getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(User lastEditor) {
		this.lastEditor = lastEditor;
	}

	public Date getLastEditDateTime() {
		return lastEditDateTime;
	}

	public void setLastEditDateTime(Date lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
	}

	public String getInitPassword() {
		return initPassword;
	}

	public void setInitPassword(String initPassword) {
		this.initPassword = initPassword;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getIsFirstLogin() {
		return isFirstLogin;
	}

	public void setIsFirstLogin(String isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}
	
	
	
}
