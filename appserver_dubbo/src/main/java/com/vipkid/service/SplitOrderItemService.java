package com.vipkid.service;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.model.Course.Type;
import com.vipkid.model.Order;
import com.vipkid.model.OrderItem;
import com.vipkid.repository.OrderRepository;
import com.vipkid.rest.vo.Response;

@Service
public class SplitOrderItemService {
	private Logger logger = LoggerFactory.getLogger(SplitOrderItemService.class
			.getSimpleName());

	@Resource
	private OrderRepository orderRepository;
	
	public Response split(){
		logger.info("split");
		List<Order> orderList = orderRepository.findOrderList();
		for (Order ord : orderList) {
			if(CollectionUtils.isNotEmpty(ord.getOrderItems())){
				createNewOrder(ord);
			}
		}
		return new Response(HttpStatus.OK.value());
	}

	private void createNewOrder(Order ord) {
		int sign = 0;
		Iterator<OrderItem>it = ord.getOrderItems().iterator();
		while(it.hasNext()){
			OrderItem orderItem = it.next();
			if(orderItem.getProduct().getCourse().getType()==Type.MAJOR){
				sign+=1;
				if(sign>1){
					//原定单中总价减去 去掉的orderitem 的价格
					ord.setTotalDealPrice(ord.getTotalDealPrice()-orderItem.getDealPrice());
					
					Order order = new Order();
					order.setSerialNumber(UUID.randomUUID().toString());
					order.setFamily(ord.getFamily());
					order.setStudent(ord.getStudent());
					order.setOnlinePayFailed(ord.isOnlinePayFailed());
					order.setPaidDateTime(ord.getPaidDateTime());
					order.setContractStartTime(ord.getContractStartTime());
					order.setContractEndTime(ord.getContractEndTime());
					order.setCanceledDateTime(ord.getCanceledDateTime());
					order.setPayBy(ord.getPayBy());
					order.setCreater(ord.getCreater());
					order.setConfirmer(ord.getConfirmer());
					//新建订单的价格为他包含的orderitem 的价格
					order.setTotalDealPrice(orderItem.getDealPrice());
					order.setOutTradeNumber(ord.getOutTradeNumber());
					order.setOutTradeStatus(ord.getOutTradeStatus());
					order.setComment(ord.getComment());
					orderRepository.create(order);//创建一个新的order
					
					order = orderRepository.findBySerialNumber(order.getSerialNumber());
					order.setCreateDateTime(ord.getCreateDateTime());
					order.setStatus(ord.getStatus());
					
					orderRepository.updateOrderItemByItemIdAndOrderId(orderItem.getId(), order.getId());
				}
			}
		}
	}

}
