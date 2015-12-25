package com.vipkid.task;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Student;

public class SMSTest {
	
	private static OnlineClass onlineClass = new OnlineClass();

	@Before
	public void Before () {
		onlineClass.setScheduledDateTime(new Date());
		Student student = new Student();
		student.setName("高源");
		onlineClass.addStudent(student);
	}
	
	@After
	public void After() {
		onlineClass = new OnlineClass();
	}
	
	@Test
	public void testSendKickOffOnlineClassScheduledTimeToParentsSMS () {
		SMS.sendKickOffOnlineClassScheduledTimeToParentsSMS("18511863246", onlineClass);
	}
	
	@Test
	public void testSendCLTCourseOnlineClassScheduledTimeToParentsSMS () {
		SMS.sendCLTCourseOnlineClassScheduledTimeToParentsSMS("18511863246", onlineClass);
	}
	
}
