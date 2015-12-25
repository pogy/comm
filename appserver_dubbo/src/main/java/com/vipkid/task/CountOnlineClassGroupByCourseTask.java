package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.service.pojo.CountOnlineClassByCourseView;
import com.vipkid.util.Configurations;

@Component
public class CountOnlineClassGroupByCourseTask {
	private Logger logger = LoggerFactory.getLogger(CountOnlineClassGroupByCourseTask.class.getSimpleName());
	@Resource
    private OnlineClassRepository onlineClassRepostiory;
	
	@Scheduled(cron = "0 0 1 * * ?") 
	//@Scheduled(fixedRate = 5000) 
	public void sendCountOnlineClassGroupByCourseEmail(){
		List<CountOnlineClassByCourseView> list = onlineClassRepostiory.countOnlineClassByCourse();
		String [] emailName = {"cxo@vipkid.com.cn","lihongtao@vipkid.com.cn","huxiaoke@vipkid.com.cn"};
		if(CollectionUtils.isNotEmpty(list)){
			try {
				if(emailName.length!=0){
					for (int i = 0; i < emailName.length; i++) {
						String toEmail = emailName[i];
						try {
							EMail.sendOnlineClassGroupByCourseEmail(list,toEmail);
							logger.info("Success: send email when everyday 01:00 from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME,toEmail);
						} catch (Throwable e) {
							logger.error("Exception found when sendCountOnlineClassGroupByCourseEmail:" + e.getMessage()+",toemail"+toEmail, e);
							
						}
					}
				}
			} catch (Throwable e) {
				logger.error("Exception found when sendCountOnlineClassGroupByCourseEmail:" + e.getMessage(), e);
			}
		}else{
			logger.info("Info: sendCountOnlineClassGroupByCourseEmail list is empty");
		}
		
	}

}
