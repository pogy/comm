package com.vipkid.controller.parent;

import java.net.URLEncoder;

import javax.servlet.http.Cookie;
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
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Student;
import com.vipkid.service.MedalService;
import com.vipkid.service.StudentService;
import com.vipkid.util.CookieUtils;

@Controller
public class ParentLayoutWebController extends BaseWebController {
	public static final String PATH = "/parent/layout";
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private MedalService medalService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ParentLayoutWebController.PATH).setViewName(ParentLayoutWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = ParentLayoutWebController.PATH, method = RequestMethod.GET)
	public String init(Model model){
		model.addAttribute("path", ParentWebController.PATH);
		return ParentWebController.PATH;
	}

	@RequestMapping(value = "changeCurrentStudent", method = RequestMethod.GET)
	public @ResponseBody String changeCurrentStudent(@RequestParam(value="studentId", required=true) long studentId, HttpServletResponse response) throws Exception{
		try {
			Student s = studentService.find(studentId);
			
			StudentVO student = new StudentVO();
			student.setId(s.getId());
			student.setEnglishName(s.getEnglishName());
			student.setStars(s.getStars());
			student.setName(s.getSafeName());
			student.setToken(s.getToken());
			student.setAvatar(s.getAvatar());
			student.setFamilyId(s.getFamily().getId());
			
			long medalCount = medalService.count(studentId);
			UserCacheUtil.storeCurrentUser(Long.toString(student.getId()), student);
			UserCacheUtil.storeMedalCount(student.getId(), medalCount);
			Cookie c = new Cookie("studentId", studentId + "" );
			c.setMaxAge(Integer.MAX_VALUE);
			c.setDomain(CookieUtils.DOMAIN);
			c.setPath("/");
			response.addCookie(c);
			
			Cookie c2 = new Cookie("userId", String.valueOf(studentId));
			c2.setMaxAge(Integer.MAX_VALUE);
			c2.setDomain(CookieUtils.DOMAIN);
			c2.setPath("/");
			response.addCookie(c2);
			
			Cookie c4 = new Cookie("userName", URLEncoder.encode(s.getName(),"UTF8"));
			c4.setMaxAge(Integer.MAX_VALUE);
			c4.setDomain(CookieUtils.DOMAIN);
			c4.setPath("/");
			response.addCookie(c4);
			
		} catch (Exception e) {
			return "FAIL";
		}
		return "OK";
	}
}
