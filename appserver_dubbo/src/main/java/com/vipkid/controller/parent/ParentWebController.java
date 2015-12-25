package com.vipkid.controller.parent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.parent.model.LessonVO;
import com.vipkid.controller.parent.model.ScheduleTable;
import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.controller.parent.model.Week;
import com.vipkid.controller.util.DateUtils;
import com.vipkid.controller.util.FieldTranslate;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Channel;
import com.vipkid.model.Course.Type;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.LearningProgress.Status;
import com.vipkid.model.Course;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Student;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.service.ChannelService;
import com.vipkid.service.LearningProgressService;
import com.vipkid.service.StudentExamService;
import com.vipkid.service.StudentService;
import com.vipkid.service.TeacherService;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.parent.LessonsView;
import com.vipkid.service.pojo.parent.TeachersView;
import com.vipkid.util.DateTimeUtils;

@Controller
public class ParentWebController extends BaseWebController{
	private Logger logger = LoggerFactory
			.getLogger(ParentWebController.class.getSimpleName());
	
	public static final String PATH = "/parent/home";
	
	@Resource
	private LearningProgressService learningProgressService;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private StudentService studentService;
	
	@Resource
	private TeacherRepository teacherRepository;
	
	@Resource
	private TeacherService teacherService;
	
	@Resource
	private StudentExamService studentExamService;
	
