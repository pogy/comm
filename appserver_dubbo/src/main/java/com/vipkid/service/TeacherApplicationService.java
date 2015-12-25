package com.vipkid.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.Type;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherApplication.Result;
import com.vipkid.model.TeacherApplication.Status;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.TeacherApplicationRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@Service
public class TeacherApplicationService {

	private Logger logger = LoggerFactory.getLogger(TeacherApplicationService.class.getSimpleName());

	@Resource
	private SecurityService securityService;

	@Resource
	private TeacherApplicationRepository teacherApplicationRepository;

	@Resource
	private TeacherRepository teacherRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;

	@Resource
	private CourseRepository courseRepository;

	public List<TeacherApplication> list(String search, DateTimeParam applyDateTimeFrom, DateTimeParam applyDateTimeTo, DateTimeParam auditDateTimeFrom, DateTimeParam auditDateTimeTo, Status status,
			Result result, Integer start, Integer length) {
		logger.debug(
				"list application with params: search = {}, applyDateTimeFrom = {}, applyDateTimeTo = {}, auditDateTimeFrom = {}, auditDateTimeTo = {}, stauts = {}, result = {}, start = {}, length = {}.",
				search, applyDateTimeFrom, applyDateTimeTo, status, result, start, length);
		return teacherApplicationRepository.list(search, applyDateTimeFrom, applyDateTimeTo, auditDateTimeFrom, auditDateTimeTo, status, result, start, length);
	}

	public Count count(String search, DateTimeParam applyDateTimeFrom, DateTimeParam applyDateTimeTo, DateTimeParam auditDateTimeFrom, DateTimeParam auditDateTimeTo, Status status, Result result,
			int start, int length) {
		logger.debug(
				"count application with params: search = {}, applyDateTimeFrom = {}, applyDateTimeTo = {}, auditDateTimeFrom = {}, auditDateTimeTo = {}, stauts = {}, result = {}, start = {}, length = {}.",
				search, applyDateTimeFrom, applyDateTimeTo, status, result, start, length);
		return new Count(teacherApplicationRepository.count(search, applyDateTimeFrom, applyDateTimeTo, auditDateTimeFrom, auditDateTimeTo, status, result));
	}

	public TeacherApplication find(@QueryParam("applicationId") long applicationId) {
        TeacherApplication teacherApplication = teacherApplicationRepository.find(applicationId);
        setStep5hasResultPracticum2(teacherApplication);
        return teacherApplication;
	}

	public List<TeacherApplication> findByTeacherId(long teacherId) {
		return teacherApplicationRepository.findByTeacherId(teacherId);
	}

	public TeacherApplication findCurrentByTeacherId(long teacherId) {
		TeacherApplication teacherApplication = teacherApplicationRepository.findCurrentByTeacherId(teacherId);
		if (null == teacherApplication) {
			logger.error("findCurrentByTeacherId get null for "+teacherId);
			return null;
		}
        setStep5hasResultPracticum2(teacherApplication);
		return teacherApplication;
	}

	// 标记第五步是否有过result:
	private void setStep5hasResultPracticum2(TeacherApplication teacherApplication) {
		if (teacherApplication.getStatus() == Status.PRACTICUM) {
			boolean step5hasResultPracticum2 = findWhetherHasResultPracticum2ByTeacherId(teacherApplication.getTeacher().getId());
			teacherApplication.setStep5hasBeenReapply(step5hasResultPracticum2);
		}
	}

