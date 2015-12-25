package com.vipkid.controller.parent;

import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
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
import com.vipkid.model.Channel;
import com.vipkid.model.Family;
import com.vipkid.model.Gender;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.service.ChannelService;
import com.vipkid.service.FamilyService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.ParentService;
import com.vipkid.service.StudentService;
import com.vipkid.util.CookieUtils;
@Controller
public class SettingWebController extends BaseWebController {
	public static final String PATH = "/parent/setting";
	
	@Autowired
	private ParentAuthService authService;
	
	@Autowired
	private ParentService parentService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private FamilyService familyService;
	
	@Resource
    private ChannelService channelService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(SettingWebController.PATH).setViewName(SettingWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = SettingWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request, HttpSession session){
		
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		
		long studentId = Long.parseLong(sid.toString());
		StudentVO studentvo = (StudentVO) UserCacheUtil.getCurrentUser(studentId);
		if(studentvo == null){
			return "redirect:/login";
		}
		initCommon(model, request);
		
		String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
		Long parentId = Long.valueOf(parentIdStr);
		Parent parent = parentService.find(parentId);
		if(parent != null){
			Long familyId = parent.getFamily().getId();
			Family family = familyService.find(familyId);
			List<Student> students = studentService.findByFamilyId(familyId);
			
			Parent prt = new Parent();
			prt.setName(parent.getName());
			prt.setMobile(parent.getMobile());
			prt.setEmail(parent.getEmail());
			model.addAttribute("parent", prt);
			
			Family fly = new Family();
			if(family.getProvince() == null){
				fly.setProvince("");
			}else{
				fly.setProvince(family.getProvince());
			}
			if(family.getCity() == null){
				fly.setCity("");
			}else{
				fly.setCity(family.getCity());
			}
			if(family.getDistrict() == null){
				fly.setDistrict("");
			}else{
				fly.setDistrict(family.getDistrict());
			}
			if(family.getAddress() == null){
				fly.setAddress("");
			}else{
				fly.setAddress(family.getAddress());
			}
			fly.setZipcode(family.getZipcode());
			model.addAttribute("family", fly);
			
			List<Student> sdts = new LinkedList<Student>();
			if(students != null && students.size() > 0){
				if(students.size()>=3){
					List<Student> threeStudents = new LinkedList<Student>();
					threeStudents.add(students.get(0));
					threeStudents.add(students.get(1));
					threeStudents.add(students.get(2));
					students.clear();
					students = threeStudents;
					model.addAttribute("alreadyThreeKids", true);
				}else{
					model.addAttribute("alreadyThreeKids", false);
				}
				int studentCount = students.size();
				for(Student student : students){
					Student sdt = new Student();
					sdt.setName(student.getName());
					sdt.setEnglishName(student.getEnglishName());
					sdt.setGender(student.getGender());
					sdt.setBirthday(student.getBirthday());
					sdt.setAvatar(student.getAvatar());
					sdt.setId(student.getId());
					sdts.add(sdt);
				}
				model.addAttribute("students", sdts);
				model.addAttribute("studentCount", studentCount);
			}
			return SettingWebController.PATH;
			
		}		
		return SettingWebController.PATH;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/parent/getParentInfo", method = RequestMethod.GET)
	public @ResponseBody JSONObject getParentAddress(Model model, HttpServletRequest request, HttpSession session){	
		String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
		Long parentId = Long.valueOf(parentIdStr);
		Parent parent = parentService.find(parentId);
		
		JSONObject parentObject = new JSONObject();
	
		if(parent != null){
			parentObject.put("name", parent.getName());  
			parentObject.put("mobile", parent.getMobile()); 
			parentObject.put("email", parent.getEmail());
			
			Long familyId = parent.getFamily().getId();
			Family family = familyService.find(familyId);
			parentObject.put("zipcode", family.getZipcode());
			String result = "";
			if(family.getProvince() == null){
				parentObject.put("address", null);
				return parentObject;
			}else if(family.getCity() == null){
				result = family.getProvince();
				parentObject.put("address", result);
				return parentObject;
			}else if(family.getDistrict() == null){
				result = family.getProvince() + ' ' + family.getCity();
				parentObject.put("address", result);
				return parentObject;
			}else if(family.getAddress() == null){
				result = family.getProvince() + ' ' + family.getCity() + ' ' + family.getDistrict();
				parentObject.put("address", result);
				return parentObject;
			}else{
				result = family.getProvince() + ' ' + family.getCity() + ' ' + family.getDistrict() + ' ' + family.getAddress();
				parentObject.put("address", result);
				return parentObject;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/parent/modifyParentInfo", method = RequestMethod.POST)
	public @ResponseBody Boolean modifyParentInfo(Model model, @RequestParam("name") String name, @RequestParam("mobile") String mobile, @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("zipcode") String zipcode,HttpServletRequest request, HttpSession session){
		String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
		Long parentId = Long.valueOf(parentIdStr);
		Parent parent = parentService.find(parentId);
		if(parent != null){
			parent.setName(name);
			parent.setMobile(mobile);
			parent.setEmail(email);
			parentService.update(parent);
			
			Long familyId = parent.getFamily().getId();
			Family family = familyService.find(familyId);
			family.setZipcode(zipcode);
			
			String[] arr = address.split(" ");
			family.setProvince(arr[0]);
			family.setCity(arr[1]);
			family.setDistrict(arr[2]);
			family.setAddress(arr[3]);
			familyService.update(family);
			
			if(!StringUtils.isBlank(parent.getName())){
    			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
    		}else{
    			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
    		}
			
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/parent/getStudentInfoByStudentId", method = RequestMethod.GET)
	public @ResponseBody JSONObject getStudentInfoByStudentId(Model model, @RequestParam("studentId") Long studentId, HttpServletRequest request, HttpSession session){	
		
		Student student = studentService.find(studentId);
		
		JSONObject studentObject = new JSONObject();
		
		if(student != null){
			studentObject.put("avatar", student.getAvatar());  
			studentObject.put("chineseName", student.getName()); 
			studentObject.put("englishName", student.getEnglishName());
			studentObject.put("gender", student.getGender());
			Date birth = student.getBirthday();
			studentObject.put("birth", birth);
			if(birth != null){
				java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		        String birthStr = format.format(birth);
				studentObject.put("birth", birthStr);
			}
			
			return studentObject;
		}else{
			return null;
		}
	}
	
	@RequestMapping(value = "/parent/saveStudentInfoByStudentId", method = RequestMethod.POST)
	public @ResponseBody Boolean saveStudentInfoByStudentId(Model model, @RequestParam("avatar") String avatar, @RequestParam("chineseName") String chineseName, @RequestParam("englishName") String englishName, @RequestParam("gender") String gender, @RequestParam("birth") Date birth, @RequestParam("studentId") Long studentId, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		//修改孩子信息
		if(studentId != null){
			Student student  = studentService.find(studentId);
			if(student != null){
				student.setAvatar(avatar);
				student.setName(chineseName);
				student.setEnglishName(englishName);
				if(gender.equals("FEMALE") ){
					student.setGender(Gender.FEMALE);
				}else{
					student.setGender(Gender.MALE);
				}
				student.setBirthday(birth);
			
				student = studentService.update(student);
				
				//保存成功之后, 如果编辑的学生是当前学生,需要更新一下学生信息
				long sid = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
				if(sid == studentId){
					StudentVO st = (StudentVO) UserCacheUtil.getCurrentUser(sid);
					st.setEnglishName(student.getEnglishName());
					st.setName(student.getSafeName());
					st.setAvatar(student.getAvatar());
					st.setTargetClassesPerWeek(student.getTargetClassesPerWeek());
					UserCacheUtil.storeCurrentUser(Long.toString(sid), st);
					
					Cookie userNameCookie = CookieUtils.createVIPKIDCookie("userName", URLEncoder.encode(st.getName(), "UTF8"));
					userNameCookie.setMaxAge(Integer.MAX_VALUE);
					response.addCookie(userNameCookie);
				}
				return true;
			}else{
				return false;
			}
		}else{
			//增加孩子
			Student student = new Student();
			student.setEnglishName(englishName);
			student.setName(chineseName);
			student.setBirthday(birth);
			student.setAvatar(avatar);
			if(gender.equals("FEMALE") ){
				student.setGender(Gender.FEMALE);
			}
			else{
				student.setGender(Gender.MALE);
			}
		
			String parentIdStr = CookieUtils.get(request.getCookies(), "Authorization").split(" ")[1];
			Long parentId = Long.valueOf(parentIdStr);
			Parent parent = parentService.find(parentId);
			if(parent != null){
				//修改 MPWS-202 start
				Channel channel = new Channel();
				channel.setId(parent.getChannel_id());
				if (parent.getChannel_id()==-1||parent.getChannel_id()==0) {
	                channel = channelService.getDefaultChannel();
	            }
				student.setChannel(channel);
				//修改 MPWS-202 end
				
				Parent returnParent = authService.AddOtherChildForParent(parent,student);
				if(returnParent != null) {
					Student returnStudent = studentService.findRecentResisteringByParentId(returnParent.getId());
					if(returnStudent != null) {
						leadsQueueSender.sendText(String.valueOf(returnStudent.getId()));
					}
				}
				return true;
			}
			return false;
		}
	}
	
}
