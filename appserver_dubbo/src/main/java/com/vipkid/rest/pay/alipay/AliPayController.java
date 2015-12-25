package com.vipkid.rest.pay.alipay;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.ext.alipay.RequestParams;
import com.vipkid.service.pay.alipay.AliPayService;
import com.vipkid.service.OrderService;

@RestController
@RequestMapping(value="/api/service/public/alipay")
public class AliPayController {

	private Logger logger = LoggerFactory.getLogger(AliPayController.class.getSimpleName());
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private AliPayService aliPayService;

	@RequestMapping(value="/buildRequest", method=RequestMethod.GET)
	public RequestParams buildRequest(@RequestParam(value = "return_url") String return_url,
			@RequestParam(value = "seller_email", required = false) String seller_email, 
			@RequestParam(value = "out_trade_no", required = false) String out_trade_no, 
			@RequestParam(value = "subject", required = false) String subject, 
			@RequestParam(value = "total_fee") String total_fee,
			@RequestParam(value = "anti_phishing_key", required = false) String anti_phishing_key, 
			@RequestParam(value = "exter_invoke_ip",required = false) String exter_invoke_ip,
			@RequestParam(value = "body", required = false) String body, 
			@RequestParam(value = "show_url", required = false) String show_url,
			@RequestParam(value = "notify_url") String notify_url,
			@RequestParam(value = "defaultbank", required = false) String defaultbank) {
		logger.info("buildRequest params,return_url = {},seller_email={},out_trade_no={},subject={},total_fee={},exter_invoke_ip={},body={},show_url={},notify_url={}",
                return_url,
                seller_email,
                out_trade_no,
                subject,
                total_fee,
                exter_invoke_ip,
                body,
                show_url,
                notify_url,
                defaultbank);
		
		return aliPayService.buildRequest(return_url, seller_email, out_trade_no, subject, total_fee, anti_phishing_key, exter_invoke_ip, body, show_url, notify_url, defaultbank);
	}

	@RequestMapping(value="/verifyResponse", method=RequestMethod.GET)
	public Response verifyResponse(@RequestParam(value = "notify_time", required = false) String notify_time, 
			@RequestParam(value = "notify_type", required = false) String notify_type, 
			@RequestParam(value = "notify_id") String notify_id, 
			@RequestParam(value = "sign_type", required = false) String sign_type, 
			@RequestParam(value = "sign", required = false) String sign, 
			@RequestParam(value = "out_trade_no") String out_trade_no, 
			@RequestParam(value = "subject") String subject, 
			@RequestParam(value = "payment_type", required = false) String payment_type, 
			@RequestParam(value = "trade_no", required = false) String trade_no, 
			@RequestParam(value = "trade_status", required = false) String trade_status, 
			@RequestParam(value = "gmt_create", required = false) String gmt_create, 
			@RequestParam(value = "gmt_payment", required = false) String gmt_payment, 
			@RequestParam(value = "gmt_close", required = false) String gmt_close, 
			@RequestParam(value = "refund_status", required = false) String refund_status, 
			@RequestParam(value = "gmt_refund", required = false) String gmt_refund, 
			@RequestParam(value = "seller_email", required = false) String seller_email, 
			@RequestParam(value = "buyer_email", required = false) String buyer_email, 
			@RequestParam(value = "seller_id", required = false) String seller_id, 
			@RequestParam(value = "buyer_id", required = false) String buyer_id, 
			@RequestParam(value = "total_fee", required = false) String total_fee, 
			@RequestParam(value = "body", required = false) String body, 
			@RequestParam(value = "out_channel_inst", required = false) String out_channel_inst, 
			@RequestParam(value = "exterface", required = false) String exterface, 
			@RequestParam(value = "is_success", required = false) String is_success) {
		logger.info("verifyResponse with param: notify_time = {}, notify_type = {}, notify_id = {}, sign_type = {}, sign = {}, out_trade_no = {}, subject = {}, " + "payment_type = {}, trade_no ={}, trade_status ={}, gmt_create ={}, gmt_payment={}, gmt_close={}, refund_status = {} gmt_refund={}" + "seller_email= {}, buyer_email = {}, seller_id ={}, buyer_id = {}, exterface = {}, is_success = {}", notify_time, notify_type, notify_id, sign_type, sign, out_trade_no, subject, payment_type, trade_no, trade_status, gmt_create, gmt_payment, gmt_close, refund_status, gmt_refund, seller_email, buyer_email, seller_id, buyer_id, exterface, is_success);
		
		return aliPayService.verifyResponse(notify_time, notify_type, notify_id, sign_type, sign, out_trade_no, subject, payment_type, trade_no, trade_status, gmt_create, gmt_payment, gmt_close, refund_status, gmt_refund, seller_email, buyer_email, seller_id, buyer_id, total_fee, body, out_channel_inst, exterface, is_success);
	}

}
