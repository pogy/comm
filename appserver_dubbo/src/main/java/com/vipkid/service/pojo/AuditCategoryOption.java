package com.vipkid.service.pojo;

import java.io.Serializable;

public class AuditCategoryOption implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
