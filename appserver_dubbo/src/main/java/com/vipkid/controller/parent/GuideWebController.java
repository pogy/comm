package com.vipkid.controller.parent;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.service.ChannelService;
import com.vipkid.service.MedalService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.ParentService;
import com.vipkid.service.StudentService;
import com.vipkid.service.VerificationCodeService;
import com.vipkid.service.pojo.Signup;
import com.vipkid.util.CookieUtils;

@Controller
public class GuideWebController extends BaseWebController {
	public static final String PATH = "/parent/guide_step1";
	private static final String DEFAULT_CHARSET = "UTF8";
	
	@Autowired
	private ParentAuthService authService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private ParentAuthService parentAuthService;
	
	@Autowired
	private ParentService parentService;
	
	@Autowired
	private VerificationCodeService verificationCodeService;
	
	@Autowired
	private MedalService medalService;
	
	@Autowired
	private OnlineClassService onlineClassService;

	@Autowired
	private ChannelService channelService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(GuideWebController.PATH).setViewName(GuideWebController.PATH);
		viewControllerRegistry.addViewController("/parent/guide_step2").setViewName("/parent/guide_step2");
	}
	
	/**
	 * 初始化
	 * @return
	 */
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String guideStep1(){
		return GuideWebController.PATH;
	}
	@RequestMapping(value = "/parent/guide_step2", method = RequestMethod.GET)
	public String guideStep2(){
		return "/parent/guide_step2";
	}
