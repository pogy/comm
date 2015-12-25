package com.vipkid.service.pay.wechat;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.ext.wechatpay.CreateUnifiedOrderRequest.TradeType;
import com.vipkid.ext.wechatpay.CreateUnifiedOrderResponse;
import com.vipkid.ext.wechatpay.UnifiedOrderNotifyRequest;
import com.vipkid.ext.wechatpay.UnifiedOrderNotifyResponse;
import com.vipkid.ext.wechatpay.WechatPayAPI;
import com.vipkid.ext.wechatpay.util.WeChatPaySignUtil;
import com.vipkid.ext.wechatpay.util.WechatPayXMLUtil;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Parent;
import com.vipkid.repository.ParentRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.service.OrderService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.util.Configurations;

@Service
public class WeChatPayService {
	
	private static final String PAYS_UCCESS = "get_brand_wcpay_request:ok";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAIL = "FAIL";
	
	private Logger logger = LoggerFactory.getLogger(WeChatPayService.class.getSimpleName());
	@Resource
	private ParentRepository parentRepository;
    @Resource
    private OrderService orderService;
	

	public  Map<String,String> buildRequest( Long orderId,Long parentId, String customerIp) {
		logger.info("Wechat request params : orderId = {},parentId = {}",orderId,parentId);
		Map<String,String> paramMap = new HashMap<String, String>();
		String wechatOpenId;
		Order order = orderService.find(orderId);
		if (order == null) {
			logger.error("order not exists,oderId = {}",orderId);
			throw new BadRequestServiceException(" order not exists");
		} else if (order.getStatus() != Order.Status.TO_PAY) {
			logger.error("order status error,orderId = {},status = {} " ,orderId,order.getStatus());
			throw new BadRequestServiceException("order status error,orderId = "+ orderId +",status = " + order.getStatus());
		} else if (order.getTotalDealPrice() <= 0) {
			logger.error("order TotalDealPrice must > 0,orderId = {} " ,orderId);
			throw new BadRequestServiceException("order status error,orderId = "+ orderId);
		}
		
		Parent parent = parentRepository.find(parentId);
		if (parent == null) {
			logger.error("parent not exists,parentId =  {}",parentId);
			throw new BadRequestServiceException(" parent not exists");
		}
		
		if (StringUtils.isBlank(parent.getWechatOpenId())) {
			logger.error("openId not exists,parentId = {},openId = {}",parentId,parent.getWechatOpenId());
			throw new BadRequestServiceException("openId not exists");
		} else {
			wechatOpenId = parent.getWechatOpenId();
		}
		
		logger.info("build wechat pay request: orderID = {}, customerIP = {}, openId = {}",orderId, customerIp,wechatOpenId);

		CreateUnifiedOrderResponse unifiedOrderResponse = WechatPayAPI.createUnifiedOrder( order, customerIp, TradeType.JSAPI, wechatOpenId);
		if (unifiedOrderResponse.isSuccess()) {
				try {
					String prepayId = unifiedOrderResponse.getPrepayId();
					paramMap.put("appId", Configurations.WechatPay.APP_ID);
					paramMap.put("timeStamp", String.valueOf(new Date().getTime()));
					paramMap.put("nonceStr", PasswordGenerator.generate(10));
					paramMap.put("package", "prepay_id=" + prepayId);
					paramMap.put("signType", "MD5");
					
					String paySign =  WeChatPaySignUtil.sign(paramMap, Configurations.WechatPay.PAY_SIGN_KEY, Configurations.WechatPay.INPUT_CHARSET,false);
					paramMap.put("paySign", paySign);
					
				} catch (UnsupportedEncodingException e) {
					logger.error("failed to createUnifiedOrder, erorMessage = {} ",unifiedOrderResponse.getErrorMessage());
					throw new BadRequestServiceException(" build request failed");
				}
		} else {
			logger.error("failed to createUnifiedOrder, errorMessage = {} ",unifiedOrderResponse.getErrorMessage());
			throw new BadRequestServiceException(" build request failed");
		}
		
		logger.info("buildRequest param : " + paramMap);
		return paramMap;
	}
	

	public Response doJSPayNotify(Long id,  String err_msg) {
		logger.info("handle wechatpay notify:orderid = {}, err_msg = {}",id,err_msg);
		Order order = orderService.find(id);
		if (order == null) {
			logger.error("order not exists,oderId " + id);
			throw new BadRequestServiceException(" order not exists");
		} else {
			if (PAYS_UCCESS.equals(err_msg)) {
                if (Order.Status.PAY_CONFIRMED != order.getStatus()) {
                    order.setStatus(Order.Status.PAID);
                    orderService.update(order);
                }
			}
		}
		return new Response(HttpStatus.OK.value(),"success");
	}
	
	 public String doWCPayCallBack(String requestXML) throws Exception {
    	String status = FAIL;
    	String errorMsg;
		logger.info(" WeChatPayNotifyServlet requestXML : {} ",requestXML);
		UnifiedOrderNotifyRequest notifyRequest = WechatPayAPI.verifyUnifiedOrderNotifyRequest(requestXML);
		errorMsg = notifyRequest.getErrorMessage();
		if (notifyRequest.isSuccess()) {
			Order order = orderService.find(Long.valueOf(notifyRequest.getOutTradeNo()));
			if (order != null) {
				if (Order.Status.PAY_CONFIRMED == order.getStatus() ) {
					status = SUCCESS;
				} else {
					updateOrderStatus(notifyRequest,order);
					status = SUCCESS;
				}
			} else {
				status = FAIL;
				errorMsg = "wrong outTradeNo";
			}
		}
		UnifiedOrderNotifyResponse notifyResponse= new UnifiedOrderNotifyResponse(status,errorMsg);
		return WechatPayXMLUtil.marshal(notifyResponse, UnifiedOrderNotifyResponse.class);
	}
	
	private void updateOrderStatus(UnifiedOrderNotifyRequest notifyRequest, Order order) {
		order.setOutTradeNumber(notifyRequest.getTransactionId());
		order.setOutTradeStatus(notifyRequest.getResultCode());
		order.setPayBy(PayBy.WECHATPAY);
		if (SUCCESS.equals(notifyRequest.getResultCode())) {
			order.setOnlinePayFailed(false);
			order.setPaidDateTime(new Date());
			orderService.doConfirm(order);
		} else {
			order.setOnlinePayFailed(true);
			orderService.update(order);
			logger.info("WeChatPayNotifyServlet, pay failed , order.serialNumber= {},error_message = {}",notifyRequest.getOutTradeNo(), notifyRequest.getErrorCode(),notifyRequest.getErrorCodeDescribe());
		}
	}
	
}
