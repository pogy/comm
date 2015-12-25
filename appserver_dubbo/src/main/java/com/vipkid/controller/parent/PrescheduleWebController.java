package com.vipkid.controller.parent;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.alibaba.fastjson.JSONObject;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherApplication;
import com.vipkid.redis.DistributedLock;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.CourseService;
import com.vipkid.service.LearningProgressService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherApplicationService;
import com.vipkid.service.TeacherService;
import com.vipkid.service.exception.ServiceException;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.parent.OnlineClassesView;
import com.vipkid.service.pojo.parent.TeView;
import com.vipkid.service.pojo.parent.TeachersView;

@Controller
public class PrescheduleWebController extends BaseWebController {
	public static final String PATH = "/parent/preschedule";

	@Resource
	private StudentService studentService;
	
	@Resource
	private TeacherService teacherService;
	
	@Resource
	private OnlineClassService onlineClassService;
	
	@Resource
	private CourseService courseService;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private LearningProgressService learningProgressService;
	
	@Resource
	private TeacherApplicationService teacherApplicationService;
	
    private Logger logger = LoggerFactory.getLogger(PrescheduleWebController.class.getSimpleName());
	
	@Override
	protected void registerViewController(	ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(PrescheduleWebController.PATH).setViewName(PrescheduleWebController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	/**
	 * 
	* @Title: init 
	* @Description: 预约课程 
	* @param mode (1 教师模式 2 日历模式)
	* @param seaType(-1搜索老师 按名称模糊查询 1 搜索老师 按teacherId 2 收藏老师 3 全部老师)
	* @param teacherId(seaType)
	* @param week (1 本周 2 下周)
	* @param day (日期不限 (周一 - 周日  ))
	* @param timeStart
	* @param timeEnd
	* @param courseType
	* @param isPageDo (1 翻页 ,-1 非翻页)
	* @author zhangfeipeng 
	* @return String
	* @throws
	 */
	@RequestMapping(value = PrescheduleWebController.PATH, method = RequestMethod.GET)
	public String init(Model model,
			HttpServletRequest request,
			Integer mode,
			Integer seaType,
			Long teacherId,
			Integer week,
			String day,
			String timeStart,
			String timeEnd,
			String courseType,
			Integer currNum,
			Integer isPageDo,
			Integer idx,
			String teacherName){
		model.addAttribute("name", "约课");
		model.addAttribute("path", PrescheduleWebController.PATH);
		
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
		
		//判断学生是否有LearningProgress
		boolean hasLearningProgress = true;
		LearningProgress learningProgress= learningProgressService.findByStudentIdAndCourseType(studentId);
		if(learningProgress==null){
			hasLearningProgress = false;
			model.addAttribute("noResultText1", "没有购买课程的小朋友");
			model.addAttribute("noResultText2", "还不能约课哦！");
		}else{
			model.addAttribute("noResultText1", "没有符合条件的可约老师时间");
			model.addAttribute("noResultText2", "换个条件试试？");
		}
		
		mode = mode==null?1:mode;
		seaType = seaType==null?3:seaType;
		teacherId = teacherId==null?-1l:teacherId;
		Date timeMondayPm=getMonedayPm();
		if(System.currentTimeMillis()>timeMondayPm.getTime()){//判断是否是周一上午
			week = week==null?2:week;
		}else{
			week = week==null?1:week;
		}
		idx = idx==null?0:idx;
		isPageDo = isPageDo==null?-1:isPageDo;
		try {
			teacherName = teacherName==null?"":java.net.URLDecoder.decode(teacherName, "UTF-8");
			day = day==null?"日期不限":java.net.URLDecoder.decode(day, "UTF-8");
			timeStart = timeStart==null?"起始时间":java.net.URLDecoder.decode(timeStart, "UTF-8");
			timeEnd = timeEnd==null?"截止时间":java.net.URLDecoder.decode(timeEnd, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		currNum = currNum==null?1:currNum;
		courseType = courseType==null?"MAJOR":courseType;
		if(mode==1){
			TeacherMode(model, seaType, teacherId, week, day, timeStart, timeEnd, courseType,studentId, currNum,isPageDo,teacherName,hasLearningProgress);
		}else{
			CalendarMode(model, seaType, teacherId, week, courseType,studentId,teacherName,hasLearningProgress);
		}
		model.addAttribute("mode", mode);
		model.addAttribute("week", week);
		model.addAttribute("day", day);
		model.addAttribute("timeStart", timeStart);
		model.addAttribute("timeEnd", timeEnd);
		model.addAttribute("courseType", courseType);
		model.addAttribute("currNum", currNum);
		model.addAttribute("studentId", studentId);
		model.addAttribute("idx", idx);
		if(mode==2){
			if(seaType==2){
				model.addAttribute("seaTypeName", "收藏老师");
			}else{
				model.addAttribute("seaTypeName", "所有老师");
			}
		}
		if(week==1){
			model.addAttribute("weekName", "本周");
		}else{
			model.addAttribute("weekName", "下周");
		}
		model.addAttribute("courseName", "VIPKID 美国小学课程");	
		String [] timeArrStart = {"起始时间","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30",
				"13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00",
				"17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30"};
		List<String>timeArrEnd = new ArrayList<String>();
		timeArrEnd.add("截止时间");
		if(StringUtils.isNotBlank(timeStart)&&!timeStart.equals("起始时间")){
			int j=ArrayUtils.indexOf(timeArrStart, timeStart);
			if(j!=-1){
				for (int i = j; i < timeArrStart.length; i++) {
					timeArrEnd.add(timeArrStart[i]);
				}
			}
		}
		model.addAttribute("timeArrStart", timeArrStart);
		model.addAttribute("timeArrEnd", timeArrEnd);
		String[] dayArr= teacherService.dayArr(week);
		model.addAttribute("dayArr", dayArr);
		return PrescheduleWebController.PATH;
	}

	private Date getMonedayPm() {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 
	* @Title: TeacherMode 
	* @Description: 教师模式处理逻辑
	* @author zhangfeipeng 
	* @return void
	* @throws
	 */
	public void TeacherMode(Model model,
			Integer seaType,
			Long teacherId,
			Integer week,
			String day,
			String timeStart,
			String timeEnd,
			String courseType,
			long studentId,
			Integer currNum,
			Integer isPageDo,
			String teacherName,
			boolean hasLearningProgress){
		List<TeachersView>teachersViews = new ArrayList<TeachersView>();
		long count = 0l;
		if(!(System.currentTimeMillis()<=getMonedayPm().getTime()&&week==2)){
			if(hasLearningProgress){//有课的话 才查询
				count = teacherService.countTeachersForPreschedule(seaType, teacherId, week, day, timeStart, timeEnd, courseType, studentId,teacherName);
				/*if(count<3&&seaType==2){//如果用户查询收藏老师 并且收藏老师结果小于3的话 自动改为查询所有老师
					seaType=3;
					count = teacherService.countTeachersForPreschedule(seaType, teacherId, week, day, timeStart, timeEnd, courseType, studentId,teacherName);
				}*/
				teachersViews =teacherService.listTeachersForPreschedule(seaType, teacherId, week, day, timeStart, timeEnd, courseType, studentId, currNum,teacherName);
			}
		}
		long totalPage =((count+5)-1)/5;
		if(seaType==2){
			model.addAttribute("seaTypeName", "收藏老师");
		}else{
			model.addAttribute("seaTypeName", "所有老师");
		}
		model.addAttribute("seaType", seaType);
		model.addAttribute("teachersViews", teachersViews);
		model.addAttribute("totalRecords", count);
		model.addAttribute("totalPage", totalPage);
		if(teachersViews.size()==0){
			model.addAttribute("noResult", "YES");
		}else{
			model.addAttribute("noResult", "NO");
		}
		if(teacherId!=-1&&seaType==1&&!teachersViews.isEmpty()){
			teacherName = teachersViews.get(0).getName();
		}
		if(teacherId==-1||isPageDo==1){
			if(!teachersViews.isEmpty()){
				teacherId = teachersViews.get(0).getTeacherId();
			}
		}
		model.addAttribute("teacherName", teacherName);
		model.addAttribute("teacherId", teacherId);
		OnlineClassesView[][] onlineClassViewsArr= teacherService.OnlineClassesViewArr(week);
		if(!(System.currentTimeMillis()<=getMonedayPm().getTime()&&week==2)){
			if(hasLearningProgress){
				onlineClassViewsArr = teacherService.findOnliclassByTeacherIdAndTime(teacherId, week, day, timeStart, timeEnd,studentId,courseType);
			}
		}
		model.addAttribute("onlineClassViewsArr", onlineClassViewsArr);
	}
	
	public void CalendarMode(Model model,
			Integer seaType,
			Long teacherId,
			Integer week,
			String courseType,
			long studentId,
			String teacherName,
			boolean hasLearningProgress){
		model.addAttribute("teachersViews", new ArrayList<TeachersView>());
		model.addAttribute("totalRecords", 0);
		model.addAttribute("totalPage", 0);
		model.addAttribute("teacherId", teacherId);
		OnlineClassesView[][] onlineClassViewsArr= teacherService.OnlineClassesViewArr(week);
		if(!(System.currentTimeMillis()<=getMonedayPm().getTime()&&week==2)){
			if(hasLearningProgress){
				onlineClassViewsArr= teacherService.findOnlineClassByTimeOrTeacherId(seaType,teacherId, week, studentId,courseType,teacherName);
			}
		}
		model.addAttribute("onlineClassViewsArr", onlineClassViewsArr);
		boolean flag = true;
		for (int i = 0; i < onlineClassViewsArr.length && flag; i++) {
			for (int j = 1; j < onlineClassViewsArr[i].length; j++) {
				if(onlineClassViewsArr[i][j].getId()>0){
					flag = false;
					break;
				}
			}
		}
		if(flag){
			model.addAttribute("noResult", "YES");
		}else{
			model.addAttribute("noResult", "NO");
		}
		model.addAttribute("seaType", seaType);
		model.addAttribute("teacherName", teacherName);
		
	}
	@RequestMapping(value = "/parent/book", method = RequestMethod.POST)
	@ResponseBody
	public String book(HttpServletRequest request,long onlineClassId,long oldOnlineClassId,String courseType){
		try {
			Date sctime = onlineClassService.findScheduledDateTimeById(onlineClassId);
			if(sctime!=null){
				if((sctime.getTime()-new Date().getTime())<24*60*60*1000){
					return "对不起，您不能预约24小时内的课程。";
				}
			}
			long studentId = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
//			Student student = (Student) UserCacheUtil.getUser(studentId);
			Student student = studentService.find(studentId);
			List<Student>students = new ArrayList<Student>();
			students.add(student);
			if(oldOnlineClassId!=0){
				String operation = "取消的onlineClassId="+oldOnlineClassId+",改约的onlineClassId="+onlineClassId+",studentId="+studentId;
				securityService.logAudit(Level.INFO, Category.BOOK_ONLINECLASS_AFTER_CANCEL_ONLINECLASS, operation.toString());
				OnlineClass onlineClassOld = onlineClassService.find(oldOnlineClassId);
				onlineClassOld.setCancelledStudents(students);
				if(onlineClassOld!=null){
					cancelOnlineClassAndDby(onlineClassOld);
				}
			}
			OnlineClass onlineClassNew = onlineClassService.find(onlineClassId);
			Course course = courseService.findByCourseType(Type.valueOf(courseType));
			if(onlineClassNew!=null){
				onlineClassNew.setCourse(course);
			}
			onlineClassNew.setStudents(students);
			OnlineClass bookedOnlineClass=onlineClassService.doBook(onlineClassNew);
			if (needCreateDBYClassroom(bookedOnlineClass)) {
				onlineClassService.doReScheduleDby(student, course, bookedOnlineClass, true);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			int status = e.getStatus();
			if (status == 620) {
				return "恭喜！您的孩子已经全部学完这门课程！如有剩余课时，请联系VIPKID，工作人员将协助您调换课程。";
			} else if (status == 621) {
				return "您的课时已用完，为不影响宝贝上课，请及时联系中教老师续费。";
			} else if (status == 624) {
				return "此课程已被其他人预约，请刷新页面后重新预约。";
			} else if (status == 625) {
				return "这个老师被约了，换个时间试试？";
			}else if(status == 622){
				return "此课时不存在或者已被取消，请刷新页面后重新预约。";
			} else {
				return "预约课程失败，如需帮助，请联系客服。";
			}
		}catch (Exception e){
			e.printStackTrace();
			return "预约课程失败，如需帮助，请联系客服。";
		}
		return "";
	}
	@RequestMapping(value = "/parent/cancelOnlineClass", method = RequestMethod.POST)
	@ResponseBody
	public String cancelOnlineClass(HttpServletRequest request,long oldOnlineClassId){
		try {
			long studentId = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
//			Student student = (Student) UserCacheUtil.getUser(studentId);
			Student student = studentService.find(studentId);
			List<Student>students = new ArrayList<Student>();
			students.add(student);
			OnlineClass onlineClassOld = onlineClassService.find(oldOnlineClassId);
			logger.info("New cancel request from operator={}, to cancel the onlineClass.id={} for the student={}, cancelledStudent={} ", securityService.getCurrentUser(), onlineClassOld.getId(), onlineClassOld.getStudentEnglishNames(), onlineClassOld.getCancelledStudentEnglishNames());
			onlineClassOld.setCancelledStudents(students);
			if(onlineClassOld!=null){
				cancelOnlineClassAndDby(onlineClassOld);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			return e.getMessage();
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}
	///recruitment/bookOnlineClassforRecruitment
	@RequestMapping(value = "/recruitment/bookOnlineClassforRecruitment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject bookForRecriutment(HttpServletRequest request, long onlineClassId, String courseType, long teacherId) {
		JSONObject json = new JSONObject();
		try {
			
			OnlineClass onlineClassNew = null;
			OnlineClass onlineClassOld = onlineClassService.find(onlineClassId);
			if(Status.BOOKED.equals(onlineClassOld.getStatus())){
				Date sctime = onlineClassService.findScheduledDateTimeById(onlineClassId);
				if(sctime!=null){
					DateParam dp = new DateParam(sctime.toString());
					List<OnlineClass> onlineClassList =  onlineClassService.findOpenTeacherRecruitmentByStartDateAndEndDate(dp, dp,courseType);
					if (onlineClassList == null || onlineClassList.size() == 0) {
						json.put("msg", "No more class time ,please book another time slot.");
						return json;
					}else{
						onlineClassNew = onlineClassList.get(0);
					}
				}
			}else{
				onlineClassNew = onlineClassOld;
			}
			
			onlineClassNew.setMinStudentNumber(0);
			onlineClassNew.setMaxStudentNumber(1);
			long time = onlineClassNew.getScheduledDateTime().getTime();
			Teacher teacher = teacherService.find(teacherId);
			Student student = null;
			if ("TEACHER_RECRUITMENT".equals(courseType)) {
				student = studentService.findAvailableInterviewStudent(teacher.getRealName(), teacher.getName());
			} else if ("PRACTICUM".equals(courseType)) {
				student = studentService.findAvailablePracticumStudent(teacher.getRealName(), teacher.getName());
			}
			
 			List<Student> students = new ArrayList<Student>();
			students.add(student);

			Course course = courseService.findByCourseType(Type.valueOf(courseType));
			if (onlineClassNew != null) {
				onlineClassNew.setCourse(course);
			}
			onlineClassNew.setStudents(students);
			OnlineClass bookedOnlineClass = onlineClassService.doBookForRecruitment(onlineClassNew);
			if (needCreateDBYClassroom(bookedOnlineClass)) {
				onlineClassService.doReScheduleDby(student, course, bookedOnlineClass, true);
			}
			onlineClassNew.setStudents(students);
			// 创建application
			TeacherApplication teacherApplication = new TeacherApplication();
			

			teacherApplication.setTeacher(teacher);
			teacherApplication.setStudent(student);
			teacherApplication.setOnlineClass(onlineClassNew);
			if ("TEACHER_RECRUITMENT".equals(courseType)) {
				teacherApplication.setStatus(TeacherApplication.Status.INTERVIEW);
			} else if (courseType.startsWith("PRACTICUM")) {
				teacherApplication.setStatus(TeacherApplication.Status.PRACTICUM);
			}
			teacherApplicationService.doApplyForRecritment(teacherApplication);

		} catch (ServiceException e) {
			e.printStackTrace();
			int status = e.getStatus();
			json.put("msg", "Create fail get service error ,please contact  customer  service if you need."+e.getMessage());

			return json;

		} catch (Exception e) {
			e.printStackTrace();
			json.put("msg", "Create fail ,please contact  customer  service if you need."+e.getMessage());
			return json;
		}
		json.put("msg", "Success");
		return json;
	}
	
	@RequestMapping(value = "/recruitment/cancelOnlineClassforRecruitment", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject cancelOnlineClassForRecriutment(HttpServletRequest request,long onlineClassId, String courseType,long teacherId){
		JSONObject json = new JSONObject();
		try {
			TeacherApplication teacherApplication = teacherApplicationService.findCurrentByTeacherId(teacherId);
			teacherApplication.setOnlineClass(null);
			teacherApplication.setStudent(null);
			teacherApplicationService.update(teacherApplication);
					
			OnlineClass onlineClassOld = onlineClassService.find(onlineClassId);
			long time = onlineClassOld.getScheduledDateTime().getTime();
			Teacher teacher = teacherService.find(teacherId);
			Student student = null;			
			
			if ("TEACHER_RECRUITMENT".equals(courseType)) {
				student = studentService.findAvailableInterviewStudent(teacher.getRealName(), teacher.getName());
			} else if ("PRACTICUM".equals(courseType)) {
				student = studentService.findAvailablePracticumStudent(teacher.getRealName(), teacher.getName());
			}
			List<Student> students = new ArrayList<Student>();
			students.add(student);
			logger.info("New cancel request from operator={}, to cancel the onlineClass.id={} for the student={}, cancelledStudent={} ", securityService.getCurrentUser(), onlineClassOld.getId(), onlineClassOld.getStudentEnglishNames(), onlineClassOld.getCancelledStudentEnglishNames());
			onlineClassOld.setCancelledStudents(students);
			onlineClassOld.setStudents(students);
			OnlineClass onlineClassnew = onlineClassService.doCancel(onlineClassOld);
			if(onlineClassOld!=null){
				cancelOnlineClassAndDby(onlineClassOld);
			}
			
			
		} catch (ServiceException e) {
			int status = e.getStatus();
			e.printStackTrace();
			json.put("msg", "Cancel fail ,please contact  customer  service if you need.");

			return json;
		}catch (Exception e){
			e.printStackTrace();
			json.put("msg", "Cancel fail ,please contact  customer  service if you need.");
			return json;
		}
		json.put("msg", "Success");
		return json;
	}
	
	@RequestMapping(value = "/mobile/bookOnlineClassformobile", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject bookformobile(HttpServletRequest request,long onlineClassId,long oldOnlineClassId,Long studentId){
		JSONObject json = new JSONObject();
		try {
			Date sctime = onlineClassService.findScheduledDateTimeById(onlineClassId);
			if(sctime!=null){
				if((sctime.getTime()-new Date().getTime())<24*60*60*1000){
					json.put("msg","对不起，您不能预约24小时内的课程。");
					return json;
				}
			}
			
			Student student = studentService.find(studentId);
			List<Student>students = new ArrayList<Student>();
			students.add(student);
			
			OnlineClass bookedOnlineClass= null;
			if(oldOnlineClassId!=0){
				if(canGetOldOnlineClassSwitchLocks(oldOnlineClassId)){
					OnlineClass onlineClassOld = onlineClassService.find(oldOnlineClassId);
					if(onlineClassOld==null||onlineClassOld.getStatus()!=OnlineClass.Status.BOOKED){
						json.put("msg","对不起，您的课程已经被更换。");
						return json;
					}
					OnlineClass onlineClassNew = onlineClassService.find(onlineClassId);
					Course course = courseService.findByCourseType(Type.MAJOR);
					if(onlineClassNew!=null){
						onlineClassNew.setCourse(course);
					}
					onlineClassService.doBookSwitch(onlineClassNew,oldOnlineClassId);
					
				}
				
			}else{
				OnlineClass onlineClassNew = onlineClassService.find(onlineClassId);
				Course course = courseService.findByCourseType(Type.MAJOR);
				if(onlineClassNew!=null){
					onlineClassNew.setCourse(course);
				}
				onlineClassNew.setStudents(students);
				bookedOnlineClass= onlineClassService.doBook(onlineClassNew);
				if (bookedOnlineClass!=null && needCreateDBYClassroom(bookedOnlineClass)) {
					onlineClassService.doReScheduleDby(student, course, bookedOnlineClass, true);
				}
			}
			
		} catch (ServiceException e) {
			e.printStackTrace();
			int status = e.getStatus();
			if (status == 620) {
				json.put("msg","恭喜！您的孩子已经全部学完这门课程！如有剩余课时，请联系VIPKID，工作人员将协助您调换课程。");
				return json;
			} else if (status == 621) {
				json.put("msg","您的课时已用完，为不影响宝贝上课，请及时联系中教老师续费。");
				return json;
			} else if (status == 624) {
				json.put("msg","此课程已被其他人预约，请刷新页面后重新预约。");
				return json;
			} else if (status == 625) {
				json.put("msg","这个老师被约了，换个时间试试？");
				return json;
			}else if(status == 622){
				json.put("msg","此课时不存在或者已被取消，请刷新页面后重新预约。");
				return json;
			} else {
				json.put("msg","预约课程失败，如需帮助，请联系客服。");
				return json;
			}
		}catch (Exception e){
			logger.error("预约课程失败={}", e.getMessage(), e);
			json.put("msg","预约课程失败，如需帮助，请联系客服。");
			return json;
		}finally{
			releaseLocks(oldOnlineClassId);
		}
		json.put("msg","");
		return json;
	}

	/*private void switchOnlineclass(long onlineClassId, long oldOnlineClassId,
			Long studentId, Student student, List<Student> students,
			OnlineClass onlineClassNew, Course course,
			OnlineClass onlineClassOld) {
		OnlineClass bookedOnlineClass=null;
		bookedOnlineClass= onlineClassService.doBookSwitch(onlineClassNew);
		if (bookedOnlineClass!=null && needCreateDBYClassroom(bookedOnlineClass)) {
			onlineClassService.doReScheduleDby(student, course, bookedOnlineClass, true);
		}
		if(bookedOnlineClass!=null){
			String operation = "取消的onlineClassId="+oldOnlineClassId+",改约的onlineClassId="+onlineClassId+",studentId="+studentId;
			securityService.logAudit(Level.INFO, Category.BOOK_ONLINECLASS_AFTER_CANCEL_ONLINECLASS, operation.toString());
			onlineClassOld.setCancelledStudents(students);
			if(onlineClassOld!=null){
				cancelOnlineClassAndDbyForSwitch(onlineClassOld);
			}
		}
	}*/
	
	@RequestMapping(value = "/mobile/cancelOnlineClassformobile", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject cancelOnlineClassformobile(HttpServletRequest request,long oldOnlineClassId,Long studentId){
		JSONObject json = new JSONObject();
		try {
			Student student = studentService.find(studentId);
			List<Student>students = new ArrayList<Student>();
			students.add(student);
			OnlineClass onlineClassOld = onlineClassService.find(oldOnlineClassId);
			logger.info("New cancel request from operator={}, to cancel the onlineClass.id={} for the student={}, cancelledStudent={} ", securityService.getCurrentUser(), onlineClassOld.getId(), onlineClassOld.getStudentEnglishNames(), onlineClassOld.getCancelledStudentEnglishNames());
			onlineClassOld.setCancelledStudents(students);
			if(onlineClassOld!=null){
				cancelOnlineClassAndDby(onlineClassOld);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			json.put("msg", e.getMessage());
			return json;
		}catch (Exception e){
			e.printStackTrace();
		}
		json.put("msg", "");
		return json;
	}

	private void cancelOnlineClassAndDby(OnlineClass onlineClassOld) {
		OnlineClass cancelOnlineClass = onlineClassService.doCancel(onlineClassOld);
		List<Student> cancelledStudents = cancelOnlineClass.getCancelledStudents();
		if(cancelOnlineClass.getLesson()!=null && cancelOnlineClass.getStatus()!=Status.OPEN){
			Course course = cancelOnlineClass.getLesson().getLearningCycle().getUnit().getCourse();
			if (course.isSequential()) {
				for (Student tStudent : cancelledStudents) {
					onlineClassService.doReScheduleDby(tStudent, course, cancelOnlineClass, false);
				}
			}
		}
	}
	
	private void cancelOnlineClassAndDbyForSwitch(OnlineClass onlineClassOld) {
		onlineClassService.doCancel(onlineClassOld);
		/*List<Student> cancelledStudents = cancelOnlineClass.getCancelledStudents();
		if(cancelOnlineClass.getLesson()!=null && cancelOnlineClass.getStatus()!=Status.OPEN){
			Course course = cancelOnlineClass.getLesson().getLearningCycle().getUnit().getCourse();
			if (course.isSequential()) {
				for (Student tStudent : cancelledStudents) {
					onlineClassService.doReScheduleDbyWithSwitch(tStudent, course, cancelOnlineClass, false);
				}
			}
		}*/
	}
	
	@RequestMapping(value = "/parent/findTeacherForCal", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject findTeacherForCal(HttpServletRequest request,String scheduledDateTime,int seaType,long teacherId,String courseType,Integer currNum,String teacherName){
		JSONObject jsonView = new JSONObject();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			currNum = currNum==null?1:currNum;
			long studentId = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
			List<TeachersView> teachersViews = teacherService.listTeacherForCal(df.parse(scheduledDateTime), seaType, teacherId, studentId, courseType, currNum,teacherName);
			Long count = teacherService.counrTeacherForCal(df.parse(scheduledDateTime), seaType, teacherId, studentId, courseType,teacherName);
			long totalPage =((count+6)-1)/6;
			/*JSONArray json = new JSONArray();
			json.add(teachersViews);*/
			
			jsonView.put("teachersViews", teachersViews);
			jsonView.put("tabTotalPage", totalPage);
			jsonView.put("tabCurrNum", currNum);
			jsonView.put("pageText", currNum+"/"+totalPage);
			jsonView.put("studentId", studentId);
			jsonView.put("message","");
			return jsonView;
			
		} catch (ServiceException e) {
			e.printStackTrace();
			jsonView.put("message", e.getMessage());
			return jsonView;
		}catch (Exception e){
			e.printStackTrace();
		}
		return jsonView;
	}
	
	@RequestMapping(value = "/parent/listTeachersView", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject listTeachersView(String courseType,String keyword){
		JSONObject json = new JSONObject();
		List<TeView>tList = teacherService.listTeachersView(courseType,keyword);
		json.put("data", tList);
		return json;
	}
	
	@RequestMapping(value = "/parent/doCollect", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject doCollect(HttpServletRequest request,long teacherId){
		JSONObject json = new JSONObject();
		try {
			long studentId = Long.valueOf(UserCacheUtil.getValueFromCookie(request, "studentId").toString()) ;
			String message = studentService.doCollect(teacherId, studentId);
			json.put("message",message);
			json.put("errMessage","");
		} catch (ServiceException e) {
			e.printStackTrace();
			json.put("errMessage", e.getMessage());
			return json;
		}
		return json;
	}
	
	private boolean needCreateDBYClassroom(final OnlineClass onlineClass){
		if (onlineClass.getStatus() == Status.BOOKED && 
				(onlineClass.getCourse().getMode() == Mode.ONE_ON_ONE
				|| onlineClass.getCourse().getType() == Type.TEACHER_RECRUITMENT
				|| onlineClass.getCourse().getType() == Type.PRACTICUM)){
			return true;
		}
		return false;
	}
	
	private boolean canGetOldOnlineClassSwitchLocks(final long olaOnlineclassId){
		boolean lockResult = canGetLockWithOldOnlineClassIdAndTime(olaOnlineclassId);
		return lockResult;
	}
	
	private boolean canGetLockWithOldOnlineClassIdAndTime(final long olaOnlineclassId){
		return DistributedLock.lock("old"+"_"+olaOnlineclassId+"_"+System.currentTimeMillis());
	}
	
	private void releaseLocks(final long olaOnlineclassId){
		DistributedLock.unlock("old"+"_"+olaOnlineclassId+"_"+System.currentTimeMillis());
	}
	
}
