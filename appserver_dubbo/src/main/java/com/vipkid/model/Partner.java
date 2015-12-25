package com.vipkid.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

@Entity
@Table(name = "partner", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class Partner extends User {
	private static final long serialVersionUID = 1L;
	
	public enum Type{
		TEACHER_RECRUITMENT,
	}
    
	@Transient
	private transient Set<Role> roleSet = new HashSet<Role>();
	
	// 电子邮箱
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "email", nullable = false)
	private String email;
	
	//类型
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private Type type;
	
	 //关联到teacher
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@OneToMany(mappedBy = "partner")
	private List<Teacher> teachers;

	public List<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}

	public Set<Role> getRoleSet() {
		return roleSet;
	}

	public void setRoleSet(Set<Role> roleSet) {
		this.roleSet = roleSet;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	@PrePersist
	public void prePersist() {
		super.prePersist();
		roles = Role.PARTNER.name();
	}
	
	public Partner() {
		roleSet.add(Role.PARTNER);
		roles = Role.PARTNER.name();
	}
	
	
}
