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
public class DiaryWebController extends BaseWebController {
	public static final String PATH = "/home/diary";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(DiaryWebController.PATH).setViewName(DiaryWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/diary", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		model.addAttribute("name", "课堂日志");
		model.addAttribute("path", DiaryWebController.PATH);
//		if(UserCacheUtil.hasLogin(request) == true){
//			return "redirect:/parent/home";
//		}

		initHome(model, request);
		return DiaryWebController.PATH;
	}
}
