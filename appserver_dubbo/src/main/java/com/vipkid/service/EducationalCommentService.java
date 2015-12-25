package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.EducationalComment;
import com.vipkid.repository.EducationalCommentRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.security.SecurityService;

@Service
public class EducationalCommentService {
	private Logger logger = LoggerFactory.getLogger(EducationalCommentService.class.getSimpleName());

	@Resource
	private EducationalCommentRepository educationalCommentRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private SecurityService securityService;

	public List<EducationalComment> findRecentByStudentIdAndClassIdAndAmount(long studentId,long onlineClassId,long amount) {
		logger.debug("find educationalComments for studentId = {}, onlineClassId={}, amount = {}", studentId, onlineClassId, amount);
		return educationalCommentRepository.findRecentByStudentIdAndClassIdAndAmount(studentId, onlineClassId, amount);
	}
	
	public List<EducationalComment> findRecentByStudentIdAndAmount(long studentId, long amount) {
		logger.debug("find educationalComments for studentId = {}, onlineClassId={}, amount = {}", studentId, amount);
		return educationalCommentRepository.findRecentByStudentIdAndAmount(studentId, amount);
	}
	
	public EducationalComment create(EducationalComment educationalComment) {
		logger.debug("create educationalComment: {}", educationalComment);
		
		educationalCommentRepository.create(educationalComment);
		
		securityService.logAudit(Level.INFO, Category.EDUCATIONAL_COMMENT_CREATE, "Create educationalComment for onlineClass: " + educationalComment.getOnlineClass().getSerialNumber());
		
		return educationalComment;
	}
	
	public EducationalComment update(EducationalComment educationalComment) {
		logger.debug("update teacherComment: {}", educationalComment);
		
		educationalCommentRepository.update(educationalComment);
		
		securityService.logAudit(Level.INFO, Category.EDUCATIONAL_COMMENT_UPDATE, "Update educationalComment for onlineClass: " + educationalComment.getOnlineClass().getSerialNumber());
		
		return educationalComment;
	}
	
	public EducationalComment find(long id) {
		logger.debug("find educationalComment for id = {}", id);
		return educationalCommentRepository.find(id);
	}
	
	public EducationalComment findByOnlineClassIdAndStudentId(long onlineClassId,long studentId) {
		logger.debug("find educationalComment for online class id = {}, student id = {}", onlineClassId, studentId);
		return educationalCommentRepository.findByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}
	
}
