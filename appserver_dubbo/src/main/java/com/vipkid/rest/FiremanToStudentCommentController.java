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

import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FiremanToStudentCommentService;

@RestController
@RequestMapping(value="/api/service/private/firemanToStudentComments")
public class FiremanToStudentCommentController {
	private Logger logger = LoggerFactory.getLogger(FiremanToStudentCommentController.class.getSimpleName());

	@Resource
	private FiremanToStudentCommentService	firemanToStudentCommentService;
	
	@Resource
	private SecurityService securityService;

	
	
	@RequestMapping(method = RequestMethod.PUT)
	public FiremanToStudentComment update(@RequestBody FiremanToStudentComment firemanToStudentComment) {
		logger.info("update FiremanComment: {}", firemanToStudentComment);		
		return firemanToStudentCommentService.update(firemanToStudentComment);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public FiremanToStudentComment create(@RequestBody FiremanToStudentComment firemanToStudentComment) {
		logger.info("create firemanComment: {}", firemanToStudentComment);
		return firemanToStudentCommentService.create(firemanToStudentComment);
	}
	
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public FiremanToStudentComment find(@RequestParam("id") long id) {
		logger.info("find teacherComment for id = {}", id);
		return firemanToStudentCommentService.find(id);
	}
	
	
	@RequestMapping(value="/findByOnlineClass",method = RequestMethod.GET)
	public List<FiremanToStudentComment> findByOnlineClass(@RequestParam("onlineClassId") long onlineClassId){
		logger.info("find OnlineClass by id: {}", onlineClassId);
		return firemanToStudentCommentService.findByOnlineClass(onlineClassId);
	}	
	

	@RequestMapping(value="/findByOnlineClassIdAndStudentId",method = RequestMethod.GET)
	public FiremanToStudentComment findByOnlineClassIdAndStudentId(@RequestParam("onlineClassId") long onlineClassId,@RequestParam("studentId") long studentId){
		logger.info("find OnlineClass by id: onlineClassId ={},studentId={}", onlineClassId,studentId);
		return firemanToStudentCommentService.findByOnlineClassIdAndStudentId(onlineClassId,studentId);
	}	
	
	
}
