package com.vipkid.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.repository.FiremanToTeacherCommentRepository;
import com.vipkid.security.SecurityService;

@Service
public class FiremanToTeacherCommentService {
	private Logger logger = LoggerFactory.getLogger(FiremanToTeacherCommentService.class.getSimpleName());

	@Resource
	private FiremanToTeacherCommentRepository firemanToTeacherCommentRepository;

	
	@Resource
	private SecurityService securityService;

	
	
	public FiremanToTeacherComment update(FiremanToTeacherComment firemanToTeacherComment) {
		logger.debug("update FiremanComment: {}", firemanToTeacherComment);		
		firemanToTeacherComment.setEmpty(false);
		firemanToTeacherCommentRepository.update(firemanToTeacherComment);		
		securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_UPDATE, "Update FiremanComment for onlineClass: " + firemanToTeacherComment.getOnlineClass().getSerialNumber());
		return firemanToTeacherComment;
	}
	
	public FiremanToTeacherComment create(FiremanToTeacherComment firemanToTeacherComment) {
		logger.debug("create firemanComment: {}", firemanToTeacherComment);
		
		firemanToTeacherCommentRepository.create(firemanToTeacherComment);			
		securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_CREATE, "Create FiremanComment for onlineClass: " + firemanToTeacherComment.getOnlineClass().getSerialNumber());
		
		return firemanToTeacherComment;
	}
	
	
	public FiremanToTeacherComment find(long id) {
		logger.debug("find teacherComment for id = {}", id);
		return firemanToTeacherCommentRepository.find(id);
	}
	
	
	public FiremanToTeacherComment findByOnlineClass(long onlineClassId){
		return firemanToTeacherCommentRepository.findByOnlineClassId(onlineClassId);
	}
	
	public FiremanToTeacherComment findByOnlineClassIdAndStudentId(long onlineClassId,long teacherId){
		return firemanToTeacherCommentRepository.findByOnlineClassIdAndTeacherId(onlineClassId,teacherId);
	}	
	
	
	
}
