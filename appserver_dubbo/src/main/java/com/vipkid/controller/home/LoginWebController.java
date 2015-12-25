package com.vipkid.controller.home;

import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.service.MedalService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.SudoCredential;
import com.vipkid.util.CookieUtils;

@Controller
public class LoginWebController extends BaseWebController {
	public static final String PATH = "/home/login";
	public static final String SUDO_LOGIN_PATH = "/home/sudo_login";
	public static final String PATH_INT = "/home/intlogin";
	private static final String DEFAULT_CHARSET = "UTF8";
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private MedalService medalService;
	
	@Resource
	private ParentAuthService parentAuthService;
	
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(LoginWebController.PATH).setViewName(LoginWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request,HttpServletResponse response, String channel_id){
		model.addAttribute("credential", new Credential());
		if(channel_id != null){
			Cookie cookie = CookieUtils.createVIPKIDCookie("channel_id", channel_id);
			response.addCookie(cookie);
		}
		
		return LoginWebController.PATH;
	}
	
	@RequestMapping(value = "/sudo_login", method = RequestMethod.GET)
	public String sudo_login(Model model, HttpServletRequest request,HttpServletResponse response, String channel_id){
		model.addAttribute("credential", new SudoCredential());
		if(channel_id != null){
			Cookie cookie = CookieUtils.createVIPKIDCookie("channel_id", channel_id);
			response.addCookie(cookie);
		}
		
		return LoginWebController.SUDO_LOGIN_PATH;
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/int-login", method = RequestMethod.GET)
	public String init2(Model model, HttpServletRequest request,HttpServletResponse response, String channel_id){
		model.addAttribute("credential", new Credential());
		if(channel_id != null){
			Cookie cookie = CookieUtils.createVIPKIDCookie("channel_id", channel_id);
			response.addCookie(cookie);
		}
		
		return LoginWebController.PATH_INT;
	}
	
	/**
	 * 登录
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("autoLogin") String autoLogin, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		if ( username.isEmpty() || !username.matches("^1[3|4|5|7|8]\\d{9}$") || password.length() < 6 ){
            return "login";
        }else {
        	Credential credential = new Credential();
        	credential.setUsername(username);
        	credential.setPassword(password);
        	String returnPath = "";
        	
        	if(parentAuthService.findByUsername(username) != null){
        		Parent parent = null;
            	try {
            		parent = parentAuthService.login(credential);
    			} catch (Exception e) {
            		return "userNamePasswordError";
    			}
            	if(parent == null) {
            		return "userNamePasswordError";
            	}else {
            		Long familyId = parent.getFamily().getId();
            		List<Student> students = studentService.findByFamilyId(familyId);
            		if(students != null && students.size() > 0){
            			if(session.getAttribute("channel_id") != null){
            				returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
            			if(UserCacheUtil.getValueFromCookie(request, "channel_id") != null){
            				Cookie cookie = CookieUtils.delVIPKIDCookie("channel_id");  
            		         response.addCookie(cookie);
            		         returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
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
            			// 2015-09-01 
            			currentStudent.setMaxTimesLevelExam(s.getMaxTimesLevelExam());
            			if (null != s.getChannel()) {
            				currentStudent.setChannelId(s.getChannel() .getId());
            			}
            			long studentId = currentStudent.getId();
            			
            			//保存token信息至 cookie, 记住登录状态, c_userId在跳转到学习端的时候用到
                        Cookie userIdCookie = CookieUtils.createVIPKIDCookie("userId",String.valueOf(currentStudent.getId()));
                        Cookie userNameCookie = CookieUtils.createVIPKIDCookie("userName", URLEncoder.encode(currentStudent.getName(), DEFAULT_CHARSET));
                        Cookie studentIdCookie = CookieUtils.createVIPKIDCookie("studentId",String.valueOf(currentStudent.getId()));
                        Cookie userToken = CookieUtils.createVIPKIDCookie("userToken",parent.getToken());
            			if("true".equals(autoLogin)){
            				userIdCookie.setMaxAge(Integer.MAX_VALUE);
            				userNameCookie.setMaxAge(Integer.MAX_VALUE);
            				studentIdCookie.setMaxAge(Integer.MAX_VALUE);
            				userToken.setMaxAge(Integer.MAX_VALUE);
            			}
            			response.addCookie(userIdCookie);
            			response.addCookie(userNameCookie);
            			response.addCookie(studentIdCookie);
            			response.addCookie(userToken);
            			
            			long medalCount = medalService.count(studentId);
            			
            			UserCacheUtil.storeCurrentUser(Long.toString(currentStudent.getId()), currentStudent);
            			UserCacheUtil.storeMedalCount(currentStudent.getId(), medalCount);
            		}else{
            			returnPath = "success_guide";
            		}
            		if(!StringUtils.isBlank(parent.getName())){
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
            		}else{
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
            		}
            		
            		Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION,new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                    Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId",String.valueOf(familyId));
                    Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId",String.valueOf(parent.getId()));
                    
        			if("true".equals(autoLogin)){
        				authCookie.setMaxAge(Integer.MAX_VALUE);
        				familyIdCookie.setMaxAge(Integer.MAX_VALUE);
        				parentIdCookie.setMaxAge(Integer.MAX_VALUE);
        			}
        			response.addCookie(authCookie);
        			response.addCookie(familyIdCookie);
        			response.addCookie(parentIdCookie);
            		return returnPath;
            	}
        	}
        	return "login";
        }
	}
	
	/**
	 * 登录
	 */
	@RequestMapping(value = "/sudo_login", method = RequestMethod.POST)
	@ResponseBody
	public String sudoLogin(@RequestParam("userName") String username, @RequestParam("adminName") String adminName, @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		if ( username.isEmpty() || !username.matches("^1[3|4|5|7|8]\\d{9}$") || password.length() < 6 ){
            return "login";
        } else {
        	SudoCredential credential = new SudoCredential();
        	credential.setUserName(username);
        	credential.setAdminName(adminName);
        	credential.setPassword(password);
        	String returnPath = "";
        	
        	if(parentAuthService.findByUsername(username) != null){
        		Parent parent = null;
            	try {
            		parent = parentAuthService.sudoLogin(credential);
    			} catch (Exception e) {
            		return "userNamePasswordError";
    			}
            	if(parent == null) {
            		return "userNamePasswordError";
            	}else {
            		Long familyId = parent.getFamily().getId();
            		List<Student> students = studentService.findByFamilyId(familyId);
            		if(students != null && students.size() > 0){
            			if(session.getAttribute("channel_id") != null){
            				returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
            			if(UserCacheUtil.getValueFromCookie(request, "channel_id") != null){
            				Cookie cookie = CookieUtils.delVIPKIDCookie("channel_id");  
            		         response.addCookie(cookie);
            		         returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
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
            			// 2015-09-01 
            			currentStudent.setMaxTimesLevelExam(s.getMaxTimesLevelExam());
            			if (null != s.getChannel()) {
            				currentStudent.setChannelId(s.getChannel() .getId());
            			}
            			long studentId = currentStudent.getId();
            			
            			//保存token信息至 cookie, 记住登录状态, c_userId在跳转到学习端的时候用到
                        Cookie userIdCookie = CookieUtils.createVIPKIDCookie("userId",String.valueOf(currentStudent.getId()));
                        Cookie userNameCookie = CookieUtils.createVIPKIDCookie("userName", URLEncoder.encode(currentStudent.getName(), DEFAULT_CHARSET));
                        Cookie studentIdCookie = CookieUtils.createVIPKIDCookie("studentId",String.valueOf(currentStudent.getId()));
                        Cookie userToken = CookieUtils.createVIPKIDCookie("userToken",parent.getToken());
            			response.addCookie(userIdCookie);
            			response.addCookie(userNameCookie);
            			response.addCookie(studentIdCookie);
            			response.addCookie(userToken);
            			
            			long medalCount = medalService.count(studentId);
            			
            			UserCacheUtil.storeCurrentUser(Long.toString(currentStudent.getId()), currentStudent);
            			UserCacheUtil.storeMedalCount(currentStudent.getId(), medalCount);
            		}else{
            			returnPath = "success_guide";
            		}
            		if(!StringUtils.isBlank(parent.getName())){
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
            		}else{
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
            		}
            		
            		Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION,new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                    Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId",String.valueOf(familyId));
                    Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId",String.valueOf(parent.getId()));
        			response.addCookie(authCookie);
        			response.addCookie(familyIdCookie);
        			response.addCookie(parentIdCookie);
            		return returnPath;
            	}
        	}
        	return "login";
        }
	}
	
	/**
	 * 登录
	 */
	@RequestMapping(value = "/intlogin", method = RequestMethod.POST)
	@ResponseBody
	public String intlogin(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("autoLogin") String autoLogin, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		if ( username.isEmpty() || password.length() < 6 ){
            return "login";
        }else {
        	Credential credential = new Credential();
        	credential.setUsername(username);
        	credential.setPassword(password);
        	String returnPath = "";
        	
        	if(parentAuthService.findByUsername(username) != null){
        		Parent parent = null;
            	try {
            		parent = parentAuthService.login(credential);
    			} catch (Exception e) {
            		return "userNamePasswordError";
    			}
            	if(parent == null) {
            		return "userNamePasswordError";
            	}else {
            		Long familyId = parent.getFamily().getId();
            		List<Student> students = studentService.findByFamilyId(familyId);
            		if(students != null && students.size() > 0){
            			if(session.getAttribute("channel_id") != null){
            				returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
            			if(UserCacheUtil.getValueFromCookie(request, "channel_id") != null){
            				Cookie cookie = CookieUtils.delVIPKIDCookie("channel_id");  
            		         response.addCookie(cookie);
            		         returnPath =  "success_openclass";
            			}else{
            				returnPath = "success_parentHome";
            			}
            			
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
            			if("true".equals(autoLogin)){
            				userIdCookie.setMaxAge(Integer.MAX_VALUE);
            				userNameCookie.setMaxAge(Integer.MAX_VALUE);
            				studentIdCookie.setMaxAge(Integer.MAX_VALUE);
            				userToken.setMaxAge(Integer.MAX_VALUE);
            			}
            			response.addCookie(userIdCookie);
            			response.addCookie(userNameCookie);
            			response.addCookie(studentIdCookie);
            			response.addCookie(userToken);
            			
            			long medalCount = medalService.count(studentId);
            			
            			UserCacheUtil.storeCurrentUser(Long.toString(currentStudent.getId()), currentStudent);
            			UserCacheUtil.storeMedalCount(currentStudent.getId(), medalCount);
            		}else{
            			returnPath = "success_guide";
            		}
            		if(!StringUtils.isBlank(parent.getName())){
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
            		}else{
            			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
            		}
            		
            		Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION,new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                    Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId",String.valueOf(familyId));
                    Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId",String.valueOf(parent.getId()));
                    
        			if("true".equals(autoLogin)){
        				authCookie.setMaxAge(Integer.MAX_VALUE);
        				familyIdCookie.setMaxAge(Integer.MAX_VALUE);
        				parentIdCookie.setMaxAge(Integer.MAX_VALUE);
        			}
        			response.addCookie(authCookie);
        			response.addCookie(familyIdCookie);
        			response.addCookie(parentIdCookie);
            		return returnPath;
            	}
        	}
        	return "login";
        }
	}
	
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String signout(HttpServletResponse response, HttpSession session){
		 Cookie cookie1 = CookieUtils.delVIPKIDCookie("studentId");
         response.addCookie(cookie1);
         
         Cookie cookie2 = CookieUtils.delVIPKIDCookie("familyId");  
         response.addCookie(cookie2);
         
         Cookie cookie3 = CookieUtils.delVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION);  
         response.addCookie(cookie3);
         
         Cookie cookie4 = CookieUtils.delVIPKIDCookie("userId");  
         response.addCookie(cookie4);
         
         Cookie cookie5 = CookieUtils.delVIPKIDCookie("userToken");  
         response.addCookie(cookie5);
         
         Cookie cookie6 = CookieUtils.delVIPKIDCookie("parentId");  
         response.addCookie(cookie6);
         
         Cookie cookie7 = CookieUtils.delVIPKIDCookie("userName");  
         response.addCookie(cookie7);
         
         Cookie cookie8 = CookieUtils.delVIPKIDCookie("skipGuide");  
         response.addCookie(cookie8);
         
         session.removeAttribute("channel_id");
         
         return Page.redirectTo("/home");
	}
}