//	@RequestMapping(value = "/parent/guide_step3", method = RequestMethod.GET)
//	public String guideStep3(){
//		return "/parent/guide_step3";
//	}
	
	@RequestMapping(value = "/parent/signupStudentInfo", method = RequestMethod.POST)
	public  @ResponseBody String signupStudentInfo(@RequestParam("chineseName") String chineseName, @RequestParam("englishName") String englishName, @RequestParam("gender") String gender, @RequestParam("birth") Date birth, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String token = CookieUtils.get(request.getCookies(), CookieUtils.HTTP_COOKIE_AUTHENTICATION);
		if(token == null){
			token = request.getHeader("Authorization");
		}
			
		String[] arr = token.split(" ");
		Long id = Long.valueOf(arr[1]);
		Parent parent = parentService.find(id);
		
		Family family = parent.getFamily();
		List<Student> studentList = studentService.findByFamilyId(family.getId());
		if(studentList.size() != 0){
			return "welcome";
		}else{
			Student student = new Student();
			student.setEnglishName(englishName);
			student.setName(chineseName);
			student.setBirthday(birth);
			if(gender.equals("FEMALE") ){
				student.setGender(Gender.FEMALE);
			}
			else{
				student.setGender(Gender.MALE);
			}
			Channel aChannel = channelService.find(parent.getChannel_id());
			if (aChannel == null) {
				aChannel = channelService.getDefaultChannel();
			}
			if (!StringUtils.isEmpty(parent.getChannelKeyword())) {
				student.setChannelKeyword(parent.getChannelKeyword());
			}
			student.setChannel(aChannel);
			Signup signup = new Signup();
			signup.setParent(parent);
			signup.setStudent(student);
			signup.setInventionCode(parent.getVerifyCode());
			
			parentAuthService.AddFirstChildForParent(signup);
			if (signup != null && signup.getStudent() != null) {
				leadsQueueSender.sendText(String.valueOf(signup.getStudent().getId()));
			}
			
			Long familyId = parent.getFamily().getId();
			List<Student> students = studentService.findByFamilyId(familyId);
			if(students.size() > 0){
				Student s = students.get(0);
				
				StudentVO stu = new StudentVO();
				stu.setId(s.getId());
    			stu.setEnglishName(s.getEnglishName());
    			stu.setStars(s.getStars());
    			stu.setName(s.getSafeName());
    			stu.setToken(s.getToken());
    			stu.setAvatar(s.getAvatar());
    			stu.setFamilyId(s.getFamily().getId());
				if (null != s.getChannel()) {
					stu.setChannelId(s.getChannel().getId());
				}
				Long studentId = stu.getId();
				
				//保存token信息至 cookie, 记住登录状态, c_userId在跳转到学习端的时候用到
                Cookie userIdCookie = CookieUtils.createVIPKIDCookie("userId",String.valueOf(stu.getId()));
                Cookie userNameCookie = CookieUtils.createVIPKIDCookie("userName", URLEncoder.encode(stu.getName(), DEFAULT_CHARSET));
                Cookie studentIdCookie = CookieUtils.createVIPKIDCookie("studentId",String.valueOf(stu.getId()));
                Cookie userToken = CookieUtils.createVIPKIDCookie("userToken",parent.getToken());
    			
				userIdCookie.setMaxAge(Integer.MAX_VALUE);
				userNameCookie.setMaxAge(Integer.MAX_VALUE);
				studentIdCookie.setMaxAge(Integer.MAX_VALUE);
				userToken.setMaxAge(Integer.MAX_VALUE);
    			
    			response.addCookie(userIdCookie);
    			response.addCookie(userNameCookie);
    			response.addCookie(studentIdCookie);
    			response.addCookie(userToken);
    			
    			long medalCount = medalService.count(studentId);
    			
    			UserCacheUtil.storeCurrentUser(Long.toString(studentId), stu);
    			UserCacheUtil.storeMedalCount(studentId, medalCount);
    			
    			if(!StringUtils.isBlank(parent.getName())){
        			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
        		}else{
        			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
        		}
    			
    			if(UserCacheUtil.getValueFromCookie(request, "channel_id") != null){
    				Cookie cookie = CookieUtils.delVIPKIDCookie("channel_id");  
    		         response.addCookie(cookie);
    				return "openclass";
    			}
    			
    			return "welcome";
			}
			else{
				if(!StringUtils.isBlank(parent.getName())){
        			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getName());
        		}else{
        			UserCacheUtil.storeParentName(String.valueOf(parent.getId()), parent.getUsername());
        		}
				
				return "studentInfo";
			}	
		}
	}
	
	@RequestMapping(value = "/parent/getParentMobileByParentId", method = RequestMethod.GET)
	public  @ResponseBody String getParentMobileByParentId(@RequestParam("id") Long id){		
		Parent parent = parentService.find(id);
		if(parent != null){
			return parent.getMobile();
		}
		return "";
	}
	
	@RequestMapping(value = "/parent/checkShowWhichPage", method = RequestMethod.GET)
	public  @ResponseBody String checkShowWhichPage(@RequestParam("id") Long id){
		Parent parent = parentService.find(id);
		Family family = parent.getFamily();
		List<Student> studentList = studentService.findByFamilyId(family.getId());
		if(studentList.size() == 0){
			return "step1";
		}else{
			return "step2";
		}
	}
	
	@RequestMapping(value = "/parent/checkTrailClassByStudentId", method = RequestMethod.GET)
	public  @ResponseBody String checkTrailClass(@RequestParam("id") long username, HttpServletRequest request){
		String info = CookieUtils.get(request.getCookies(), "studentId");
		String[] arr = info.split(" ");
		String studentId = arr[1];
		
		Calendar startCalendar =  Calendar.getInstance();
//		startCalendar.set(2010, 1, 1);
		Calendar endCalendar =  Calendar.getInstance();
		int year = endCalendar.get(Calendar.YEAR);
		endCalendar.set(Calendar.YEAR,year+1);
		
		Date startDate = startCalendar.getTime();
		Date endDate = endCalendar.getTime();		
		
		OnlineClass onlineClass = onlineClassService.checkTrailClassByStudentId(Long.valueOf(studentId),startDate,endDate);
		if(onlineClass.getScheduledDateTime() != null){
			Date date = onlineClass.getScheduledDateTime();
			String param1 = DateFormat.getDateInstance(DateFormat.FULL).format(date);
			SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
			String param2 = format1.format(date);
			return param1+" "+param2;
		}
		return null;
	}

		
	
}
