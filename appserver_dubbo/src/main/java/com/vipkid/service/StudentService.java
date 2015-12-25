package com.vipkid.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Channel;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Family;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Parent;
import com.vipkid.model.Pet;
import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Student.Source;
import com.vipkid.model.User;
import com.vipkid.model.User.AccountType;
import com.vipkid.model.User.Status;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.repository.ChannelRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.FamilyNotExistServiceException;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.CltStudent;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.Option;
import com.vipkid.util.Configurations;


@Service
public class StudentService {
	private Logger logger = LoggerFactory.getLogger(StudentService.class.getSimpleName());

	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private AirCraftRepository airCraftRepository;
	
	@Resource
	private AirCraftThemeRepository airCraftThemeRepository;
	
	@Resource
	private PetRepository petRepository;
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private CourseRepository courseRepository;
	
	@Resource
	private LearningProgressRepository learningProgressRepository;
	
	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;

	@Resource
	private ChannelRepository channelRepository;
	
	@Resource
	private ChannelService channelService;

	public Student find(long id) {
		return studentRepository.find(id);
	}
	
	public List<Student> findByFamilyId(long familyId) {
		logger.info("find students by family id = {}", familyId);
		return studentRepository.findByFamilyId(familyId);
	}
	
	public List<Student> findByParentId(long parentId) {
		logger.info("find students by family id = {}", parentId);
		return studentRepository.findByParentId(parentId);
	}
	
	public Student findAvailableInterviewStudent(String name, String englishName) {
	
		List<Student> students = studentRepository.findByName(name);
		if (students.isEmpty()){
			Student student = new Student();
			student.setAccountType(AccountType.TEST);
			student.setStatus(Status.TEST);
			student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
			student.setName(name);
			student.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STUDENT_PASSWORD));
			student.setEnglishName(englishName);
			Channel aChannel = channelService.findBySourceName(Configurations.Channel.WWW_DEFAULT_CHANNEL);
			student.setChannel(aChannel);
			studentRepository.create(student);
			studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
			
			// 新建招聘课学习进度
			Course recruitmentCourse = courseRepository.findByCourseType(Type.TEACHER_RECRUITMENT);
			LearningProgress recruitmentLearningProgress = new LearningProgress();
			recruitmentLearningProgress.setStudent(student);
			recruitmentLearningProgress.setStatus(LearningProgress.Status.STARTED);
			recruitmentLearningProgress.setCourse(recruitmentCourse);
			recruitmentLearningProgress.setLeftClassHour(10);
			recruitmentLearningProgress.setTotalClassHour(10);
			learningProgressRepository.create(recruitmentLearningProgress);			
			
			securityService.logAudit(Level.INFO, Category.STUDENT_CREATE, "Create Interview student: " + student.getName());
			
