package com.vipkid.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.FiremanLog;
import com.vipkid.model.FiremanLog.Event;
import com.vipkid.model.OnlineClass;
import com.vipkid.repository.AuditRepository;
import com.vipkid.repository.FiremanLogRepository;
import com.vipkid.service.pojo.FiremanLogView;

@Service
public class FiremanLogService {
	private Logger logger = LoggerFactory.getLogger(FiremanLogService.class.getSimpleName());
	
	@Resource
	private AuditRepository auditRepository;
	
	@Resource
	private FiremanLogRepository firemanLogRepository;

	public FiremanLog create(FiremanLog firemanLog) {
		logger.debug("create fireman log: {}", firemanLog);
		
		FiremanLog firemanLogInDB = firemanLogRepository.findRecentLogByOnlineClassIdAndEvent(firemanLog.getOnlineClass().getId(), firemanLog.getEvent());
		if (firemanLogInDB != null && firemanLogInDB.getStatus().equals(FiremanLog.Status.RESOLVING)) {
			return firemanLogInDB;
		} else {
			return firemanLogRepository.create(firemanLog);
		}
	}
	
	public FiremanLog update(FiremanLogView firemanLogView) {
		logger.debug("update fireman log: {}", firemanLogView);
		FiremanLog firemanLog = new FiremanLog();
		firemanLog = firemanLogRepository.findFiremanLogById(firemanLogView.getId());
		firemanLog.setHelpYell(true);
		return firemanLogRepository.update(firemanLog);
	}
	
	public FiremanLog findRecentLogByOnlineClassIdAndEvent(long onlineClassId,Event event) {
		logger.debug("findRecentLogByOnlineClassId: " + onlineClassId);
		return firemanLogRepository.findRecentLogByOnlineClassIdAndEvent(onlineClassId, event);
	}
	
	public List<FiremanLogView> findOnlineClassSupportingStatus(List<Long> courseIds) {
		logger.debug("findRecentLogByOnlineClassId: ");
		
		List<FiremanLog> firemanLogList = new ArrayList<FiremanLog>();
		firemanLogList = firemanLogRepository.findOnlineClassSupportingStatus();

//        List<FiremanLog> firemanLogForCourseList = new ArrayList<FiremanLog>();
//        for(FiremanLog firemanLog : firemanLogList){
//            if(Arrays.asList(courseIds).contains(firemanLog.getOnlineClass().getCourse().getId())){
//                firemanLogForCourseList.add(firemanLog);
//            }
//        }

        List<FiremanLogView> simplifiedFiremanLogList = new ArrayList<FiremanLogView>();
		
		if(CollectionUtils.isNotEmpty(firemanLogList)){
			for(FiremanLog firemanLog : firemanLogList){
				FiremanLogView simplifiedFiremanLogView = new FiremanLogView();
				
				simplifiedFiremanLogView.setEvent(firemanLog.getEvent());				
				simplifiedFiremanLogView.setOnlineClassId(firemanLog.getOnlineClass().getId());
				simplifiedFiremanLogView.setOnlineClassStatus(firemanLog.getOnlineClass().getStatus());
				simplifiedFiremanLogView.setOnlineClassScheduledDateTime(firemanLog.getOnlineClass().getScheduledDateTime());
				simplifiedFiremanLogView.setHelpYell(firemanLog.isHelpYell());
				simplifiedFiremanLogView.setId(firemanLog.getId());
				
				simplifiedFiremanLogList.add(simplifiedFiremanLogView);
				
				
			}
		}
		
		return simplifiedFiremanLogList;
	}
	
	public FiremanLog resolvedStudentProblem(long onlineClassId) {
		logger.debug("resolvedStudentProblem: " + onlineClassId);
		FiremanLog firemanLog = new FiremanLog();
		firemanLog =  firemanLogRepository.findStudentFiremanLogByOnlineClassId(onlineClassId);
		
		if (firemanLog != null){
			Date date = new Date();
			firemanLog.setResolveDateTime(date);
			firemanLog.setStatus(FiremanLog.Status.RESOLVED);
			return firemanLogRepository.update(firemanLog);
		}
		else{
			return null;
		}
	}
	
	public FiremanLog resolvedTeacherProblem(long onlineClassId) {
		logger.debug("resolvedTeacherProblem: " + onlineClassId);
		FiremanLog firemanLog = new FiremanLog();
		firemanLog =  firemanLogRepository.findTeacherFiremanLogByOnlineClassId(onlineClassId);
		
		if (firemanLog != null){
			Date date = new Date();
			firemanLog.setResolveDateTime(date);
			firemanLog.setStatus(FiremanLog.Status.RESOLVED);
			return firemanLogRepository.update(firemanLog);
		}
		else{
			return null;
		}
	}
	
	public FiremanLog checkTeacherNotEnterYell(Long onlineClassId) {
		logger.debug("checkTeacherNotEnterYell: " + onlineClassId);
		FiremanLog firemanLog1 = new FiremanLog();
		firemanLog1 =  firemanLogRepository.findFiremanLogByOnlineClassIdAndTeacherNotEnter(onlineClassId);
		if(firemanLog1 != null){
			return firemanLog1;
		}
		else{
			Date date = new Date();
			OnlineClass onlineClass = new OnlineClass();
			onlineClass.setId(onlineClassId);
			
			FiremanLog firemanLog2 = new FiremanLog();
			firemanLog2.setEvent(Event.TEACHER_NOT_ENTER);
			firemanLog2.setHelpYell(false);
			firemanLog2.setCreatedDateTime(date);
			firemanLog2.setOnlineClass(onlineClass);
			FiremanLog createdFiremanLog = firemanLogRepository.create(firemanLog2);
			
			return createdFiremanLog;
		}		
	}
	
	public FiremanLog checkStudentNotEnterYell(Long onlineClassId) {
		logger.debug("checkStudentNotEnterYell: " + onlineClassId);
		FiremanLog firemanLog1 = new FiremanLog();
		firemanLog1 =  firemanLogRepository.findFiremanLogByOnlineClassIdAndStudentNotEnter(onlineClassId);
		if(firemanLog1 != null){
			return firemanLog1;
		}
		else{
			Date date = new Date();
			OnlineClass onlineClass = new OnlineClass();
			onlineClass.setId(onlineClassId);
			
			FiremanLog firemanLog2 = new FiremanLog();
			firemanLog2.setEvent(Event.STUDENT_NOT_ENTER);
			firemanLog2.setHelpYell(false);
			firemanLog2.setCreatedDateTime(date);
			firemanLog2.setOnlineClass(onlineClass);
			FiremanLog createdFiremanLog = firemanLogRepository.create(firemanLog2);
			
			return createdFiremanLog;
		}		
	}
	
}
