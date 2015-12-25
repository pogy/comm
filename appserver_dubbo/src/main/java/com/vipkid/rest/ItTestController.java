package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.ItTest;
import com.vipkid.security.SecurityService;
import com.vipkid.service.ItTestService;

@RestController
@RequestMapping(value="/api/service/private/ittest")
public class ItTestController {
	private Logger logger = LoggerFactory.getLogger(ItTestController.class.getSimpleName());
	
	@Resource
	private ItTestService itTestService;	
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public ItTest find(@RequestParam("id") long id) {
		logger.info("find ItTest for id = {}", id);
		return itTestService.find(id);
	}
	
	@RequestMapping(value="/findByFamilyId",method = RequestMethod.GET)
	public List<ItTest> findByFamilyId(@RequestParam("familyId") long familyId) {
		logger.info("find ItTest for familyId = {}", familyId);
		return itTestService.findByFamilyId(familyId);
	}
	
	@RequestMapping(value="/findByTeacherId",method = RequestMethod.GET)
	public List<ItTest> findByTeacherId(@RequestParam("teacherId") long teacherId) {
		logger.info("find ItTest for teacherId = {}", teacherId);
		return itTestService.findByTeacherId(teacherId);
	}
	
	@RequestMapping(value="/findByStudentId",method = RequestMethod.GET)
	public List<ItTest> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find ItTest for studentId = {}", studentId);
		return itTestService.findByStudentId(studentId);
	}
}
