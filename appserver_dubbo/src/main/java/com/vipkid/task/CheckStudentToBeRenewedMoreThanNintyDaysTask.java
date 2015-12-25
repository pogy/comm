package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.model.Student;
import com.vipkid.model.StudentLifeCycleLog;
import com.vipkid.service.StudentLifeCycleLogService;

@Component
public class CheckStudentToBeRenewedMoreThanNintyDaysTask {
	private final int toBeRenewedDucation = 30;

	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;
	
	/** 每周提醒 start */	
	// 每天凌晨3:00
	@Scheduled(cron = "0 3 * * * *")
	public void checkStudents() {
		
		List<StudentLifeCycleLog> studentLifeCycleLogs = studentLifeCycleLogService.findToBeRenewedMoreThanNintyDays(toBeRenewedDucation);
		
		for (StudentLifeCycleLog log : studentLifeCycleLogs) {
			Student student = log.getStudent();
			studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.WONT_RENEW);
		}
	}
}
