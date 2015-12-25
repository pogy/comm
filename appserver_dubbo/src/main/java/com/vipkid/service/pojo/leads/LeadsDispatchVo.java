package com.vipkid.service.pojo.leads;

import java.util.List;

public class LeadsDispatchVo {
	
	private Long staffId;
	private List<Long> leadsList;
	
	
	public Long getStaffId() {
		return staffId;
	}
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	public List<Long> getLeadsList() {
		return leadsList;
	}
	public void setLeadsList(List<Long> leadsList) {
		this.leadsList = leadsList;
	}
	
	

}
