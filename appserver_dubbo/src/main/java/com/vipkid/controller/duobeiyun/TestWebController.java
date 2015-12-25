package com.vipkid.controller.duobeiyun;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
@Controller
public class TestWebController extends BaseWebController {
	public static final String PATH = "/duobei/db";
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(TestWebController.PATH).setViewName(TestWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = TestWebController.PATH, method = RequestMethod.GET)
	public String init(Model model){
		return TestWebController.PATH;
	}
}
