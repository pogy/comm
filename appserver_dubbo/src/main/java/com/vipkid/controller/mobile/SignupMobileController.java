package com.vipkid.controller.mobile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.model.Parent;
import com.vipkid.repository.ParentRepository;
@Controller
public class SignupMobileController extends BaseWebController {
	public static final String PATH = "/mobile/signup";
	
	@Resource
	private ParentRepository parentRepository;

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(SignupMobileController.PATH).setViewName(SignupMobileController.PATH);
	}

	@RequestMapping(value="/mobile/signup", method=RequestMethod.GET)
	public String init(HttpServletResponse response) {
		return SignupMobileController.PATH;
	}
	
	@RequestMapping(value="/mobile/signup/username", method=RequestMethod.POST)
	public String login(@RequestParam(value="mobile", required=true) String mobile, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Parent parent = parentRepository.findByMobile(mobile);
		
		if (parent == null) {
			redirectAttributes.addFlashAttribute("username", mobile);
			return Page.redirectTo(SetPasswordMobileController.PATH);
		} else {
			redirectAttributes.addFlashAttribute("mobile_used_error", "这个手机号已经被注册过啦");
			redirectAttributes.addFlashAttribute("used_mobile", mobile);
			return Page.redirectTo(SignupMobileController.PATH);
		}	
	}
}
