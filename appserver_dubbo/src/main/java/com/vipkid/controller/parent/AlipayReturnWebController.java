package com.vipkid.controller.parent;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.UserCacheUtil;
@Controller
public class AlipayReturnWebController extends BaseWebController {
	public static final String PATH = "/parent/return_url";
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(AlipayReturnWebController.PATH).setViewName(AlipayReturnWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/pay/return_url", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		String pid = String.valueOf(UserCacheUtil.getValueFromCookie(request, "parentId"));
		model.addAttribute("parentName", UserCacheUtil.getParentName(pid));
		
		initCommon(model, request);
		
		return AlipayReturnWebController.PATH;
	}
}
