package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.repository.StatisticsRenewStudentRepository;
import com.vipkid.service.pojo.parent.StatisticsRenewStudentView;
import com.vipkid.util.Configurations;


@Component
public class StatisticsRenewStudentTask {
	private Logger logger = LoggerFactory.getLogger(OpenClassTask.class.getSimpleName());
	
	@Resource
	private StatisticsRenewStudentRepository statisticsRenewStudentRepository;
	
	@Scheduled(cron = "0 0 1 * * ?")  
	//@Scheduled(fixedRate = 5000) 
	public void sendStatisticsRenewStudentSMS(){
		List<StatisticsRenewStudentView> list = statisticsRenewStudentRepository.list();
		String [] emailName = {"lihongtao@vipkid.com.cn","huxiaoke@vipkid.com.cn"};
		try {
			if(emailName.length!=0){
				for (int i = 0; i < emailName.length; i++) {
					String toEmail = emailName[i];
					try {
						EMail.sendStatisticsRenewStudentEmail(list,toEmail);
						logger.info("Success: send email when everyday 01:00 from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME,toEmail);
					} catch (Throwable e) {
						logger.error("Exception found when sendStatisticsRenewStudentSMS:" + e.getMessage()+",toemail"+toEmail, e);
						
					}
				}
			}
			statisticsRenewStudentRepository.insertStatisticsRenewStudent();//重新更新statistics_renew_student
		} catch (Throwable e) {
			logger.error("Exception found when sendStatisticsRenewStudentSMS:" + e.getMessage(), e);
		}
		
	}
}
