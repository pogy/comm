package com.vipkid.controller.parent;

import java.util.Calendar;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
@Controller
public class NoStudiedLessonDetailWebController extends BaseWebController {
	public static final String PATH = "/parent/nostudiedlessondetail";

	@Resource
	private StudentService studentService;
	@Resource
	private OnlineClassService onlineClassService;
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(NoStudiedLessonDetailWebController.PATH).setViewName(NoStudiedLessonDetailWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = NoStudiedLessonDetailWebController.PATH, method = RequestMethod.GET)
	public String init(@RequestParam(value="onlineClassId", required=true) long onlineClassId, Model model,HttpServletRequest request){
		model.addAttribute("name", "未上课程详情");
		
		//公共信息
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return Page.redirectTo("/login");
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			return Page.redirectTo("/login");
		}
		initCommon(model, request);
		
		OnlineClass onlineClass = onlineClassService.find(onlineClassId);
		Lesson lesson = onlineClass.getLesson();
		String levelString = lesson.getLearningCycle().getUnit().getLevel().toString();
		int underScoreIndex = levelString.indexOf('_');
		
		if(null != lesson.getLearningCycle().getUnit().getNumber()){
			model.addAttribute("classIntro", "课程: Level " + levelString.substring(underScoreIndex+1) + " " + lesson.getLearningCycle().getUnit().getNumber() + " " + lesson.getName());
		} else {
			model.addAttribute("classIntro", "课程: Level " + levelString.substring(underScoreIndex+1) + " " + lesson.getName());
		}
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(onlineClass.getScheduledDateTime());
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(onlineClass.getScheduledDateTime());
		endTime.add(Calendar.MINUTE, 25);
		model.addAttribute("classTime", "时间: " + startTime.get(Calendar.YEAR) + "-" + (startTime.get(Calendar.MONTH)+1) + "-" + startTime.get(Calendar.DAY_OF_MONTH) + 
				" " + startTime.get(Calendar.HOUR_OF_DAY) + ":" + (startTime.get(Calendar.MINUTE) == 0 ? "00":"30") + ":00");
		model.addAttribute("teacherName", "老师: " + onlineClass.getTeacher().getName());
		model.addAttribute("object", onlineClass.getLesson().getObjective());
		model.addAttribute("vocabulary", onlineClass.getLesson().getVocabularies());
		model.addAttribute("grammar", onlineClass.getLesson().getSentencePatterns());
		model.addAttribute("teacherId", onlineClass.getTeacher().getId());
		
		
		return NoStudiedLessonDetailWebController.PATH;
	}
}
