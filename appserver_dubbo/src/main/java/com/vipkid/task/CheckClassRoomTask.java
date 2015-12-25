package com.vipkid.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.OnlineClassService;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;

@Component
public class CheckClassRoomTask {
    private Logger logger = LoggerFactory.getLogger(CheckClassRoomTask.class.getSimpleName());
    
    @Resource
    private OnlineClassRepository onlineClassRepository;
    
    @Resource
    private SecurityService securityService;
    
    @Resource
    private OnlineClassService onlineClassService;
    
    /**
     * 每天下午六点开始扫描第二天上午的onlineclass，
     * 查看是否有booked的课没有classroom
     * 若无则创建，创建不成功，则发送邮件提醒
     */
    @Scheduled(cron = "0 0 18 * * ?")
//    @Scheduled(fixedRate = 5000)  //5 秒触发一次
    public void checkClassRoomAM(){
    	logger.info("Enter checkClassRoomAM : Start Scanning the tomorrow morning onlineclass classroom");
         Date startDate = DateTimeUtils.getTomorrow(9);     //DateTimeUtils.getTomorrow(9)
         Date endDate = DateTimeUtils.getTomorrow(12);
         try{
         	if(Configurations.Deploy.ENABLE_ATTACH_DBY){
         		 autoUpdateClassRoom(startDate,endDate);
         	}
         }catch(Throwable e){
         	securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, e.getMessage());
 			logger.error("Exception found when checkClassRooAM:" + e.getMessage(), e);
         }
     	logger.info("Leave checkClassRoomAM : Stop Scanning the today afternoon onlineclass classroom");
    	
    }
    /**
     * 每天凌晨两点开始扫描第二天下午的onlineclass，
     * 查看是否有booked的课没有classroom
     * 若无则创建，创建不成功，则发送邮件提醒
     */
    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(fixedRate = 5000)
    public void checkClassRoomPM(){
    	logger.info("Enter checkClassRoomPM : Start Scanning the today afternoon onlineclass classroom");
    	Date startDate = DateTimeUtils.getToday(0);     
        Date endDate = DateTimeUtils.getTomorrow();
        try{
        	if(Configurations.Deploy.ENABLE_ATTACH_DBY){
        		 autoUpdateClassRoom(startDate,endDate);
        	}
        }catch(Throwable e){
        	securityService.logSystemAudit(Level.ERROR, Category.ONLINE_CLASS_CLASSROOM_CREATE, e.getMessage());
			logger.error("Exception found when checkClassRoomPM:" + e.getMessage(), e);
        }
    	logger.info("Leave checkClassRoomPM : Stop Scanning the today afternoon onlineclass classroom");       
    }
    
    private void autoUpdateClassRoom(Date startDate,Date endDate){
    	
    	List <OnlineClass> onlineClasses = onlineClassRepository.findBookedOnlineClassByStartDateAndEndDate(startDate, endDate);
    	List <OnlineClass> onlineClassesEmail = new ArrayList<OnlineClass>();
    	for(OnlineClass onlineClass : onlineClasses){
    		try{
    			if(StringUtils.isBlank(onlineClass.getClassroom())){
    				//创建教室
    				onlineClassService.createDBYClassroomTask(onlineClass);
    				if(StringUtils.isBlank(onlineClass.getClassroom())){
    					onlineClassesEmail.add(onlineClass);   					
    				}    				
    			}
    		}catch(Exception e){
    			logger.error("Exception found when check the onlineclass room .........."+e.getMessage(),e);
    		}
    	}
    	if(!onlineClassesEmail.isEmpty()){
    		EMail.sendReminderForAutoCreateClassRoomFailed("tech_support@vipkid.com.cn", onlineClassesEmail);
    	}
    	
    }
    

}
