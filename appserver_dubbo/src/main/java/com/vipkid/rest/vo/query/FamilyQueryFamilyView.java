package com.vipkid.rest.vo.query;

import java.util.ArrayList;
import java.util.List;

public class FamilyQueryFamilyView {

	private Long id;
	private String name;
	private String phone;
	private String province;
	private String city;
	private String district;
	private String address;
	private List<FamilyQueryParentView> parents = new ArrayList<FamilyQueryParentView>();
	private List<FamilyQueryStudentView> students = new ArrayList<FamilyQueryStudentView>();
	
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<FamilyQueryParentView> getParents() {
		return parents;
	}
	public void setParents(List<FamilyQueryParentView> parents) {
		this.parents = parents;
	}
	public List<FamilyQueryStudentView> getStudents() {
		return students;
	}
	public void setStudents(List<FamilyQueryStudentView> students) {
		this.students = students;
	}

}
