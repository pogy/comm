package com.vipkid.controller.parent;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Order;
import com.vipkid.model.Order.Status;
import com.vipkid.service.OrderService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pay.alipay.AliPayService;
import com.vipkid.util.Configurations;
@Controller
public class PaymentWebController extends BaseWebController {
	public static final String PATH = "/parent/payment";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(PaymentWebController.PATH).setViewName(PaymentWebController.PATH);
	}
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private AliPayService aliPayService;
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = PaymentWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request, long orderId){
		model.addAttribute("path", PaymentWebController.PATH);
		
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
		
		Order order = orderService.find(orderId);
//		OrderVO vo = new OrderVO(order.getId(),order.getSerialNumber(),order.getOrderItems(),0,order.getTotalDealPrice(),order.getStudent(),order.getStatus(),order.getCreateDateTime());
		model.addAttribute("order", order);
		model.addAttribute("returnUrl", Configurations.ALIPAY.RETURN_URL);
		model.addAttribute("notifyUrl", Configurations.ALIPAY.NOTIFY_URL);
		model.addAttribute("seller_email", Configurations.ALIPAY.SELLER_EMAIL);
		return PaymentWebController.PATH;
	}
	
	@RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
	public @ResponseBody Order cancelOrder(@RequestParam(value="orderId", required=true) long orderId){
		Order order = orderService.find(orderId);
		order.setCanceledDateTime(new Date());
		order.setStatus(Status.CANCELED);
		order = orderService.cancel(order);
		return order;
	}
}
