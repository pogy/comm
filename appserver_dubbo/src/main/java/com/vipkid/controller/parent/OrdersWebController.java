package com.vipkid.controller.parent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.alibaba.fastjson.JSONObject;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.OrderVO;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Order;
import com.vipkid.model.OrderItem;
import com.vipkid.service.OrderService;
import com.vipkid.service.StudentService;
@Controller
public class OrdersWebController extends BaseWebController {
	public static final String PATH = "/parent/orders";
	
    private Logger logger = LoggerFactory.getLogger(OrdersWebController.class.getSimpleName());
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(OrdersWebController.PATH).setViewName(OrdersWebController.PATH);
	}
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private StudentService studentService;
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = OrdersWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request,Integer rowNum,Integer currNum){
		if(rowNum==null){
			rowNum =20;
		}
		if(currNum==null){
			currNum =1;
		}
		model.addAttribute("name", "订单中心");
		model.addAttribute("path", OrdersWebController.PATH);
		
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			return "redirect:/login";
		}
		initCommon(model, request);
		
		List<Order> oldOrders = orderService.listOrderByFamilyId(student.getFamilyId(), rowNum, currNum);
		long totalRecords = orderService.countOrderByFamilyId(student.getFamilyId());
		List<OrderVO> newOrders = transferOrder(oldOrders);
		model.addAttribute("orders", newOrders);
		model.addAttribute("totalRecords",totalRecords);
		model.addAttribute("currNum",currNum);
		return OrdersWebController.PATH;
	}
	
	private List<OrderVO> transferOrder(List<Order> list){
		List<OrderVO> result = new ArrayList<OrderVO>();
		if(list != null && list.size() > 0){
			for (Order order : list){
				float totalPrice = 0;
				float totalDealPrice = 0;
				for(OrderItem oi : order.getOrderItems()){
					totalPrice += oi.getPrice();
					totalDealPrice += oi.getDealPrice();
				}
				OrderVO vo = new OrderVO(order.getId(), order.getSerialNumber(), order.getOrderItems(), totalPrice, totalDealPrice, order.getStudent(), order.getStatus(), order.getCreateDateTime());
				result.add(vo);
			}
		}
		return result;
	}
	
	@RequestMapping(value = "/transferConfirm", method = RequestMethod.POST)
	@ResponseBody
	public Order transferConfirm(@RequestParam(value="orderId", required=true) long orderId){
		Order order = orderService.find(orderId);
		order = orderService.transferConfirm(order);
		
		Order o = new Order();
		o.setId(order.getId());
		o.setStatus(order.getStatus());
		
		return o;
	}
	
	@RequestMapping(value = "/pingConfirm", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject pingConfirm(HttpServletRequest request, String serialNumber,
			String outTradeNumber, String outTradeStatus, String payBy) {
		JSONObject json = new JSONObject();
		logger.info("pingConfirm 订单号 = {} start",serialNumber);
		try {
			Order order = orderService.findBySerialNumber(serialNumber);
	        if (null != order) {
	    		order.setOutTradeNumber(outTradeNumber);
	    		order.setOutTradeStatus(outTradeStatus);
	    		order.setPayBy(Order.PayBy.valueOf(payBy));
	    		order.setOnlinePayFailed(false);
	            //在learningProgress里面增加lefthour
	            orderService.doConfirm(order);
	            logger.info("order status change success ！订单号 = {}",serialNumber);
	            json.put("result", "OK");
	        } else {
	            logger.error("未找到支付订单，更新订单失败，订单号={}",serialNumber);
	            json.put("result", "ERROR");
	            json.put("msg", "未找到支付订单，更新订单失败!");
	        }
		} catch(Exception e) {
			logger.error("更新订单失败，订单号={} error={} Trace={}",serialNumber, e.getMessage(), e);
            json.put("result", "ERROR");
            json.put("msg", e.getMessage());
            json.put("msgTrace", e);
			e.printStackTrace();
		}
        return json;
	}

	@RequestMapping(value = "/ordercancel", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject ordercancel(HttpServletRequest request, long id) {
		logger.info("cancel order: id = {} start", id);
		JSONObject json = new JSONObject();
		try {
			Order order = new Order();
			order.setId(id);
			order.setCanceledDateTime(new Date());
			order.setStatus(Order.Status.CANCELED);
			orderService.cancel(order);
			json.put("result", "OK");
			logger.info("cancel order: id = {} ok", id);
		} catch(Exception e) {
			logger.error("取消订单失败，订单id={}",id);
            json.put("result", "ERROR");
            json.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
