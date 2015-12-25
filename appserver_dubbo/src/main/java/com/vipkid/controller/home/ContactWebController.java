package com.vipkid.controller.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;

@Controller
public class ContactWebController extends BaseWebController {
	public static final String PATH = "/home/contact";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ContactWebController.PATH).setViewName(ContactWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){		
		return ContactWebController.PATH;
	}
}
