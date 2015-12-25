package com.vipkid.service.pojo.leads;

public class OrderItemVo {
	private Long id;
	private String productName;
	private float classHour;
	private float price;
	private float dealPrice;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public float getClassHour() {
		return classHour;
	}
	public void setClassHour(float classHour) {
		this.classHour = classHour;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(float dealPrice) {
		this.dealPrice = dealPrice;
	}

}
