package com.vipkid.task;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Teacher;
import com.vipkid.model.TeacherApplication;
import com.vipkid.repository.TeacherApplicationRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.util.DateTimeUtils;

@Component
public class RemindTeacherRecruitmentTask {
	
	private Logger logger = LoggerFactory.getLogger(RemindTeacherRecruitmentTask.class.getSimpleName());
	
	@Resource
	private TeacherApplicationRepository teacherApplicationRepository;
	
	@Resource
	private SecurityService securityService;
	
	// 48小时提醒应聘老师，做上课准备
	//@Schedule(hour = "0-23", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 0-23 * * ?") 
	public void reminderEmailForInterviewBefore48Hour() {
		try{
			List<TeacherApplication> teacherApplications = teacherApplicationRepository.findInterviewTeacherApplicationByStartDateAndEndDate(DateTimeUtils.getDateByOffset(2, -5), DateTimeUtils.getDateByOffset(2, 5));
			for(TeacherApplication teacherApplication : teacherApplications) {
				EMail.sendToApplicantForInterviewBefore48Hour(teacherApplication);
			}					
		}catch (Exception e){
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_DEMO_PRE_2DAYS, "Sent Email to teacher applicant when 2 days before the demo class");
			logger.error("Exception found when reminderEmailForInterviewBefore48Hour:" + e.getMessage(), e);
		}
	}
	
	/**
	 * 大于24小时而小于48小时的课程，要在24小时时提醒面试
	 * 大于48小时的课程，要在48小时提醒上课
	 * TEAC-209
	 */
	//@Schedule(hour = "0-23", minute = "0,30", second = "0")
	@Scheduled(cron = "0 0,30 0-23 * * ?") 
	public void reminderEmailForInterviewBefore24Hour() {
		try{
			List<TeacherApplication> teacherApplications = teacherApplicationRepository.findInterviewTeacherApplicationByStartDateAndEndDateAndDiffDayBetweenScheduleTimeBookTime(DateTimeUtils.getDateByOffset(1, -5), DateTimeUtils.getDateByOffset(1, 5), 1, 2);
			for(TeacherApplication teacherApplication : teacherApplications) {
				EMail.sendToApplicantForInterviewBefore48Hour(teacherApplication); // 模版和48h的一样
			}					
		}catch (Exception e) {
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_DEMO_PRE_1DAYS, "Sent Email to teacher applicant when 1 days before the demo class");
			logger.error("Exception found when reminderEmailForInterviewBefore24Hour:" + e.getMessage(), e);
		}
	}
	
	/**
	 * interview online-class 开始后30分钟 感谢邮件
	 * History: 2015-05-06 修改: 不发送邮件!
	 */
	// @Schedule(hour="0-23", minute="0,30", second="0")
	public void thanksForInterviewAfter30MinitesEmail() {
		
		try {
			List<TeacherApplication> teacherApplications = teacherApplicationRepository.findCurrentTeacherApplicationByOnlineClassStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(-20), DateTimeUtils.getNthMinutesLater(-40));
			
			for(TeacherApplication teacherApplication : teacherApplications) {
				EMail.sendThxEmailForDemoClass(teacherApplication);
			}	
			
			
		} catch (Exception e) {
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_DEMO_THX_AFTER_30MIN, "Sent Email to teacher applicant when 30 miniters after the demo class ");
			logger.error("Exception found when reminderEmailForInterviewBefore48Hour:" + e.getMessage(), e);
		}
		return;
	}
	
	/**
	 * interview online-class 
	 * History: 2015-05-06 修改： 30分钟未出席，发送拒绝邮件
	 */
	//@Schedule(hour="0-23", minute="0,30", second="0")
	@Scheduled(cron = "0 0,30 0-23 * * ?") 
	public void absentInterviewAfter30MinitesEmail() {
		
		try {
			List<TeacherApplication> teacherApplications = teacherApplicationRepository.findCurrentTeacherApplicationByOnlineClassStartDateAndEndDate(DateTimeUtils.getNthMinutesLater(-20), DateTimeUtils.getNthMinutesLater(-40));
			
			for(TeacherApplication teacherApplication : teacherApplications) {
				if (teacherApplication.getOnlineClass().getTeacherEnterClassroomDateTime() == null ||
						teacherApplication.getOnlineClass().getStudentEnterClassroomDateTime() == null ) {
					//
					EMail.sendAbsentEmailForDemoClass(teacherApplication);
				}
				
			}	
			
			
		} catch (Exception e) {
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_DEMO_THX_AFTER_30MIN, "Sent Email to teacher applicant when 30 miniters after the demo class ");
			logger.error("Exception found when reminderEmailForInterviewBefore48Hour:" + e.getMessage(), e);
		}
		return;
	}

	/**
	 * 招聘step 3阶段后，116小时，进行提醒，提交contract信息
	 * 2015-05-05 改为72小时后
	 */
	//@Schedule(hour="0-23", minute="0,30", second="0")
	@Scheduled(cron = "0 0,30 0-23 * * ?") 
	public void emailReminderForContractAfter116Hour() {
		
//		// after 116 hours.
//		Date startDate = DateTimeUtils.getDateByOffset(5, -255);
//		Date endDate = DateTimeUtils.getDateByOffset(5, -225);
		
		//2015-05-04  after 72 hours.
		Date startDate = DateTimeUtils.getDateByOffset(3, -15);
		Date endDate = DateTimeUtils.getDateByOffset(3, 15);
		
		
		
		try {
			List<Teacher> teachers = teacherApplicationRepository.findApplicantWithoutContractSignInfoBy116Hours(startDate, endDate);
			for(Teacher  teacher : teachers) {
				EMail.sendToApplicantForContractSignReminder(teacher);
			}
		} catch (Exception e) {
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_CONTRACT_REMINDER, "Sent Email to teacher applicant after 116 when not contract info");
			logger.error("Exception found when reminderEmailForInterviewBefore48Hour:" + e.getMessage(), e);
		}
		return;
	}
	
	
	/**
	 * 提交contract资料的时间过期，失败处理
	 * 2015-07-09 根据changwen意见，现在的招聘时间会比较长，时间限制取消。
	 * Note: 并无终止application等status等得操作。 
	 */
	//@Schedule(hour="0-23", minute="0,30", second="0")
	//@Scheduled(cron = "0 0,30 0-23 * * ?") 
	public void emailTeacherCruitContractSignTimeTerminate() {
		
		try {
			// 
			List<Teacher> teachers = teacherApplicationRepository.findApplicantContractSignInfoTerminate();
			for(Teacher  teacher : teachers) {
				EMail.sendToApplicantForContractTeminate(teacher);
			}				
		} catch (Exception e) {
			//audit log
			securityService.logAudit(Level.ERROR, Category.EMAIL_TEACHERCRUIT_CONTRACT_SIGN_END, "Sent Email to teacher applicant over the deadline");
			logger.error("Exception found when reminderEmailForInterviewBefore48Hour:" + e.getMessage(), e);
		}
		return;
	}
}
