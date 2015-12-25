package com.vipkid.controller.mobile;

import java.util.Calendar;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.context.AppContext;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.model.Gender;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.model.User;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.security.CustomizedPrincipal;
import com.vipkid.service.ParentService;
import com.vipkid.service.StudentService;
@Controller
public class ChildrenInfoMobileController extends BaseWebController {
	public static final String PATH = "/mobile/childreninfo";
	
	private Logger logger = LoggerFactory.getLogger(SetPasswordMobileController.class.getSimpleName());
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private ParentService parentService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ChildrenInfoMobileController.PATH).setViewName(ChildrenInfoMobileController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = ChildrenInfoMobileController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request){
		return ChildrenInfoMobileController.PATH;
	}
	
	@RequestMapping(value = ChildrenInfoMobileController.PATH + "/save", method = RequestMethod.POST)
	public String save(@RequestParam("englishName") String englishName,
			@RequestParam("chineseName") String chineseName, 
			@RequestParam("gender") String gender,
			@RequestParam("birthday") String birthDay,
			Model model, HttpServletRequest request){
		logger.debug("create child with englishName: {}, chineseName: {}, gender: {}, birthday: {}", englishName, chineseName, gender, birthDay);
		
		Student student = new Student();
		student.setEnglishName(englishName);
		student.setName(chineseName);
		student.setGender(gender.equals("girl")?Gender.FEMALE:Gender.MALE);
		String [] dateElement = birthDay.split("-");
		int year = Integer.parseInt(dateElement[0]);
		int month = Integer.parseInt(dateElement[1]);
		int day = Integer.parseInt(dateElement[2]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		student.setBirthday(calendar.getTime());
		student.setSource(Student.Source.WEIXIN);
		
		try {
			User parent = ((CustomizedPrincipal)AppContext.getPrincipal()).getUser();
			Parent inDbParent = parentService.find(parent.getId());
			student.setFamily(inDbParent.getFamily());
			Student returnStudent = studentService.create(student, parent);
			if (returnStudent != null) {
				leadsQueueSender.sendText(String.valueOf(returnStudent.getId()));
			}
		} catch (NullPointerException e) {
			Student returnStudent = studentService.create(student);
			if (returnStudent != null) {
				leadsQueueSender.sendText(String.valueOf(returnStudent.getId()));
			}
		}
		
		return Page.redirectTo("http://www.sina.com.cn");
	}
}