			return student;
		} else{
			Student student = students.get(0);
			Course recruitmentCourse = courseRepository.findByCourseType(Type.TEACHER_RECRUITMENT);
			LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), recruitmentCourse.getId());
			if (learningProgress == null) {
				LearningProgress recruitmentLearningProgress = new LearningProgress();
				recruitmentLearningProgress.setStudent(student);
				recruitmentLearningProgress.setStatus(LearningProgress.Status.STARTED);
				recruitmentLearningProgress.setCourse(recruitmentCourse);
				recruitmentLearningProgress.setLeftClassHour(10);
				recruitmentLearningProgress.setTotalClassHour(10);
				learningProgressRepository.create(recruitmentLearningProgress);	
			}
			return student;
		}
	}
	
	public Student findAvailablePracticumStudent(String name, String englishName) {
		Course practicumCourse = courseRepository.findByCourseType(Type.PRACTICUM);
		List<Student> students = studentRepository.findByName(name);
		if (students.isEmpty()){
			Student student = new Student();
			student.setAccountType(AccountType.TEST);
			student.setStatus(Status.TEST);
			student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
			student.setName(name);
			student.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STUDENT_PASSWORD));
			student.setEnglishName(englishName);
			Channel aChannel = channelService.findBySourceName(Configurations.Channel.WWW_DEFAULT_CHANNEL);
			student.setChannel(aChannel);
			studentRepository.create(student);
			studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
			
			// 新建招聘课学习进度	
			LearningProgress practicumLearningProgress = new LearningProgress();
			practicumLearningProgress.setStudent(student);
			practicumLearningProgress.setStatus(LearningProgress.Status.STARTED);
			practicumLearningProgress.setCourse(practicumCourse);
			practicumLearningProgress.setLeftClassHour(10);
			practicumLearningProgress.setTotalClassHour(10);
			learningProgressRepository.create(practicumLearningProgress);
			
			securityService.logAudit(Level.INFO, Category.STUDENT_CREATE, "Create Practicum student: " + student.getName());
			
			return student;
		} else{
			//bug :如果在interview创建了学生和learning progress 到了practicum的时候有学生 ，但是没有learning progress ，这样就无法book practicum 课程
			Student student = students.get(0);
			Course recruitmentCourse = courseRepository.findByCourseType(Type.PRACTICUM);
			LearningProgress learningProgress = learningProgressRepository.findStartedLearningProgressByStudentIdAndCourseId(student.getId(), recruitmentCourse.getId());
			if (learningProgress == null) {
				LearningProgress recruitmentLearningProgress = new LearningProgress();
				recruitmentLearningProgress.setStudent(student);
				recruitmentLearningProgress.setStatus(LearningProgress.Status.STARTED);
				recruitmentLearningProgress.setCourse(recruitmentCourse);
				recruitmentLearningProgress.setLeftClassHour(10);
				recruitmentLearningProgress.setTotalClassHour(10);
				learningProgressRepository.create(recruitmentLearningProgress);	
			}
			return student;
		}
	}

	public List<Student> list(
			String gender, Integer age, String province, String city, String lifeCycle, String status, String source, DateTimeParam followUpTargetDateTimeFrom, DateTimeParam followUpTargetDateTimeTo, DateTimeParam followUpCreateDateTimeFrom, DateTimeParam followUpCreateDateTimeTo, // for General
			Long salesIdForPermission, Long chineseLeadTeacherId,
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo, Long courseId, DateTimeParam enrollmentDateFrom, DateTimeParam enrollmentDateTo, Integer customerStage, Long salesId, Integer leftClassHour, DateTimeParam firstClassDateFrom, DateTimeParam firstClassDateTo, // for sale
			Long productId, String payBy, // for product
			Long lastOnlineClassCourseId, Long lastOnlineClassUnitId, Long lastOnlineClassLearningCycleId, Long lastOnlineClassLessonId, DateTimeParam lastEducationalServiceDateFrom, DateTimeParam lastEducationalServiceDateTo, // for education
			String search, Boolean forBooking, String finalResult, int start, int length, String channel, String currentPerformance, AccountType accountType) {
		logger.info("list Student with params: gender = {}, age = {}, province = {}, city = {}, lifeCycle = {}, status = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, courseId = {}, enrollmentDateFrom = {}, enrollmentDateTo = {}, customerStage = {}, saleId = {}, leftClassHour = {}, productId = {}, payBy = {}, search = {}, start = {}, length = {}.", gender, age, province, city, lifeCycle, status, source, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId, leftClassHour, firstClassDateTo, productId, payBy, search, start, length);
		List<Student> students = studentRepository.list(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom, followUpTargetDateTimeTo, followUpCreateDateTimeFrom, followUpCreateDateTimeTo, salesIdForPermission, chineseLeadTeacherId, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId, leftClassHour, /* TODO firstClassDateFrom, firstClassDateTo, */productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId, search, forBooking, finalResult, start, length,channel,currentPerformance,accountType);

		for(Student student : students) {
			List<LearningProgress> learningProgresses = student.getLearningProgresses();
			for(LearningProgress learningProgress : learningProgresses) {
				List<OnlineClass> onlineClasses = learningProgress.getCompletedOnlineClasses();
				OnlineClass firstCompletedOnlineClass = onlineClassRepository.findFirstByTimeInOnlineClassList(onlineClasses);
				OnlineClass lastCompletedOnlineClass = onlineClassRepository.findLastByTimeInOnlineClassList(onlineClasses);
				learningProgress.setFirstCompletedOnlineClass(firstCompletedOnlineClass);
				learningProgress.setLastCompletedOnlineClass(lastCompletedOnlineClass);
			}
		}
		//  TODO 待在Accessor层面优化
		if(courseId != null){
			
			if(firstClassDateFrom != null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students){
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses) {
						if(learningProgress.getCourse().getId() == courseId) {
							if(learningProgress.getFirstCompletedOnlineClass() == null) {
								removeList.add(student);
							}else {
								long firtCompletedOnlineClassTime = learningProgress.getFirstCompletedOnlineClass().getScheduledDateTime().getTime();
								long firstClassDateFromTime = firstClassDateFrom.getValue().getTime();
								if( firtCompletedOnlineClassTime < firstClassDateFromTime){
									removeList.add(student);
								}	
							}	
						}																
					}
				}
				students.removeAll(removeList);
			}
			
			if(firstClassDateTo != null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses) {
						if(learningProgress.getCourse().getId() == courseId) {
							if(learningProgress.getLastCompletedOnlineClass() == null) {
								removeList.add(student);
							}else {
								long LastCompletedOnlineClassTime = learningProgress.getLastCompletedOnlineClass().getScheduledDateTime().getTime();
								long LastClassDateFromTime = firstClassDateTo.getValue().getTime();
								if( LastCompletedOnlineClassTime > LastClassDateFromTime){
									removeList.add(student);
								}
							}	
						}										
					}
				}
				students.removeAll(removeList);
			}
			
		}
		
		if(lastOnlineClassCourseId != null) {
			if(lastOnlineClassUnitId != null && lastOnlineClassLearningCycleId == null && lastOnlineClassLessonId == null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses) {
						if(learningProgress.getLastCompletedOnlineClass() != null && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getLearningCycle().getUnit().getCourse().getId() == lastOnlineClassCourseId && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getLearningCycle().getUnit().getId() != lastOnlineClassUnitId) {
							removeList.add(student);
						}
					}
				}
				students.removeAll(removeList);
			}
			if(lastOnlineClassUnitId != null && lastOnlineClassLearningCycleId != null && lastOnlineClassLessonId == null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses){
						if(learningProgress.getLastCompletedOnlineClass() != null && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getLearningCycle().getUnit().getCourse().getId() == lastOnlineClassCourseId && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getLearningCycle().getId() != lastOnlineClassLearningCycleId) {
							removeList.add(student);
						}
					}
				}
				students.removeAll(removeList);
			}
			if(lastOnlineClassUnitId != null && lastOnlineClassLearningCycleId != null && lastOnlineClassLessonId != null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses){
						if(learningProgress.getLastCompletedOnlineClass() != null && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getLearningCycle().getUnit().getCourse().getId() == lastOnlineClassCourseId && 
								learningProgress.getLastCompletedOnlineClass().getLesson().getId() != lastOnlineClassLessonId) {
							removeList.add(student);
						}
					}
				}
				students.removeAll(removeList);
			}
			
			if(lastEducationalServiceDateFrom != null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses){
						if(learningProgress.getCourse().getId() == lastOnlineClassCourseId) {
							if(learningProgress.getLastCompletedOnlineClass() == null) {
								removeList.add(student);
							}else {
								long lastCompletedOnlineClassTime = learningProgress.getLastCompletedOnlineClass().getScheduledDateTime().getTime();
								long lastClassDateFromTime = lastEducationalServiceDateFrom.getValue().getTime();
								if( lastCompletedOnlineClassTime < lastClassDateFromTime){
									removeList.add(student);
								}
							}	
						}																
					}
				}
				students.removeAll(removeList);
			}
			
			if(lastEducationalServiceDateTo != null) {
				List<Student> removeList = new ArrayList<Student>();
				for(Student student : students) {
					List<LearningProgress> learningProgresses = student.getLearningProgresses();
					for(LearningProgress learningProgress : learningProgresses) {
						if(learningProgress.getLastCompletedOnlineClass() == null) {
							removeList.add(student);
						}else {
							long lastCompletedOnlineClassTime = learningProgress.getLastCompletedOnlineClass().getScheduledDateTime().getTime();
							long lastClassDateToTime = lastEducationalServiceDateTo.getValue().getTime();
							if( lastCompletedOnlineClassTime > lastClassDateToTime) {
								removeList.add(student);
							}
						}												
					}
				}
				students.removeAll(removeList);
			}
		}		
		
		return 	students;
	}

	public Count count(
			String gender, Integer age, String province, String city, String lifeCycle, String status, String source,  DateTimeParam followUpTargetDateTimeFrom, DateTimeParam followUpTargetDateTimeTo, DateTimeParam followUpCreateDateTimeFrom, DateTimeParam followUpCreateDateTimeTo,  // for General
			Long salesIdForPermission, Long chineseLeadTeacherId,
			DateTimeParam registrationDateFrom, DateTimeParam registrationDateTo, Long courseId, DateTimeParam enrollmentDateFrom, DateTimeParam enrollmentDateTo, Integer customerStage, Long salesId, Integer leftClassHour, DateTimeParam firstClassDateFrom, DateTimeParam firstClassDateTo, // for sale// for sale
			Long productId, String payBy, // for product
			Long lastOnlineClassCourseId, Long lastOnlineClassUnitId, Long lastOnlineClassLearningCycleId, Long lastOnlineClassLessonId, DateTimeParam lastEducationalServiceDateFrom, DateTimeParam lastEducationalServiceDateTo, // for education
			String search, Boolean forBooking, String finalResult,String channel, String currentPerformance, AccountType accountType) {
		logger.info("list Student with params: gender = {}, age = {}, province = {}, city = {}, lifeCycle = {}, status = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, courseId = {}, enrollmentDateFrom = {}, enrollmentDateTo = {}, customerStage = {}, saleId = {}, leftClassHour = {}, productId = {}, payBy = {}, search = {}, accountType = {}.", gender, age, province, city, lifeCycle, status, source, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId, leftClassHour, productId, payBy, search, accountType);
		//  TODO 待在Accessor层面优化
		if( firstClassDateFrom == null && firstClassDateTo == null && lastOnlineClassLessonId == null &&  lastOnlineClassLearningCycleId == null && lastOnlineClassUnitId == null && lastOnlineClassCourseId == null && lastEducationalServiceDateFrom == null && lastEducationalServiceDateTo == null) {
			return new Count(studentRepository.count(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom, followUpTargetDateTimeTo, followUpCreateDateTimeFrom, followUpCreateDateTimeTo, salesIdForPermission, chineseLeadTeacherId, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId, leftClassHour, productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId, search, forBooking, finalResult,channel,currentPerformance,accountType));
		}else {
			int start = 0;
			int length = 0;
			List<Student> students = list(gender, age, province, city, lifeCycle, status, source, followUpTargetDateTimeFrom, followUpTargetDateTimeTo, followUpCreateDateTimeFrom, followUpCreateDateTimeTo, salesIdForPermission, chineseLeadTeacherId, registrationDateFrom, registrationDateTo, courseId, enrollmentDateFrom, enrollmentDateTo, customerStage, salesId, leftClassHour, firstClassDateFrom, firstClassDateTo, productId, payBy, lastOnlineClassCourseId, lastOnlineClassUnitId, lastOnlineClassLearningCycleId, lastOnlineClassLessonId, lastEducationalServiceDateFrom, lastEducationalServiceDateTo, search, forBooking, finalResult, start, length,channel,currentPerformance,accountType);
			return new Count(students.size());
		}
	}

	public Student create(Student student, User creater) {
		logger.info("create Student: {}", student);
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
		Student findStudent = studentRepository.findByUsername(student.getUsername());
		if(findStudent == null) {
			Family findFamily = familyRepository.find(student.getFamily().getId());
			if(findFamily != null) {
				if (student.getPassword() == null) {
					String parentPassword = PasswordGenerator.generate();
					student.setPassword(parentPassword);
				}
				student.setFamily(findFamily);
				student.setAvatar("boy_3");
				if (creater != null) {
					student.setCreater(creater);
					student.setLastEditor(creater);
				}
				//updateAssignedToSalesDateTime(student);
				student = studentRepository.create(student);
				studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
				
				if(findFamily.getStudents() != null) {
					findFamily.getStudents().add(student);
				}else {
					List<Student> students = new ArrayList<Student>();
					students.add(student);
					findFamily.setStudents(students);
				}
				
				// 给该学生所有家长发短息
				List<Parent> parents = parentRepository.findByFamilyId(findFamily.getId());
				for(Parent parent : parents) {
					SMS.sendNewStudentSignupToParentsSMS(parent.getMobile(), student.getEnglishName());
				}
				
				// 新建ITTest课学习进度
				Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
				LearningProgress itTestLearningProgress = new LearningProgress();
				itTestLearningProgress.setStudent(student);
				itTestLearningProgress.setStatus(LearningProgress.Status.STARTED);
				itTestLearningProgress.setCourse(itTestCourse);
				itTestLearningProgress.setLeftClassHour(1);
				itTestLearningProgress.setTotalClassHour(1);
				learningProgressRepository.create(itTestLearningProgress);
				
				// 新建试听课学习进度
				Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
				LearningProgress demoLearningProgress = new LearningProgress();
				demoLearningProgress.setStudent(student);
				demoLearningProgress.setStatus(LearningProgress.Status.STARTED);
				demoLearningProgress.setCourse(demoCourse);
				demoLearningProgress.setLeftClassHour(1);
				demoLearningProgress.setTotalClassHour(1);
				learningProgressRepository.create(demoLearningProgress);
				
				//给学生一个默认的飞机
				AirCraft airCraft = new AirCraft();
				airCraft.setStudent(student);
				airCraft.setSequence(1);
				airCraftRepository.create(airCraft);

				AirCraftTheme airCraftTheme = new AirCraftTheme();
				airCraftTheme.setAirCraft(airCraft);
				airCraftTheme.setCurrent(true);
				airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
				airCraftTheme.setLevel(1);
				airCraftTheme.setName("阿波罗号");
				airCraftTheme.setPrice(0);
				airCraftTheme.setUrl("ac1_1");
				airCraftThemeRepository.create(airCraftTheme);

				//给学生一个默认的宠物
				Pet pet = new Pet();
				pet.setStudent(student);
				pet.setName(student.getEnglishName() + "'s Spirit");
				pet.setSequence(10);
				pet.setPrice(0);
				pet.setUrl("pet10");
				pet.setCurrent(true);
				pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
				petRepository.create(pet);			

				
				securityService.logAudit(Level.INFO, Category.STUDENT_CREATE, "Create student: " + student.getName());
				return student;
			}else {
				throw new FamilyNotExistServiceException("Family not exist.");
			}			
		}else{
			throw new UserAlreadyExistServiceException("student already exist.");
		}
	}
	
	public Student create(Student student) {
		return create(student, securityService.getCurrentUser());
	}

	public Student update(Student studentFromJson) {
		if (studentFromJson == null){
			throw new IllegalStateException("Student from json can not be null!");
		}
		Student student = studentRepository.find(studentFromJson.getId());
		if (student == null){
			throw new IllegalStateException("Student from db can not be null!");
		}	
		if (studentFromJson.getChannel() == null){
			throw new IllegalStateException("Student from json's channel can not be null!");
		}
		logger.info("Student orginal channel=",  student.getChannel() == null ? "" : student.getChannel().getSourceName());
		//在执行对象拷贝之前判断是否更改了CLT
		boolean needSetCLTAssignTimeNow = false;
		boolean needSetCLTAssignTimeNull = false;
		if(studentFromJson.getChineseLeadTeacher() != null && student.getChineseLeadTeacher() != null){
			if(studentFromJson.getChineseLeadTeacher().getId() != student.getChineseLeadTeacher().getId()){
				needSetCLTAssignTimeNow = true;
			}
		}else if(studentFromJson.getChineseLeadTeacher() != null){
			needSetCLTAssignTimeNow = true;
		}else if(student.getChineseLeadTeacher() != null){
			needSetCLTAssignTimeNull = true;
		}
		refreshStudent(student, studentFromJson);
		if(needSetCLTAssignTimeNow){
			student.setAssignCltTime(new Date());
		}else if(needSetCLTAssignTimeNull){
			student.setAssignCltTime(null);
		}
		logger.info("Student new channel=", studentFromJson.getChannel().getSourceName());
		
		Channel channel = channelRepository.find(studentFromJson.getChannel().getId());
		if (channel == null){
			throw new IllegalStateException("Student channel's from json should be validated!");
		}
		student.setChannel(channel);
		User lastEditor = securityService.getCurrentUser();
		student.setLastEditor(lastEditor);
		studentRepository.update(student);
		
		securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student: " + student.getEnglishName());
		
		if (student.getLifeCycle().equals(Student.LifeCycle.REFUND)) {
			securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student: " + student.getEnglishName() + " change life cycle to: " + Student.LifeCycle.REFUND);
		}
		
		return student;
	}
	
	public Student updateTargetClassesPerWeek(Student studentFromJson){
		final int targetClassesPerWeek = studentFromJson.getTargetClassesPerWeek();
		if (targetClassesPerWeek < 0 || targetClassesPerWeek > 20){
			throw new IllegalStateException("Incorrect nubmer of target classes per week for student");
		}
		Student student = studentRepository.find(studentFromJson.getId());
		int origTargetClassesPerWeek = student.getTargetClassesPerWeek();
		student.setTargetClassesPerWeek(studentFromJson.getTargetClassesPerWeek());
		User lastEditor = securityService.getCurrentUser();
		student.setLastEditor(lastEditor);	
		studentRepository.update(student);
		
		securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student:" + student.getEnglishName() + " with targetClassesPerWeek property, from " + origTargetClassesPerWeek + " to " + student.getTargetClassesPerWeek());
		
		return student;
	}

	public void doAssignToForeignLeadTeacher(List<Student> students) {
		for(Student student : students){
			Student findStudent = studentRepository.find(student.getId());
			if(findStudent != null && student.getForeignLeadTeacher() != null){
				findStudent.setForeignLeadTeacher(student.getForeignLeadTeacher());
				User lastEditor = securityService.getCurrentUser();
				findStudent.setLastEditor(lastEditor);		
				studentRepository.update(findStudent);
				securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student: " + findStudent.getName());
			}else {
				if(findStudent == null){
					throw new UserNotExistServiceException("Student[id: {}] is not exist.", student.getId());
				}else {
					throw new UserNotExistServiceException("This student's foreign lead teacher can not find");
				}				
			}
		}
	}
	

	public void doAssignToChineseLeadTeacher(List<Student> students) {
		for(Student student : students) {
			Student findStudent = studentRepository.find(student.getId());			
			if(findStudent != null && student.getChineseLeadTeacher() != null){
				findStudent.setChineseLeadTeacher(student.getChineseLeadTeacher());
				findStudent.setAssignCltTime(new Date());	//记录该学生分配给当前CLT的时间
				User lastEditor = securityService.getCurrentUser();
				findStudent.setLastEditor(lastEditor);		
				studentRepository.update(findStudent);
				securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student: " + findStudent.getName());
			}else {
				if(findStudent == null){
					throw new UserNotExistServiceException("Student[id: {}] is not exist.", student.getId());
				}else {
					throw new UserNotExistServiceException("This student's local lead teacher can not find");
				}				
			}
		}
	}
	

	public void doAssignToSale(List<Student> students) {
		for(Student student : students) {
			Student findStudent = studentRepository.find(student.getId());			
			if(findStudent != null && student.getSales() != null){
				findStudent.setSales(student.getSales());
				User lastEditor = securityService.getCurrentUser();
				findStudent.setLastEditor(lastEditor);
				if(findStudent.getLifeCycle() == LifeCycle.SIGNUP) {
					findStudent.setLifeCycle(LifeCycle.ASSIGNED);
				}			
				findStudent.setAssignedToSalesDateTime(new Date());
				studentRepository.update(findStudent);
				securityService.logAudit(Level.INFO, Category.STUDENT_UPDATE, "Update student: " + findStudent.getName());
			}else {
				if(findStudent == null){
					throw new UserNotExistServiceException("Student[id: {}] is not exist.", student.getId());
				}else {
					throw new UserNotExistServiceException("This student's sales can not find");
				}				
			}
		}
	}
	

	public Student doResetPassword(long id) {
		Student student = studentRepository.find(id);
		if(student == null) {
			throw new UserNotExistServiceException("Student[id: {}] is not exist.", id);
		}else {
			String password = PasswordGenerator.generate();
			student.setPassword(password);
			User lastEditor = securityService.getCurrentUser();
			student.setLastEditor(lastEditor);
			studentRepository.update(student);
			
			// 给该学生所有家长发短息
			List<Parent> parents = student.getFamily().getParents();
			for(Parent parent : parents) {
				SMS.sendNewPasswordOfStudentToParentsSMS(parent.getMobile(), student.getEnglishName(), password);
			}
			
			securityService.logAudit(Level.INFO, Category.STUDENT_RESET_PASSWORD, "Reset password for student: " + student.getName());
		}
		
		return student;
	}

	public Student updateFavoredTeachers(Student student) {
		return studentRepository.upateFavoredTeachers(student);
	}
	

	public List<Option> listStudentSource() {
		List<Option> studentSourceOptions = new ArrayList<Option>(); 
		for(Source source : Source.values()) {
			Option option = new Option();
			option.setValue(source.toString());
			option.setLabel(source.toString());
			studentSourceOptions.add(option);
		}
		return studentSourceOptions;
	}
	

	public List<Student> listForAgent(
			String lifeCycle, 
			String source, 
			DateTimeParam registrationDateFrom,
			DateTimeParam registrationDateTo, 
			String search,
			int start, 
			int length) {
		logger.info("list Student with params:  lifeCycle = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, search = {},start = {},length = {}.",  lifeCycle, source, registrationDateFrom, registrationDateTo,  search,start,length);
		
		List<Student> students = studentRepository.listForAgent(lifeCycle, source, registrationDateFrom, registrationDateTo, search, start, length);

		return 	students;
	}


	public Count countForAgent(
			String lifeCycle, 
			String source, 
			DateTimeParam registrationDateFrom,
			DateTimeParam registrationDateTo, 
			String search) {
		logger.info("count Student with params:  lifeCycle = {}, source = {}, registrationDateFrom = {}, registrationDateTo = {}, search = {}.",  lifeCycle, source, registrationDateFrom, registrationDateTo,  search);
		
		return new Count(studentRepository.countForAgent(lifeCycle, source, registrationDateFrom, registrationDateTo, search));

	}
	
	public List<Student> findByNameOrEnglishName(String name, int start,
			int length) {
		return studentRepository.findByNameOrEnglishName(name, start, length);
	}
	
	private void refreshStudent(final Student student, final Student studentFromJson){
		List<OnlineClass> dbOnlineClasses = student.getOnlineClasses();
		List<LearningProgress> dbLearningProgresses = student.getLearningProgresses();
		BeanUtils.copyProperties(studentFromJson, student);
		student.setOnlineClasses(dbOnlineClasses);
		student.setLearningProgresses(dbLearningProgresses);
	}
	
	public String doCollect(long teacherId,long studentId){
		logger.info("doCollect with params:  teacherId = {}, studentId = {}.",  teacherId, studentId);
		long count = studentRepository.countFavorateTeacher(teacherId, studentId);
		if(count==0){
			studentRepository.doCollectAdd(teacherId, studentId);
			return "<p class='tc'>收藏成功</p>";
		}else{
			studentRepository.doCollectRemove(teacherId, studentId);
			return "<p class='tc'>取消收藏成功</p>";
		}
	}
	
	public List<Student> findByMarketingActivityId(long marketingActivityId) {
		logger.info("findByMarketingActivityId:  marketingActivityId = {}", marketingActivityId);
		return studentRepository.findByMarketingActivityId(marketingActivityId);
	}
	
	/**
	 * 
	* @Title: doTakeStar 
	* @Description: 公开课学生领取5课星星
	* @param parameter
	* @author zhangfeipeng 
	* @return void
	* @throws
	 */
	public void doTakeStar(long studentId,long teacherId,long onlineClassId){
		logger.info("doTakeStar:  studentId = {},teacherId = {},onlineClassId = {}", studentId,teacherId,onlineClassId);
		studentRepository.doTakeStarStudentTable(studentId);
		studentRepository.doTakeStarTeacherCommentTable(studentId, teacherId, onlineClassId);
	}
	
	/**
	 * 
	* @Title: sendEmailToSale 
	* @Description: 公开课学生预约trail课，给sale 发邮件
	* @param parameter
	* @author zhangfeipeng 
	* @return void
	* @throws
	 */
	public void sendEmailToSale(long id,String lessonSerialNumber){
		Student student = studentRepository.find(id);
		String toEmail = null;
		if(student.getSales()!=null){
			toEmail = student.getSales().getEmail();
		}
		String phone = "";
		for(Parent parent : student.getFamily().getParents()) {
			if(StringUtils.isNotBlank(parent.getMobile())){
				phone+=parent.getMobile()+"/";
			}
		}
		if(phone.indexOf("/")>-1){
			phone = phone.substring(0,phone.length()-1);
		}
		try {
			EMail.sendTrialOnlineClassFromOpenClass(student, phone, lessonSerialNumber, toEmail);
			logger.info("Success: sendEmailToSale from = {} to email = {}",Configurations.System.SYSTEM_USER_NAME,toEmail);
		} catch (Throwable e) {
			logger.error("Exception found when sendEmailToSale:" + e.getMessage()+",toemail"+toEmail, e);
		}
	}
	public Student findRecentResisteringByParentId (long parentId) {
		logger.info("find recent resistering student by parent id = {}", parentId);
		return studentRepository.findRecentResisteringByParentId(parentId);
	}

	public List<CltStudent> findCLTStudents(Date assignDateFrom, Date assignDateTo, Date followUpCreateDateFrom, Date followUpCreateDateTo, Date followUpTargetDateFrom, Date followUpTargetDateTo, String followUpDashType, String channel, String lifeCycle, Long cltId, String searchStudentText, String studentLevel, Integer start, Integer length){
		return studentRepository.selectCltStudents(assignDateFrom, assignDateTo, followUpCreateDateFrom, followUpCreateDateTo, followUpTargetDateFrom, followUpTargetDateTo, followUpDashType, channel, lifeCycle, cltId, searchStudentText, studentLevel, start, length);
	}

	public Count findCLTStudentsCount(Date assignDateFrom, Date assignDateTo, Date followUpCreateDateFrom, Date followUpCreateDateTo, Date followUpTargetDateFrom, Date followUpTargetDateTo, String followUpDashType, String channel, String lifeCycle, Long cltId, String searchStudentText, String studentLevel){
		long res = studentRepository.selectCltStudentsCount(assignDateFrom, assignDateTo, followUpCreateDateFrom, followUpCreateDateTo, followUpTargetDateFrom, followUpTargetDateTo, followUpDashType, channel, lifeCycle, cltId, searchStudentText, studentLevel);
		return new Count(res);
	}
}
