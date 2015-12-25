package com.vipkid.controller.parent.base;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

public class BaseLayoutWebController extends BaseWebController {
	public static final String PATH = "/common/baseLayout";
	
	@Override
	protected void registerViewController( ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(BaseLayoutWebController.PATH).setViewName(BaseLayoutWebController.PATH);
	}
}
