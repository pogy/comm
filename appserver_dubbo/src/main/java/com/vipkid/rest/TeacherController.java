package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.handler.TeacherHandler;
import com.vipkid.model.Country;
import com.vipkid.model.Course;
import com.vipkid.model.Gender;
import com.vipkid.model.ItTest.FinalResult;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.Hide;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.User.Status;
import com.vipkid.rest.vo.query.TeacherQueryCourseView;
import com.vipkid.rest.vo.query.TeacherQueryOnlineClassView;
import com.vipkid.rest.vo.query.TeacherQueryPartnerView;
import com.vipkid.rest.vo.query.TeacherQueryTApplicationView;
import com.vipkid.rest.vo.query.TeacherQueryTeacherView;
import com.vipkid.security.SecurityService;
import com.vipkid.service.StaffService;
import com.vipkid.service.TeacherApplicationService;
import com.vipkid.service.TeacherLifeCycleLogService;
import com.vipkid.service.TeacherService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.ParentPortalTeacherQueryResult;
import com.vipkid.service.pojo.StringWrapper;
import com.vipkid.service.pojo.TeacherInfoVO;
import com.vipkid.service.pojo.TeacherNameView;

@RestController
@RequestMapping("/api/service/private/teachers")
public class TeacherController {
	private Logger logger = LoggerFactory.getLogger(TeacherController.class.getSimpleName());
	
	@Resource
	private TeacherService teacherService;
	
	@Resource
	private TeacherApplicationService teacherApplicationService;
	
	//2015-08-15 添加security service，获取当前操作员
	@Resource
	private SecurityService securityService;//.getCurrentUser(),
	
	//2015-08-15 添加StaffService，获取当前staff.
	@Resource
	private StaffService staffService;
	
