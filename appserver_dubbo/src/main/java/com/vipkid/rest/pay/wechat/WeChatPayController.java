package com.vipkid.rest.pay.wechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.pay.wechat.WeChatPayService;

@RestController
@RequestMapping("/api/service")
public class WeChatPayController {
	
	private Logger logger = LoggerFactory.getLogger(WeChatPayController.class.getSimpleName());
	private static final String FAIL = "FAIL";
	
	@Resource
	private WeChatPayService weChatPayService;
	
	@RequestMapping(value = "/private/wechatpay/buildRequest", method = RequestMethod.GET)
	public  Map<String,String> buildRequest(@RequestParam(value = "orderId") Long orderId,@RequestParam(value = "parentId") Long parentId,HttpServletRequest httpRequest) {
		logger.info("request params : orderId = {},parentId = {}",orderId, parentId);
		String customerIp = this.getRealIp(httpRequest);
		return weChatPayService.buildRequest(orderId, parentId, customerIp);
	}
	
	@RequestMapping(value = "/private/wechatpay/jsPayNotify", method = RequestMethod.POST)
	public Response doJSPayNotify(@RequestParam(value = "id") Long id, @RequestParam(value = "err_msg") String err_msg) {
		logger.info("handle wechat pay notify : orderID = {}, err_msg = {}",id,err_msg);
		return weChatPayService.doJSPayNotify(id, err_msg);
	}
	
	@RequestMapping(value = "/public/wechatpay/callBack", method = RequestMethod.POST)
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	logger.info(" ----------------WeChatPay CallBack begin ----------------");
    	
    	String responseXML;
		try {
			String requestXML = this.getCallBackRequestBody(request);
			 responseXML = weChatPayService.doWCPayCallBack(requestXML);
		} catch (Exception e) {
			responseXML = "<xml><return_code><![CDATA[" + FAIL + "]]></return_code><return_msg></return_msg></xml>";
		}
		
		logger.info("WeChatPayNotifyServlet responseXML : {} ",responseXML);
        response.setContentType("text/xml;charset=utf-8");
    	response.getWriter().write(responseXML);
    	response.flushBuffer();
    	logger.info(" ----------------Success,WeChatPay CallBack end ----------------");
    	
    }
	
	
	private String getCallBackRequestBody(HttpServletRequest request) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = request.getReader();
		String line;
		while((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
		
	}
	
	private String getRealIp(HttpServletRequest request) {
		String address = request.getHeader("X-Forwarded-For");  
		if (address != null && address.length() > 0  
		        && !"unknown".equalsIgnoreCase(address)) {
			String[] addressList = address.split(",");
		    return addressList[0];
		    
		}  
		address = request.getHeader("X-Real-IP");  
		if (address != null && address.length() > 0  
		        && !"unknown".equalsIgnoreCase(address)) {  
		    return address;  
		}    
		return request.getRemoteAddr();  
	}
	
}
