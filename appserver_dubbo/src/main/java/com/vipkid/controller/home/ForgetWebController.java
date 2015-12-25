package com.vipkid.controller.home;


import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.service.MedalService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.StudentService;
import com.vipkid.util.CookieUtils;

@Controller
public class ForgetWebController extends BaseWebController {
	public static final String PATH = "/home/forget";
	private static final String DEFAULT_CHARSET = "UTF8";
	private static final Logger logger = LoggerFactory.getLogger(ForgetWebController.class);
	
	@Autowired
	private ParentAuthService authService;
	
	@Autowired
	private ParentAuthService parentAuthService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private MedalService medalService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ForgetWebController.PATH).setViewName(ForgetWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/forget", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		initHome(model, request);
		return ForgetWebController.PATH;
	}
	

	@RequestMapping(value = "/forget", method = RequestMethod.POST)
	public @ResponseBody String parentChangePassword(@RequestParam("parentMobile") String username, @RequestParam("parentPassword") String password, @RequestParam("inventionCode") String inventionCode, HttpServletResponse response, HttpSession session) throws Exception{
		if ( username.isEmpty() || !username.matches("^1[3|4|5|7|8]\\d{9}$") || password.length() < 6 ){
			logger.info("111111");
			return "validationFail";
		}
		else{
			String code = new String();
        	try{
        		code = UserCacheUtil.getVerifyCode(username);
        	}catch(Exception e){
        		logger.info("222222");
        		return "validationFail";
        	}
        	
        	if(code == null){
        		logger.info("33333");
        		return "validationFail";
        	}else if(code != null && code.equals(inventionCode)){
        		Parent parent = parentAuthService.findByUsername(username);
        		if(parent == null){
        			logger.info("4444");
        			return "validationFail";
        		}else{
        			parent.setPassword(password);
        			parent.setVerifyCode(inventionCode);
        			parent = parentAuthService.changePassword(parent);
        			
        			Long familyId = parent.getFamily().getId();
        			List<Student> students = studentService.findByFamilyId(familyId);
            		if(students != null && students.size() > 0){
            			Student s = students.get(0);
            			
            			StudentVO currentStudent = new StudentVO();
            			currentStudent.setId(s.getId());
            			currentStudent.setEnglishName(s.getEnglishName());
            			currentStudent.setStars(s.getStars());
            			currentStudent.setName(s.getSafeName());
            			currentStudent.setToken(s.getToken());
            			currentStudent.setAvatar(s.getAvatar());
            			currentStudent.setFamilyId(s.getFamily().getId());
            			currentStudent.setTargetClassesPerWeek(s.getTargetClassesPerWeek());
            			long studentId = currentStudent.getId();
            			
            			//保存token信息至 cookie, 记住登录状态, c_userId在跳转到学习端的时候用到
                        Cookie userIdCookie = CookieUtils.createVIPKIDCookie("userId",String.valueOf(currentStudent.getId()));
                        Cookie userNameCookie = CookieUtils.createVIPKIDCookie("userName", URLEncoder.encode(currentStudent.getName(), DEFAULT_CHARSET));
                        Cookie studentIdCookie = CookieUtils.createVIPKIDCookie("studentId",String.valueOf(currentStudent.getId()));
                        Cookie userToken = CookieUtils.createVIPKIDCookie("userToken",parent.getToken());
                        
                        userIdCookie.setMaxAge(Integer.MAX_VALUE);
                        userNameCookie.setMaxAge(Integer.MAX_VALUE);
            			studentIdCookie.setMaxAge(Integer.MAX_VALUE);
            			userToken.setMaxAge(Integer.MAX_VALUE);
            
            			response.addCookie(userIdCookie);
            			response.addCookie(userNameCookie);
            			response.addCookie(studentIdCookie);
            			response.addCookie(userToken);
            			
            			long medalCount = medalService.count(studentId);
            			
            			UserCacheUtil.storeCurrentUser(Long.toString(currentStudent.getId()), currentStudent);
            			UserCacheUtil.storeMedalCount(currentStudent.getId(), medalCount);
            			
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
                		
                		Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION,new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                        Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId",String.valueOf(familyId));
                        Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId",String.valueOf(parent.getId()));
            			
            			authCookie.setMaxAge(Integer.MAX_VALUE);
            			familyIdCookie.setMaxAge(Integer.MAX_VALUE);
            			parentIdCookie.setMaxAge(Integer.MAX_VALUE);
            			
            			response.addCookie(authCookie);
            			response.addCookie(familyIdCookie);
            			response.addCookie(parentIdCookie);
            			
            			if(!StringUtils.isBlank(parent.getName())){
                			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
                		}else{
                			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
                		}
                		
                		return "parentCenter";
            		}else{
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
                		
                		Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION,new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                        Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId",String.valueOf(familyId));
                        Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId",String.valueOf(parent.getId()));
            			
            			authCookie.setMaxAge(Integer.MAX_VALUE);
            			familyIdCookie.setMaxAge(Integer.MAX_VALUE);
            			parentIdCookie.setMaxAge(Integer.MAX_VALUE);
            			
            			response.addCookie(authCookie);
            			response.addCookie(familyIdCookie);
            			response.addCookie(parentIdCookie);
            			
            			if(!StringUtils.isBlank(parent.getName())){
                			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
                		}else{
                			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
                		}
            			return "studentInfo";
            		}
        		}
        	} 
        	logger.info("55555");
        	return "validationFail";
        }
	}
}
