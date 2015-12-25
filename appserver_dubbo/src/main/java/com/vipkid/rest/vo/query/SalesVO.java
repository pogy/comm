package com.vipkid.rest.vo.query;

public class SalesVO {
    private Long id;
    private String name;
    private String englishName;
    private String email;
    private String mobile;
    private Boolean autoAssignLeads;
    private String managerName;
    private String managerEnglishName;
    private Long managerId;
    private Long salesTeamId;
    protected String roles;

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

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getAutoAssignLeads() {
		return autoAssignLeads;
	}

	public void setAutoAssignLeads(Boolean autoAssignLeads) {
		this.autoAssignLeads = autoAssignLeads;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerEnglishName() {
		return managerEnglishName;
	}

	public void setManagerEnglishName(String managerEnglishName) {
		this.managerEnglishName = managerEnglishName;
	}

	public Long getSalesTeamId() {
		return salesTeamId;
	}

	public void setSalesTeamId(Long salesTeamId) {
		this.salesTeamId = salesTeamId;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

}
