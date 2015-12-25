package com.vipkid.service.pay.alipay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import com.vipkid.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.alipay.AlipayAPI;
import com.vipkid.ext.alipay.RequestParams;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;
import com.vipkid.util.Configurations;

@Service
public class AliPayService {

	private Logger logger = LoggerFactory.getLogger(AliPayService.class.getSimpleName());
	
	@Resource
	private OrderService orderService;

	public RequestParams buildRequest(String return_url, String seller_email, String out_trade_no, 
			String subject, String total_fee, String anti_phishing_key, String exter_invoke_ip, 
			String body, String show_url, String notify_url, String defaultbank) {
		if(subject != null)
			subject = subject.replaceAll("\\s*", "");  
		RequestParams requestPara = new RequestParams();
		requestPara.setAnti_phishing_key(anti_phishing_key);
		requestPara.setBody(body);
		requestPara.setExter_invoke_ip(exter_invoke_ip);
		requestPara.setPayment_type("1");
		requestPara.setReturn_url(return_url);
		requestPara.setSeller_email(seller_email);
		requestPara.setShow_url(show_url);
		requestPara.setSubject(subject);
		requestPara.setTotal_fee(total_fee);
		requestPara.setOut_trade_no(out_trade_no);
		requestPara.setNotify_url(notify_url);
		if(defaultbank != null){
			requestPara.setPaymethod(Configurations.ALIPAY.PAYMETHOD);
			requestPara.setDefaultbank(defaultbank);
			
			requestPara.setPartner(Configurations.ALIPAY.partner);
			requestPara.setSign(Configurations.ALIPAY.key);
		}
		
		return AlipayAPI.buildRequestParas(requestPara);
	}
	
	public Response verifyResponse(String notify_time, 
			String notify_type, 
			String notify_id, 
			String sign_type, 
			String sign, 
			String out_trade_no, 
			String subject, 
			String payment_type, 
			String trade_no, 
			String trade_status, 
			String gmt_create, 
			String gmt_payment, 
			String gmt_close, 
			String refund_status, 
			String gmt_refund, 
			String seller_email, 
			String buyer_email, 
			String seller_id, 
			String buyer_id, 
			String total_fee, 
			String body, 
			String out_channel_inst, 
			String exterface, 
			String is_success) {
		logger.debug("verifyResponse with param: notify_time = {}, notify_type = {}, notify_id = {}, sign_type = {}, sign = {}, out_trade_no = {}, subject = {}, " + "payment_type = {}, trade_no ={}, trade_status ={}, gmt_create ={}, gmt_payment={}, gmt_close={}, refund_status = {} gmt_refund={}" + "seller_email= {}, buyer_email = {}, seller_id ={}, buyer_id = {}, exterface = {}, is_success = {}", notify_time, notify_type, notify_id, sign_type, sign, out_trade_no, subject, payment_type, trade_no, trade_status, gmt_create, gmt_payment, gmt_close, refund_status, gmt_refund, seller_email, buyer_email, seller_id, buyer_id, exterface, is_success);
		Map<String, String> params = new HashMap<String, String>();
		if (notify_time != null)
			params.put("notify_time", notify_time);
		if (notify_type != null)
			params.put("notify_type", notify_type);
		if (notify_id != null)
			params.put("notify_id", notify_id);
		if (sign_type != null)
			params.put("sign_type", sign_type);
		if (sign != null)
			params.put("sign", sign);
		if (out_trade_no != null)
			params.put("out_trade_no", out_trade_no);
		if (subject != null)
			params.put("subject", subject);
		if (payment_type != null)
			params.put("payment_type", payment_type);
		if (trade_no != null)
			params.put("trade_no", trade_no);
		if (trade_status != null)
			params.put("trade_status", trade_status);
		if (gmt_create != null)
			params.put("gmt_create", gmt_create);
		if (gmt_payment != null)
			params.put("gmt_payment", gmt_payment);
		if (gmt_close != null)
			params.put("gmt_close", gmt_close);
		if (refund_status != null)
			params.put("refund_status", refund_status);
		if (gmt_refund != null)
			params.put("gmt_refund", gmt_refund);
		if (seller_email != null)
			params.put("seller_email", seller_email);
		if (buyer_email != null)
			params.put("buyer_email", buyer_email);
		if (seller_id != null)
			params.put("seller_id", seller_id);
		if (buyer_id != null)
			params.put("buyer_id", buyer_id);
		if (total_fee != null)
			params.put("total_fee", total_fee);
		if (body != null)
			params.put("body", body);
		if (out_channel_inst != null)
			params.put("out_channel_inst", out_channel_inst);
		if (exterface != null)
			params.put("exterface", exterface);
		if (is_success != null)
			params.put("is_success", is_success);

		if (params.get("is_success").equals("T")){
			Order order = orderService.find(Integer.valueOf(out_trade_no));
		    order.setOutTradeNumber(trade_no);
			order.setOutTradeStatus(trade_status);
			order.setPayBy(PayBy.ALIPAY);
			order.setPaidDateTime(new Date());
			order.setStatus(Status.PAY_CONFIRMED);
		    order.setOnlinePayFailed(false);
		    orderService.update(order);
		    logger.error("Pay with Alipay successfully! out_trade_no = " + out_trade_no + " trade_no = " + trade_no + " trade_status = " + trade_status.toString());
			return Response.ok("success").build();
		} else{
			logger.error("Alipay verification failed!");
			return Response.ok("fail").build();
		}	
	}

}
