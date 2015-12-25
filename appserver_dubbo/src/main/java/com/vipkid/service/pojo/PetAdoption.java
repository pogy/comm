package com.vipkid.service.pojo;

import java.io.Serializable;

import com.vipkid.model.PetType;

public class PetAdoption implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long studentId;
	private PetType petType;
	
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public PetType getPetType() {
		return petType;
	}
	public void setPetType(PetType petType) {
		this.petType = petType;
	}
	
	
}
