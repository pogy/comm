package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.OrderItem;

public class OrderItemAdapter extends XmlAdapter<OrderItem, OrderItem> {
	@Override
	public OrderItem unmarshal(OrderItem orderItem) throws Exception {
		return orderItem;
	}

	@Override
	public OrderItem marshal(OrderItem orderItem) throws Exception {
		if(orderItem == null) {
			return null;
		}else {
			OrderItem simplifiedOrderItem = new OrderItem();
			simplifiedOrderItem.setId(orderItem.getId());
			simplifiedOrderItem.setClassHour(orderItem.getClassHour());
			simplifiedOrderItem.setClassHourPrice(orderItem.getClassHourPrice());
			simplifiedOrderItem.setProduct(orderItem.getProduct());
			simplifiedOrderItem.setPrice(orderItem.getPrice());
			simplifiedOrderItem.setDealPrice(orderItem.getDealPrice());
			simplifiedOrderItem.setUnits(orderItem.getUnits());
			simplifiedOrderItem.setStartUnit(orderItem.getStartUnit());
			simplifiedOrderItem.setComment(orderItem.getComment());
			return simplifiedOrderItem;
		}
	}
}
