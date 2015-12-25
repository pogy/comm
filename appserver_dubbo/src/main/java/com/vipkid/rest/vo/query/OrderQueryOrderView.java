package com.vipkid.rest.vo.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;

public class OrderQueryOrderView {
	private Long id;
	private String serialNumber;
	private Status status;
	private Date createDateTime;
	private Date paidDateTime;
	private PayBy payBy;
	private OrderQueryStudentView student;
	private List<OrderQueryOrderItemView> orderItems = new ArrayList<OrderQueryOrderItemView>();
	private String payer;
	
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
	public OrderQueryStudentView getStudent() {
		return student;
	}
	public void setStudent(OrderQueryStudentView student) {
		this.student = student;
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
	public List<OrderQueryOrderItemView> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderQueryOrderItemView> orderItems) {
		this.orderItems = orderItems;
	}

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getPaidDateTime() {
        return paidDateTime;
    }

    public void setPaidDateTime(Date paidDateTime) {
        this.paidDateTime = paidDateTime;
    }
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
}
