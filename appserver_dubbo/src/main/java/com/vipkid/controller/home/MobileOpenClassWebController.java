package com.vipkid.controller.home;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.service.OpenClassService;
import com.vipkid.service.pojo.OpenClassDescView;

@Controller
public class MobileOpenClassWebController extends BaseWebController{
	
	public static final String MOBILE_PATH = "/mobile/mobileOpenClass";
	
	@Resource
	private OpenClassService openClassService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		// TODO Auto-generated method stub
	}
	
	@RequestMapping(value = "public/mobile/openclass", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request, Integer ageRange){
		ageRange = ageRange==null?-1:ageRange;
		List<OpenClassDescView> list = openClassService.listOpenClassForMobile(ageRange);
		model.addAttribute("openClassList", list);
		model.addAttribute("ageRange", ageRange);
		return MobileOpenClassWebController.MOBILE_PATH;
	}

}
