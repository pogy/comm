package com.vipkid.controller.mobile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class ForgetPassword1MobileController extends BaseWebController {
	public static final String PATH = "/mobile/forgetpassword1";
	
	@Resource
	private ParentRepository parentRepository;

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ForgetPassword1MobileController.PATH).setViewName(ForgetPassword1MobileController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = ForgetPassword1MobileController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		return ForgetPassword1MobileController.PATH;
	}
	
	@RequestMapping(value=ForgetPassword1MobileController.PATH + "/next", method=RequestMethod.POST)
	public String next(@RequestParam("username") String mobile, RedirectAttributes redirectAttributes) {
		if (mobile == null) {
			redirectAttributes.addFlashAttribute("error", "username not find");
			return Page.redirectTo(ForgetPassword1MobileController.PATH);
		}
		
		Parent parent = parentRepository.findByMobile(mobile);
		if (parent != null) {
			redirectAttributes.addFlashAttribute("username", mobile);
			return Page.redirectTo(ForgetPassword2MobileController.PATH);
		} else {
			redirectAttributes.addFlashAttribute("error", "username not find");
			return Page.redirectTo(ForgetPassword1MobileController.PATH);
		}
	}
}
