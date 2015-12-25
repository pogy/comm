package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FiremanToTeacherCommentService;

@RestController
@RequestMapping(value="/api/service/private/firemanToTeacherComments")
public class FiremanToTeacherCommentController {
	private Logger logger = LoggerFactory.getLogger(FiremanToTeacherCommentController.class.getSimpleName());

	@Resource
	private FiremanToTeacherCommentService firemanToTeacherCommentService;

	
	@Resource
	private SecurityService securityService;

	
	
	@RequestMapping(method = RequestMethod.PUT)
	public FiremanToTeacherComment update(@RequestBody FiremanToTeacherComment firemanToTeacherComment) {
		logger.info("update FiremanComment: {}", firemanToTeacherComment);		
		return firemanToTeacherCommentService.update(firemanToTeacherComment);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public FiremanToTeacherComment create(@RequestBody FiremanToTeacherComment firemanToTeacherComment) {
		logger.info("create firemanComment: {}", firemanToTeacherComment);
		return firemanToTeacherCommentService.create(firemanToTeacherComment);
	}
	
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public FiremanToTeacherComment find(@RequestParam("id") long id) {
		logger.info("find teacherComment for id = {}", id);
		return firemanToTeacherCommentService.find(id);
	}
	
	
	@RequestMapping(value="/findByOnlineClass",method = RequestMethod.GET)
	public FiremanToTeacherComment findByOnlineClass(@RequestParam("onlineClassId") long onlineClassId){
		logger.info("find teacherComment for OnlineClassid = {}", onlineClassId);
		return firemanToTeacherCommentService.findByOnlineClass(onlineClassId);
	}
	
	@RequestMapping(value="/findByOnlineClassIdAndStudentId",method = RequestMethod.GET)
	public FiremanToTeacherComment findByOnlineClassIdAndStudentId(@RequestParam("onlineClassId") long onlineClassId,@RequestParam("studentId") long teacherId){
		logger.info("find teacherComment for onlineClassId = {},studentId", onlineClassId,teacherId);
		return firemanToTeacherCommentService.findByOnlineClassIdAndStudentId(onlineClassId,teacherId);
	}	
	
	
	
}
