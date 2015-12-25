package com.vipkid.controller.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;

@Controller
public class HelpWebController extends BaseWebController {
	public static final String PATH = "/home/help";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(HelpWebController.PATH).setViewName(HelpWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		model.addAttribute("name", "帮助中心");
		model.addAttribute("path", HelpWebController.PATH);
//		if(UserCacheUtil.hasLogin(request) == true){
//			return "redirect:/parent/home";
//		}

		initHome(model, request);
		return HelpWebController.PATH;
	}
}
