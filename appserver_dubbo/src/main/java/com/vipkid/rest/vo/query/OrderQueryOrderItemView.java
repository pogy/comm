package com.vipkid.rest.vo.query;

public class OrderQueryOrderItemView {
	
	private Long id;
	private Integer classHour;
	private Float dealPrice;
	private Float price;
	private OrderQueryUnitView startUnit;
	private OrderQueryProductView product;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getClassHour() {
		return classHour;
	}
	public void setClassHour(Integer classHour) {
		this.classHour = classHour;
	}
	public Float getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(Float dealPrice) {
		this.dealPrice = dealPrice;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public OrderQueryUnitView getStartUnit() {
		return startUnit;
	}
	public void setStartUnit(OrderQueryUnitView startUnit) {
		this.startUnit = startUnit;
	}
	public OrderQueryProductView getProduct() {
		return product;
	}
	public void setProduct(OrderQueryProductView product) {
		this.product = product;
	}
	

}