	@Autowired
	private ChannelService channelService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ParentWebController.PATH).setViewName(ParentWebController.PATH);
	}
	
	/**
	 * 首页初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = ParentWebController.PATH, method = RequestMethod.GET)
	public String init(Model model, HttpServletRequest request, @RequestParam(value="whichWeek", required=false)Integer whichWeek){
		model.addAttribute("path", ParentWebController.PATH);
		
		Object sid = UserCacheUtil.getValueFromCookie(request, "studentId");
		if(UserCacheUtil.hasLogin(request) == false || sid == null){
			return "redirect:/login";
		}
		initCommon(model, request);
		long studentId = Long.parseLong(sid.toString());
		StudentVO student = UserCacheUtil.getCurrentUser(studentId);
		if(student == null){
			return "redirect:/login";
		}
		
		//当前等级 & 距离下级还需N节课
		List<LearningProgress> lpes = learningProgressService.findByStudentId(studentId);
		//主修课的LearningProgress
		LearningProgress major = null;
		//剩余课
		List<LearningProgress> restLearningProgress = new ArrayList<LearningProgress>();
		//剩余课时
		float restClassHour = 0;
		//总课时
		float totalClassHour = 0;
		for(LearningProgress lp : lpes){
			if(lp.getCourse().getType() == Type.MAJOR){
				major = lp;
			}
			if(lp.getStatus() == Status.STARTED){
				restLearningProgress.add(lp);
				restClassHour += lp.getLeftClassHour();
			}
			totalClassHour += lp.getTotalClassHour();
		}
		if(major != null){
			Lesson nextLesson = major.getNextShouldTakeLesson();
			if(nextLesson != null){
				Level level = nextLesson.getLearningCycle().getUnit().getLevel();
				String levelString = level.toString();
				int underScoreIndex = levelString.indexOf('_');
				String levelNumber = levelString.substring(underScoreIndex + 1);
				levelString = "Level " + levelNumber; 
				
				model.addAttribute("currentLevel", levelString);
				model.addAttribute("nextLevelLessons", classHoursToNextLevel(nextLesson.getSequence()) + "节课");
			}else{
				model.addAttribute("currentLevel", "-");
				model.addAttribute("nextLevelLessons", "-");
			}
		}else{
			model.addAttribute("currentLevel", "-");
			model.addAttribute("nextLevelLessons", "-");
		}
		
		//剩余课时
		model.addAttribute("restClassHour", new Float(restClassHour).intValue());
		model.addAttribute("restLearningProgress", restLearningProgress);
		//已完成课时
		int completedClassHour = new Float(totalClassHour - restClassHour).intValue();
		model.addAttribute("completedClassHour", completedClassHour);
		//model.addAttribute("completedClassHour", 0);
		//周目标完成
		long target = onlineClassRepository.countByStudentIdAndFinishedAsScheduledAndThisWeek(studentId);
		model.addAttribute("completedTarget", target + "/" + student.getTargetClassesPerWeek());
		//累计说英语(小时)
		if (completedClassHour < 40) {
			model.addAttribute("completedHour", completedClassHour*25 + "分钟");
		} else {
			float completedHour = (totalClassHour - restClassHour) * 25 / 60;
			if (completedHour*10 % 10 > 0) {
				model.addAttribute("completedHour", (float)(Math.round(completedHour*10))/10 + "小时");
			} else {
				model.addAttribute("completedHour", (int)completedHour + "小时");
			}
		}
		
		Week week = null;
		if(whichWeek == null){
			whichWeek = 0;
		}
		if(whichWeek == -1){
			week = DateUtils.getLaskWeek();
		}else if(whichWeek == 0){
			week = DateUtils.getThisWeek();
		}else if(whichWeek == 1){
			week = DateUtils.getNextWeek();
		}else{
			week = DateUtils.getThisWeek();
		}
		model.addAttribute("week", week);
		model.addAttribute("whichWeek", whichWeek);
		
		//我的课表
		ScheduleTable table = findOnlineClassByStudentId(studentId, whichWeek);
		model.addAttribute("scheduleTable", table);
		
		//有没有约Trail课
		boolean hasTrail = false;
		long lessonCount = onlineClassRepository.countByStudentId(studentId);
		if(lessonCount > 0){
			hasTrail = true;
		}
		model.addAttribute("hasTrail", hasTrail);
		
		//最后一次学习进度
		List<LessonsView> lessonsViews = onlineClassRepository.listForLessons(studentId);
		//LessonsView bookedLessonTimeout = onlineClassRepository.getNearestLessons(studentId);
		
		LessonsView latestOnlineClass = null;
		if(lessonsViews != null && lessonsViews.size() > 0){
			latestOnlineClass = lessonsViews.get(0);
			latestOnlineClass.setFinishType(FieldTranslate.getOnlineClass_Status(latestOnlineClass.getFinishType()));
			String level = latestOnlineClass.getLevel().replace("LEVEL_", "Level ");
			latestOnlineClass.setLevel(level);
			if(latestOnlineClass.getFinishType().equals("生成中...")){
				latestOnlineClass.setOnlineClassStatus("BOOKED");
			}else{
				latestOnlineClass.setOnlineClassStatus("FINISHED");
			}
		}
		
		model.addAttribute("latestOnlineClass", latestOnlineClass);
		
		//我收藏的老师
		List<TeachersView> teachersViews = teacherService.listTeachers(studentId);
		model.addAttribute("teachersViews2", teachersViews);
		
		// 水平测试是否进行
		boolean bAllowGuide = isAllowStudentLevelGuid(student);
		//bAllowGuide = true;
		if (bAllowGuide) {
			model.addAttribute("showLevelExamGuide",true);
		} else {
			model.addAttribute("showLevelExamGuide",false);
		}
		
		// 更新水平测试引导设置
		updateStudentLevelExamGuide(studentId);
		
		
		return ParentWebController.PATH;
	}
	
	private void updateStudentLevelExamGuide(long studentId) {
		// 
		try {
			Student student = studentService.find(studentId);
			if (0 == student.getGuideToLevelExam()) {
				student.setGuideToLevelExam(1);
				// F*ck the channel null ---2015-09-14
				if (null == student.getChannel()) {
					Channel channel = channelService.getDefaultChannel();
					student.setChannel(channel);
				}
				studentService.update(student);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		//
		StudentVO svo = UserCacheUtil.getCurrentUser(studentId);
		if (0 == svo.getGuideToLevelExam()) {
			svo.setGuideToLevelExam(1);
			String strId = new Long(studentId).toString();
			UserCacheUtil.storeCurrentUser(strId, svo);
		}
		
	}

	/**
	 * history: 2015-09-08 
	 * 判断该学生是否需要进行水平测试引导
	 * @param student
	 * @return
	 */
	private boolean isAllowStudentLevelGuid(StudentVO student) {
		boolean bAllow = false;
		
		if (1 == student.getGuideToLevelExam()) {
			return false;
		}
		// channel 
		long channelId = student.getChannelId();
		
		// 156 --本网站自然流量进入客户
		final long [] kAllowChannelId = {156};
		
		boolean bChannleOK = false;
		for (int nIndex = 0; nIndex < kAllowChannelId.length; nIndex++) {
			if (kAllowChannelId[nIndex] == channelId) {
				bChannleOK = true;
				break;
			}
		}
		
		if (false == bChannleOK) {
			return bAllow;
		}
		
		//		
		// 未level exam
		long studentId = student.getId();
		Count count = studentExamService.count(studentId);
		long len = count.getTotal();
		if (len<1) {
			bAllow = true;
		}
		
		return bAllow;
	}
	
	//计算离下一个level还有多少课时(只针对主修课)
	private int classHoursToNextLevel(int sequence){
		//因为Level 1是13个unit(156个lesson),其他level都是12个unit,所以level1要特殊考虑
		int count = 0;
		if(sequence <= 156 && sequence > 0){
			count = 156 - sequence;
		}else if(sequence > 156){
			count = 144 - ((sequence - 156) % 144);
		}else{
			count = 0;
		}
		return count;
	}
	
	//获取学生的课表信息
	private ScheduleTable findOnlineClassByStudentId(long studentId, int whichWeek){
		Date start;
		Date end;
		if(whichWeek == -1){
			start = DateTimeUtils.getLastMonday();
			end = DateTimeUtils.getThisMonday();
		}else if(whichWeek == 0){
			start = DateTimeUtils.getThisMonday();
			end = DateTimeUtils.getNextMonday();
		}else if(whichWeek == 1){
			start = DateTimeUtils.getNextMonday();
			end = DateTimeUtils.getNextNextMonday();
		}else{
			start = DateTimeUtils.getThisMonday();
			end = DateTimeUtils.getNextMonday();
		}
		
		ScheduleTable table = new ScheduleTable();
		
		List<OnlineClass> list = onlineClassRepository.findByStudentIdAndStartDateAndEndDate(studentId, start, end);
		table.setWhichWeek(whichWeek);
		if(null != list && list.size()>0){
			table.setHasLesson(true);
		}else{
			table.setHasLesson(false);
		}
		
		Date monday = start;
		Date tuesday = DateTimeUtils.getNextDay(monday);
		Date wednesday = DateTimeUtils.getNextDay(tuesday);
		Date thursday = DateTimeUtils.getNextDay(wednesday);
		Date friday = DateTimeUtils.getNextDay(thursday);
		Date saturday = DateTimeUtils.getNextDay(friday);
		Date sunday = DateTimeUtils.getNextDay(saturday);
		
		Date monday_am_from = DateTimeUtils.getNthMinutesLater(monday, 9*60);
		Date monday_am_to = DateTimeUtils.getNthMinutesLater(monday, 12*60-1);
		Date monday_pm_from = DateTimeUtils.getNthMinutesLater(monday, 12*60);
		Date monday_pm_to = DateTimeUtils.getNthMinutesLater(monday, 17*60-1);
		Date monday_night_from = DateTimeUtils.getNthMinutesLater(monday, 17*60);
		Date monday_night_to = DateTimeUtils.getNthMinutesLater(monday, 22*60-1);
		
		Date tuesday_am_from = DateTimeUtils.getNthMinutesLater(tuesday, 9*60);
		Date tuesday_am_to = DateTimeUtils.getNthMinutesLater(tuesday, 12*60-1);
		Date tuesday_pm_from = DateTimeUtils.getNthMinutesLater(tuesday, 12*60);
		Date tuesday_pm_to = DateTimeUtils.getNthMinutesLater(tuesday, 17*60-1);
		Date tuesday_night_from = DateTimeUtils.getNthMinutesLater(tuesday, 17*60);
		Date tuesday_night_to = DateTimeUtils.getNthMinutesLater(tuesday, 22*60-1);
		
		Date wednesday_am_from = DateTimeUtils.getNthMinutesLater(wednesday, 9*60);
		Date wednesday_am_to = DateTimeUtils.getNthMinutesLater(wednesday, 12*60-1);
		Date wednesday_pm_from = DateTimeUtils.getNthMinutesLater(wednesday, 12*60);
		Date wednesday_pm_to = DateTimeUtils.getNthMinutesLater(wednesday, 17*60-1);
		Date wednesday_night_from = DateTimeUtils.getNthMinutesLater(wednesday, 17*60);
		Date wednesday_night_to = DateTimeUtils.getNthMinutesLater(wednesday, 22*60-1);
		
		Date thursday_am_from = DateTimeUtils.getNthMinutesLater(thursday, 9*60);
		Date thursday_am_to = DateTimeUtils.getNthMinutesLater(thursday, 12*60-1);
		Date thursday_pm_from = DateTimeUtils.getNthMinutesLater(thursday, 12*60);
		Date thursday_pm_to = DateTimeUtils.getNthMinutesLater(thursday, 17*60-1);
		Date thursday_night_from = DateTimeUtils.getNthMinutesLater(thursday, 17*60);
		Date thursday_night_to = DateTimeUtils.getNthMinutesLater(thursday, 22*60-1);
		
		Date friday_am_from = DateTimeUtils.getNthMinutesLater(friday, 9*60);
		Date friday_am_to = DateTimeUtils.getNthMinutesLater(friday, 12*60-1);
		Date friday_pm_from = DateTimeUtils.getNthMinutesLater(friday, 12*60);
		Date friday_pm_to = DateTimeUtils.getNthMinutesLater(friday, 17*60-1);
		Date friday_night_from = DateTimeUtils.getNthMinutesLater(friday, 17*60);
		Date friday_night_to = DateTimeUtils.getNthMinutesLater(friday, 22*60-1);
		
		Date saturday_am_from = DateTimeUtils.getNthMinutesLater(saturday, 9*60);
		Date saturday_am_to = DateTimeUtils.getNthMinutesLater(saturday, 12*60-1);
		Date saturday_pm_from = DateTimeUtils.getNthMinutesLater(saturday, 12*60);
		Date saturday_pm_to = DateTimeUtils.getNthMinutesLater(saturday, 17*60-1);
		Date saturday_night_from = DateTimeUtils.getNthMinutesLater(saturday, 17*60);
		Date saturday_night_to = DateTimeUtils.getNthMinutesLater(saturday, 22*60-1);
		
		Date sunday_am_from = DateTimeUtils.getNthMinutesLater(sunday, 9*60);
		Date sunday_am_to = DateTimeUtils.getNthMinutesLater(sunday, 12*60-1);
		Date sunday_pm_from = DateTimeUtils.getNthMinutesLater(sunday, 12*60);
		Date sunday_pm_to = DateTimeUtils.getNthMinutesLater(sunday, 17*60-1);
		Date sunday_night_from = DateTimeUtils.getNthMinutesLater(sunday, 17*60);
		Date sunday_night_to = DateTimeUtils.getNthMinutesLater(sunday, 22*60-1);
		
		for (OnlineClass oc : list){
			//不显示公开课
			if(oc.getLesson()==null||oc.getLesson().getLearningCycle().getUnit().getCourse().getMode() == Course.Mode.ONE_TO_MANY){
				continue;
			}
			
			Date scheduleDate = new Date();
			long dat = oc.getScheduledDateTime().getTime() + 60*1000;
			scheduleDate.setTime(dat);
			LessonVO vo = new LessonVO();
			vo.setId(oc.getId());
			vo.setLearned(oc.getStatus() == com.vipkid.model.OnlineClass.Status.FINISHED ? true : false);
			vo.setName(oc.getTeacher().getName());
			vo.setTime(DateTimeUtils.format(oc.getScheduledDateTime(),DateTimeUtils.TIME_FORMAT));
			vo.setFullTime(DateTimeUtils.format(oc.getScheduledDateTime(),DateTimeUtils.DATETIME_FORMAT2));
			try {
				String level = oc.getLesson().getLearningCycle().getUnit().getLevel().toString();
				String level2 = level.replace("LEVEL_", "Level ");
				String unitName = oc.getLesson().getLearningCycle().getUnit().getNumber();
				String lessonName = oc.getLesson().getName();
				StringBuffer ocname = new StringBuffer(level2);
				ocname.append("-");
				if(null != unitName){
					ocname.append(unitName);
					ocname.append("-");
				}
				ocname.append(lessonName);
				vo.setClassName(ocname.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			List<LessonVO> lessonList = null;
			if(scheduleDate.after(monday_am_from) && scheduleDate.before(monday_am_to)){
				if(table.getR1c1s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c1s();
				}
				lessonList.add(vo);
				table.setR1c1s(lessonList);
			}else if(scheduleDate.after(monday_pm_from) && scheduleDate.before(monday_pm_to)){
				if(table.getR2c1s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c1s();
				}
				lessonList.add(vo);
				table.setR2c1s(lessonList);
			}else if(scheduleDate.after(monday_night_from) && scheduleDate.before(monday_night_to)){
				if(table.getR3c1s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c1s();
				}
				lessonList.add(vo);
				table.setR3c1s(lessonList);
			}else if(scheduleDate.after(tuesday_am_from) && scheduleDate.before(tuesday_am_to)){
				if(table.getR1c2s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c2s();
				}
				lessonList.add(vo);
				table.setR1c2s(lessonList);
			}else if(scheduleDate.after(tuesday_pm_from) && scheduleDate.before(tuesday_pm_to)){
				if(table.getR2c2s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c2s();
				}
				lessonList.add(vo);
				table.setR2c2s(lessonList);
			}else if(scheduleDate.after(tuesday_night_from) && scheduleDate.before(tuesday_night_to)){
				if(table.getR3c2s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c2s();
				}
				lessonList.add(vo);
				table.setR3c2s(lessonList);
			}else if(scheduleDate.after(wednesday_am_from) && scheduleDate.before(wednesday_am_to)){
				if(table.getR1c3s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c3s();
				}
				lessonList.add(vo);
				table.setR1c3s(lessonList);
			}else if(scheduleDate.after(wednesday_pm_from) && scheduleDate.before(wednesday_pm_to)){
				if(table.getR2c3s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c3s();
				}
				lessonList.add(vo);
				table.setR2c3s(lessonList);
			}else if(scheduleDate.after(wednesday_night_from) && scheduleDate.before(wednesday_night_to)){
				if(table.getR3c3s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c3s();
				}
				lessonList.add(vo);
				table.setR3c3s(lessonList);
			}else if(scheduleDate.after(thursday_am_from) && scheduleDate.before(thursday_am_to)){
				if(table.getR1c4s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c4s();
				}
				lessonList.add(vo);
				table.setR1c4s(lessonList);
			}else if(scheduleDate.after(thursday_pm_from) && scheduleDate.before(thursday_pm_to)){
				if(table.getR2c4s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c4s();
				}
				lessonList.add(vo);
				table.setR2c4s(lessonList);
			}else if(scheduleDate.after(thursday_night_from) && scheduleDate.before(thursday_night_to)){
				if(table.getR3c4s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c4s();
				}
				lessonList.add(vo);
				table.setR3c4s(lessonList);
			}else if(scheduleDate.after(friday_am_from) && scheduleDate.before(friday_am_to)){
				if(table.getR1c5s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c5s();
				}
				lessonList.add(vo);
				table.setR1c5s(lessonList);
			}else if(scheduleDate.after(friday_pm_from) && scheduleDate.before(friday_pm_to)){
				if(table.getR2c5s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c5s();
				}
				lessonList.add(vo);
				table.setR2c5s(lessonList);
			}else if(scheduleDate.after(friday_night_from) && scheduleDate.before(friday_night_to)){
				if(table.getR3c5s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c5s();
				}
				lessonList.add(vo);
				table.setR3c5s(lessonList);
			}else if(scheduleDate.after(saturday_am_from) && scheduleDate.before(saturday_am_to)){
				if(table.getR1c6s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c6s();
				}
				lessonList.add(vo);
				table.setR1c6s(lessonList);
			}else if(scheduleDate.after(saturday_pm_from) && scheduleDate.before(saturday_pm_to)){
				if(table.getR2c6s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c6s();
				}
				lessonList.add(vo);
				table.setR2c6s(lessonList);
			}else if(scheduleDate.after(saturday_night_from) && scheduleDate.before(saturday_night_to)){
				if(table.getR3c6s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c6s();
				}
				lessonList.add(vo);
				table.setR3c6s(lessonList);
			}else if(scheduleDate.after(sunday_am_from) && scheduleDate.before(sunday_am_to)){
				if(table.getR1c7s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR1c7s();
				}
				lessonList.add(vo);
				table.setR1c7s(lessonList);
			}else if(scheduleDate.after(sunday_pm_from) && scheduleDate.before(sunday_pm_to)){
				if(table.getR2c7s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR2c7s();
				}
				lessonList.add(vo);
				table.setR2c7s(lessonList);
			}else if(scheduleDate.after(sunday_night_from) && scheduleDate.before(sunday_night_to)){
				if(table.getR3c7s() == null){
					lessonList = new ArrayList<LessonVO>();
				}else{
					lessonList = table.getR3c7s();
				}
				lessonList.add(vo);
				table.setR3c7s(lessonList);
			}
		}
		
		return table;
	}
	/**
	 * 
	* @Title: DateTimeForHome 
	* @Description: TODO 
	* @param parameter
	* @author zhangfeipeng 
	* @return String 返回 1 显示 '取消课程' 2 '进入教室' 3 '都显示' 4 '都不显示'
	* @throws
	 */
	@RequestMapping(value = "/parent/DateTimeForHome", method = RequestMethod.GET)
	@ResponseBody
	private String DateTimeForHome(String time){
		boolean cancelOnlineClass = true;
		boolean enterClassRoom = true;
		long sysTime = System.currentTimeMillis();
		long times = DateTimeUtils.parse(time, DateTimeUtils.DATETIME_FORMAT2).getTime();
		if(sysTime>=times){
			cancelOnlineClass = false;
		}
		if((times-sysTime)>30*60*1000){
			enterClassRoom = false;
		}
		if(cancelOnlineClass&&enterClassRoom){
			return "3";
		}else if(cancelOnlineClass){
			return "1";
		}else if(enterClassRoom){
			return "2";
		}else{
			return "4";
		}
	}
}
