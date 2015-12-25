package com.vipkid.controller.parent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherService;
import com.vipkid.service.pojo.parent.TeacherDetailView;
@Controller
public class TeacherDetailWebController extends BaseWebController {
	public static final String PATH = "/parent/teacherdetail";
	
	@Resource
	private TeacherService teacherService;
	
	@Resource
	private StudentService studentService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(TeacherDetailWebController.PATH).setViewName(TeacherDetailWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = TeacherDetailWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpSession session,HttpServletRequest request, Long teacherId){
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
				
		if(teacherId != null){
			TeacherDetailView teacherDetail = teacherService.findTeacherDetailById(teacherId, studentId);
			model.addAttribute("teacherDetail", teacherDetail);
			boolean flag=false;
			if(teacherDetail.getTag()!=null&&teacherDetail.getTag().length!=0){
				String tag[] = teacherDetail.getTag();
				for (int i = 0; i < tag.length; i++) {
					if(tag[i]!=null){
						flag=true;
						break;
					}
				}
			}
			model.addAttribute("flag", flag);
		}
		return TeacherDetailWebController.PATH;
	}
}
