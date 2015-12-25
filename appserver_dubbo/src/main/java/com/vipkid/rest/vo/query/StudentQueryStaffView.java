package com.vipkid.rest.vo.query;

public class StudentQueryStaffView {
	
	private Long id;
	private String name;
    private UserVO sales;
    private String englishName;
    private String cltName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StudentQueryStaffView() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public UserVO getSales() {
        return sales;
    }

    public void setSales(UserVO sales) {
        this.sales = sales;
    }
    
	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getCltName() {
		return cltName;
	}

	public void setCltName(String cltName) {
		this.cltName = cltName;
	}
}