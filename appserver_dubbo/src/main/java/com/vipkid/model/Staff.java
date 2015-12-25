package com.vipkid.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.util.TextUtils;

/**
 * 员工
 * 用户名员工邮箱
 */
@Entity
@Table(name = "staff", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class Staff extends User {
	private static final long serialVersionUID = 1L;
	
	// 角色列表
	@Transient
    @XmlTransient
	private Set<Role> roleSet = new HashSet<Role>();
	
	// 英文姓名
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "english_name", nullable = false)
	private String englishName;
	
	// 手机
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "mobile", nullable = false)
	private String mobile;

	// 电子邮箱
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "email")
	private String email;
	
	// 记录sales(tmk) team id, 相关信息在VO中取
	@Column(name = "sales_team_id")
	private Long salesTeamId;
	
	@PrePersist
	public void prePersist() {
		super.prePersist();
	}
	
	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
	}

    @XmlTransient
    @Override
	public Set<Role> getRoleSet() {
		String[] strings = roles.split(TextUtils.SPACE);
		for(String string : strings) {
			roleSet.add(Role.valueOf(string));
		}
		return roleSet;
	}
	
	public void addRole(Role role) {
		roleSet.add(role);
		
		StringBuilder sbRoles = new StringBuilder();
		for(Role iRole : roleSet) {
			sbRoles.append(iRole.name()).append(TextUtils.SPACE);
		}
		
		this.roles = sbRoles.toString().trim();
	}
	
	public void removeRole(Role role) {
		roleSet.remove(role);
		
		StringBuilder sbRoles = new StringBuilder();
		for(Role iRole : roleSet) {
			sbRoles.append(iRole.name()).append(TextUtils.SPACE);
		}
		
		this.roles = sbRoles.toString().trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Long getSalesTeamId() {
		return salesTeamId;
	}

	public void setSalesTeamId(Long salesTeamId) {
		this.salesTeamId = salesTeamId;
	}

	
}
