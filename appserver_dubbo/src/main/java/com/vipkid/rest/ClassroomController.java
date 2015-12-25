package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.service.ClassroomService;
import com.vipkid.service.pojo.Classroom;

@RestController
@RequestMapping(value="/api/service/public/classrooms")
public class ClassroomController {
	
	@Resource
	private ClassroomService classroomService;
	
	private Logger logger = LoggerFactory.getLogger(ClassroomController.class.getSimpleName());

	@RequestMapping(value="/findOnlineClassById",method = RequestMethod.GET)
	public Classroom findOnlineClassById(@RequestParam("id") long id) {
		logger.info("find OnlineClass for id = {}" + id);
		return classroomService.findOnlineClassById(id);
	}
}
