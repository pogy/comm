package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Leads;
import com.vipkid.model.Parent;
import com.vipkid.model.Role;
import com.vipkid.model.Student;
import com.vipkid.model.User;
import com.vipkid.model.User.AccountType;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.rest.vo.query.StudentQueryFamilyView;
import com.vipkid.rest.vo.query.StudentQueryMarketingActivityView;
import com.vipkid.rest.vo.query.StudentQueryParentView;
import com.vipkid.rest.vo.query.StudentQueryStaffView;
import com.vipkid.rest.vo.query.StudentQueryStudentView;
import com.vipkid.security.SecurityService;
import com.vipkid.service.LeadsManageService;
import com.vipkid.service.StudentService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.CltStudent;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.Option;

@RestController
@RequestMapping("/api/service/private/students")
public class StudentController {
	private Logger logger = LoggerFactory.getLogger(StudentController.class.getSimpleName());

	@Resource
	private StudentService studentService;

	@Resource
	private SecurityService securityService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;
	@Resource
	private LeadsManageService leadsManageService;

	@RequestMapping(value="/find",method = RequestMethod.GET)
	public Student find(@RequestParam("id") long id) {
		return studentService.find(id);
	}


	@RequestMapping(value="/findByFamilyId",method = RequestMethod.GET)
	public List<Student> findByFamilyId(@RequestParam("familyId") long familyId) {
		logger.info("find students by family id = {}", familyId);
		return studentService.findByFamilyId(familyId);
	}


	@RequestMapping(value="/findByParentId",method = RequestMethod.GET)
	public List<Student> findByParentId(@RequestParam("parentId") long parentId) {
		logger.info("find students by family id = {}", parentId);
		return studentService.findByParentId(parentId);
	}

	@RequestMapping(value="/findAvailableInterviewStudent",method = RequestMethod.GET)
	public Student findAvailableInterviewStudent(@RequestParam("name") String name, @RequestParam("englishName") String englishName){
		logger.info("find available interview student");
		return studentService.findAvailableInterviewStudent(name, englishName);
	}

