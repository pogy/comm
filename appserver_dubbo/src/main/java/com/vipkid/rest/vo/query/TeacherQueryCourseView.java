package com.vipkid.rest.vo.query;

import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;

public class TeacherQueryCourseView {
	
	private Long id;
	private String name;
    private String realName;
	private String serialNumber;
	private Mode mode;
	private Type type;
	private Boolean needBackupTeacher;
	private Boolean sequential;
	private Boolean free;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Boolean isNeedBackupTeacher() {
		return needBackupTeacher;
	}

	public void setNeedBackupTeacher(Boolean needBackupTeacher) {
		this.needBackupTeacher = needBackupTeacher;
	}

	public Boolean isSequential() {
		return sequential;
	}

	public void setSequential(Boolean sequential) {
		this.sequential = sequential;
	}

	public Boolean isFree() {
		return free;
	}

	public void setFree(Boolean free) {
		this.free = free;
	}
    
    
}
