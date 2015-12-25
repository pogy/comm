package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.repository.OpenClassRepository;
import com.vipkid.util.Configurations;

@Component
public class OpenClassTask {
	private Logger logger = LoggerFactory.getLogger(OpenClassTask.class.getSimpleName());
	
	@Resource
    private OpenClassRepository openClassRepository;
	
	@Scheduled(cron = "0 0,30 8-22 * * ?") 
	//@Scheduled(fixedRate = 5000) 
	public void sendTodayOpenClassReminderToParentSMS(){
		try{
			List<OnlineClass> onlineClasses = openClassRepository.findOpenClassBeforeTwoHours();
			for(OnlineClass onlineClass : onlineClasses) {
				if(onlineClass.getStudents()==null || onlineClass.getStudents().isEmpty()){//如果onlineClass 下没有学生 跳出此次循环，进入下次循环
					continue;
				}
				for(Student student : onlineClass.getStudents()) {
					for(Parent parent : student.getFamily().getParents()) {
						SMS.sendOpenClassReminderToParentSMS(parent.getMobile(), student, onlineClass);
						logger.info("Success: send sms before Open class from = {} to mobile = {}", Configurations.System.SYSTEM_USER_NAME, parent.getMobile());
					}
				}
			}					
		}catch (Exception e){
			//audit log
			logger.error("Exception found when sendTodayOpenClassReminderToParentSMS:" + e.getMessage(), e);
		}
		
	}
}
