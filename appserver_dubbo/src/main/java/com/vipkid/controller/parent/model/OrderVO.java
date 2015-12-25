package com.vipkid.controller.parent.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vipkid.model.OrderItem;
import com.vipkid.model.Student;
import com.vipkid.model.Order.Status;

public class OrderVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public OrderVO(long id, String serialNumber, List<OrderItem> orderItems,
			float totalPrice, float totalDealPrice, Student student,
			Status status, Date createDateTime) {
		super();
		this.id = id;
		this.serialNumber = serialNumber;
		this.orderItems = orderItems;
		this.totalPrice = totalPrice;
		this.totalDealPrice = totalDealPrice;
		this.student = student;
		this.status = transferStatus(status);
		this.createDateTime = createDateTime;
	}
	
	private long id;
	private String serialNumber;
	private List<OrderItem> orderItems;
	private float totalPrice;
	private float totalDealPrice;
	private Student student;
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDateTime;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public float getTotalDealPrice() {
		return totalDealPrice;
	}
	public void setTotalDealPrice(float totalDealPrice) {
		this.totalDealPrice = totalDealPrice;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	private String transferStatus(Status status){
		String result = "";
		switch(status){
		case TO_PAY:
			result = "等待支付";
			break;
		case PAID:
			result = "已支付, 审核中...";
			break;
		case CANCELED:
			result = "已取消";
			break;
		case PAY_CONFIRMED:
			result= "已支付，订单成功";
			break;
		}
		return result;
	}
}
