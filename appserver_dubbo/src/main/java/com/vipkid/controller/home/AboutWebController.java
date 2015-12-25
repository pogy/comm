package com.vipkid.controller.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;

@Controller
public class AboutWebController extends BaseWebController {
	public static final String PATH = "/home/about";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(AboutWebController.PATH).setViewName(AboutWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){		
		return AboutWebController.PATH;
	}
}