	@Resource
	private TeacherLifeCycleLogService teacherLifeCycleLogService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Teacher find(@RequestParam("id") long id) {
		logger.info("find teacher for id = {}", id);
		return teacherService.find(id);
	}
    @RequestMapping(value = "/findPersonalInfo", method = RequestMethod.GET)
    public TeacherInfoVO findPersonalInfo(@RequestParam("id") long id) {
        logger.info("find teacher for id = {}", id);
        Teacher teacher = teacherService.find(id);
        return TeacherHandler.convertVO(teacher);
    }
    @RequestMapping(value = "/updatePersonalInfo", method = RequestMethod.PUT)
    public TeacherInfoVO updatePersonalInfo(@RequestBody TeacherInfoVO teacherInfoVO) {
        logger.info("updatePersonalInfo teacher for id = {}", teacherInfoVO.getId());
        teacherService.updatePersonalInfo(teacherInfoVO);
        return teacherInfoVO;
    }
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Teacher> list(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "contractStartDate", required = false) String contractStartDate,
			@RequestParam(value = "contractEndDate", required = false) String contractEndDate,
			@RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
			@RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
			@RequestParam(value = "certificatedCourseId", required = false) Long certificatedCourseId,
			@RequestParam(value = "partnerId", required = false) Long partnerId,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "finalResult", required = false) String finalResult,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "teacherTags",required = false) String teacherTags) {
		logger.info("list teacher with params: search = {}, gender = {}, status = {},, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}, start = {}, length = {}.", search, gender, status, country,  contractStartDate, contractEndDate, certificatedCourseId, partnerId, teacherType, finalResult, start, length);
		
		return teacherService.list(Arrays.asList(lifeCycles==null?new String[0]:lifeCycles), search,
				gender == null ? null : Gender.valueOf(gender),
				status == null ? null : Status.valueOf(status),
				country == null ? null : Country.valueOf(country),
				contractStartDate == null ? null : new DateTimeParam(contractStartDate),
				contractEndDate == null ? null : new DateTimeParam(contractEndDate),
				scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
				scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
				certificatedCourseId, partnerId,
				teacherType,
				finalResult == null ? null : FinalResult.valueOf(finalResult),
				start, length,teacherTags);
	}
	
	@RequestMapping(value = "/findBySearchCondition", method = RequestMethod.GET)
	public List<TeacherNameView> findBySearchCondition(@RequestParam("search") String search, @RequestParam("courseId") long courseId, @RequestParam("length") int length) {
		logger.info("find teacher with params: search = {}, courseId = {}, length = {}.", search, courseId, length);
		return teacherService.findBySearchCondition(search, courseId, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "lifeCycle", required = false) String lifeCycle,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "contractStartDate", required = false) String contractStartDate,
			@RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
			@RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
			@RequestParam(value = "contractEndDate", required = false) String contractEndDate,
			@RequestParam(value = "courses", required = false) Long certificatedCourseId,
			@RequestParam(value = "recruit_channel", required = false) Long partnerId,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "finalResult", required = false) String finalResult,
			@RequestParam(value = "teacherTags",required = false) String teacherTags,
			@RequestParam(value="accountType", required = false) String strAccountType,
			// 2015-08-13 添加其他条件 TM-improve quit-time 时间段
			@RequestParam(value="quitStartDate", required = false) String strQuitStartDate,
			@RequestParam(value="quitEndDate", required = false) String strQuitEndDate,
			// 2015-08-13 添加其他条件 TM-improve apply-time 时间段 -- 和application相关的
			@RequestParam(value="applyStartDate", required = false) String strApplyFromDate,
			@RequestParam(value="applyEndtDate", required = false) String strApplyEndDate,
			@RequestParam(value="applyResult",required=false) String applyResult,
			@RequestParam(value="interviewer",required = false) String interviewerId,
			@RequestParam(value="practicumTeacher",required = false) String practicumTeacherId,
			@RequestParam(value="managers",required = false) String[] managers
			) {
		logger.info("count teacher with params: search = {}, gender = {}, status = {}, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}.", search, gender, status, lifeCycle, country, contractStartDate, contractEndDate, certificatedCourseId, teacherType, finalResult);
		return teacherService.count(Arrays.asList(lifeCycles==null?new String[0]:lifeCycles), search,
				gender == null ? null :  Gender.valueOf(gender),
				status == null ? null : Status.valueOf(status),
				lifeCycle == null ? null :  LifeCycle.valueOf(lifeCycle),
				country == null ? null :  Country.valueOf(country),
				contractStartDate == null ? null : new DateTimeParam(contractStartDate),
				contractEndDate == null ? null : new DateTimeParam(contractEndDate),
				scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
				scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
				certificatedCourseId,
				partnerId, teacherType,
				finalResult == null ? null : FinalResult.valueOf(finalResult),teacherTags,
				strAccountType,applyResult,interviewerId,practicumTeacherId,managers);
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public Teacher resetPassword(@RequestParam("id") long id) {
		logger.info("reset teacher password with params: id = {}", id);
		return teacherService.doResetPassword(id);
	}
	
	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public Teacher lock(@RequestParam("id") long id) {
		logger.info("lock teacher with params: id = {}", id);
		return teacherService.doLock(id);
	}
	
	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public Teacher unlock(@RequestParam("id") long id) {
		logger.info("unlock teacher with params: id = {}", id);
		return teacherService.doUnlock(id);
	}
	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ParentPortalTeacherQueryResult query(
			@RequestParam(value="studentUserId", required=false) long studentUserId,
			@RequestParam(value="teacher", required=false) String teacherCondition,
			@RequestParam(value="available", required=false) String availableCondition,
			@RequestParam(value="hide", required=false) Hide hide,
			@RequestParam(value="amount", required=false) long amount,
			@RequestParam(value="start", required=false) long start,
			@RequestParam(value="courseId", required=false) long courseId,
			@RequestParam(value="startDate", required=false) DateParam startDateParam,
			@RequestParam(value="endDate", required=false) DateParam endDateParam,
			@RequestParam(value="dateFilter", required=false) DateParam dateFilter,
			@RequestParam(value="timeFilter", required=false) DateParam timeFilter) {
		logger.info("query teacher with params: studentUserId = {}, teacherCondition = {}, availableCondition = {}, hide = {}, amount = {}, start = {}, courseId = {}, startDateParam = {}, endDateParam = {}, dateFilter = {}, timeFilter = {}.", 
				studentUserId, teacherCondition, availableCondition, hide, amount, start, courseId, startDateParam, endDateParam, dateFilter, timeFilter);
		return teacherService.query(studentUserId, teacherCondition, availableCondition, hide, amount, start, courseId, startDateParam, endDateParam, dateFilter, timeFilter);
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public List<Teacher> search(
			@RequestParam("date") DateParam dateParam,
			@RequestParam("gender") String gender,
			@RequestParam("studentId") long studentUserId,
			@RequestParam("teacherName") String teacherName,
			@RequestParam("hide") Hide hide) {
		logger.info("in search service: data = {}, gender = {}, studentUserId = {}, teacherName = {}", dateParam, gender, studentUserId, teacherName);

		return teacherService.search(dateParam, gender, studentUserId, teacherName, hide);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Teacher create(@RequestBody Teacher teacher) {
		logger.info("create teacher: {}", teacher);
		return teacherService.create(teacher);
	}

	/**
	 * teacher recruit apply update.
	 * it should send apply email to applicant and partner.
	 * @param teacher
	 * @return
	 */
	@RequestMapping(value = "/updateApply", method = RequestMethod.PUT)
	public Teacher updateApply(@RequestBody Teacher teacher) {
		logger.info("updateApply teacher: {}", teacher);
		return teacherService.updateApply(teacher);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Teacher update(@RequestBody Teacher teacher) {
		logger.info("update teacher: {}", teacher);
//		return teacherService.updateApply(teacher);	// 2015-07-10 更新teacher，不需要处理apply邮件
		
//		// 2015-08-15 设置operator
//		try {
//			User user = securityService.getCurrentUser();
//			Staff staff = staffService.find(user.getId());
//			if (null == teacher.getQuitOperator()) {
//				//
//				teacher.setQuitOperator(staff);
//			}
//				
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
		return teacherService.update(teacher);
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	public Teacher changePassword(@RequestParam("teacherId") long teacherId, @RequestParam("password") String password, @RequestParam("originalPassword") String originalPassword) {
		logger.info("change password for teacherId: {}", teacherId);
		return teacherService.changePassword(teacherId, password, originalPassword);
	}
	
	@RequestMapping(value = "/next", method = RequestMethod.PUT)
	public Teacher next(@RequestBody Teacher teacher) {
		logger.info("next teacher: {}", teacher);
		return teacherService.doNext(teacher);
	}
	
	@RequestMapping(value = "/findNotExistsOnlineClassByScheduledDateAndCertificatedCourseId", method = RequestMethod.GET)
	public List<Teacher> findNotExistsOnlineClassByScheduledDateAndCertificatedCourseId(@RequestParam("scheduledDateTime") DateTimeParam scheduledDateTime, @RequestParam("courseId") Long courseId) {
		logger.info("find not exists onlineClass for scheduled date = {}, certificated course id = {}", scheduledDateTime, courseId);
		return teacherService.findNotExistsOnlineClassByScheduledDate(scheduledDateTime.getValue(), courseId);
	}
	
	@RequestMapping(value = "/findLifeCycleById", method = RequestMethod.GET)
	public StringWrapper findLifeCycleById(@RequestParam("id")Long id){
		return new StringWrapper(teacherService.findLifeCycleById(id).toString());
	}
	
	@RequestMapping(value = "/listNameAndId", method = RequestMethod.GET)
	public List<StringWrapper> listNameAndId(){
		logger.info("list teacher name and id");
		return teacherService.listNameAndId();
	}
	
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public List<TeacherQueryTeacherView> filter(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "contractStartDate", required = false) String contractStartDate,
			@RequestParam(value = "contractEndDate", required = false) String contractEndDate,
			@RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
			@RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
			@RequestParam(value = "certificatedCourseId", required = false) Long certificatedCourseId,
			@RequestParam(value = "partnerId", required = false) Long partnerId,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "finalResult", required = false) String finalResult,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "teacherTags",required = false) String teacherTags) {
		logger.info("list teacher with params: search = {}, gender = {}, status = {},, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}, start = {}, length = {}.", search, gender, status, country,  contractStartDate, contractEndDate, certificatedCourseId, partnerId, teacherType, finalResult, start, length);
		
		List<Teacher> teacherList =  teacherService.list(Arrays.asList(lifeCycles==null?new String[0]:lifeCycles), search,
				gender == null ? null : Gender.valueOf(gender),
				status == null ? null : Status.valueOf(status),
				country == null ? null : Country.valueOf(country),
				contractStartDate == null ? null : new DateTimeParam(contractStartDate),
				contractEndDate == null ? null : new DateTimeParam(contractEndDate),
				scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
				scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
				certificatedCourseId, partnerId,
				teacherType,
				finalResult == null ? null : FinalResult.valueOf(finalResult),
				start, length,teacherTags);
		
		return this.getTeacherQueryResultView(teacherList);
	}
	
	@RequestMapping(value = "/filterApplication", method = RequestMethod.GET)
	public List<TeacherQueryTeacherView> filter(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "contractStartDate", required = false) String contractStartDate,
			@RequestParam(value = "contractEndDate", required = false) String contractEndDate,
			@RequestParam(value = "scheduledDateTimeFrom", required = false) String scheduledDateTimeFrom,
			@RequestParam(value = "scheduledDateTimeTo", required = false) String scheduledDateTimeTo,
			@RequestParam(value = "courses", required = false) Long certificatedCourseId,
			@RequestParam(value = "recruit_channel", required = false) Long partnerId,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "finalResult", required = false) String finalResult,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "teacherTags",required = false) String teacherTags,
			@RequestParam(value="accountType", required = false) String strAccountType,
			// 2015-08-13 添加其他条件 TM-improve quit-time 时间段
			@RequestParam(value="quitStartDate", required = false) String strQuitStartDate,
			@RequestParam(value="quitEndDate", required = false) String strQuitEndDate,
			// 2015-08-13 添加其他条件 TM-improve apply-time 时间段 -- 和application相关的
			@RequestParam(value="applyStartDate", required = false) String strApplyFromDate,
			@RequestParam(value="applyEndtDate", required = false) String strApplyEndDate,
			//2015-08-14 basic_info筛选需要
			@RequestParam(value="applyResult",required = false) String applyResult,
			@RequestParam(value="interviewer",required = false) Long interviewerId,
			@RequestParam(value="practicumTeacher",required = false) Long practicumTeacherId,
			@RequestParam(value="managers",required = false) String[] managers
			) {
		logger.info("list teacher with params: search = {}, gender = {}, status = {}, country = {}, contractStartDate = {}, contractEndDate = {}, certificatedCourseId = {}, teacherType = {}, testFinalResult = {}, start = {}, length = {}, applyResult = {}.", search, gender, status, country,  contractStartDate, contractEndDate, certificatedCourseId, partnerId, teacherType, finalResult, start, length, applyResult);
		
		List<Teacher> teacherList =  teacherService.list(Arrays.asList(lifeCycles==null?new String[0]:lifeCycles), search,
				gender == null ? null : Gender.valueOf(gender),
				status == null ? null : Status.valueOf(status),
				country == null ? null : Country.valueOf(country),
				contractStartDate == null ? null : new DateTimeParam(contractStartDate),
				contractEndDate == null ? null : new DateTimeParam(contractEndDate),
				scheduledDateTimeFrom == null ? null : new DateTimeParam(scheduledDateTimeFrom),
				scheduledDateTimeTo == null ? null : new DateTimeParam(scheduledDateTimeTo),
				certificatedCourseId, partnerId,
				teacherType,
				finalResult == null ? null : FinalResult.valueOf(finalResult),
				start, length,teacherTags,strAccountType,
				// 2015-08-13 添加其他条件 TM-improve quit-time 时间段
				strQuitStartDate == null ? null: new DateTimeParam(strQuitStartDate),
				strQuitEndDate == null ? null: new DateTimeParam(strQuitEndDate),
				// 2015-08-13 添加其他条件 TM-improve apply-time 时间段 -- (user.registerDateTime)
				strApplyFromDate == null ? null: new DateTimeParam(strApplyFromDate),
				strApplyEndDate == null ? null : new DateTimeParam(strApplyEndDate),
				//2015-08-14 basic_info 需要筛选
				applyResult,interviewerId,practicumTeacherId,managers);
		
		return this.getTeacherQueryResultView(teacherList);
	}
	
	private List<TeacherQueryTeacherView> getTeacherQueryResultView(List<Teacher> teacherList) {
		
		List<TeacherQueryTeacherView> teacherViewList = new ArrayList<TeacherQueryTeacherView>();
		
		if (teacherList != null && teacherList.size() > 0) {
			Iterator<Teacher> iterator = teacherList.iterator();
			while (iterator.hasNext()) {
				Teacher teacher = iterator.next();
				TeacherQueryTeacherView teacherView = new TeacherQueryTeacherView();
				
				//partner
				TeacherQueryPartnerView partnerView = null;
				if (teacher.getPartner() != null) {
					partnerView = new TeacherQueryPartnerView();
					partnerView.setId(teacher.getPartner().getId());
					partnerView.setName(teacher.getPartner().getName());
				}
				
				//certificatedCourses
				List<TeacherQueryCourseView> certificatedCourseViews = new ArrayList<TeacherQueryCourseView>();
				if (teacher.getCertificatedCourses() != null && teacher.getCertificatedCourses().size() > 0) {
					for (Course course : teacher.getCertificatedCourses()) {
						TeacherQueryCourseView courseView = new TeacherQueryCourseView();
						courseView.setId(course.getId());
						courseView.setName(course.getName());
						courseView.setType(course.getType());
						courseView.setSerialNumber(course.getSerialNumber());
						courseView.setMode(course.getMode());
						courseView.setNeedBackupTeacher(course.isNeedBackupTeacher());
						courseView.setSequential(course.isSequential());
						courseView.setFree(course.isFree());
						
						certificatedCourseViews.add(courseView);
					}
				}
				
				//currentTeacherApplication
				TeacherQueryTApplicationView applicationView = null;
				applicationView = new TeacherQueryTApplicationView();
				if (teacher.getTeacherApplications() != null && !teacher.getTeacherApplications().isEmpty()) {
					String strStatus = teacher.getLifeCycle().toString();
					TeacherApplication.Status appStatus = null;//TeacherApplication.Status.valueOf(strStatus);
					TeacherApplication.Status prevStatus = null;//TeacherApplication.Status.prevStatus(appStatus);
					try {
						appStatus = TeacherApplication.Status.valueOf(strStatus);
						prevStatus = TeacherApplication.Status.prevStatus(appStatus);
					} catch (Exception e) {
						//
					}
					
					for (TeacherApplication application : teacher.getTeacherApplications()) {
						// 2015-08-25 修改处理前一阶段的pass时间
						if (null != prevStatus && prevStatus == application.getStatus()) {
							applicationView.setPassedPreviousPhaseDateTime(application.getAuditDateTime());
						}
						
						if (application.isCurrent() && application.getStatus().toString().equals(teacher.getLifeCycle().toString())){
							applicationView.setId(application.getId());
							applicationView.setApplicationResult(application.getResult());
							applicationView.setApplicationStatus(application.getStatus());	// 2015-08-13 添加current 的 status阶段值
							applicationView.setApplyDateTime(application.getApplyDateTime());
							applicationView.setAuditDateTime(application.getAuditDateTime());
							
//							//找出通过上一阶段的时间	-- 					
//							Date passedPreviousPhaseDateTime = teacherLifeCycleLogService.getPassedPreviousPhaseDateTime(teacher.getId(),teacher.getLifeCycle());
//							if(passedPreviousPhaseDateTime != null){
//								applicationView.setPassedPreviousPhaseDateTime(passedPreviousPhaseDateTime);
//							}
							
							//onlineclass
							if (application.getOnlineClass() != null) {
								Date scheduledDateTime = application.getOnlineClass().getScheduledDateTime();
								TeacherQueryOnlineClassView onlineClassView = new TeacherQueryOnlineClassView();
								onlineClassView.setId(application.getOnlineClass().getId());
								onlineClassView.setScheduledDateTime(scheduledDateTime != null ? scheduledDateTime.getTime() : null);
								onlineClassView.setTeacherName(application.getOnlineClass().getTeacher().getRealName());
								applicationView.setOnlineClass(onlineClassView);
							}
						}
					}
					
				}
				
				teacherView.setId(teacher.getId());
				teacherView.setRealName(teacher.getRealName());
				teacherView.setName(teacher.getName());
				teacherView.setStatus(teacher.getStatus());
				teacherView.setType(teacher.getType());
				teacherView.setGender(teacher.getGender());
				teacherView.setCountry(teacher.getCountry());
				teacherView.setLifeCycle(teacher.getLifeCycle());
				teacherView.setEmail(teacher.getEmail());
				teacherView.setContractStartDate(teacher.getContractStartDate() != null ? teacher.getContractStartDate().getTime() : null);
				teacherView.setContractEndDate(teacher.getContractEndDate() != null ? teacher.getContractEndDate().getTime() : null);
				teacherView.setPartner(partnerView);
				teacherView.setCertificatedCourses(certificatedCourseViews);
				teacherView.setCurrentTeacherApplication(applicationView);
				teacherView.setSignUpDateTime(teacher.getRegisterDateTime().getTime());
				teacherView.setManagerName(teacher.getManagerName());
				
//				teacherView.setOperatorName(teacher.getOperatorName());
//				if(null != teacher.getOperationDateTime()){
//					teacherView.setOperationTime(teacher.getOperationDateTime());
//				}else{
//					teacherView.setOperationTime(null);
//				}
				
				TeacherLifeCycleLog operateInfo = teacherLifeCycleLogService.getOperateInfoWithTeacherIdCurrentPhase(teacher.getId(),teacher.getLifeCycle());
				if(null != operateInfo){
					teacherView.setOperatorName(operateInfo.getOperator().getName());
					teacherView.setOperationTime(operateInfo.getCreateDateTime().getTime());
				}
				
				
				
				// 2015-07-27 添加teacher refer
				String strRefer = teacher.getReferee();
				if (null != strRefer) {
					//
					String [] strRefers = strRefer.split(",");
					if (strRefers.length>1) {
						teacherView.setReferee(strRefers[1]);
					}
				}
				
				teacherViewList.add(teacherView);
			}
		}
		
		return teacherViewList;
	}
	
	// 2015-08-15 signup 的teacher filter 和count
	@RequestMapping(value = "/countSignup", method = RequestMethod.GET)
	public Count countSignup(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "recruit_channel", required = false) String recruitChannel
			) {
		logger.info("count teacher with params: search = {}, status = {}, recruit_channel = {}.", search, status, recruitChannel);
		return teacherService.countSignup(search,status,recruitChannel);
	}
	
	@RequestMapping(value = "/filterSignup", method = RequestMethod.GET)
	public List<TeacherQueryTeacherView> filterSignup(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "recruit_channel", required = false) String recruitChannel) {
		logger.info("list teacher with params: search = {}, status = {}, start = {}, length = {}.", search,  status,  start, length);
		
		List<Teacher> teacherList =  teacherService.listSignup( search,
				status == null ? null : Status.valueOf(status),
				recruitChannel,
				start, length);
		
		return this.getTeacherQueryResultView(teacherList);
	}
	
	// ==== 2015-08-15 Regular or Quit teacher filter 
	@RequestMapping(value = "/countNormal", method = RequestMethod.GET)
	public Count countNormal(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
//			@RequestParam(value = "lifeCycle", required = false) String lifeCycle,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "managers", required = false) String[] managers,
			@RequestParam(value = "operator", required = false) Long operatorId,
			@RequestParam(value = "courses", required = false) String[] certificatedCourseId,
			@RequestParam(value="operationStartDate", required=false) String operationStartDate,
			@RequestParam(value="operationEndDate", required=false) String operationEndDate,
			@RequestParam(value = "contractEndDate", required = false) String[] contractEndDate,
			@RequestParam(value="accountType", required=false) String strAccountType
			) {		
		// 
		String lifeCycle = null;
		if (null != lifeCycles && lifeCycles.length>0) {
			lifeCycle = lifeCycles[0];
		} else {
			lifeCycle = LifeCycle.REGULAR.toString();
		}
		logger.info("countNormal teacher with params: search = {}, status = {}, lifeCycle = {}.", search, status,lifeCycle);
		
		return teacherService.countNormal(
				lifeCycle == null? null:LifeCycle.valueOf(lifeCycle), 
				search, status, certificatedCourseId, managers, operatorId, 
				gender == null?null:Gender.valueOf(gender), 
				country == null ? null: Country.valueOf(country), 
				teacherType == null ? null : Teacher.Type.valueOf(teacherType),
				operationStartDate == null ? null : new DateTimeParam(operationStartDate),
				operationEndDate == null ? null : new DateTimeParam(operationEndDate),
				contractEndDate,strAccountType);
	}
	
	@RequestMapping(value = "/filterNormal", method = RequestMethod.GET)
	public List<TeacherQueryTeacherView> filterNormal(
			@RequestParam(value = "lifeCycles", required = false) String[] lifeCycles,
//			@RequestParam(value = "lifeCycle", required = false) String lifeCycle,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "teacherType", required = false) String teacherType,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length,
			@RequestParam(value = "managers", required = false) String[] managers,
			@RequestParam(value = "operator", required = false) Long operatorId,
			@RequestParam(value = "courses", required = false) String[] certificatedCourseIds,
			@RequestParam(value="operationStartDate", required=false) String operationStartDate,
			@RequestParam(value="operationEndDate", required=false) String operationEndDate,
			@RequestParam(value = "contractEndDate", required = false) String[] contractEndDate,
			@RequestParam(value="accountType", required=false) String strAccountType) {
		//
		String lifeCycle = null;
		if (null != lifeCycles && lifeCycles.length>0) {
			lifeCycle = lifeCycles[0];
		} else {
			lifeCycle = LifeCycle.REGULAR.toString();
		}
		
		logger.info("filterNormal teacher with params: search = {}, lifeCycle = {}, start = {}, length = {}.", search,  lifeCycle,  start, length);
		
		List<Teacher> teacherList =  teacherService.listNormal( 
				lifeCycle == null? null:LifeCycle.valueOf(lifeCycle), 
				search, status, certificatedCourseIds, managers,
				gender == null?null:Gender.valueOf(gender), 
				country == null ? null: Country.valueOf(country), 
				teacherType == null ? null : Teacher.Type.valueOf(teacherType),
				start, length,
				contractEndDate,
				operatorId,
				operationStartDate == null ? null : new DateTimeParam(operationStartDate),
				operationEndDate== null ? null : new DateTimeParam(operationEndDate),
				strAccountType);
		
		return this.getTeacherQueryResultView(teacherList);
	}
	
