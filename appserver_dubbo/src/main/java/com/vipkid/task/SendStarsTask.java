package com.vipkid.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.model.TeacherComment;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.repository.UpdateStarsRepository;

@Component
public class SendStarsTask {
	private Logger logger = LoggerFactory.getLogger(SendStarsTask.class.getSimpleName());
	
	@Resource
    private TeacherCommentRepository teacherCommentRepository;
	
	@Resource
	private UpdateStarsRepository updateStarsRepository;
	
	@Scheduled(cron = "0 0,30 * * * ?") 
	//@Scheduled(fixedRate = 5000) 
	public void sendStarsToStudents(){
		try{
			List<TeacherComment> teacherComments = teacherCommentRepository.findTeacherCommentFinishedBeforeTwoHours();
			if(CollectionUtils.isNotEmpty(teacherComments)){
				for (TeacherComment tec: teacherComments) {
					updateStarsRepository.updateStarsAndOperatorIdByTeacherCommentId(tec.getId(), 5, -2);
					if(tec.getStudent()!=null){
						updateStarsRepository.updateStarsByStudentId(tec.getStudent().getId(), 5);
					}
					logger.info("Success: sendStarsToStudents teacherCommentId = {} ",tec.getId());
				}
			}
		}catch (Exception e){
			logger.error("Exception found when sendStarsToStudents:" + e.getMessage(), e);
		}
		
	}
	
}
