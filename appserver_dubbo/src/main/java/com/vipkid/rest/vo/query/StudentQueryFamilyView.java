package com.vipkid.rest.vo.query;

import java.util.ArrayList;
import java.util.List;


public class StudentQueryFamilyView {
	private Long id;
	private String province;
	private String city;
	private List<StudentQueryParentView> parents = new ArrayList<StudentQueryParentView>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public List<StudentQueryParentView> getParents() {
		return parents;
	}
	public void setParents(List<StudentQueryParentView> parents) {
		this.parents = parents;
	}
}