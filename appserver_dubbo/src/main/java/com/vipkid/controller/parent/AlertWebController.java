package com.vipkid.controller.parent;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;

@Controller
public class AlertWebController extends BaseWebController {
	
	
	public static final String PATH = "/home/alert";
	
	@Override
	protected void registerViewController( ViewControllerRegistry viewControllerRegistry) {
	
	}

	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/alert", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request,String msg){
		msg = (String) request.getAttribute("msg");
		model.addAttribute("msg", msg);
		return AlertWebController.PATH;
	}

}
