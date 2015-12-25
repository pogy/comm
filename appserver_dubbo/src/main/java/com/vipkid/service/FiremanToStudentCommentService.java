package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.repository.FiremanToStudentCommentRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.util.TextUtils;

@Service
public class FiremanToStudentCommentService {
	private Logger logger = LoggerFactory.getLogger(FiremanToStudentCommentService.class.getSimpleName());

	@Resource
	private FiremanToStudentCommentRepository firemanToStudentCommentRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private SecurityService securityService;

	
	
	public FiremanToStudentComment update(FiremanToStudentComment firemanToStudentComment) {
		logger.debug("create firemanComment: {}", firemanToStudentComment);
		if (firemanToStudentComment.getStudentBehaviorProblem().size() > 0 || firemanToStudentComment.getStudentITProblem().size() > 0 || !TextUtils.isEmpty(firemanToStudentComment.getSupplement())) {
			firemanToStudentComment.setEmpty(false);
		}
		firemanToStudentCommentRepository.update(firemanToStudentComment);			
		
		return firemanToStudentComment;
	}
	
	public FiremanToStudentComment create(FiremanToStudentComment firemanToStudentComment) {
		logger.debug("create firemanComment: {}", firemanToStudentComment);
		if (firemanToStudentComment.getStudentBehaviorProblem().size() > 0 || firemanToStudentComment.getStudentITProblem().size() > 0 || !TextUtils.isEmpty(firemanToStudentComment.getSupplement())) {
			firemanToStudentComment.setEmpty(false);
		}

		firemanToStudentCommentRepository.create(firemanToStudentComment);			
		securityService.logAudit(Level.INFO, Category.TEACHER_COMMENT_CREATE, "Create FiremanComment for onlineClass: " + firemanToStudentComment.getOnlineClass().getSerialNumber());
		
		return firemanToStudentComment;
	}
	
	
	public FiremanToStudentComment find(long id) {
		logger.debug("find teacherComment for id = {}", id);
		return firemanToStudentCommentRepository.find(id);
	}
	
	
	public List<FiremanToStudentComment> findByOnlineClass(long onlineClassId){
		return firemanToStudentCommentRepository.findByOnlineClassId(onlineClassId);
	}	
	

	public FiremanToStudentComment findByOnlineClassIdAndStudentId(long onlineClassId,long studentId){
		return firemanToStudentCommentRepository.findByOnlineClassIdAndStudentId(onlineClassId,studentId);
	}	
	
	
}
