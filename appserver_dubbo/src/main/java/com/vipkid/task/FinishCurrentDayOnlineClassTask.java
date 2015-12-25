package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.service.OnlineClassService;


@Component
public class FinishCurrentDayOnlineClassTask {
	private Logger logger = LoggerFactory.getLogger(FinishCurrentDayOnlineClassTask.class.getSimpleName());
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private OnlineClassService onlineClassService;
	

	@Scheduled(cron = "0 0 2 * * ?") 
	public void finishOnlineClassScheduler() {
		try{
			logger.info("Enter finishOnlineClassScheduler()");
			finishOnlineClass();		
			logger.info("Leave finishOnlineClassScheduler()");
		}catch (Throwable t){
			logger.error("Exception found when FinishCurrentDayOnlineClassScheduler:" + t.getMessage(), t);
		}	
	}
	
	private void finishOnlineClass(){
		List<OnlineClass> onlineClassList = onlineClassRepository.findNeedFinishAutomaticallyOnlineClasses();	
		for(OnlineClass onlineClass : onlineClassList){
			try{
				onlineClass.setFinishType(FinishType.AS_SCHEDULED);	
				onlineClass = onlineClassService.doFinish(onlineClass);
			}catch (Throwable t){
				logger.error("Exception found when FinishCurrentDayOnlineClassScheduler:" + t.getMessage(), t);
			}
		}
		
	}

}

