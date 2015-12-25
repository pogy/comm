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

import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherApplication.Result;
import com.vipkid.model.TeacherApplication.Status;
import com.vipkid.service.TeacherApplicationService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/teacherApplications")
public class TeacherApplicationController {
	
	private Logger logger = LoggerFactory.getLogger(TeacherApplicationController.class.getSimpleName());

	@Resource
	private TeacherApplicationService teacherApplicationService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<TeacherApplication> list(@RequestParam("search") String search, @RequestParam("applyDateTimeFrom") DateTimeParam applyDateTimeFrom, @RequestParam("applyDateTimeTo") DateTimeParam applyDateTimeTo,  @RequestParam("auditDateTimeFrom") DateTimeParam auditDateTimeFrom, @RequestParam("auditDateTimeTo") DateTimeParam auditDateTimeTo, @RequestParam("status") Status status, @RequestParam("result") Result result, @RequestParam("start") Integer start, @RequestParam("length") Integer length) {
		logger.info("list application with params: search = {}, applyDateTimeFrom = {}, applyDateTimeTo = {}, auditDateTimeFrom = {}, auditDateTimeTo = {}, stauts = {}, result = {}, start = {}, length = {}.", search, applyDateTimeFrom, applyDateTimeTo, status, result, start, length);
		return teacherApplicationService.list(search, applyDateTimeFrom, applyDateTimeTo, auditDateTimeFrom, auditDateTimeTo, status, result, start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam("search") String search, @RequestParam("applyDateTimeFrom") DateTimeParam applyDateTimeFrom, @RequestParam("applyDateTimeTo") DateTimeParam applyDateTimeTo,  @RequestParam("auditDateTimeFrom") DateTimeParam auditDateTimeFrom, @RequestParam("auditDateTimeTo") DateTimeParam auditDateTimeTo, @RequestParam("status") Status status, @RequestParam("result") Result result, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("count application with params: search = {}, applyDateTimeFrom = {}, applyDateTimeTo = {}, auditDateTimeFrom = {}, auditDateTimeTo = {}, stauts = {}, result = {}, start = {}, length = {}.", search, applyDateTimeFrom, applyDateTimeTo, status, result, start, length);
		return teacherApplicationService.count(search, applyDateTimeFrom, applyDateTimeTo, auditDateTimeFrom, auditDateTimeTo, status, result, start, length);
	}
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public TeacherApplication find(@RequestParam("applicationId") long applicationId){
		logger.info("find application with params: applicationId = {}.", applicationId);
		return teacherApplicationService.find(applicationId);
	}
	
	@RequestMapping(value = "/findByTeacherId", method = RequestMethod.GET)
	public List<TeacherApplication> findByTeacherId(@RequestParam("teacherId") long teacherId){
		logger.info("list application with params: teacherId = {}.", teacherId);
		return teacherApplicationService.findByTeacherId(teacherId);
	}
	
	@RequestMapping(value = "/findCurrentByTeacherId", method = RequestMethod.GET)
	public TeacherApplication findCurrentByTeacherId(@RequestParam("teacherId") long teacherId){
		logger.info("find current application with params: teacherId = {}.", teacherId);
		return teacherApplicationService.findCurrentByTeacherId(teacherId);
	}
	
	@RequestMapping(value = "/audit", method = RequestMethod.PUT)
	public TeacherApplication audit(@RequestBody TeacherApplication teacherApplication) {
		logger.info("audit application with params: id = {}.", teacherApplication.getId());
		return teacherApplicationService.doAudit(teacherApplication);
	}
	
	@RequestMapping(value = "/apply", method = RequestMethod.PUT)
	public TeacherApplication apply(@RequestBody TeacherApplication teacherApplication) {
		logger.info("apply application with params: id = {}.", teacherApplication.getId());
		return teacherApplicationService.doApply(teacherApplication);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public TeacherApplication update(@RequestBody TeacherApplication application) {
		logger.info("update application with params:  id = {}.", application.getId());
		teacherApplicationService.update(application);
		return application;
	}
	
	@RequestMapping(value = "/findPreStepPassedTeacherApplicationByTeacherId", method = RequestMethod.GET)
	public TeacherApplication findPreStepPassedTeacherApplication(@RequestParam("status") Status status, @RequestParam("teacherId") long teacherId) {
		logger.info("find pre step passed application with params: status = {}, teacherId = {}.", status, teacherId);
		return teacherApplicationService.findPreStepPassedTeacherApplicationByTeacherId(status, teacherId);
	}
}
