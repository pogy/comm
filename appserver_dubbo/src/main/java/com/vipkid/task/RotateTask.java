package com.vipkid.task;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OpenClassRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.util.DateTimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

@Component
public class RotateTask {
	 private Logger logger = LoggerFactory.getLogger(RotateTask.class.getSimpleName());
	 
	 @Resource
	 private OnlineClassRepository onlineClassRepository;
	 
	 @Resource
	 private SecurityService securityService;
	 
	 @Resource
	 private OpenClassRepository openClassRepository;
	 
	 //@Schedule(hour = "9-22", minute = "0,30", second = "0")
	 @Scheduled(cron = "0 0,30 9-22 * * ?")
	 public void updateExpiredStatusScheduler(){
		try{
			logger.info("Enter updateExpiredStatusScheduler()");
			updateExpiredOnlineClasses();
			logger.info("Leave updateExpiredStatusScheduler()");
		}catch (Throwable e){
			//audit log
			securityService.logSystemAudit(Level.ERROR, Category.SYSTEM_ERROR, e.getMessage());
			logger.error("Exception found when updateExpiredStatusScheduler:" + e.getMessage(), e);
            throw e;
		}
	 }
	 
	 private void updateExpiredOnlineClasses(){
		Date startDate = DateTimeUtils.getBeginningOfTheDay();
		Date endDate = DateTimeUtils.getPrevMinutes(5);
	    List<OnlineClass> onlineClasses = onlineClassRepository.findAllAvaiableByStartDateAndEndDate(startDate, endDate);
		for (OnlineClass onlineClass : onlineClasses){
			long count = openClassRepository.countOpenClassStudentById(onlineClass.getId());
			if(onlineClass.getStatus() == Status.OPEN && count > 0){
				//TO-DO
			}else{
				onlineClass.setStatus(Status.EXPIRED);
				onlineClassRepository.update(onlineClass);
				securityService.logSystemAudit(Level.INFO, Category.ONLINE_CLASS_UPDATE, "Update" + onlineClass.getOnlineClassName() +" to Expired Status");
			}
		}
	 }
}
