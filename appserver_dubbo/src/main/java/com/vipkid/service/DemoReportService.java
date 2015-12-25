package com.vipkid.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.DemoReport;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.User;
import com.vipkid.repository.DemoReportRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.UserAlreadyExistServiceException;

@Service
public class DemoReportService {
	private Logger logger = LoggerFactory.getLogger(DemoReportService.class.getSimpleName());

	@Resource
	private DemoReportRepository demoReportRepository;
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private SecurityService securityService;
	
	public DemoReport find(long id) {
		logger.info("find demo report for id = {}", id);
		DemoReport find = demoReportRepository.find(id);
		Student student = find.getOnlineClass().getStudents().get(0);
		if (find != null && student != null && find.getStudent().getId() != student.getId()) {
			find.setStudent(student);
			find = demoReportRepository.update(find);
		}
		return find;
	}
	
	public DemoReport findByOnlineClassId(long onlineClassId) {
		logger.info("find demo report for online class id = {}", onlineClassId);
		return demoReportRepository.findByOnlineClassId(onlineClassId);
	}

	public DemoReport findByStudentId(long studentId) {
		logger.info("find demo report for online class id = {}", studentId);
		DemoReport find = demoReportRepository.findByStudentId(studentId);
		if (find != null && find.getOnlineClass() != null) {
			Student student = find.getOnlineClass().getStudents().get(0);
			if (student != null && studentId != student.getId()) {
				find.setStudent(student);
				demoReportRepository.update(find);
				find = demoReportRepository.findByStudentId(studentId);
				return find;
			}
		}
		return find;
		// demoReportRepository.findByStudentId(studentId);
	}

	public DemoReport update(DemoReport demoReport) {
		logger.info("update demo report: {}", demoReport);
		changeStudent(demoReport);
		demoReportRepository.update(demoReport);
		if (demoReport.getStudent() != null) {
			securityService.logAudit(Level.INFO, Category.DEMO_REPORT_UPDATE, "Update demo report for student: " + demoReport.getStudent().getSafeName());
		}
		return demoReport;
	}

	private void changeStudent(DemoReport demoReport) {
		Student demoReportStudent = demoReport.getStudent();
		OnlineClass demoReportOnlineClass = demoReport.getOnlineClass();
		if (demoReportStudent != null && demoReportOnlineClass != null && CollectionUtils.isNotEmpty(demoReportOnlineClass.getStudents())) {
			if (demoReportStudent.getId() != demoReportOnlineClass.getStudents().get(0).getId()) {
				logger.info("update demo report,DemoReport's student not equal OnlineClass's student," + "change student,DemoReportID={},demoreport's studentID={},onlineClassID={}",
						demoReport.getId(), demoReport.getStudent().getId(), demoReportOnlineClass.getStudents().get(0).getId());
				demoReport.setStudent(demoReportOnlineClass.getStudents().get(0));
			}
		}
	}

	
	public DemoReport create(DemoReport demoReport) {
		logger.info("create demoReport: {}", demoReport);
		
		DemoReport findDemoReport = this.find(demoReport.getId());
		if (findDemoReport == null) {
			demoReportRepository.create(demoReport);
			securityService.logAudit(Level.INFO, Category.DEMO_REPORT_CREATE, "Create demo report for student: " + demoReport.getStudent().getSafeName());
		} else {
			throw new UserAlreadyExistServiceException("Demo report already exist.");
		}
		
		return demoReport;
	}
	
	public DemoReport confirm(DemoReport demoReport) {
		logger.info("confirm demo report: {}", demoReport);
		changeStudent(demoReport);
		demoReport.setLifeCycle(DemoReport.LifeCycle.CONFIRMED);
		demoReport.setConfirmDateTime(new Date());
		demoReportRepository.update(demoReport);
		try {
			demoReport = demoReportRepository.find(demoReport.getId());
			Student student = studentRepository.find(demoReport.getStudent().getId());
			User currentUser = securityService.getCurrentUser();
			String staffName = currentUser.getName();
			if(currentUser instanceof Staff) {
				Staff currentStaff = (Staff)currentUser;
				staffName = currentStaff.getEnglishName();
			}
			EMail.sendToSaleTheDemoReportIsConfirmedEmail(demoReport, student, staffName);
		}catch(Exception e) {
			logger.error("Exception Send Email to sale when the student demo report is confirmed:" + e.getMessage(), e);
		}
		
		securityService.logAudit(Level.INFO, Category.DEMO_REPORT_CONFIRMED, "Confirm demo report for student: " + demoReport.getStudent().getSafeName());
		
		return demoReport;
	}

	public DemoReport submit(DemoReport demoReport) {
		logger.info("submit demo report: {}", demoReport);
		changeStudent(demoReport);
		demoReport.setLifeCycle(DemoReport.LifeCycle.SUBMITTED);
		demoReport.setSubmitDateTime(new Date());
		demoReportRepository.update(demoReport);				
		securityService.logAudit(Level.INFO, Category.DEMO_REPORT_CONFIRMED, "submit demo report for student: " + demoReport.getStudent().getSafeName());
		
		return demoReport;
	}
	
}