	@RequestMapping(value="/findAvailablePracticumStudent",method = RequestMethod.GET)
	public Student findAvailablePracticumStudent(@RequestParam("name") String name, @RequestParam("englishName") String englishName){
		logger.info("find available practicum student");
		return studentService.findAvailablePracticumStudent(name, englishName);
	}
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<Student> list(
			@RequestParam(value="gender",required=false) String gender,
			@RequestParam(value="age",required = false) Integer age,
			@RequestParam(value="province",required = false) String province,
			@RequestParam(value="city",required = false) String city,
			@RequestParam(value="lifeCycle",required = false) String lifeCycle,
			@RequestParam(value="status",required = false) String status,
			@RequestParam(value="source",required = false) String source,
			@RequestParam(value="followUpTargetDateTimeFrom",required = false) String followUpTargetDateTimeFrom,
			@RequestParam(value="followUpTargetDateTimeTo",required = false) String followUpTargetDateTimeTo,
			@RequestParam(value="followUpCreateDateTimeFrom",required = false) String followUpCreateDateTimeFrom,
			@RequestParam(value="followUpCreateDateTimeTo",required = false) String followUpCreateDateTimeTo, // for
																							// General
			@RequestParam(value="salesIdForPermission",required = false) Long salesIdForPermission,
			@RequestParam(value="chineseLeadTeacherId",required = false) Long chineseLeadTeacherId,
			@RequestParam(value="registrationDateFrom",required = false) String registrationDateFrom,
			@RequestParam(value="registrationDateTo",required = false) String registrationDateTo,
			@RequestParam(value="courseId",required = false) Long courseId,
			@RequestParam(value="enrollmentDateFrom",required = false) String enrollmentDateFrom,
			@RequestParam(value="enrollmentDateTo",required = false) String enrollmentDateTo,
			@RequestParam(value="customerStage",required = false) Integer customerStage,
			@RequestParam(value="salesId",required = false) Long salesId,
			@RequestParam(value="leftClassHour",required = false) Integer leftClassHour,
			@RequestParam(value="firstClassDateFrom",required = false) String firstClassDateFrom,
			@RequestParam(value="firstClassDateTo",required = false) String firstClassDateTo, // for
																			// sale
			@RequestParam(value="productId",required = false) Long productId,
			@RequestParam(value="payBy",required = false) String payBy, // for product
			@RequestParam(value="lastOnlineClassCourseId",required = false) Long lastOnlineClassCourseId, @RequestParam(value="lastOnlineClassUnitId",required = false) Long lastOnlineClassUnitId,
			@RequestParam(value="lastOnlineClassLearningCycleId",required = false) Long lastOnlineClassLearningCycleId,
			@RequestParam(value="lastOnlineClassLessonId",required = false) Long lastOnlineClassLessonId,
			@RequestParam(value="lastEducationalServiceDateFrom",required = false) String lastEducationalServiceDateFrom,
			@RequestParam(value="lastEducationalServiceDateTo",required = false) String lastEducationalServiceDateTo, // for
																									// education
			@RequestParam(value="search",required = false) String search, @RequestParam(value="forBooking",required = false) Boolean forBooking, 
			@RequestParam(value="finalResult",required = false) String finalResult, @RequestParam(value="start",required=false) Integer start,
			@RequestParam(value="currentPerformance",required = false) String currentPerformance, 	// 2015-07-01 添加student performance
			@RequestParam(value="length",required=false) Integer length, 
			@RequestParam(value="channel",required = false) String channel,
			@RequestParam(value="accountType",required = false) AccountType accountType) {
		if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        long startTime = System.currentTimeMillis();
        List<Student> students =studentService.list(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom==null?null:new DateTimeParam(followUpTargetDateTimeFrom), followUpTargetDateTimeTo==null?null:new DateTimeParam(followUpTargetDateTimeTo), followUpCreateDateTimeFrom==null?null:new DateTimeParam(followUpCreateDateTimeFrom), followUpCreateDateTimeTo==null?null:new DateTimeParam(followUpCreateDateTimeTo),
                salesIdForPermission, chineseLeadTeacherId, registrationDateFrom==null?null:new DateTimeParam(registrationDateFrom), registrationDateTo==null?null:new DateTimeParam(registrationDateTo), courseId, enrollmentDateFrom==null?null:new DateTimeParam(enrollmentDateFrom), enrollmentDateTo==null?null:new DateTimeParam(enrollmentDateTo), customerStage, salesId, leftClassHour,
                firstClassDateFrom==null?null:new DateTimeParam(firstClassDateFrom), firstClassDateTo==null?null:new DateTimeParam(firstClassDateTo), productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId,
                lastEducationalServiceDateFrom==null?null:new DateTimeParam(lastEducationalServiceDateFrom), lastEducationalServiceDateTo==null?null:new DateTimeParam(lastEducationalServiceDateTo), search, forBooking, finalResult, start, length, channel,currentPerformance, accountType);
        long endTime = System.currentTimeMillis();
        logger.error("++++++++++++++++++++++++++++++++++++++++++{}",endTime-startTime);
        return students;

	}