//	@RequestMapping(value = "/loadOptionsWithLifeCycle", method = RequestMethod.GET)
//	@ResponseBody
//	public String loadOptionsWithLifeCycle(@RequestParam(value = "lifeCycles", required = true) String lifeCycle){
//		List<String> nameList = new ArrayList<String>();
//		if(lifeCycle == "INTERVIEW"){
////			nameList = teacherService.loadOptions("interviewer");
//			
//		}else if(lifeCycle == "TRAINING" || lifeCycle == "PRACTICUM" || lifeCycle == "REGULAR"){
//			nameList = teacherService.loadOptions("manager");
//		}else if(lifeCycle == "QUIT" || lifeCycle == "FAIL"){
//			nameList = teacherService.loadOptions("operator");
//		}
//		if(nameList.isEmpty()){
//			return null;
//		}else{
//			String resultList = "[";
//			for(String name:nameList){
//				String result = "{value:'"+name+"',label:'"+name+"'}";
//				resultList += result;
//				result += ",";
//			}
//			resultList += "]";
//			return resultList;
//		}
//	}
	
	@RequestMapping(value = "/filterAll", method = RequestMethod.GET)
	public List<TeacherQueryTeacherView> filterAll(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length) {
		logger.info("list teacher with params: search = {}, status = {}, start = {}, length = {}.", search,  start, length);
		
		List<Teacher> teacherList =  teacherService.listAll( search,	start, length);
		
		return this.getTeacherQueryResultView(teacherList);
	}
	
	@RequestMapping(value = "/countAll", method = RequestMethod.GET)
	public Count countAll(@RequestParam(value = "search", required = false) String search) {
		logger.info("count teacher with params: search = {}.", search);
		return teacherService.countAll(search);
	}
	
	@RequestMapping(value = "/getRegularTeacherContractDate", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getRegularTeacherContractDate() {
		logger.info("getRegularTeacherContractDate");
		
		List<String> dateList =  teacherService.getRegularTeacherContractDate();
		
		return dateList;
	}
	
	
	/**
	 * 
	 * @param studentId
	 * @param strResult
	 * @return
	 */
	@RequestMapping(value="/notifyTrialTestResultForEmail", method=RequestMethod.POST)
	public Response notifyTrialTestResultForEmail(@RequestParam(value="studentId")Long studentId, @RequestParam(value="trialResult")String  strResult) {
		logger.info("notifyTrialTestResultForEmail for {} with {}", studentId, strResult);
		boolean bResult = teacherService.sendEmailForTrialTestResult(studentId, strResult);
		return Response.ok("succeed!!").build();
	}
}
