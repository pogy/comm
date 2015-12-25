package com.vipkid.controller.parent;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.google.common.base.Strings;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Role;
import com.vipkid.model.Student;
import com.vipkid.model.TeacherComment;
import com.vipkid.service.DBYService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherCommentService;
import com.vipkid.service.pojo.Room;

@Controller
public class ClassroomWebController extends BaseWebController {
	private static final String PATH = "/parent/classroom";
	
	@Resource
	private DBYService dbyService;
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private OnlineClassService onlineClassService;
	
	@Resource 
	private TeacherCommentService teacherCommentService;
	
	@Override
	protected void registerViewController( ViewControllerRegistry viewControllerRegistry) {

	}

	@RequestMapping(value = ClassroomWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request, long studentId, long onlineClassId,String scheduledDateTime){
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return Page.redirectTo("/login");
		}
		long teacherId=-1;
		if(!Strings.isNullOrEmpty(Long.toString(studentId)) && !Strings.isNullOrEmpty(Long.toString(onlineClassId))){
			Student student = studentService.find(studentId);
			OnlineClass oc = onlineClassService.find(onlineClassId);
			if(student != null && oc != null){
				Room room = dbyService.getDBYRoomURL(Long.toString(student.getId()), student.getEnglishName(), oc.getClassroom(), Role.STUDENT);
				if(room != null){
					model.addAttribute("roomURL", room.getUrl());
					model.addAttribute("lessonName", oc.getLesson().getTopic());
					model.addAttribute("scheduleTime", oc.getScheduledDateTime().getTime());
					model.addAttribute("currentTime",System.currentTimeMillis());
					model.addAttribute("teacherName",oc.getTeacher().getName());
					model.addAttribute("lessonSerialNumber",oc.getLesson().getSerialNumber());
					model.addAttribute("teacherId",oc.getTeacher().getId());
					teacherId = oc.getTeacher().getId();
				}
			}
		}
		
		if(StringUtils.isNotBlank(scheduledDateTime)){//有传递scheduledDateTime 表明没有报名的学生，自动给学生报名
			String msg = onlineClassService.doBookOnToManyForOpen(teacherId, studentId, onlineClassId, scheduledDateTime);
			if(!"".equals(msg)){
				request.setAttribute("msg", msg);
				return "forward:/alert";
			}
		}
		
		TeacherComment tComment = teacherCommentService.findTeacherCommentByIds(teacherId, onlineClassId, studentId);
		Object hasOrder = UserCacheUtil.getValueFromCookie(request, studentId+"_hasOrder");
		if(tComment.getStars()>=5){
			model.addAttribute("hasStars","hasStars");
		}
		model.addAttribute("hasOrder",hasOrder==null?"":hasOrder.toString());
		model.addAttribute("onlineClassId",onlineClassId);
		
		long trailNum = onlineClassService.countTrailByStudentId(studentId);
		model.addAttribute("trailNum",trailNum);
		
		return ClassroomWebController.PATH;
	}
	
	
	@RequestMapping(value = "/parent/doTakeStar", method = RequestMethod.POST)
	@ResponseBody
	public String doTakeStar(HttpServletRequest request,
			HttpServletResponse response,
			long teacherId,
			long onlineClassId){
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		long studentId = Long.parseLong(sid.toString());
		try {
			studentService.doTakeStar(studentId,teacherId,onlineClassId);
			return "";
		} catch (Exception e) {
			return "领取失败，如需帮助，请联系客服。";
		}
	}
	
	@RequestMapping(value = "/parent/doOrderOnlineClass", method = RequestMethod.POST)
	@ResponseBody
	public String doOrderOnlineClass(HttpServletRequest request,HttpServletResponse response,String lessonSerialNumber){
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		long studentId = Long.parseLong(sid.toString());
		String text = studentId+"_hasOrder";
		try {
			studentService.sendEmailToSale(studentId, lessonSerialNumber);
			Cookie cookie = new Cookie(text,"hasOrder");//预约发邮件后记住状态
			response.addCookie(cookie);
			return "";
		} catch (Exception e) {
			return "预约失败，如需帮助，请联系客服。";
		}
	}
}