	// 标记第5步是否有过result:Practicum2
	public boolean findWhetherHasResultPracticum2ByTeacherId(long teacherId) {
		List<TeacherApplication> reapplyTeacherApplications = teacherApplicationRepository.findWhetherHasResultPracticum2ByTeacherId(teacherId);
		if (reapplyTeacherApplications.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public TeacherApplication doAudit(TeacherApplication teacherApplication) {

		teacherApplication.setAuditDateTime(new Date());
		teacherApplication.setAuditor(securityService.getCurrentUser());
		teacherApplicationRepository.update(teacherApplication);

		setTeacherExtraClassSalary(teacherApplication);

		String operation = "Audit the Application[" + teacherApplication.getId() + "," + teacherApplication.getStatus() + "] as" + teacherApplication.getResult();
		securityService.logAudit(Level.INFO, Category.AUDIT_APPLICATION, operation);

		Teacher appliedTeacher = teacherRepository.find(teacherApplication.getTeacher().getId());
		teacherApplication.setTeacher(appliedTeacher);
		
		String strRefereeMail = getTeacherRefereeMail(teacherApplication);
		
		EMail.sendToTeacherRecruitmentResult(teacherApplication,strRefereeMail);

		// 2015-05-12 如果完成了所有的recruit phase， set lifeCycle
		if (teacherApplication.getStatus().equals(Status.PRACTICUM) && teacherApplication.getResult().equals(Result.PASS)) {
			teacherApplication.setStatus(Status.FINISHED);
			long tid = teacherApplication.getTeacher().getId();
			Teacher foundTeacher = teacherRepository.find(tid);
			if (foundTeacher != null) {
				foundTeacher.setLifeCycle(LifeCycle.REGULAR);
				foundTeacher.setType(Type.PART_TIME);
				Course course = courseRepository.findByCourseType(Course.Type.MAJOR);
				List<Course> courses = new ArrayList<Course>();
				courses.add(course);
				foundTeacher.setCertificatedCourses(courses);
				teacherRepository.update(foundTeacher);
			}
		}else if(teacherApplication.getStatus().equals(Status.INTERVIEW) && teacherApplication.getResult().equals(Result.PASS)){
			if (teacherApplication.getOnlineClass() != null) {
				long onlinClassId = teacherApplication.getOnlineClass().getId();
				OnlineClass oc = onlineClassRepository.find(onlinClassId);
				oc.setStatus(OnlineClass.Status.FINISHED);
				onlineClassRepository.update(oc);
			}
		}

		return teacherApplication;
	}

	/**
	 * 2015-09-17 为推荐渠道 - 加入refer的邮箱
	 * @param teacherApplication
	 * @return
	 */
	private String getTeacherRefereeMail(TeacherApplication teacherApplication) {
		
		String strRefereeMail = null;
		
		if (null == teacherApplication.getTeacher()) {
			return null;
		}
		
		String strRefer = teacherApplication.getTeacher().getReferee();
		
		if (StringUtils.isEmpty(strRefer)) {
			return null;
		}
		
		// 获取current teacher's email.
		String[] strParts = strRefer.split(",");
		if (strParts.length < 0) {
			return null;
		}

		String refereeId = strParts[0];

		try {
			long teacherId = 0;
			teacherId = Long.parseLong(refereeId);

			Teacher referee = teacherRepository.find(teacherId);
			String teacherMail = referee.getEmail();
			return teacherMail;
		} catch (Exception e) {
			//
			logger.error("Error teacher's referee id");
		}

		return null;
	}

	private void setTeacherExtraClassSalary(TeacherApplication teacherApplication) {
		try {
			if (teacherApplication.getStatus() == Status.INTERVIEW) {
				Teacher teacher = teacherRepository.find(teacherApplication.getTeacher().getId());
				Course course = courseRepository.findByCourseType(Course.Type.MAJOR);
				float teacherApplicationBasePay = teacherApplication.getBasePay();
				float courseBaseClassSalary = course.getBaseClassSalary();
				float teacherExtraClassSalary = teacherApplicationBasePay - courseBaseClassSalary;
				teacher.setExtraClassSalary(teacherExtraClassSalary);
				teacherRepository.update(teacher);
			}
		} catch (Exception e) {
			logger.error("set extra class salary failed :", e);
		}
	}

	public TeacherApplication doApply(TeacherApplication teacherApplication) {
		Status status = teacherApplication.getStatus();
		if (status == Status.FINISHED) {
			throw new IllegalStateException("Invalide Status");
		}

		TeacherApplication application = teacherApplicationRepository.findCurrentByTeacherIdAndStatus(teacherApplication.getTeacher().getId(), status);
		if (application != null && application.getResult() == null) {
			application.setStudent(teacherApplication.getStudent());
			application.setOnlineClass(teacherApplication.getOnlineClass());

			teacherApplicationRepository.update(application);
			logger.debug("Find the teacherApplication, update it");
			return application;
		}

		List<TeacherApplication> teacherApplications = teacherApplicationRepository.findByTeacherId(teacherApplication.getTeacher().getId());
		for (TeacherApplication tmp : teacherApplications) {
			if (tmp.isCurrent()) {
				tmp.setCurrent(false);
			}
			teacherApplicationRepository.update(tmp);
		}

		teacherApplication.setCurrent(true);
		teacherApplication.setApplyDateTime(new Date());
		teacherApplication.setTeacher(teacherApplication.getTeacher());
		teacherApplicationRepository.create(teacherApplication);

		return teacherApplication;

	}
	
	public TeacherApplication doApplyForRecritment(TeacherApplication teacherApplication) {
		Status status = teacherApplication.getStatus();
		if (status == Status.FINISHED) {
			throw new IllegalStateException("Invalide Status");
		}


		List<TeacherApplication> teacherApplications = teacherApplicationRepository.findByTeacherId(teacherApplication.getTeacher().getId());
		for (TeacherApplication tmp : teacherApplications) {
			if (tmp.isCurrent()) {
				tmp.setCurrent(false);
			}
			teacherApplicationRepository.update(tmp);
		}
				

		teacherApplication.setCurrent(true);
		teacherApplication.setApplyDateTime(new Date());
		teacherApplication.setTeacher(teacherApplication.getTeacher());
		teacherApplicationRepository.create(teacherApplication);

		return teacherApplication;

	}

	public TeacherApplication update(TeacherApplication application) {
		teacherApplicationRepository.update(application);
		return application;
	}

	public TeacherApplication findPreStepPassedTeacherApplicationByTeacherId(Status status, long teacherId) {
		return teacherApplicationRepository.findPreStepPassedTeacherApplicationByTeacherId(status, teacherId);
	}
	
	public TeacherApplication findCurrentByTeacherIdAndStatus(long teacherId, Status status) {
		return teacherApplicationRepository.findCurrentByTeacherIdAndStatus(teacherId, status);
	}
	
	public TeacherApplication create(TeacherApplication teacherApplication) {
		return teacherApplicationRepository.create(teacherApplication);
	}

	
}
