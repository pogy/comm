package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.List;

public class AuditCategoriesOption implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String label;
	private List<AuditCategoryOption> auditCategoryOptions;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<AuditCategoryOption> getAuditCategoryOptions() {
		return auditCategoryOptions;
	}
	public void setAuditCategoryOptions(List<AuditCategoryOption> auditCategoryOptions) {
		this.auditCategoryOptions = auditCategoryOptions;
	}
	
}
