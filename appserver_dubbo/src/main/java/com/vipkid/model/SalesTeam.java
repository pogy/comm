package com.vipkid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 销售team
 */
@Entity
@Table(name = "sales_team", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class SalesTeam extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		SALES,
		TMK,
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 记录manager id, 相关信息在VO中取
	@NotNull(message = ValidateMessages.NOT_NULL)
	@Column(name = "manager_id", nullable = false, unique = true)
	private long managerId;
	
	// 类型
	@NotNull(message = ValidateMessages.NOT_NULL)
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private Type type;

	// 其他字段预留, 以备业务扩展
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getManagerId() {
		return managerId;
	}

	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
