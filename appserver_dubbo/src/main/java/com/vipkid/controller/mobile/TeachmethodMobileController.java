package com.vipkid.controller.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
@Controller
public class TeachmethodMobileController extends BaseWebController {
	public static final String PATH = "/mobile/teachmethod";

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(TeachmethodMobileController.PATH).setViewName(TeachmethodMobileController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/mobile/teachmethod", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		return TeachmethodMobileController.PATH;
	}
}
