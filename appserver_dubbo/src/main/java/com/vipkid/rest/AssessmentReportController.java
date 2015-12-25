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

import com.vipkid.model.AssessmentReport;
import com.vipkid.service.AssessmentReportService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/assessmentReports")
public class AssessmentReportController {
	private Logger logger = LoggerFactory.getLogger(AssessmentReportController.class.getSimpleName());

	@Resource
	private AssessmentReportService assessmentReportService;

	@RequestMapping(value="/find", method=RequestMethod.GET)
	public AssessmentReport find(@RequestParam(value="id") long id) {
		logger.debug("find assessment report for id = {}", id);
		return assessmentReportService.find(id);
	}

	@RequestMapping(value="/findByStudentId", method=RequestMethod.GET)
	public List<AssessmentReport> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.debug("find assessment report for student = {}", studentId);
		return assessmentReportService.findByStudentId(studentId);
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<AssessmentReport> list(@RequestParam(value="search",required=false) String search, @RequestParam(value="studentId",required=false) Long studentId,  @RequestParam("start") Integer start, @RequestParam("length") Integer length) {
		if(null==studentId){
			studentId=0l;
		}
		if(null==start){
			start=0;
		}
		if(null==length){
			length=0;
		}
		logger.debug("list staff with params: search = {}, studentId = {}, start = {}, length = {}.", search, studentId, start, length);
		return assessmentReportService.list(search, studentId, start, length);
	}

	@RequestMapping(value="/count", method=RequestMethod.GET)
	public Count count(@RequestParam(value="search",required=false) String search, @RequestParam(value="studentId",required=false) Long studentId) {
		if(null==studentId){
			studentId=0l;
		}
		logger.debug("count staff with params: search = {}, studentId ={}, status = {}.", search, studentId);
		return assessmentReportService.count(search, studentId);
	}

	@RequestMapping(method=RequestMethod.POST)
	public AssessmentReport create(@RequestBody AssessmentReport assessmentReport) {
		logger.debug("create assessment report: {}", assessmentReport);

		return assessmentReportService.create(assessmentReport);
	}

	@RequestMapping(method=RequestMethod.PUT)
	public AssessmentReport update(@RequestBody AssessmentReport assessmentReport) {
		logger.debug("update aassessment report: {}", assessmentReport);
		
		return assessmentReportService.update(assessmentReport);
	}

}
