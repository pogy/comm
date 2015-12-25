package com.vipkid.controller.mobile;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.repository.StudentRepository;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Credential;
import com.vipkid.util.CookieUtils;
@Controller
public class LoginMobileController extends BaseWebController {
	public static final String PATH = "/mobile/login";
	
	private Logger logger = LoggerFactory.getLogger(LoginMobileController.class.getSimpleName());
	
	public static final String DOMAIN = "http://parent.vipkid.com.cn";  
	
	@Resource
	public ParentAuthService parentAuthService;
	
	@Resource
	public StudentRepository studentRepository;

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(LoginMobileController.PATH).setViewName(LoginMobileController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/mobile/login", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request){
		return LoginMobileController.PATH;
	}
	
	@RequestMapping(value="/mobile/login", method=RequestMethod.POST)
	public String login(@RequestParam(value="mobile", required=true) String mobile, 
						@RequestParam(value="password", required=true) String password,
						HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		Credential credential = new Credential();
    	credential.setUsername(mobile);
    	credential.setPassword(password);
    	
    	Parent parent = null;
    	try {
    		parent = parentAuthService.login(credential);
    	} catch (UserNotExistServiceException e) {
    		logger.error("parent not find for username: {} password: {}", mobile, password);
    	}
    	
    	if (parent != null) {
    		Long familyId = parent.getFamily().getId();
    		
    		response.addCookie(new Cookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION, "parent " + parent.getId() + " " + parent.getToken()));
    		
    		List<Student> children = studentRepository.findByFamilyId(familyId);
    		
    		response.addCookie(new Cookie("userId", Long.toString(parent.getId())));
    		
    		if (children != null && children.size() != 0) {
    			Student s = children.get(0);
    			
    			StudentVO currentChild = new StudentVO();
    			currentChild.setId(s.getId());
    			currentChild.setEnglishName(s.getEnglishName());
    			currentChild.setStars(s.getStars());
    			currentChild.setName(s.getSafeName());
    			currentChild.setToken(s.getToken());
    			currentChild.setAvatar(s.getAvatar());
    			currentChild.setFamilyId(s.getFamily().getId());
    			
    			long studentId = currentChild.getId();
    			//保存student id 至cookie
    			response.addCookie(new Cookie("studentId", Long.toString(studentId)));
    			
    			UserCacheUtil.storeCurrentUser(Long.toString(currentChild.getId()), currentChild);
    			
//    			return Page.redirectTo(DOMAIN + "/lessonManagement/dashboard?openid=" + parent.getWechatOpenId() + "&target=dashboard");
    			return Page.redirectTo(DOMAIN + "/lessonManagement/dashboard");
    		} else {
    			return Page.redirectTo(ChildrenInfoMobileController.PATH);
    		}
    	} else {
			redirectAttributes.addFlashAttribute("loginFailedErrorMessage", "用户名或者密码错误");
			return Page.redirectTo(LoginMobileController.PATH);
		}
	}
}
