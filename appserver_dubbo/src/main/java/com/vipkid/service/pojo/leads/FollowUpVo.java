package com.vipkid.service.pojo.leads;

import java.util.Date;

public class FollowUpVo {

	private Long id;
	private Date createDateTime;
	private Date targetDateTime;
	private String assignee;
	private String content;
	private String creatorName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Date getTargetDateTime() {
		return targetDateTime;
	}
	public void setTargetDateTime(Date targetDateTime) {
		this.targetDateTime = targetDateTime;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
}
