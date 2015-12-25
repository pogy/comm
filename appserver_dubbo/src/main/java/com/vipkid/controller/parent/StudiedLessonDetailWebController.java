package com.vipkid.controller.parent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.alibaba.fastjson.JSONObject;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.ext.baidu.BaiduTranslateAPI;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.TeacherComment;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherCommentService;
import com.vipkid.service.pojo.parent.LessonsView;
@Controller
public class StudiedLessonDetailWebController extends BaseWebController {
	public static final String PATH = "/parent/studiedlessondetail";
	
	@Resource
	private OnlineClassService onlineClassService;

	@Autowired
	private StudentService studentService;
	
	@Resource
	TeacherCommentService teacherCommentService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(StudiedLessonDetailWebController.PATH).setViewName(StudiedLessonDetailWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = StudiedLessonDetailWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request,long onlineClassId,long teacherCommentId,int sign){
		model.addAttribute("name", "已上课程详情");
		model.addAttribute("path", LessonsWebController.PATH);
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
		
		LessonsView lessonsView = onlineClassService.findStudiedLessonsDetail(onlineClassId, teacherCommentId,studentId);
		if(lessonsView.getLevel()!=null){
			lessonsView.setLevel(lessonsView.getLevel().replace("LEVEL_", "Level "));
		}
		model.addAttribute("lessonsView", lessonsView);
		model.addAttribute("sign", sign);
		
		return StudiedLessonDetailWebController.PATH;
	}
	
	@RequestMapping(value = "/parent/translate", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject translateComment(@RequestParam("commentId") Long commentId) {
		JSONObject retObj = new JSONObject();
		retObj.put("content", "");
		TeacherComment teacherComment = teacherCommentService.find(commentId);
		if (teacherComment == null) {
			return retObj;
		}
		
		if (StringUtils.isNotBlank(teacherComment.getTeacherFeedback())) {
			if (StringUtils.isBlank(teacherComment.getFeedbackTranslation())) {//没有翻译过,调用接口翻译
				String text = BaiduTranslateAPI.translate(teacherComment.getTeacherFeedback());
				if (text != null) {
					retObj.put("content", text);
					teacherComment.setFeedbackTranslation(text);
					teacherCommentService.update(teacherComment);
				}
			} else {//已经有翻译,直接取出
				retObj.put("content", teacherComment.getFeedbackTranslation());
			}
		} else if (teacherComment.getOnlineClass() != null && teacherComment.getOnlineClass().getFinishType() == FinishType.AS_SCHEDULED){
			retObj.put("content", LessonsView.feedbackTranslation);
		}
		return retObj;
		
	}
}
