package com.vipkid.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;
import com.vipkid.util.TextUtils;

@Entity
@Table(name = "role_permission", schema = DBInfo.SCHEMA, indexes = {@Index(name="index_role_permission_role", columnList = "role")})
public class RolePermission extends Base{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "role", nullable = false, unique = true)
	private String role;
	
	@Lob
	@Column(name = "permissions")
	private String permissions;
	
	@Transient
	private transient Set<String> permissionList = new HashSet<String>();
	
	@Transient
	private transient Set<String> permissionCodeList = new HashSet<String>();
	
	public Set<String> getPermissionList() {
		String[] strings = permissions.split(TextUtils.SPACE);
		for(String string : strings) {
			permissionList.add(string);
		}
		return permissionList;
	}
	
	public Set<String> getPermissionCodeList() {
		String[] permissionStrings = permissions.split(TextUtils.SPACE);
		for(String permissionString : permissionStrings) {
			Permission permission = null;
			try{
				permission = Permission.valueOf(TextUtils.removeEnter(permissionString));
			}catch(Exception e) {
				
			}
			
			if(permission != null 
					&& permission.getUri() != null 
					&& !permission.getUri().equals("")
					&& permission.getUri().indexOf("/") < 0) {
				permissionList.add(permission.getUri());
			}
		}
		return permissionList;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

}
