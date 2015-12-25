package com.vipkid.service;

import com.vipkid.ext.wechatpay.CreateUnifiedOrderRequest.TradeType;
import com.vipkid.ext.wechatpay.CreateUnifiedOrderResponse;
import com.vipkid.ext.wechatpay.WechatPayAPI;
import com.vipkid.model.Order;

public class WechatPayAPITester {

	public static void main(String[] args) {
		Order order = new Order();
		order.setSerialNumber("123456");
		order.setTotalDealPrice(15);
		CreateUnifiedOrderResponse createUnifiedOrderResponse = WechatPayAPI.createUnifiedOrder(order, "127.0.0.1", TradeType.JSAPI, "omEhctxOqWfs0yKhieL82dasrSkA");
		if(createUnifiedOrderResponse != null) {
			System.out.println(createUnifiedOrderResponse.isSuccess());
		}
	}
}
