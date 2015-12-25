package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.EducationalComment;
import com.vipkid.security.SecurityService;
import com.vipkid.service.EducationalCommentService;

@RestController
@RequestMapping(value="/api/service/private/educationalComments")
public class EducationalCommentController {
	private Logger logger = LoggerFactory.getLogger(EducationalCommentController.class.getSimpleName());

	@Resource
	private EducationalCommentService educationalCommentService;
	
	
	@Resource
	private SecurityService securityService;

	@RequestMapping(value="/findRecentByStudentIdAndClassIdAndAmount",method = RequestMethod.GET)
	public List<EducationalComment> findRecentByStudentIdAndClassIdAndAmount(@RequestParam("studentId") long studentId, @RequestParam("onlineClassId") long onlineClassId,  @RequestParam("amount") long amount) {
		logger.info("find educationalComments for studentId = {}, onlineClassId={}, amount = {}", studentId, onlineClassId, amount);
		return educationalCommentService.findRecentByStudentIdAndClassIdAndAmount(studentId, onlineClassId, amount);
	}
	
	@RequestMapping(value="/findRecentByStudentIdAndAmount",method = RequestMethod.GET)
	public List<EducationalComment> findRecentByStudentIdAndAmount(@RequestParam("studentId") long studentId, @RequestParam("amount") long amount) {
		logger.info("find educationalComments for studentId = {}, onlineClassId={}, amount = {}", studentId, amount);
		return educationalCommentService.findRecentByStudentIdAndAmount(studentId, amount);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public EducationalComment create(@RequestBody EducationalComment educationalComment) {
		logger.info("create educationalComment: {}", educationalComment);
		return educationalCommentService.create(educationalComment);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public EducationalComment update(@RequestBody EducationalComment educationalComment) {
		logger.info("update teacherComment: {}", educationalComment);
		
		educationalCommentService.update(educationalComment);
		
		securityService.logAudit(Level.INFO, Category.EDUCATIONAL_COMMENT_UPDATE, "Update educationalComment for onlineClass: " + educationalComment.getOnlineClass().getSerialNumber());
		
		return educationalComment;
	}
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public EducationalComment find(@RequestParam("id") long id) {
		logger.info("find educationalComment for id = {}", id);
		return educationalCommentService.find(id);
	}
	
	@RequestMapping(value="/findByOnlineClassIdAndStudentId",method = RequestMethod.GET)
	public EducationalComment findByOnlineClassIdAndStudentId(@RequestParam("onlineClassId") long onlineClassId, @RequestParam("studentId") long studentId) {
		logger.info("find educationalComment for online class id = {}, student id = {}", onlineClassId, studentId);
		return educationalCommentService.findByOnlineClassIdAndStudentId(onlineClassId, studentId);
	}
	
}
