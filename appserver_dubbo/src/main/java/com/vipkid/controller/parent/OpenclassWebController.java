package com.vipkid.controller.parent;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Student;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.OpenClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.OpenClassDescView;

@Controller
public class OpenclassWebController extends BaseWebController {
	public static final String WWW_PATH = "/home/classlist";
	public static final String PARENT_PATH = "/parent/openclass";
	
	@Resource
	private OpenClassService openClassService;
	
	@Resource
	private OnlineClassService onlineClassService;
	
	@Resource
	private StudentService studentService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		// TODO Auto-generated method stub
	}
	
	
	@RequestMapping(value = "/classlist", method = RequestMethod.GET)
	public String init(Model model,HttpServletRequest request, Integer ageRange,Integer sign, Integer rowNum, Integer currNum,String type){
		model.addAttribute("path", OpenclassWebController.WWW_PATH);
		ageRange = ageRange==null?-1:ageRange;
		rowNum = rowNum==null?5:rowNum;
		currNum = currNum==null?1:currNum;
		type = type==null?"ALL":type;
		
		List<OpenClassDescView> list = openClassService.listOpenClass(ageRange, -1, rowNum, currNum,type);
		long count = openClassService.countOpenClass(ageRange,type);

		model.addAttribute("openClassList", list);
		model.addAttribute("totalRecords", count);
		model.addAttribute("ageRange", ageRange);
		model.addAttribute("rowNum", rowNum);
		model.addAttribute("currNum", currNum);
		model.addAttribute("sign", sign==null?-1:sign);
		model.addAttribute("type", type);
		return OpenclassWebController.WWW_PATH;
	}
	
	/**
	 * 
	* @Title: init2 
	* @Description: 公开课列表 
	* @param ageRange  -1 : 不限 , 1 : 4-8岁 , 2 : 9-12 岁
	* @author zhangfeipeng 
	* @return String
	* @throws
	 */
	@RequestMapping(value = OpenclassWebController.PARENT_PATH, method = RequestMethod.GET)
	public String init2(Model model,HttpServletRequest request,
			Integer ageRange,
			Integer sign,
			Integer rowNum,
			Integer currNum,
			String type){
		model.addAttribute("path", OpenclassWebController.PARENT_PATH);
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		Student student = studentService.find(studentId);
		if(null != student){
			model.addAttribute("studentChannelId", student.getChannel().getId());
		}
		
		initCommon(model, request);
		ageRange = ageRange==null?-1:ageRange;
		rowNum = rowNum==null?5:rowNum;
		currNum = currNum==null?1:currNum;
		type = type==null?"ALL":type;
		
		List<OpenClassDescView> list = openClassService.listOpenClass(ageRange, studentId, rowNum, currNum,type);
		long count = openClassService.countOpenClass(ageRange,type);

		model.addAttribute("openClassList", list);
		model.addAttribute("totalRecords", count);
		model.addAttribute("ageRange", ageRange);
		model.addAttribute("rowNum", rowNum);
		model.addAttribute("currNum", currNum);
		model.addAttribute("studentId", studentId);
		model.addAttribute("sign", sign==null?-1:sign);
		model.addAttribute("type", type);
				
		return OpenclassWebController.PARENT_PATH;
	}
	
	@RequestMapping(value = "/parent/doSignUpOpenClass", method = RequestMethod.POST)
	@ResponseBody
	public String doSignUpOpenClass(long teacherId,long studentId,long onlineClassId,String time){
		try {
			String msg = onlineClassService.doBookOnToManyForOpen(teacherId,studentId, onlineClassId,time);
			return msg;
		} catch (Exception e) {
			return "报名失败，如需帮助，请联系客服。";
		}
	}
	
	
}
