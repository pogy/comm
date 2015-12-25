package com.vipkid.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course.Type;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.model.StudentPerformance;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.User.AccountType;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.TeacherCommentService;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;

@Component
public class RemindOnlineClassTask {
	private Logger logger = LoggerFactory.getLogger(RemindOnlineClassTask.class.getSimpleName());
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private TeacherRepository teacherRepository;
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private OrderRepository orderRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource 
	TeacherCommentService teacherCommentService;
	
	/** 每周提醒 start */	
	// 每周一10:00 所有家长
	// @Schedule(dayOfWeek = "Mon", hour = "10", minute = "0", second = "0")
	@Scheduled(cron = "0 0 10 * * MON")
	// @Scheduled(fixedRate = 20000) //20 秒触发一次
	public void remindOnlineClassScheduler() {
		try{
			List<Parent> parents = parentRepository.findStudentsByLifeCycleAndCourseTyeAndTotalClassHour(LifeCycle.LEARNING, Type.MAJOR, Configurations.Learning.TOTAL_CLASS_HOUR);
			for(Parent parent : parents) {
				SMS.sendMondayBookOnlineClassReminderToParentSMS(parent.getMobile(), parent);	
				logger.info("Success: send sms when Mon 10:00 from = {} to mobile = {}",Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
			}
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_MONDAY_PARENT, "Sent SMS to all parents on Monday");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 每周四10:00	未约课家长
	//@Schedule(dayOfWeek = "Thu", hour = "10", minute = "0", second = "0")
	@Scheduled(cron = "0 0 10 * * THU ") 
	public void remindNoBookParent() {
		try{
			List<Student> students = studentRepository.findByLifeCycleAndCourseTyeAndTotalClassHour(LifeCycle.LEARNING, com.vipkid.model.Course.Type.MAJOR, Configurations.Learning.TOTAL_CLASS_HOUR);
			for(Student student : students) {
				List<OnlineClass> onlineClasses = onlineClassRepository.findNextWeekBookedOnlineClassesByStudentId(student.getId());
				if(onlineClasses.isEmpty() && student.getFamily() != null && student.getFamily().getParents() != null) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendThursdayBookOnlineClassReminderToParentSMS(parent.getMobile(), student);
						logger.info("Success: send sms when Thu 10:00 from = {} to mobile = {}",Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_MONDAY_PARENT, "Sent SMS to all parents on Monday");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 每周日19:00	家长   下周课程
	//@Schedule(dayOfWeek = "Sun", hour = "19", minute = "0", second = "0")
	@Scheduled(cron = "0 0 19 * * SUN") 
	public void remindParentNextWeekOnlineClass() {
		try{
			List<Student> students = studentRepository.findAll();
			for(Student student : students) {
				List<OnlineClass> onlineClasses = onlineClassRepository.findNextWeekBookedOnlineClassesByStudentId(student.getId());
				if(onlineClasses.size() > 0) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendNexWeekOnlineClassReminderToParentSMS(parent.getMobile(), student, onlineClasses);
						logger.info("Success: send sms when Sun 19:00 from = {} to email = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}					
				}		
			}		
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_SUNDAY_PARENT, "Sent SMS to parents on Sunday");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 每周六10:00	有课＋无课的老师
	//@Schedule(dayOfWeek = "Sat", hour = "10", minute = "00", second = "0")
	@Scheduled(cron = "0 0 10 * * SAT") 
	public void sendToTeacherScheduleTheComingWeekEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findNormalAndRegular();
			for(Teacher teacher : teachers) {
				List<OnlineClass> onlineClasses = onlineClassRepository.findNextWeekBookedOnlineClassesByTeacherId(teacher.getId());
				List<OnlineClass> backUpOnlineClasses = onlineClassRepository.findNextWeekBackUpOnlineClassesByTeacherId(teacher.getId());
				EMail.sendToTeacherScheduleTheComingWeekEmail(teacher, onlineClasses, backUpOnlineClasses);
				logger.info("Success: send email when Sun 19:00 from = {} to email = {}", Configurations.System.SYSTEM_USER_NAME, teacher.getEmail());
			}					
		}catch (Exception e){
			// 观察：避免在catch 中记 audit 出错导致Scheduler 重试发送重复邮件
			//audit log
//			securityController.logSystemAudit(Level.ERROR, Category.EMAIL_SATURDAY_TEACHER, "Sent Email to teacher on Saturday");
//			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 每周日19:00	IT
	/** 取消发送 VK-1738 */
	//@Schedule(dayOfWeek = "Sun", hour = "19", minute = "0", second = "0")
	/*@Scheduled(cron = "0 0 19 * * SUN")
	public void sendToItNextWeekItOnlineClassesReminderEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findItTestTeachers();
			if(!teachers.isEmpty()) {
				for(Teacher teacher : teachers) {
					List<OnlineClass> onlineClasses = onlineClassRepository.findNextWeekBookedItTestOnlineClassesByTeacherId(teacher.getId());
					for(OnlineClass onlineClass : onlineClasses) {
						Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
						List<Student> students = new ArrayList<Student>();
						students.add(student);
						onlineClass.setStudents(students);
					}
					EMail.sendToItNextWeekItOnlineClassesReminderEmail(onlineClasses, teacher);
				}
			}						
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.EMAIL_SUNDAY_IT, "Sent Email to teacher on Saturday");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}*/
	
	// 每周六面试结果汇总
	//@Schedule(dayOfWeek = "Sat", hour = "23", minute = "59", second = "0")
	@Scheduled(cron = "0 59 23 * * SAT") 
	public void sendToTeacherIntervieweeStudentsJoinedVIPKIDThisWeekEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findDemoTeachers();
			if(!teachers.isEmpty()) {
				List<Order> orders = orderRepository.findLastWeekPayConfirmedOrders();
				for(Teacher teacher : teachers) {
					List<Student> interviewStudents = new ArrayList<Student>();
					for(Order order : orders) {
						long studentId = order.getStudent().getId();
						OnlineClass onlineClass = onlineClassRepository.findFinishAsScheduledDemoOnlineClassByStudentId(studentId);
						Student student = onlineClass.getStudents().get(0);
						if(student.getId() == order.getStudent().getId()) {
							interviewStudents.add(student);
						}
					}
					if(interviewStudents.size() > 0) {
						int studentCount = interviewStudents.size();
						EMail.sendToTeacherInterviewResultEmail(teacher, interviewStudents, studentCount);
						logger.info("Success: send email when Sat 23:59 from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME, teacher.getEmail());
					}
				}
			}						
		}catch (Exception e){
			// 观察：避免在catch 中记 audit 出错导致Scheduler 重试发送重复邮件
			//audit log
//			securityController.logSystemAudit(Level.ERROR, Category.EMAIL_SATURDAY_TEACHER_INTERVIEW_STUDENTS, "Sent interview students count email to teacher on Saturday");
//			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	/** 每周提醒 end */	
	
	// 每日提醒	有课的日子，第一节课前3小时
	//@Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?") 
	public void sendToTeacherTodayAndTomorrowBookedOnlineClassesEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findNormalAndRegular();
			for(Teacher teacher : teachers) {
				List<OnlineClass> todayOnlineClasses = onlineClassRepository.findTodayBookedOrBackupOnlineClassesByTeacherId(teacher.getId());
				List<OnlineClass> tomorrowOnlineClasses = onlineClassRepository.findTomorrowBookedOrBackupOnlineClassesByTeacherId(teacher.getId());
					if(!todayOnlineClasses.isEmpty()) {
					long timeDifference = todayOnlineClasses.get(0).getScheduledDateTime().getTime() - (new Date()).getTime();
					if(timeDifference > 3*60*60*1000-5*60*1000 && timeDifference < 3*60*60*1000+5*60*1000) {
						EMail.sendToTeacherTodayAndTomorrowBookedOnlineClassesEmail(teacher, todayOnlineClasses, tomorrowOnlineClasses);
						logger.info("Success: send email before the first booked online class(everyday) from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME, teacher.getEmail());
					}	
				}					
			}						
		}catch (Exception e){
			// 观察：避免在catch 中记 audit 出错导致Scheduler 重试发送重复邮件
			//audit log
//			securityController.logSystemAudit(Level.ERROR, Category.EMAIL_EVERYDAY_TEACHER, "Sent Email to teacher everday");
//			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 每天00:00	IT  	今日将做测试,明日安排
	//@Schedule(hour = "0", minute = "0", second = "0")
	@Scheduled(cron = "0 0 0 * * ?") 
	public void sendToItTomorrowItOnlineClassesReminderEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findItTestTeachers();
			if(!teachers.isEmpty()) {
				for(Teacher teacher : teachers) {
					List<OnlineClass> todayItOnlineClasses = onlineClassRepository.findTodayBookedItTestOnlineClassesByTeacherId(teacher.getId());
					for(OnlineClass onlineClass : todayItOnlineClasses) {
						Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
						List<Student> students = new ArrayList<Student>();
						students.add(student);
						onlineClass.setStudents(students);
					}
					
					List<OnlineClass> tomorrowItOnlineClasses = onlineClassRepository.findTomorrowBookedItTestOnlineClassesByTeacherId(teacher.getId());
					for(OnlineClass onlineClass : tomorrowItOnlineClasses) {
						Student student = studentRepository.find(onlineClass.getStudents().get(0).getId());
						List<Student> students = new ArrayList<Student>();
						students.add(student);
						onlineClass.setStudents(students);
					}
					EMail.sendToItTomorrowItOnlineClassesReminderEmail(todayItOnlineClasses, tomorrowItOnlineClasses, teacher);
					logger.info("Sucess: send email when 00:00(everyday) from = {} to it teacher:email = {}",Configurations.System.SYSTEM_USER_NAME, teacher.getEmail());
				}
			}							
		}catch (Exception e){
			// 观察：避免在catch 中记 audit 出错导致Scheduler 重试发送重复邮件
			//audit log
//			securityController.logSystemAudit(Level.ERROR, Category.EMAIL_EVERYDAY_IT, "Sent Email to IT everday");
//			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 上课当日提醒	课前1小时	老师
	//@Schedule(hour = "8-20", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 8-20 * * ?") 
	public void sendToTeacherNextClassReminderEmail() {
        logger.info("SendToTeacherNextClassReminderEmail begin");
		try{
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedMajorOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(35), DateTimeUtils.getNthMinutesLater(65));
            if (CollectionUtils.isNotEmpty(onlineClasses)) {
                for(OnlineClass onlineClass : onlineClasses) {
                    logger.info("SendToTeacherNextClassReminderEmail:onlineClassID={},Students' EnglishNames={},LessonID={}",onlineClass.getId(),onlineClass.getStudentEnglishNames(),onlineClass.getLesson().getId());
                    EMail.sendToTeacherNextClassReminderEmail(onlineClass);
                    logger.info("Success: send email before every booked online class from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME, onlineClass.getTeacher().getEmail());
                }
            } else {
                logger.warn("SendToTeacherNextClassReminderEmail:Can not get online class at this time");
            }
		}catch (Exception e){
            logger.error("SendToTeacherNextClassReminderEmail error,msg is:",e);
		}
	}
	
	// 小翻译 课 和aeeseement 2 上课当日提醒 课前1小时 老师
	// @Schedule(hour = "8-20", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 8-20 * * ?")
	public void sendToTeacherNextClassReminderEmailForLT() {
		try {
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedLTOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(35), DateTimeUtils.getNthMinutesLater(65));
			for (OnlineClass onlineClass : onlineClasses) {
				EMail.sendToTeacherNextClassReminderEmail(onlineClass);
			}

			List<OnlineClass> assOnlineClasses = onlineClassRepository
					.findBookedAssessment2OnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(35), DateTimeUtils.getNthMinutesLater(65));
			for (OnlineClass onlineClass : assOnlineClasses) {
				for (Student student : onlineClass.getStudents()) {
					for (Parent parent : student.getFamily().getParents()) {
						SMS.sendAssessmentOnlineClassReminderToParentsSMS(parent.getMobile(), onlineClass);
                        logger.info("Send sms before trial online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		} catch (Exception e) {
            logger.error("RemindOnlineClassTask error:",e);
			// 观察：避免在catch 中记 audit 出错导致Scheduler 重试发送重复邮件
			// audit log
			// securityController.logSystemAudit(Level.ERROR,
			// Category.EMAIL_PRE_ONE_HOUR_TEACHER,
			// "Sent Email to teacher before online class starts");
			// logger.error("Exception found when remindOnlineClassScheduler:" +
			// e.getMessage(), e);
		}
	}

	// 上课当日提醒	课前2小时	家长
	//@Schedule(hour = "7-19", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 7-19 * * ?") 
	public void sendTodayOnlineClassReminderToParentSMS() {
		try{
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedMajorOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(95), DateTimeUtils.getNthMinutesLater(125));
			for(OnlineClass onlineClass : onlineClasses) {
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendTodayOnlineClassReminderToParentSMS(parent.getMobile(), student, onlineClass);
						logger.info("Success: send sms before major online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}					
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_TWO_HOUR_PARENT, "Sent SMS to parent before online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 试听当天提醒	课前3小时
	//@Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?") 
	public void sendDemoOnlineClassScheduledTimeToParentsSMS() {
		try{
			List<OnlineClass> demoOnlineClasses = onlineClassRepository.findBookedDemoOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			List<OnlineClass> trialOnlineClasses = onlineClassRepository.findBookedTrialOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for(OnlineClass onlineClass : demoOnlineClasses) {
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						// 先Type DEMO中有demo 和 Assessment 两个课程，但DEMO已经废除不约且不发相关短信
						SMS.sendAssessmentOnlineClassReminderToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before demo online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
			for(OnlineClass onlineClass : trialOnlineClasses) {
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendTrialOnlineClassReminderToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before trial online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}					
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_THREE_HOUR_PARENT, "Sent SMS to parent before demo online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:", e);
		}
	}
	
	// 小翻译课  assessment2   当天提醒	课前3小时  提醒家长
	//@Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?") 
	public void sendLTAndAssessment2OnlineClassScheduledTimeToParentsSMS() {
		try{
			List<OnlineClass> ltOnlineClasses = onlineClassRepository.findBookedLTOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for(OnlineClass onlineClass : ltOnlineClasses) {
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendTodayOnlineClassReminderToParentSMS(parent.getMobile(), student, onlineClass);
						logger.info("Success: send sms before it test online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}

			List<OnlineClass> assOnlineClasses = onlineClassRepository.findBookedAssessment2OnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for(OnlineClass onlineClass : assOnlineClasses) {
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendAssessmentOnlineClassReminderToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before it test online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_THREE_HOUR_PARENT, "Sent SMS to parent before it test online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:", e);
		}
	}

	// 当天提醒 课前3小时
	// @Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?")
	public void sendItOnlineClassScheduledTimeToParentsSMS() {
		try {
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedItTestOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for (OnlineClass onlineClass : onlineClasses) {
				for (Student student : onlineClass.getStudents()) {
					for (Parent parent : student.getFamily().getParents()) {
						SMS.sendItOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before it test online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		} catch (Exception e) {
			// audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_THREE_HOUR_PARENT, "Sent SMS to parent before it test online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 当天提醒 课前3小时
	// @Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?")
	public void sendKickoffOnlineClassScheduledTimeToParentsSMS() {
		try {
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedKickoffOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for (OnlineClass onlineClass : onlineClasses) {
				for (Student student : onlineClass.getStudents()) {
					if (!student.getAccountType().equals(AccountType.NORMAL) && !student.getLifeCycle().equals(LifeCycle.LEARNING)) {
						continue;
					}
					for (Parent parent : student.getFamily().getParents()) {
						SMS.sendKickOffOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before it test online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		} catch (Exception e) {
			// audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_THREE_HOUR_PARENT, "Sent SMS to parent before it test online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 当天提醒 课前3小时
	// @Schedule(hour = "6-18", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 6-18 * * ?")
	public void sendCLTCourseOnlineClassScheduledTimeToParentsSMS() {
		try {
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedCLTCourseOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(155), DateTimeUtils.getNthMinutesLater(185));
			for (OnlineClass onlineClass : onlineClasses) {
				for (Student student : onlineClass.getStudents()) {
					if (!student.getAccountType().equals(AccountType.NORMAL) && !student.getLifeCycle().equals(LifeCycle.LEARNING)) {
						continue;
					}
					for (Parent parent : student.getFamily().getParents()) {
						SMS.sendCLTCourseOnlineClassScheduledTimeToParentsSMS(parent.getMobile(), onlineClass);
						logger.info("Success: send sms before it test online class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}
		} catch (Exception e) {
			// audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_PRE_THREE_HOUR_PARENT, "Sent SMS to parent before it test online class starts");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}
	
	// 预排	老师未放置足够的AvailableTime  每月
	//@Schedule(dayOfMonth = "1", hour = "0", minute = "0", second = "0")
	/** 取消发送: VK-1739 */
	/*@Scheduled(cron = "0 0 0 1 * ?") 
	public void sendToTeacherArrangeTimeReminderEmail() {
		try{
			List<Teacher> teachers = teacherRepository.findNormalAndRegular();
			for(Teacher teacher : teachers) {
				Calendar endOfNextWeekCalendar = Calendar.getInstance();
				endOfNextWeekCalendar.add(Calendar.MONTH, 1);
				endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
				endOfNextWeekCalendar.set(Calendar.MINUTE, 0);
				endOfNextWeekCalendar.set(Calendar.SECOND, 0);
				endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
				List<OnlineClass> onlineClasses = onlineClassRepository.findAvailableByTeacherIdAndStartDateAndEndDate(teacher.getId(), new Date(), endOfNextWeekCalendar.getTime());
				if(onlineClasses.size() < Configurations.Schedule.AVAILABLE_HOUR_NEXT_MONTH_LIMITATION) {
					EMail.sendToTeacherArrangeTimeReminderEmail(teacher);
				}
			}
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.EMAIL_PRE_MONTH_TEACHER, "Sent Email to teacher if not set enough available time");
			logger.error("Exception found when remindOnlineClassScheduler:" + e.getMessage(), e);
		}
	}*/

	// 上课后0min学生没有打开页面时	家长
	//@Schedule(hour = "9-21", minute = "0,30", second = "1")
	@Scheduled(cron = "1 0,30 9-21 * * ?") 
	public void sendStudentIsLateForOnlineClassToParentSMS() {
		try{
			List<OnlineClass> onlineClasses = onlineClassRepository.findBookedMajorOnlineClassByStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(-5), DateTimeUtils.getNthMinutesLater(5));
			for(OnlineClass onlineClass : onlineClasses) {
				if(onlineClass.getStudentEnterClassroomDateTime() == null) {
					for(Student student : onlineClass.getStudents()) {
						for(Parent parent : student.getFamily().getParents()) {
							SMS.sendStudentIsLateForOnlineClassToParentSMS(parent.getMobile(), student, onlineClass);
							logger.info("Success: send sms student is no show after online class begins from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
						}
					}
				}
				
			}					
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_LATE_PARENT, "Sent SMS to parent if student not enter online class");
			logger.error("Exception found when remindOnlineClassScheduler:", e);
		}
	}
 	
	// 老师合同到期前4周提醒
	//@Schedule(hour = "10", minute = "0", second = "0")
	@Scheduled(cron = "0 0 10 * * ?") 
	public void sendTeacherContractWillRunOutToEductionEmail(){
		try{
			List<Teacher> teachers = teacherRepository.findNormalAndRegular();
			Calendar currentTimeCalendar = Calendar.getInstance();
			currentTimeCalendar.set(Calendar.SECOND, 0);
			currentTimeCalendar.set(Calendar.MINUTE, 0);
			currentTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
			currentTimeCalendar.set(Calendar.MILLISECOND, 0);
			Date datePoint = new Date(currentTimeCalendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(28));
			for(Teacher teacher : teachers) {
				Date contractEndDate = teacher.getContractEndDate();
				if(contractEndDate != null && contractEndDate.equals(datePoint)) {
					EMail.sendTeacherContractEndDateToEducation(teacher);
					logger.info("Success: send email when teacher contract will run out, from = {} to mail = {}", Configurations.System.SYSTEM_USER_NAME, teacher.getEmail());
				}
			}				
		}catch (Exception e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SMS_LATE_PARENT, "Sent email to education if teacher almost reach contract end date");
			logger.error("Exception found when remindOnlineClassScheduler:", e);
		}
	}
	
	/**
	 * 2015-06-30 学生performance处理
	 *  每天凌晨1点, 2点 执行 -- 07-02 移到到teacherComment更新时处理
	 */
	@Scheduled(cron="0 0 1,2 * * ?")
//	@Scheduled(cron="0 0/5 * * * ?") // just test
	public void checkStudentPerformance() {
		
		logger.info( "checkStudentPerformance" );
		//
		List<Long> studentList = teacherCommentService.getStudentListForPerformance();
		if (null == studentList || studentList.size()<1) {
			logger.info("reminder's checkStudentPerformance: no student to update");
			return;
		}
		
		//
		for (Long studentId : studentList) {
			// 获取并更新
			TeacherComment teacherComment = teacherCommentService.updateTeacherCommentPerformanceByStudentId(studentId.longValue());
			if (teacherComment != null) {
				Student student = studentRepository.find(studentId);
				StudentPerformance studentPerformance = teacherComment.getCurrentPerforance();
				student.setCurrentPerformance(studentPerformance);
				studentRepository.update(student);
				securityService.logSystemAudit(Level.INFO, Category.AUDIT_APPLICATION, "Update student performance to "+studentPerformance);
				
			}
		}
	
	}

	// 每天20:30	ITTest  	提醒ITTest的同事明天要进行ITTest 的名单信息
	//@Schedule(hour = "20", minute = "30", second = "0")
	@Scheduled(cron = "0 30 20 * * ?")
	public void sendItTestReminderEmail() {
		try{
			List<OnlineClass> aList = onlineClassRepository.getITTestClassInfo();
			EMail.sendITTestEmail(aList, Configurations.EMail.IT_Test_Email);
			logger.info("Sucess: send email when 20:30(everyday) from = {} to it itTest:email = {}",Configurations.System.SYSTEM_USER_NAME, Configurations.EMail.IT_Test_Email);							
		}catch (Exception e){
			logger.error("Unable to send ITTest Email for Exception : " + e.getMessage());
		}
	}
}
