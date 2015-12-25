package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.AssessmentReport;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.AssessmentReportRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.Count;

@Service
public class AssessmentReportService {
	private Logger logger = LoggerFactory.getLogger(AssessmentReportService.class.getSimpleName());

	@Resource
	private AssessmentReportRepository assessmentReportRepository;
	
	@Resource
	private SecurityService securityService;

	public AssessmentReport find(long id) {
		logger.debug("find assessment report for id = {}", id);
		return assessmentReportRepository.find(id);
	}

	public List<AssessmentReport> findByStudentId(long studentId) {
		logger.debug("find assessment report for student = {}", studentId);
		return assessmentReportRepository.findByStudentId(studentId);
	}

	public List<AssessmentReport> list(String search, Long studentId, int start, int length) {
		logger.debug("list staff with params: search = {}, studentId = {}, start = {}, length = {}.", search, studentId, start, length);
		return assessmentReportRepository.list(search, studentId, start, length);
	}

	public Count count(String search, Long studentId) {
		logger.debug("count staff with params: search = {}, studentId ={}, status = {}.", search, studentId);
		return new Count(assessmentReportRepository.count(search, studentId));
	}

	public AssessmentReport create(AssessmentReport assessmentReport) {
		logger.debug("create assessment report: {}", assessmentReport);

		assessmentReportRepository.create(assessmentReport);
			
		securityService.logAudit(Level.INFO, Category.ASSESSMENT_REPORT_UPLOAD, "Create assessmentReport: " + assessmentReport.getName());
			
		return assessmentReport;
	}

	public AssessmentReport update(AssessmentReport assessmentReport) {
		logger.debug("update aassessment report: {}", assessmentReport);
		
		assessmentReportRepository.update(assessmentReport);
		
		securityService.logAudit(Level.INFO, Category.ASSESSMENT_REPORT_UPLOAD, "Update staff: " + assessmentReport.getName());
		
		return assessmentReport;
	}

}
