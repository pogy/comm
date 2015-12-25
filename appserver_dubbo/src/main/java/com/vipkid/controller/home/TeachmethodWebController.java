package com.vipkid.controller.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.UserCacheUtil;

@Controller
public class TeachmethodWebController extends BaseWebController {
	public static final String PATH = "/home/teachmethod";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(TeachmethodWebController.PATH).setViewName(TeachmethodWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/teachmethod", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		model.addAttribute("name", "教学方法");
		model.addAttribute("path", TeachmethodWebController.PATH);
		if(UserCacheUtil.hasLogin(request) == true){
			return "redirect:/parent/home";
		}

		initHome(model, request);
		return TeachmethodWebController.PATH;
	}
}
