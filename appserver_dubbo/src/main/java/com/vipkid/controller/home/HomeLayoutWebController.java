package com.vipkid.controller.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;

@Controller
public class HomeLayoutWebController extends BaseWebController {
	public static final String PATH = "/home/layout";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(HomeLayoutWebController.PATH).setViewName(HomeLayoutWebController.PATH);
	}

	/**
	 * 首页初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/", HomeLayoutWebController.PATH}, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		model.addAttribute("path", HomeLayoutWebController.PATH);
		initHome(model, request);
		return Page.forwardTo("/home");
	}
}
