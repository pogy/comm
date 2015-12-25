package com.vipkid.service.pojo.leads;

import java.util.ArrayList;
import java.util.List;

import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;

public class OrderVo {
	
	private Long id;
	private String serialNumber;
	private Long stuId;
	private String stuName;
	private String stuEnglishName;
	private float totalDealPrice;
	private float totalPrice;
	private int totalClassHour;
	private Status status;
	private PayBy payBy;
	private Long salesId;
	private String salesName;
	private List<OrderItemVo> orderItems = new ArrayList<OrderItemVo>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Long getStuId() {
		return stuId;
	}
	public void setStuId(Long stuId) {
		this.stuId = stuId;
	}
	public String getStuName() {
		return stuName;
	}
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}
	public String getStuEnglishName() {
		return stuEnglishName;
	}
	public void setStuEnglishName(String stuEnglishName) {
		this.stuEnglishName = stuEnglishName;
	}
	public float getTotalDealPrice() {
		return totalDealPrice;
	}
	public void setTotalDealPrice(float totalDealPrice) {
		this.totalDealPrice = totalDealPrice;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public int getTotalClassHour() {
		return totalClassHour;
	}
	public void setTotalClassHour(int totalClassHour) {
		this.totalClassHour = totalClassHour;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public PayBy getPayBy() {
		return payBy;
	}
	public void setPayBy(PayBy payBy) {
		this.payBy = payBy;
	}
	public Long getSalesId() {
		return salesId;
	}
	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	public List<OrderItemVo> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItemVo> orderItems) {
		this.orderItems = orderItems;
	}

}
