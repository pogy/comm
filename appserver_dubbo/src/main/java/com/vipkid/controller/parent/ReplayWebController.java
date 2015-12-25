package com.vipkid.controller.parent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.google.common.base.Strings;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Role;
import com.vipkid.model.Student;
import com.vipkid.service.DBYService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Room;

@Controller
public class ReplayWebController extends BaseWebController {
	private static final String PATH = "/parent/replay";
	
	@Resource
	private DBYService dbyService;
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private OnlineClassService onlineClassService;
	
	@Override
	protected void registerViewController( ViewControllerRegistry viewControllerRegistry) {

	}

	@RequestMapping(value = ReplayWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request, long studentId, long onlineClassId){
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return Page.redirectTo("/login");
		}
		
		if(!Strings.isNullOrEmpty(Long.toString(studentId)) && !Strings.isNullOrEmpty(Long.toString(onlineClassId))){
			Student student = studentService.find(studentId);
			OnlineClass oc = onlineClassService.find(onlineClassId);
			if(student != null && oc != null){
				
				//看回放是 判断这个学生是否已经报名 如果没有  扣除课时
				String msg = onlineClassService.doSubstractForOpenClassReplay(studentId, onlineClassId, oc);
				if(!"".equals(msg)){
					request.setAttribute("msg", msg);
					return "forward:/alert";
				}
				//看回放是 判断这个学生是否已经报名 如果没有  扣除课时
				Room room = dbyService.getDBYRoomURL(Long.toString(student.getId()), student.getEnglishName(), oc.getClassroom(), Role.STUDENT);
				if(room != null){
					model.addAttribute("roomURL", room.getUrl());
					model.addAttribute("lessonName", oc.getLesson().getName());
					model.addAttribute("teacherName",oc.getTeacher().getName());
				}
			}
		}
		
		return ReplayWebController.PATH;
	}

}
