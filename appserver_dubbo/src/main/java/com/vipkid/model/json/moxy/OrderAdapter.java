package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Order;

public class OrderAdapter extends XmlAdapter<Order, Order> {

	@Override
	public Order unmarshal(Order order) throws Exception {
		return order;
	}

	@Override
	public Order marshal(Order order) throws Exception {
		if(order == null) {
			return null;
		}else {
			Order simplifiedorder = new Order();
			simplifiedorder.setId(order.getId());
			return simplifiedorder;
		}	
	}
}