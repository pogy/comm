package com.vipkid.controller.parent;

import java.util.ArrayList;
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
import com.vipkid.service.CourseService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherService;
import com.vipkid.service.pojo.parent.TeachersView;
@Controller
public class TeachersWebController extends BaseWebController {
	public static final String PATH = "/parent/teachers";
	
	@Resource
	private CourseService courseService;
	
	@Resource
	private TeacherService teacherService;

	@Autowired
	private StudentService studentService;
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(TeachersWebController.PATH).setViewName(TeachersWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	/**
	 * 
	* @Title: init 
	* @Description: TODO 
	* @param tabSign 断查询那个tab -1 全部老师 1收藏老师
	* @param courseType 所选课程 'All' 所有 ,'MAJOR' major 主修课
	* @param timeWeek -1 不限、1本周、2下周
	* @param seachSign 判断是否是由姓名进行搜索 -1初始    1 按姓名 0不按姓名 按时间和课程
	* @author zhangfeipeng 
	* @return String
	* @throws
	 */
	@RequestMapping(value = TeachersWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpSession session,HttpServletRequest request,
			String courseType,
			Integer tabSign,
			Integer timeWeek,
			String teacherName,
			Integer seachSign,
			Integer rowNum,
			Integer currNum){
		model.addAttribute("name", "我的老师");
		model.addAttribute("path", TeachersWebController.PATH);
		
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
		
		tabSign=tabSign==null?1:tabSign;
		timeWeek=timeWeek==null?-1:timeWeek;
		seachSign=seachSign==null?-1:seachSign;
		rowNum=rowNum==null?10:rowNum;
		currNum=currNum==null?1:currNum;
		courseType=courseType==null?"All":courseType;
		List<TeachersView> teachersViews = teacherService.listTeachers(tabSign, courseType, timeWeek, teacherName, seachSign, studentId, rowNum, currNum);
		long count = teacherService.countTeachers(tabSign, courseType, timeWeek, teacherName, seachSign, studentId);
		if(tabSign==-1){
			model.addAttribute("teachersViews1",teachersViews);
			model.addAttribute("teachersViews2",new ArrayList<TeachersView>());
		}else{
			model.addAttribute("teachersViews2",teachersViews);
			model.addAttribute("teachersViews1",new ArrayList<TeachersView>());
		}
		model.addAttribute("totalRecords", count);
		model.addAttribute("tabSign", tabSign);
		model.addAttribute("timeWeek", timeWeek);
		model.addAttribute("seachSign", seachSign);
		model.addAttribute("currNum", currNum);
		model.addAttribute("teacherName", teacherName);
		model.addAttribute("courseType", courseType);
		if(courseType.equals("MAJOR")){
			model.addAttribute("selectName", "Major Course 主修课");
		}else{
			model.addAttribute("selectName", "不限");
		}
		
		return TeachersWebController.PATH;
	}
}
