/*
package com.vipkid.ext.alipay;

import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.service.OrderService;
import com.vipkid.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Servlet implementation class AlipayNotifyServlet
 *//*

@WebServlet(value = "/AlipayNotifyServlet")
public class AlipayNotifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AlipayNotifyServlet.class.getCanonicalName());	

    */
/**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     *//*

    public AlipayNotifyServlet() {
        super();
    }

    
	*/
/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	 *//*

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("------------------------------>>>>>>>>>>>>>>>>>>>>> do alipay notify servlet");
		logger.info("getContextPath ==== " + request.getContextPath());
		Map<String,String> params = doHandleParameters(request);
		if(changeOrderStatus(params,request)){
			logger.info("order status change success ！");
			response.getWriter().println("success");
		}else{
			logger.info("order status change fail ！");
			response.getWriter().println("fail");
		}
	}

	*/
/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	 *//*

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("do alipay notify servlet");
		Map<String,String> params = doHandleParameters(request);
		if(changeOrderStatus(params,request)){
			logger.info("order status change success ！");
			response.getWriter().println("success");
		}else{
			logger.info("order status change fail ！");
			response.getWriter().println("fail");
		}
	}
	
	//改变订单状态
	private boolean changeOrderStatus(Map<String, String> params,HttpServletRequest request){
		logger.debug(" begain change order status ！");

		if(params.get("is_success").equals("T")){
            OrderService orderService = SpringUtil.getSpringBean(request,"orderService",OrderService.class);
			Order order = orderService.find(Integer.valueOf(params.get("out_trade_no")));
		    order.setOutTradeNumber(params.get("trade_no"));
			order.setOutTradeStatus(params.get("trade_status"));
			order.setPayBy(PayBy.ALIPAY);
			order.setPaidDateTime(new Date());
//			order.setStatus(Status.PAID);
		    order.setOnlinePayFailed(false);
//		    Order updateOrder = orderService.update(order);
		    
		    //在learningProgress里面增加lefthour
		    Order confirmOrder = orderService.updateConfirm(order);
		    logger.info("order status change success ！");
		    if(confirmOrder != null){
		    	return true;
		    }
		}
		return false;
	}
	
	private Map<String,String> doHandleParameters(HttpServletRequest request) throws UnsupportedEncodingException{
		Map<String,String> params = new HashMap<String,String>();
		//商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
		//支付结果
		String is_success = null;
		if(request.getParameter("is_success") != null){
			is_success = new  String(request.getParameter("is_success").getBytes("ISO-8859-1"),"UTF-8");
		}else{
			if("TRADE_SUCCESS".equals(trade_status)){
				is_success = "T";
			}else{
				is_success = "F";
			}
		}
		
		params.put("out_trade_no", out_trade_no);
		params.put("trade_no", trade_no);
		params.put("trade_status", trade_status);
		params.put("is_success", is_success);
		
		logger.info("Pay with Alipay Status， out_trade_no = " + out_trade_no + " trade_no = " + trade_no + " trade_status = " + trade_status.toString());
		
		return params;
	}
}
*/