	@RequestMapping(value="/count",method = RequestMethod.GET)
	public Count count(
			@RequestParam(value="gender",required = false) String gender,
			@RequestParam(value="age",required = false) Integer age,
			@RequestParam(value="province",required = false) String province,
			@RequestParam(value="city",required = false) String city,
			@RequestParam(value="lifeCycle",required = false) String lifeCycle,
			@RequestParam(value="status",required = false) String status,
			@RequestParam(value="source",required = false) String source,
			@RequestParam(value="followUpTargetDateTimeFrom",required = false) String followUpTargetDateTimeFrom,
			@RequestParam(value="followUpTargetDateTimeTo",required = false) String followUpTargetDateTimeTo,
			@RequestParam(value="followUpCreateDateTimeFrom",required = false) String followUpCreateDateTimeFrom,
			@RequestParam(value="followUpCreateDateTimeTo",required = false) String followUpCreateDateTimeTo, // for General
			@RequestParam(value="salesIdForPermission",required = false) Long salesIdForPermission,
			@RequestParam(value="chineseLeadTeacherId",required = false) Long chineseLeadTeacherId,
			@RequestParam(value="registrationDateFrom",required = false) String registrationDateFrom,
			@RequestParam(value="registrationDateTo",required = false) String registrationDateTo,
			@RequestParam(value="courseId",required = false) Long courseId,
			@RequestParam(value="enrollmentDateFrom",required = false) String enrollmentDateFrom,
			@RequestParam(value="enrollmentDateTo",required = false) String enrollmentDateTo,
			@RequestParam(value="customerStage",required = false) Integer customerStage,
			@RequestParam(value="salesId",required = false) Long salesId,
			@RequestParam(value="leftClassHour",required = false) Integer leftClassHour,
			@RequestParam(value="firstClassDateFrom",required = false) String firstClassDateFrom,
			@RequestParam(value="firstClassDateTo",required = false) String firstClassDateTo, // for sale
			@RequestParam(value="productId",required = false) Long productId,
			@RequestParam(value="payBy",required = false) String payBy, // for product
			@RequestParam(value="lastOnlineClassCourseId",required = false) Long lastOnlineClassCourseId, 
			@RequestParam(value="lastOnlineClassUnitId",required = false) Long lastOnlineClassUnitId,
			@RequestParam(value="lastOnlineClassLearningCycleId",required = false) Long lastOnlineClassLearningCycleId,
			@RequestParam(value="lastOnlineClassLessonId",required = false) Long lastOnlineClassLessonId,
			@RequestParam(value="lastEducationalServiceDateFrom",required = false) String lastEducationalServiceDateFrom,
			@RequestParam(value="lastEducationalServiceDateTo",required = false) String lastEducationalServiceDateTo, // for education
			@RequestParam(value="search",required = false) String search, @RequestParam(value="forBooking",required = false) Boolean forBooking, 
			@RequestParam(value="finalResult",required = false) String finalResult,
			@RequestParam(value="currentPerformance",required = false) String currentPerformance, 	// 2015-07-01 添加student performance
			@RequestParam(value="channel",required = false) String channel,
			@RequestParam(value="accountType", required = false) AccountType accountType) {
		logger.info(
				"list Student with params: gender = {}, age = {}, province = {}, city = {}, lifeCycle = {}, status = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, courseId = {}, enrollmentDateFrom = {}, enrollmentDateTo = {}, customerStage = {}, saleId = {}, leftClassHour = {}, productId = {}, payBy = {}, search = {} performance={}, accountType = {}.",
				gender, age, province, city, lifeCycle, status, source, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId,
				leftClassHour, productId, payBy, search, currentPerformance, accountType);
        return studentService.count(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom==null?null:new DateTimeParam(followUpTargetDateTimeFrom), followUpTargetDateTimeTo==null?null:new DateTimeParam(followUpTargetDateTimeTo), followUpCreateDateTimeFrom==null?null:new DateTimeParam(followUpCreateDateTimeFrom), followUpCreateDateTimeTo==null?null:new DateTimeParam(followUpCreateDateTimeTo),
                salesIdForPermission, chineseLeadTeacherId,  registrationDateFrom==null?null:new DateTimeParam(registrationDateFrom), registrationDateTo==null?null:new DateTimeParam(registrationDateTo), courseId, enrollmentDateFrom==null?null:new DateTimeParam(enrollmentDateFrom), enrollmentDateTo==null?null:new DateTimeParam(enrollmentDateTo), customerStage, salesId, leftClassHour,
                firstClassDateFrom==null?null:new DateTimeParam(firstClassDateFrom), firstClassDateTo==null?null:new DateTimeParam(firstClassDateTo), productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId,
                lastEducationalServiceDateFrom==null?null:new DateTimeParam(lastEducationalServiceDateFrom), lastEducationalServiceDateTo==null?null:new DateTimeParam(lastEducationalServiceDateTo), search, forBooking, finalResult, channel,currentPerformance, accountType);

	}
	
	public Student create(Student student, User creater) {
		logger.info("create Student: {}", student);
		Student returnStudent = studentService.create(student, creater);
		if (returnStudent != null) {
			doAssignAfterStudentCreatedManually(returnStudent,creater);
		}
		return returnStudent;
	}

