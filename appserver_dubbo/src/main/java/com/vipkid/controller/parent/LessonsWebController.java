package com.vipkid.controller.parent;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.parent.LessonsView;
@Controller
public class LessonsWebController extends BaseWebController {
	public static final String PATH = "/parent/lessons";
	
	@Resource
	private OnlineClassService onlineClassService;

	@Autowired
	private StudentService studentService;
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(LessonsWebController.PATH).setViewName(LessonsWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = LessonsWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request,Integer rowNum,Integer currNum, HttpSession session){
		//公共信息
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			return "redirect:/login";
		}
		initCommon(model, request);
		
//		Cookie[] cookie = request.getCookies();
//		long studentId = -1;
//		for (int i = 0; i < cookie.length; i++) {
//			if(cookie[i].getName().equals("studentId")){
//				studentId = Long.valueOf(cookie[i].getValue().trim());
//				break;
//			}
//		}
		if(rowNum==null){
			rowNum =5;
		}
		if(currNum==null){
			currNum =1;
		}
		List<LessonsView>lessonsViews = onlineClassService.listForLessons(studentId,rowNum,currNum);
		long totalRecords = onlineClassService.countForLessons(studentId,rowNum,currNum);
		model.addAttribute("name", "已上课程");
		model.addAttribute("lessonsViews", lessonsViews);
		model.addAttribute("path", LessonsWebController.PATH);
		model.addAttribute("totalRecords",totalRecords);
		model.addAttribute("currNum",currNum);
		return LessonsWebController.PATH;
	}
	
}
