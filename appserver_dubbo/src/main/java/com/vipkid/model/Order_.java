package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.Order.Status;
import com.vipkid.model.Order.PayBy;;

@StaticMetamodel(Order.class)
public class Order_ {
	
	public static volatile SingularAttribute<Order, Long> id;
	public static volatile SingularAttribute<Order, String> serialNumber;
	public static volatile SingularAttribute<Order, Date> createDateTime;
	public static volatile SingularAttribute<Order, Parent> parent;
	public static volatile SingularAttribute<Order, Student> student;
	public static volatile SingularAttribute<Order, Status> status;
	public static volatile SingularAttribute<Order, Date> paidDateTime;
	public static volatile SingularAttribute<Order, Date> canceledDateTime;
	public static volatile SingularAttribute<Order, PayBy> payBy;
	public static volatile SingularAttribute<Order, Staff> creater;
	public static volatile SingularAttribute<Order, Staff> confirmer;
	public static volatile SingularAttribute<Order, Float> totalDealPrice;
	public static volatile SingularAttribute<Order, String> comment;
	public static volatile ListAttribute<Order, OrderItem> orderItems;
}
