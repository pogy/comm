package com.vipkid.controller.parent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.model.Parent;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.ParentService;
import com.vipkid.util.CookieUtils;


@Controller
public class ModifyPasswordWebController extends BaseWebController {
	public static final String PATH = "/parent/modifypassword";
	
	@Autowired
	private ParentAuthService authService;
	
	@Autowired
	private ParentService parentService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ModifyPasswordWebController.PATH).setViewName(ModifyPasswordWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/parent/modifyPassword", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		initCommon(model,request);
		return ModifyPasswordWebController.PATH;
	}
	
	@RequestMapping(value = "/parent/modifyPassword", method = RequestMethod.POST)
	public @ResponseBody Boolean modifyPassword(@RequestParam("oldPassword") String OldPassword, @RequestParam("password") String password, HttpServletRequest request){
		String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
		Long parentId = Long.valueOf(parentIdStr);
		Parent parent = parentService.find(parentId);
		
		if(parent != null && parent.getPassword().equals(PasswordEncryptor.encrypt(OldPassword))){
			parent.setPassword(password);
			if(parentService.changePassword(parent) != null){
				return true;
			}
		}
		return false;
	}
	
	
	@RequestMapping(value = "/verifyOriginalPasswordByMobile", method = RequestMethod.GET)
	public @ResponseBody Boolean getPasswordByMobile(@RequestParam("password") String password,HttpServletRequest request, HttpServletResponse response){
		String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
		Long parentId = Long.valueOf(parentIdStr);
		Parent parent = parentService.find(parentId);
		
		if(parent != null){
			return parent.getPassword().equals(PasswordEncryptor.encrypt(password));
		}else{
			return false;
		}
	}
}