	private void doAssignAfterStudentCreatedManually(Student student,User creater) {
		if (student != null) {
			if (creater != null && creater.getRoleList() != null 
					&& creater.getRoleList().contains(Role.STAFF_SALES.name())) {//sales 手动创建的leads直接分给自己
				Leads leads = leadsManageService.creatDefaultLeadsInfo(student.getId());
				if (leads != null) {
					logger.info("assign leads to sales after student created,salesId = {}", creater.getId());
					leadsManageService.doManualLeadsDispatchToSales(creater, leads.getId());
				}
			} else {//否则自动分配
				leadsQueueSender.sendText(String.valueOf(student.getId()));
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public Student create(@RequestBody Student student) {
		return create(student, securityService.getCurrentUser());
	}
	
	@RequestMapping(value="/createLeads", method = RequestMethod.POST)
	public Student createLeads(@RequestBody Student student) {
		if (student != null) {
			leadsQueueSender.sendText(String.valueOf(student.getId()));
		}
		return student;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Student update(@RequestBody Student student) {
		logger.info("Get request student.id={}, student.channel={}", student.getId(), student.getChannel());
		return studentService.update(student);

	}
	
	@RequestMapping(value="/updateTargetClassesPerWeek", method = RequestMethod.PUT)
	public Student updateTargetClassesPerWeek(@RequestBody Student student) {
		return studentService.updateTargetClassesPerWeek(student);

	}

	@RequestMapping(value="/assignToForeignLeadTeacher",method = RequestMethod.PUT)
	public void assignToForeignLeadTeacher(@RequestBody List<Student> students) {
		studentService.doAssignToForeignLeadTeacher(students);

	}


	@RequestMapping(value="/assignToChineseLeadTeacher",method = RequestMethod.PUT)
	public void assignToChineseLeadTeacher(@RequestBody List<Student> students) {
		studentService.doAssignToChineseLeadTeacher(students);
	}


	@RequestMapping(value="/assignToSales",method = RequestMethod.PUT)
	public void assignToSale(@RequestBody List<Student> students) {
		studentService.doAssignToSale(students);

	}


	@RequestMapping(value="/resetPassword",method = RequestMethod.GET)
	public Student resetPassword(@RequestParam("id") long id) {
		return studentService.doResetPassword(id);

	}


	@RequestMapping(value="/updateFavoredTeachers",method = RequestMethod.PUT)
	public Student updateFavoredTeachers(@RequestBody Student student) {
		return studentService.updateFavoredTeachers(student);
	}


	@RequestMapping(value="/listStudentSource",method = RequestMethod.GET)
	public List<Option> listStudentSource() {
		return studentService.listStudentSource();

	}

	@RequestMapping("/listByName")
	public List<StudentQueryStudentView> listByName(@RequestParam("search") String name, @RequestParam("start") int start,
			@RequestParam("length") int length) {
		List<Student> studentList = studentService.findByNameOrEnglishName(name, start, length);
		List<StudentQueryStudentView> studentViews = new ArrayList<StudentQueryStudentView>();
		if (studentList != null && studentList.size() > 0) {
			for (Student stu : studentList) {
				StudentQueryStudentView stuView = new StudentQueryStudentView();
				stuView.setId(stu.getId());
				stuView.setName(stu.getName());
				stuView.setEnglishName(stu.getEnglishName());
				studentViews.add(stuView);
			}
		}
		return studentViews;
	}

	@RequestMapping(value="/listForAgent",method = RequestMethod.GET)
	public List<Student> listForAgent(@RequestParam(value="lifeCycle",required=false)String lifeCycle, @RequestParam(value="source",required=false) String source, @RequestParam(value="registrationDateFrom",required=false) String registrationDateFrom,
			@RequestParam(value="registrationDateTo",required=false) String registrationDateTo, @RequestParam(value="search",required=false) String search, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list Student with params:  lifeCycle = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, search = {},start = {},length = {}.", lifeCycle, source,
				registrationDateFrom, registrationDateTo, search, start, length);

		List<Student> students = studentService.listForAgent(lifeCycle, source, registrationDateFrom==null?null:new DateTimeParam(registrationDateFrom), registrationDateTo==null?null:new DateTimeParam(registrationDateTo), search, start, length);
		return students;
	}


	@RequestMapping(value="/countForAgent",method = RequestMethod.GET)
	public Count countForAgent(@RequestParam(value="lifeCycle",required=false)String lifeCycle, @RequestParam(value="source",required=false) String source, @RequestParam(value="registrationDateFrom",required=false) String registrationDateFrom,
			@RequestParam(value="registrationDateTo",required=false) String registrationDateTo, @RequestParam(value="search",required=false) String search) {
		logger.info("count Student with params:  lifeCycle = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, search = {}.", lifeCycle, source, registrationDateFrom,
				registrationDateTo, search);
		return studentService.countForAgent(lifeCycle, source, registrationDateFrom==null?null:new DateTimeParam(registrationDateFrom), registrationDateTo==null?null:new DateTimeParam(registrationDateTo), search);

	}
	
	@RequestMapping(value="/filter",method = RequestMethod.GET)
	public List<StudentQueryStudentView> filter(
			@RequestParam(value="gender",required=false) String gender,
			@RequestParam(value="age",required = false) Integer age,
			@RequestParam(value="province",required = false) String province,
			@RequestParam(value="city",required = false) String city,
			@RequestParam(value="lifeCycle",required = false) String lifeCycle,
			@RequestParam(value="status",required = false) String status,
			@RequestParam(value="source",required = false) String source,
			@RequestParam(value="followUpTargetDateTimeFrom",required = false) String followUpTargetDateTimeFrom,
			@RequestParam(value="followUpTargetDateTimeTo",required = false) String followUpTargetDateTimeTo,
			@RequestParam(value="followUpCreateDateTimeFrom",required = false) String followUpCreateDateTimeFrom,
			@RequestParam(value="followUpCreateDateTimeTo",required = false) String followUpCreateDateTimeTo, // for
																							// General
			@RequestParam(value="salesIdForPermission",required = false) Long salesIdForPermission,
			@RequestParam(value="chineseLeadTeacherId",required = false) Long chineseLeadTeacherId,
			@RequestParam(value="registrationDateFrom",required = false) String registrationDateFrom,
			@RequestParam(value="registrationDateTo",required = false) String registrationDateTo,
			@RequestParam(value="courseId",required = false) Long courseId,
			@RequestParam(value="enrollmentDateFrom",required = false) String enrollmentDateFrom,
			@RequestParam(value="enrollmentDateTo",required = false) String enrollmentDateTo,
			@RequestParam(value="customerStage",required = false) Integer customerStage,
			@RequestParam(value="salesId",required = false) Long salesId,
			@RequestParam(value="leftClassHour",required = false) Integer leftClassHour,
			@RequestParam(value="firstClassDateFrom",required = false) String firstClassDateFrom,
			@RequestParam(value="firstClassDateTo",required = false) String firstClassDateTo, // for
																			// sale
			@RequestParam(value="productId",required = false) Long productId,
			@RequestParam(value="payBy",required = false) String payBy, // for product
			@RequestParam(value="lastOnlineClassCourseId",required = false) Long lastOnlineClassCourseId, @RequestParam(value="lastOnlineClassUnitId",required = false) Long lastOnlineClassUnitId,
			@RequestParam(value="lastOnlineClassLearningCycleId",required = false) Long lastOnlineClassLearningCycleId,
			@RequestParam(value="lastOnlineClassLessonId",required = false) Long lastOnlineClassLessonId,
			@RequestParam(value="lastEducationalServiceDateFrom",required = false) String lastEducationalServiceDateFrom,
			@RequestParam(value="lastEducationalServiceDateTo",required = false) String lastEducationalServiceDateTo,
			@RequestParam(value="accountType", required = false) AccountType accountType, // for
																									// education
			@RequestParam(value="currentPerformance",required = false) String currentPerformance, 	// 2015-07-01 添加student performance
			@RequestParam(value="search",required = false) String search, @RequestParam(value="forBooking",required = false) Boolean forBooking, @RequestParam(value="finalResult",required = false) String finalResult, @RequestParam(value="start",required=false) Integer start,
			@RequestParam(value="length",required=false) Integer length, @RequestParam(value="channel",required = false) String channel) {
		if (null == start) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        long startTime = System.currentTimeMillis();
        List<Student> students =studentService.list(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom==null?null:new DateTimeParam(followUpTargetDateTimeFrom), followUpTargetDateTimeTo==null?null:new DateTimeParam(followUpTargetDateTimeTo), followUpCreateDateTimeFrom==null?null:new DateTimeParam(followUpCreateDateTimeFrom), followUpCreateDateTimeTo==null?null:new DateTimeParam(followUpCreateDateTimeTo),
                salesIdForPermission, chineseLeadTeacherId, registrationDateFrom==null?null:new DateTimeParam(registrationDateFrom), registrationDateTo==null?null:new DateTimeParam(registrationDateTo), courseId, enrollmentDateFrom==null?null:new DateTimeParam(enrollmentDateFrom), enrollmentDateTo==null?null:new DateTimeParam(enrollmentDateTo), customerStage, salesId, leftClassHour,
                firstClassDateFrom==null?null:new DateTimeParam(firstClassDateFrom), firstClassDateTo==null?null:new DateTimeParam(firstClassDateTo), productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId,
                lastEducationalServiceDateFrom==null?null:new DateTimeParam(lastEducationalServiceDateFrom), lastEducationalServiceDateTo==null?null:new DateTimeParam(lastEducationalServiceDateTo), search, forBooking, finalResult, start, length, channel, currentPerformance, accountType);
        long endTime = System.currentTimeMillis();
        logger.error("++++++++++++++++++++++++++++++++++++++++++{}",endTime-startTime);
        
        return this.getStudentQueryResultView(students);

	}
	
	private List<StudentQueryStudentView> getStudentQueryResultView(List<Student> studentList) {
		List<StudentQueryStudentView> resultViewList = new ArrayList<StudentQueryStudentView>();
		if (studentList != null && studentList.size() > 0) {
			Iterator<Student> iterator = studentList.iterator();
			while (iterator.hasNext()) {
				Student stu = iterator.next();
				StudentQueryStudentView stuView = new StudentQueryStudentView();
				
				//family
				StudentQueryFamilyView familyView = null;
				if (stu.getFamily() != null) {
					familyView = new StudentQueryFamilyView();
					familyView.setId(stu.getFamily().getId());
					familyView.setCity(stu.getFamily().getCity());
					familyView.setProvince(stu.getFamily().getProvince());
					//parents
					List<StudentQueryParentView> parentViews = new ArrayList<StudentQueryParentView>();
					List<Parent> parents = stu.getFamily().getParents();
					if (parents != null && parents.size() > 0) {
						for (Parent parent : parents) {
							StudentQueryParentView parentView = new StudentQueryParentView();
							parentView.setId(parent.getId());
							parentView.setName(parent.getName());
							parentView.setRelation(parent.getRelation());
							parentView.setMobile(parent.getMobile());
							parentViews.add(parentView);
						}
					}
					familyView.setParents(parentViews);
				}
				
				//chineseLeadTeacher
				StudentQueryStaffView chineseLeadTeacherView = null;
				if (stu.getChineseLeadTeacher() != null) {
					chineseLeadTeacherView = new StudentQueryStaffView();
					chineseLeadTeacherView.setId(stu.getChineseLeadTeacher().getId());
					chineseLeadTeacherView.setName(stu.getChineseLeadTeacher().getName());
				}
				
				//sales
				StudentQueryStaffView salesView = null;
				if (stu.getSales() != null) {
					salesView = new StudentQueryStaffView();
					salesView.setId(stu.getSales().getId());
					salesView.setName(stu.getSales().getName());
				}
				
				//marketingActivity
				StudentQueryMarketingActivityView marketingActivityView = new StudentQueryMarketingActivityView();
				if (stu.getChannel() != null) {
					marketingActivityView.setChannel(stu.getChannel().getSourceName());
				}
				if (stu.getMarketingActivity() != null) {					
					marketingActivityView.setId(stu.getMarketingActivity().getId());				
				}
				
				//age
				Integer age = null;
				if (stu.getBirthday() != null) {
					try {
						Calendar cal = Calendar.getInstance();
						
						if (cal.getTime().after(stu.getBirthday())) {
							int yearNow = cal.get(Calendar.YEAR);
							cal.setTime(stu.getBirthday());
							int yearBirth = cal.get(Calendar.YEAR);
							age = yearNow - yearBirth;
						}
					} catch (Exception e) {
						logger.error("error when calculate  age of the student, id = " + stu.getId() , e);
					}
				}
				
				// channel
				stuView.setChannel(stu.getChannel());
				
				stuView.setId(stu.getId());
				stuView.setName(stu.getName());
				stuView.setEnglishName(stu.getEnglishName());
				stuView.setGender(stu.getGender());
				stuView.setAge(age);
				stuView.setRegisterDateTime( stu.getRegisterDateTime() != null ? stu.getRegisterDateTime().getTime() : null);
				stuView.setLifeCycle(stu.getLifeCycle());
				stuView.setSource(stu.getSource());
				stuView.setFamily(familyView);
				stuView.setChineseLeadTeacher(chineseLeadTeacherView);
				stuView.setSales(salesView);
				stuView.setMarketingActivity(marketingActivityView);
				
				resultViewList.add(stuView);
			}
			
		}
		
		return resultViewList;
		
	}

	@RequestMapping(value="/countByMarketingActivityId",method = RequestMethod.GET)
	public Count countByMarketingActivityId(@RequestParam(value="marketingActivityId", required=false) long marketingActivityId) {
		logger.info("findByMarketingActivityId:  marketingActivityId = {}", marketingActivityId);
		List<Student> list = studentService.findByMarketingActivityId(marketingActivityId);
		long num = 0;
		if(null != list){
			num = list.size();
		}else{
			num = 0;
		}
		return new Count(num);
	}

	@RequestMapping(value="/cltList",method = RequestMethod.GET)
	public List<CltStudent> findCLT(
			@RequestParam(value="assignDateFrom",required = false) String assignDateFrom,
			@RequestParam(value="assignDateTo",required = false) String assignDateTo,
			@RequestParam(value="followUpCreateDateFrom",required = false) String followUpCreateDateFrom,
			@RequestParam(value="followUpCreateDateTo",required = false) String followUpCreateDateTo,
			@RequestParam(value="followUpTargetDateFrom",required = false) String followUpTargetDateFrom,
			@RequestParam(value="followUpTargetDateTo",required = false) String followUpTargetDateTo,
			@RequestParam(value="followUpDashType",required = false) String followUpDashType,
			@RequestParam(value="channel",required = false) String channel,
			@RequestParam(value="lifeCycle",required = false) String lifeCycle,
			@RequestParam(value="cltId",required = false) Long cltId,
			@RequestParam(value="searchStudentText",required = false) String searchStudentText,
			@RequestParam(value="studentLevel",required = false) String studentLevel,
			@RequestParam(value="start",required = false) Integer start,
			@RequestParam(value="length",required = false) Integer length) throws Throwable{
		return studentService.findCLTStudents(DateParam.dateValueOf(assignDateFrom), DateParam.dateValueOf(assignDateTo), DateParam.dateValueOf(followUpCreateDateFrom), DateParam.dateValueOf(followUpCreateDateTo), DateParam.dateValueOf(followUpTargetDateFrom), DateParam.dateValueOf(followUpTargetDateTo), followUpDashType, channel, lifeCycle, cltId, searchStudentText, studentLevel, start, length);
	}

	@RequestMapping(value="/cltCount",method = RequestMethod.GET)
	public Count cltCount(
			@RequestParam(value="assignDateFrom",required = false) String assignDateFrom,
			@RequestParam(value="assignDateTo",required = false) String assignDateTo,
			@RequestParam(value="followUpCreateDateFrom",required = false) String followUpCreateDateFrom,
			@RequestParam(value="followUpCreateDateTo",required = false) String followUpCreateDateTo,
			@RequestParam(value="followUpTargetDateFrom",required = false) String followUpTargetDateFrom,
			@RequestParam(value="followUpTargetDateTo",required = false) String followUpTargetDateTo,
			@RequestParam(value="followUpDashType",required = false) String followUpDashType,
			@RequestParam(value="channel",required = false) String channel,
			@RequestParam(value="lifeCycle",required = false) String lifeCycle,
			@RequestParam(value="cltId",required = false) Long cltId,
			@RequestParam(value="searchStudentText",required = false) String searchStudentText,
			@RequestParam(value="studentLevel",required = false) String studentLevel,
			@RequestParam(value="start",required = false) Integer start,
			@RequestParam(value="length",required = false) Integer length) throws Throwable{
		return studentService.findCLTStudentsCount(DateParam.dateValueOf(assignDateFrom), DateParam.dateValueOf(assignDateTo), DateParam.dateValueOf(followUpCreateDateFrom), DateParam.dateValueOf(followUpCreateDateTo), DateParam.dateValueOf(followUpTargetDateFrom), DateParam.dateValueOf(followUpTargetDateTo), followUpDashType, channel, lifeCycle, cltId, searchStudentText, studentLevel);
	}
}
