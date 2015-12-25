package com.vipkid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.model.Activity;
import com.vipkid.model.Country;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.EducationalComment;
import com.vipkid.model.FiremanToStudentComment;
import com.vipkid.model.FiremanToTeacherComment;
import com.vipkid.model.Gender;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.PPT;
import com.vipkid.model.Product;
import com.vipkid.model.Resource;
import com.vipkid.model.Resource.Type;
import com.vipkid.model.Role;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.RecruitmentChannel;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.Unit;
import com.vipkid.model.User;
import com.vipkid.repository.ActivityRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.EducationalCommentRepository;
import com.vipkid.repository.FiremanToStudentCommentRepository;
import com.vipkid.repository.FiremanToTeacherCommentRepository;
import com.vipkid.repository.LearningCycleRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.PPTRepository;
import com.vipkid.repository.ProductRepository;
import com.vipkid.repository.ResourceRepository;
import com.vipkid.repository.SlideRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.UnitRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;

@Service
public class DeploymentService {
	private Logger logger = LoggerFactory.getLogger(DeploymentService.class
			.getSimpleName());

	@javax.annotation.Resource
	private StaffRepository staffAccessor;

	@javax.annotation.Resource
	private CourseRepository courseAccessor;

	@javax.annotation.Resource
	private UnitRepository unitAccessor;

	@javax.annotation.Resource
	private LearningCycleRepository learningCycleAccessor;

	@javax.annotation.Resource
	private LessonRepository lessonAccessor;

	@javax.annotation.Resource
	private ActivityRepository activityAccessor;

	@javax.annotation.Resource
	private ResourceRepository resourceAccessor;

	@javax.annotation.Resource
	private ProductRepository productAccessor;

	@javax.annotation.Resource
	private PPTRepository pptAccessor;

	@javax.annotation.Resource
	private SlideRepository slideAccessor;

	@javax.annotation.Resource
	private ExcelToXMLReaderService excelToXMLReaderService;

	@javax.annotation.Resource
	private StudentRepository studentAccessor;

	@javax.annotation.Resource
	private LearningProgressRepository learningProgressAccessor;

	@javax.annotation.Resource
	private TeacherRepository teacherAccessor;

	@Context
	private ServletContext servletContext;

	@javax.annotation.Resource
	private OnlineClassRepository onlineClassAccessor;

	@javax.annotation.Resource
	private TeacherCommentRepository teacherCommentAccessor;

	@javax.annotation.Resource
	private EducationalCommentRepository educationalCommentAccessor;

	@javax.annotation.Resource
	private FiremanToStudentCommentRepository firemanToStudentCommentAccessor;

	@javax.annotation.Resource
	private FiremanToTeacherCommentRepository firemanToTeacherCommentAccessor;

	public Response updateData() {
		File favoriteFile = new File(
				servletContext.getRealPath("/WEB-INF/data/book.txt"));
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(favoriteFile));
			String temp = null;

			List<String> errors = new ArrayList<String>();

			while ((temp = reader.readLine()) != null) {
				String[] splits = temp.split(",");
				if (true) {
					String studentName = splits[0].trim();
					String bookDateTime = splits[1].trim();
					String lessonSerialNumber = splits[2].trim();
					String teacherName = splits[4].trim();

					List<Student> students = studentAccessor
							.findByName(studentName);
					if (students.isEmpty()) {
						errors.add("can't find student by name: " + studentName);
						continue;
					} else if (students.size() > 1) {
						errors.add("find more than one student by name: "
								+ studentName);
						continue;
					}

					List<Teacher> teachers = teacherAccessor
							.findByName(teacherName);
					if (teachers.isEmpty()) {
						errors.add("can't find teacher by name: " + teacherName);
						continue;
					} else if (teachers.size() > 1) {
						errors.add("find more than one teacher by name: "
								+ teacherName);
						continue;
					}

					Lesson lesson = lessonAccessor
							.findBySerialNumber(lessonSerialNumber);
					if (lesson == null) {
						errors.add("can't find lesson by number: "
								+ lessonSerialNumber);
						continue;
					}

					Student student = students.get(0);
					Teacher teacher = teachers.get(0);
					Date scheduledDateTime = DateTimeUtils.parse(bookDateTime,
							DateTimeUtils.DATETIME_FORMAT);
					Calendar scheduledCalendar = Calendar.getInstance();
					scheduledCalendar.setTime(scheduledDateTime);
					scheduledCalendar.set(Calendar.MILLISECOND, 0);
					scheduledDateTime = scheduledCalendar.getTime();

					User system = staffAccessor
							.findByUsername(Configurations.System.SYSTEM_USERNAME);

					OnlineClass onlineClass = onlineClassAccessor
							.findByTeacherIdAndScheduledDateTime(
									teacher.getId(), scheduledDateTime);
					if (onlineClass != null
							&& (onlineClass.getStatus() == Status.AVAILABLE || onlineClass
									.getStatus() == Status.BOOKED)) {
						onlineClass.addStudent(student);
						onlineClass.setLesson(lesson);
						onlineClass.setBooker(system);
						onlineClass.setBookDateTime(new Date());
						onlineClass.setLastEditor(system);
						onlineClass.setStatus(Status.BOOKED);
						onlineClassAccessor.update(onlineClass);
					} else {
						onlineClass = new OnlineClass();
						onlineClass.addStudent(student);
						onlineClass.setTeacher(teacher);
						onlineClass.setLesson(lesson);
						onlineClass.setScheduledDateTime(scheduledDateTime);
						onlineClass.setBooker(system);
						onlineClass.setBookDateTime(new Date());
						onlineClass.setLastEditor(system);
						onlineClass.setStatus(Status.BOOKED);
						onlineClassAccessor.create(onlineClass);
					}

					createComments(onlineClass);

					LearningProgress learningProgress = learningProgressAccessor
							.findByStudentIdAndCourseId(student.getId(), 5102);
					learningProgress.setLastScheduledLesson(lesson);
					learningProgressAccessor.update(learningProgress);

					logger.info("Success to book for: " + temp);
				}
			}
			for (String error : errors) {
				logger.error(error);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭文件流
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new Response(HttpStatus.OK.value());
	}

	private void createComments(OnlineClass bookedOnlineClass) {
		for (Student theStudent : bookedOnlineClass.getStudents()) {
			TeacherComment teacherComment = teacherCommentAccessor
					.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(),
							theStudent.getId());
			if (teacherComment == null) {
				teacherComment = new TeacherComment();
				teacherComment.setOnlineClass(bookedOnlineClass);
				teacherComment.setStudent(theStudent);
				teacherComment.setTeacher(bookedOnlineClass.getTeacher());
				teacherComment.setEmpty(true);
				teacherCommentAccessor.create(teacherComment);
			}

			EducationalComment educationalComment = educationalCommentAccessor
					.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(),
							theStudent.getId());
			if (educationalComment == null) {
				educationalComment = new EducationalComment();
				educationalComment.setOnlineClass(bookedOnlineClass);
				educationalComment.setStudent(theStudent);
				educationalComment.setEmpty(true);
				educationalCommentAccessor.create(educationalComment);
			}

			FiremanToStudentComment firemanToStudentComment = firemanToStudentCommentAccessor
					.findByOnlineClassIdAndStudentId(bookedOnlineClass.getId(),
							theStudent.getId());
			if (firemanToStudentComment == null) {
				firemanToStudentComment = new FiremanToStudentComment();
				firemanToStudentComment.setOnlineClass(bookedOnlineClass);
				firemanToStudentComment.setStudent(theStudent);
				firemanToStudentComment.setEmpty(true);
				firemanToStudentCommentAccessor.create(firemanToStudentComment);
			}
		}

		FiremanToTeacherComment firemanToTeacherComment = firemanToTeacherCommentAccessor
				.findByOnlineClassIdAndTeacherId(bookedOnlineClass.getId(),
						bookedOnlineClass.getTeacher().getId());
		if (firemanToTeacherComment == null) {
			firemanToTeacherComment = new FiremanToTeacherComment();
			firemanToTeacherComment.setOnlineClass(bookedOnlineClass);
			firemanToTeacherComment.setTeacher(bookedOnlineClass.getTeacher());
			firemanToTeacherComment.setEmpty(true);
			firemanToTeacherCommentAccessor.create(firemanToTeacherComment);
		}
	}

	public Response deploy2() {
		Teacher teacher = new Teacher();
		teacher.setUsername("test2@vipkid.com");
		teacher.setPassword("test");
		teacher.setSerialNumber("12345678");
		teacher.setEmail("test2@vipkid.com");
		teacher.setName("Samantha Song");
		teacher.setGender(Gender.FEMALE);
		teacher.setType(Teacher.Type.FULL_TIME);
		teacher.setCountry(Country.AUSTRALIA);
		teacher.setTimezone("Pacific/Apia");
		teacher.setMobile("15811062327");
		teacher.setRoles("TEACHER");
		teacher.setAddress("ssss");
		teacher.setSkype("ssss");
		teacher.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacher.setLifeCycle(LifeCycle.SIGNUP);
		teacherAccessor.create(teacher);

		return new Response(HttpStatus.OK.value());
	}

	public Response deploy() {
		// 创建管理员
		Staff staff = new Staff();
		staff.setUsername("huozhenzhong@vipkid.com.cn");
		staff.setPassword(PasswordEncryptor
				.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff.addRole(Role.STAFF_ADMIN);
		staff.addRole(Role.STAFF_CXO);
		staff.setEmail("huozhenzhong@vipkid.com.cn");
		staff.setMobile("18501361687");
		staff.setName("霍振中");
		staff.setEnglishName("Forest Huo");
		staffAccessor.create(staff);

		// 创建系统用户
		Staff system = new Staff();
		system.setUsername("system@vipkid.com.cn");
		system.setPassword(PasswordEncryptor
				.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		system.addRole(Role.STAFF_ADMIN);
		system.setEmail("system@vipkid.com.cn");
		system.setMobile("18501361687");
		system.setName("系统");
		system.setEnglishName("system");
		system.setStatus(User.Status.LOCKED);
		staffAccessor.create(system);

		// 创建IT测试课
		createITTest();

		// 创建试听课
		createDemo();

		// 创建Foundation试听课
		// updateFoundationDemo();

		// 创建新生指导课
		createGuide();

		try {
			excelToXMLReaderService.doInitForCourse();
			excelToXMLReaderService.doInitForSlide();
		} catch (Exception e) {
			logger.error("Exception when init course and slide: "
					+ e.getMessage());
		}

		return new Response(HttpStatus.OK.value());
	}

	private void createITTest() {
		// 创建IT测试课程
		Course course = new Course();
		course.setSerialNumber("IT1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("IT测试课程");
		course.setDescription("保障家长可以通过在线教室正常上课。");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.IT_TEST);
		courseAccessor.create(course);

		Unit unit = new Unit();
		unit.setName("IT测试单元");
		unit.setSerialNumber("IT1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("IT测试单元");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("IT测试学习闭环");
		learningCycle.setSerialNumber("IT1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson = new Lesson();
		lesson.setName("IT测试课");
		lesson.setSerialNumber("IT1-U1-LC1-L1");
		lesson.setSequence(1);
		lesson.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson);

		Activity activity = new Activity();
		activity.setName("IT测试活动");
		activity.setLesson(lesson);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("IT测试PPT");
		resource.setType(Type.PPT);
		resource.setUrl(Configurations.OSS.Template.PPT.replace(
				Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt = new PPT();
		ppt.setResource(resource);
		pptAccessor.create(ppt);

		// 创建IT测试课商品
		Product product = new Product();
		product.setName("IT测试课");
		product.setDescription("用于保障家长可以通过在线教室正常上课。");
		product.setStatus(Product.Status.ON_SALE);
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}

	private void createDemo() {
		// 创建试听课程
		Course course = new Course();
		course.setSerialNumber("DEMO1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("试听课程");
		course.setDescription("学生试听课程。");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.DEMO);
		courseAccessor.create(course);

		Unit unit = new Unit();
		unit.setName("试听单元");
		unit.setSerialNumber("DEMO1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("试听单元");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("试听学习闭环");
		learningCycle.setSerialNumber("DEMO1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson = new Lesson();
		lesson.setName("试听课");
		lesson.setSerialNumber("DEMO1-U1-LC1-L1");
		lesson.setSequence(1);
		lesson.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson);

		Activity activity = new Activity();
		activity.setName("试听活动");
		activity.setLesson(lesson);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("试听PPT");
		resource.setType(Type.PPT);
		resource.setUrl(Configurations.OSS.Template.PPT.replace(
				Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt = new PPT();
		ppt.setResource(resource);
		pptAccessor.create(ppt);

		// 创建试听课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("试听课");
		product.setDescription("学生试听课程。");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}

	@SuppressWarnings("unused")
	private void updateFoundationDemo() {
		LearningCycle learningCycle = learningCycleAccessor
				.findBySerialNumber("DEMO1-U1-LC1");

		Lesson lesson = new Lesson();
		lesson.setName("Foundation Demo Lesson（基础试听课）");
		lesson.setSerialNumber("DEMO1-U1-LC1-L2");
		lesson.setSequence(2);
		lesson.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson);

		Activity activity = new Activity();
		activity.setName("Foundation Demo Activity（试听活动）");
		activity.setLesson(lesson);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("Foundation Demo PPT（试听PPT）");
		resource.setType(Type.PPT);
		resource.setUrl(Configurations.OSS.Template.PPT.replace(
				Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt = new PPT();
		ppt.setResource(resource);
		pptAccessor.create(ppt);
	}

	@SuppressWarnings("unused")
	private void createFoundationDemo() {
		// 创建Fundation试听课程
		Course course = new Course();
		course.setSerialNumber("DEMO2");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("Foundation Demo Course（试听课程）");
		course.setDescription("Demo to the foundation students on VIPKID educational program. Fundation. 学生试听课程。");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.DEMO);
		courseAccessor.create(course);

		Unit unit = new Unit();
		unit.setName("Demo Unit（试听单元）");
		unit.setSerialNumber("DEMO2-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("Demo Unit 试听单元");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Demo Learning Cycle（试听学习闭环）");
		learningCycle.setSerialNumber("DEMO2-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson = new Lesson();
		lesson.setName("Demo Lesson（试听课）");
		lesson.setSerialNumber("DEMO2-U1-LC1-L1");
		lesson.setSequence(1);
		lesson.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson);

		Activity activity = new Activity();
		activity.setName("Demo Activity（试听活动）");
		activity.setLesson(lesson);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("试听PPT");
		resource.setType(Type.PPT);
		resource.setUrl(Configurations.OSS.Template.PPT.replace(
				Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt = new PPT();
		ppt.setResource(resource);
		pptAccessor.create(ppt);

		// 创建Foundation试听课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("Foundation试听课");
		product.setDescription("Foundation学生试听课程。");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}

	private void createGuide() {
		// 创建新生指导课程
		Course course = new Course();
		course.setSerialNumber("G1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("新生指导课程");
		course.setDescription("帮助学生熟悉教学系统和教学方法。");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.GUIDE);
		courseAccessor.create(course);

		Unit unit = new Unit();
		unit.setName("新生指导单元");
		unit.setSerialNumber("G1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("新生指导单元");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("新生指导学习闭环");
		learningCycle.setSerialNumber("G1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson = new Lesson();
		lesson.setName("新生指导课");
		lesson.setSerialNumber("G1-U1-LC1-L1");
		lesson.setSequence(1);
		lesson.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson);

		Activity activity = new Activity();
		activity.setName("新生指导活动");
		activity.setLesson(lesson);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("新生指导PPT");
		resource.setType(Type.PPT);
		resource.setUrl(Configurations.OSS.Template.PPT.replace(
				Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt = new PPT();
		ppt.setResource(resource);
		pptAccessor.create(ppt);

		// 创建新生指导课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("新生指导课");
		product.setDescription("新生指导课程。");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}

	private void createRecruitmentCourse() {
		// 创建教室招聘课程
		Course recruitmentCourse = new Course();
		recruitmentCourse.setSerialNumber("R1");
		recruitmentCourse.setMode(Mode.ONE_TO_MANY);
		recruitmentCourse.setName("Recruitment");
		recruitmentCourse.setDescription("Interview teacher.");
		recruitmentCourse.setNeedBackupTeacher(false);
		recruitmentCourse.setSequential(false);
		recruitmentCourse.setFree(true);
		recruitmentCourse.setType(Course.Type.TEACHER_RECRUITMENT);
		courseAccessor.create(recruitmentCourse);

		Unit unit = new Unit();
		unit.setName("Recruitment Unit");
		unit.setNameInLevel("R1-U1");
		unit.setSerialNumber("R1-U1");
		unit.setCourse(recruitmentCourse);
		unit.setLevel(Level.LEVEL_0);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Recruitment LearningCycle");
		learningCycle.setSerialNumber("R1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson4Recruitment = new Lesson();
		lesson4Recruitment.setName("Recruitment Lesson");
		lesson4Recruitment.setSerialNumber("R1-U1-LC1-L1");
		lesson4Recruitment.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson4Recruitment);

		Activity activity = new Activity();
		activity.setName("Recruitment Activity");
		activity.setLesson(lesson4Recruitment);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("Recruitment PPT");
		resource.setType(Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt1 = new PPT();
		ppt1.setResource(resource);
		pptAccessor.create(ppt1);

		// 创建教师招聘课商品
		Product recruitmentProduct = new Product();
		recruitmentProduct.setName("Recruitment Product");
		recruitmentProduct.setDescription("Interview Product");
		recruitmentProduct.setStatus(Product.Status.ON_SALE);
		recruitmentProduct.setCourse(recruitmentCourse);
		recruitmentProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		recruitmentProduct.setUnits(units);
		productAccessor.create(recruitmentProduct);
	}
	
	public Response createOpen1Course(){
		//创建一对多课程
		Course open1Course = new Course();
		open1Course.setSerialNumber("OPEN1");
		open1Course.setMode(Mode.ONE_TO_MANY);
		open1Course.setName("Open1");
		open1Course.setShowName("公开课");
		open1Course.setDescription("Open1");
		open1Course.setNeedBackupTeacher(false);
		open1Course.setSequential(false);
		open1Course.setFree(true);
		open1Course.setType(Course.Type.OPEN1);
		courseAccessor.create(open1Course);

		Unit unit = new Unit();
		unit.setName("Unit1");
		unit.setNameInLevel("Unit1");
		unit.setSerialNumber("OPEN1-U1");
		unit.setCourse(open1Course);
		unit.setLevel(Level.LEVEL_0);
		unit.setUnitTestPath("");
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("LearningCycle1");
		learningCycle.setSerialNumber("OPEN1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson1 = new Lesson();
		lesson1.setName("Lesson1");
		lesson1.setSerialNumber("OPEN1-U1-LC1-L1");
		lesson1.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson1);


		Activity activity = new Activity();
		activity.setName("Open1 Activity One");
		activity.setLesson(lesson1);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("Open1 PPT One");
		resource.setType(Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt1 = new PPT();
		ppt1.setResource(resource);
		pptAccessor.create(ppt1);

		// 创建Open1课商品
		Product Open1Product = new Product();
		Open1Product.setName("Open1");
		Open1Product.setDescription("Open1.");
		Open1Product.setStatus(Product.Status.ON_SALE);
		Open1Product.setCourse(open1Course);
		Open1Product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		Open1Product.setUnits(units);
		productAccessor.create(Open1Product);
		
		return new Response(HttpStatus.OK.value());
	}

	private void createPracticumCourse() {
		// 创建教室招聘课程
		Course prcticumCourse = new Course();
		prcticumCourse.setSerialNumber("P1");
		prcticumCourse.setMode(Mode.ONE_TO_MANY);
		prcticumCourse.setName("Practicum");
		prcticumCourse.setDescription("Practicum");
		prcticumCourse.setNeedBackupTeacher(false);
		prcticumCourse.setSequential(false);
		prcticumCourse.setFree(true);
		prcticumCourse.setType(Course.Type.PRACTICUM);
		courseAccessor.create(prcticumCourse);

		Unit unit = new Unit();
		unit.setName("Practicum Unit");
		unit.setNameInLevel("P1-U1");
		unit.setSerialNumber("P1-U1");
		unit.setCourse(prcticumCourse);
		unit.setLevel(Level.LEVEL_0);
		unitAccessor.create(unit);

		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Practicum LearningCycle");
		learningCycle.setSerialNumber("P1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);

		Lesson lesson4Practicum = new Lesson();
		lesson4Practicum.setName("Practicum Lesson One");
		lesson4Practicum.setSerialNumber("P1-U1-LC1-L1");
		lesson4Practicum.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson4Practicum);

		Lesson lesson4Practicum2 = new Lesson();
		lesson4Practicum2.setName("Practicum Lesson Two");
		lesson4Practicum2.setSerialNumber("P1-U1-LC1-L2");
		lesson4Practicum2.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson4Practicum2);

		Activity activity = new Activity();
		activity.setName("Practicum Activity One");
		activity.setLesson(lesson4Practicum);
		activityAccessor.create(activity);

		Resource resource = new Resource();
		resource.setName("Practicum PPT One");
		resource.setType(Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceAccessor.create(resource);

		PPT ppt1 = new PPT();
		ppt1.setResource(resource);
		pptAccessor.create(ppt1);

		Activity activity2 = new Activity();
		activity2.setName("Practicum Activity Two");
		activity2.setLesson(lesson4Practicum2);
		activityAccessor.create(activity2);

		Resource resource2 = new Resource();
		resource2.setName("Practicum PPT Two");
		resource2.setType(Type.PPT);
		resource2.setUrl("xxx");
		List<Activity> activities2 = new ArrayList<Activity>();
		activities2.add(activity2);
		resource2.setActivities(activities2);
		resourceAccessor.create(resource2);

		PPT ppt2 = new PPT();
		ppt2.setResource(resource2);
		pptAccessor.create(ppt2);

		// 创建教师招聘课商品
		Product practicumProduct = new Product();
		practicumProduct.setName("Practicum Proudct");
		practicumProduct.setDescription("Practicum Product.");
		practicumProduct.setStatus(Product.Status.ON_SALE);
		practicumProduct.setCourse(prcticumCourse);
		practicumProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		practicumProduct.setUnits(units);
		productAccessor.create(practicumProduct);
	}

	public Response initTeacherRecruitment() {

		createRecruitmentCourse();
		createPracticumCourse();

		return new Response(HttpStatus.OK.value());
	}
	


	public Response initGuide() {
		createGuide();

		return new Response(HttpStatus.OK.value());
	}

	public Response initTrial() {
		createTrial();
		
		return new Response(HttpStatus.OK.value());
	}

	public Response initAssessment() {
		createAssessment();
		
		return new Response(HttpStatus.OK.value());
	}
	
	public Response doInitLTAndAccessment(){
		createLittleTranslation();
		createAssessment2();
		
		return new Response(HttpStatus.OK.value());
		
	}

	private void createAssessment2() {

		// 创建测评课程
		Course course = new Course();
		course.setSerialNumber("A2");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("Assessment2");
		course.setDescription("Assessment2 Course");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.ASSESSMENT2);
		course.setBaseClassSalary(8F);
		courseAccessor.create(course);
		
		
		
		Unit unit = new Unit();
		unit.setName("Assessment2 Unit");
		unit.setSerialNumber("A2-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("Assessment2 Unit");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Assessment2 Learning Cycle");
		learningCycle.setSerialNumber("A2-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);
		
		Lesson lesson1 = new Lesson();
		lesson1.setName("Assessment2 Lesson");
		lesson1.setSerialNumber("A2-U1-LC1-L1");
		lesson1.setSequence(1);
		lesson1.setLearningCycle(learningCycle);
		//lesson1.setDbyDocument("53df1ba5-15e5-4f29-b04d-90572362ad75");
		lessonAccessor.create(lesson1);
		
		Activity activity1 = new Activity();
		activity1.setName("Assessment Activity");
		activity1.setLesson(lesson1);
		activityAccessor.create(activity1);
	
		Resource resource1 = new Resource();
		resource1.setName("Assessment2 PPT");
		resource1.setType(Type.PPT);
		resource1.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson1.getSerialNumber()));
		List<Activity> activities1 = new ArrayList<Activity>();
	    activities1.add(activity1);
	    resource1.setActivities(activities1);
		resourceAccessor.create(resource1);
		
		PPT ppt1 = new PPT();
		ppt1.setResource(resource1);
        pptAccessor.create(ppt1);
        
		// 创建测评课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("Assessment2 Product");
		product.setDescription("Assessment2 Product");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);		
		
	}

	private void createLittleTranslation() {

		// 创建测评课程
		Course course = new Course();
		course.setSerialNumber("LT1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("小翻译课程");
		course.setDescription("Little Translator Course");
		course.setNeedBackupTeacher(false);
		course.setSequential(true);
		course.setFree(true);
		course.setType(Course.Type.ELECTIVE_LT);
		course.setBaseClassSalary(8F);
		courseAccessor.create(course);
		
		Unit unit = new Unit();
		unit.setName("Little Translator Unit");
		unit.setSerialNumber("LT1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("Little Translator Unit");
		unit.setSequence(1);
		unit.setCourse(course);
		unit.setObjective("Learning Cycle Objective");
		unitAccessor.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Little Translator Learning Cycle");
		learningCycle.setSerialNumber("LT1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);
		
		//课程1
		Lesson lesson1 = new Lesson();
		lesson1.setName("Little Translator Lesson1");
		lesson1.setSerialNumber("LT1-U1-LC1-L1");
		lesson1.setSequence(1);
		lesson1.setTopic("Common Situations");
		lesson1.setLearningCycle(learningCycle);
		lesson1.setObjective("Introduce basic vocabulary, cultural concepts and conversational skills for common situations while traveling. Concepts: tip.");
		lesson1.setGoal("N/A");
		lesson1.setMathTarget("N/A");
		lesson1.setReviewTarget("N/A");

		lesson1.setVocabularies("Basic: speak Chinese/ English,hotel,check in,bus, taxi restaurant, eat, drink, tip, ticket,money,pay Intermediate: room number,"
				+ "key, bill, buy,ticket, menu,train, subwayAdvanced: depart, taxi driver,waiter/waitress,appetizer, maincourse, dessert,cash, credit card");
		//lesson1.setDbyDocument("53df1ba5-15e5-4f29-b04d-90572362ad75");
		lesson1.setSentencePatterns("Basic: - Do you speak (Chinese/English)? Yes, I speak (Chinese/English). - No, I don’t. HOTEL- Hello! How can I help you?-"
				+ " Hi! My name is __. I would like to check in.RESTAURANT- What would you/he/she like to eat/drink?- I would like___, please."
				+ "Intermediate: - He/she would like ___.Does it taste good or bad?- It tastes good/bad.TRANSPORT- Where do you want to g?- "
				+ "I want to go to the hotel/restaurant, please.- What do you need to take the ___?- I need a ticket/money to take the ___.Intermediate: HOTEL- Your room number is __. Here is your key."
				+ "- Thank you.TRANSPORT- I would like (#) ticket(s), please.- That will be (amount).- Here you go.- Thank you.RESTAURANT"
				+ "- Can I see the menu, please?- Here you go.- Thank you.- I would like the bill, please.- Here you go.- Thank you.Advanced: HOTEL- Do you need help with your bags?"
				+ "- Yes, please./No, thanks.RESTAURANT- What would you like for your appetizer/main course/dessert?- For my appetizer/main course/dessert, I would like ____."
				+ "- Do you accept credit cards?- Yes, we do.- No, sorry. We only accept cash.TRANSPORT- When does the train/bus depart?- The train/bus departs at ___. ");
		lesson1.setLssTarget("N/A");
        lessonAccessor.create(lesson1);
		
		Activity activity1 = new Activity();
		activity1.setName("level1 Little Translator Activity");
		activity1.setLesson(lesson1);
		activityAccessor.create(activity1);
	
		Resource resource1 = new Resource();
		resource1.setName("level 1Little Translator PPT");
		resource1.setType(Type.PPT);
		resource1.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson1.getSerialNumber()));
		List<Activity> activities1 = new ArrayList<Activity>();
	    activities1.add(activity1);
	    resource1.setActivities(activities1);
		resourceAccessor.create(resource1);
		
		PPT ppt1 = new PPT();
		ppt1.setResource(resource1);
        pptAccessor.create(ppt1);
        
        //课程2
        Lesson lesson2 = new Lesson();
        lesson2.setLssTarget("N/A");
        lesson2.setName("Little Translator Lesson2");
        lesson2.setTopic("Getting Help & Staying Safe");
		lesson2.setObjective("Introduce basic vocabulary,Cultural concepts and conversational skills to use at the airport and when needing help. Concepts: safety.");
		lesson2.setGoal("N/A");
		lesson2.setMathTarget("N/A");
		lesson1.setReviewTarget("N/A");
		lesson2.setVocabularies("Basic: help, map,here,there,airport,airplane,passport,bag, (not)feel well,doctor,police officer,"
				+ "safeIntermediate: turn left/right, go straight, suitcase,water, bathroom,boarding pass Advaned: ambulance, turn around, phone number, address");
		lesson2.setSentencePatterns("Basic: GIVING/RECEIVING DIRECTIONS- Where is the (restaurant/hotel/airport)?- The ___ is here/there.ASKING FOR HELP/SAFETY- Excuse me! I need help."
				+ "- How can I help you?- I can't find my mom/dad/hotel.- Who can help?- A doctor/police officer can help.- Is it safe?- Yes, it is. / "
				+ "- No, it isn’t.Intermediate: AT THE AIRPORT- I would like to check in.- Can I see your passport, please?- Here you go.GIVING/RECEIVING DIRECTIONS"
				+ "- How can we get to the ___?- Go straight./ - Turn left/right.ASKING FOR HELP/SAFETY- What do you need?- I need water/a doctor.- I need to go to the bathroom."
				+ "- What should we do to be safe?- We should ___ to be safe.AT THE AIRPORT- Do you have a suitcase- Yes, here you go.Advanced: GIVING/RECEIVING DIRECTIONS"
				+ "- Tell the taxi driver how to get to the hotel/airport/restaurant.- Please _____, ____, and ______.ASKING FOR HELP/SAFETY- Who/what can help you when ___ ?"
				+ "- A/an ___ can help me when ___.AT THE AIRPORT- Here is your boarding pass. Have a safe trip!- Thank you!");

        lesson2.setSerialNumber("LT1-U1-LC1-L2");
        lesson2.setSequence(2);
        lesson2.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson2);
		
		Activity activity2 = new Activity();
		activity2.setName("Level 2 Little Translator Activity");
		activity2.setLesson(lesson2);
		activityAccessor.create(activity2);
	
		Resource resource2 = new Resource();
		resource2.setName("Level 2 Little Translator PPT");
		resource2.setType(Type.PPT);
		resource2.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson2.getSerialNumber()));
		List<Activity> activities2 = new ArrayList<Activity>();
	    activities2.add(activity2);
	    resource2.setActivities(activities2);
		resourceAccessor.create(resource2);
		
		PPT ppt2 = new PPT();
		ppt2.setResource(resource2);
        pptAccessor.create(ppt2);
        
		// 创建测评课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("Little Translator Product");
		product.setDescription("Little Translator Product");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);			
	}
	
	
	private void createTrial() {
		// 创建试听课程
		Course course = new Course();
		course.setSerialNumber("T1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("Trial");
		course.setDescription("Trial Course");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.DEMO);
		courseAccessor.create(course);
		
		Unit unit = new Unit();
		unit.setName("Trial Unit");
		unit.setSerialNumber("T1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("Trial Unit");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Trial Learning Cycle");
		learningCycle.setSerialNumber("T1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);
		
		Lesson lesson1 = new Lesson();
		lesson1.setName("Foundation Trial Lesson");
		lesson1.setSerialNumber("T1-U1-LC1-L1");
		lesson1.setSequence(1);
		lesson1.setLearningCycle(learningCycle);
		lesson1.setDbyDocument("53df1ba5-15e5-4f29-b04d-90572362ad75");
		lessonAccessor.create(lesson1);
		
		Activity activity1 = new Activity();
		activity1.setName("Foundation Trial Activity");
		activity1.setLesson(lesson1);
		activityAccessor.create(activity1);
	
		Resource resource1 = new Resource();
		resource1.setName("Foundation Trial PPT");
		resource1.setType(Type.PPT);
		resource1.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson1.getSerialNumber()));
		List<Activity> activities1 = new ArrayList<Activity>();
	    activities1.add(activity1);
	    resource1.setActivities(activities1);
		resourceAccessor.create(resource1);
		
		PPT ppt1 = new PPT();
		ppt1.setResource(resource1);
        pptAccessor.create(ppt1);
        
        Lesson lesson2 = new Lesson();
        lesson2.setName("Level 1 Trial Lesson");
        lesson2.setSerialNumber("T1-U1-LC1-L2");
        lesson2.setSequence(2);
        lesson2.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson2);
		
		Activity activity2 = new Activity();
		activity2.setName("Level 1 Trial Activity");
		activity2.setLesson(lesson2);
		activityAccessor.create(activity2);
	
		Resource resource2 = new Resource();
		resource2.setName("Level 1 Trial PPT");
		resource2.setType(Type.PPT);
		resource2.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson2.getSerialNumber()));
		List<Activity> activities2 = new ArrayList<Activity>();
	    activities2.add(activity2);
	    resource2.setActivities(activities2);
		resourceAccessor.create(resource2);
		
		PPT ppt2 = new PPT();
		ppt2.setResource(resource2);
        pptAccessor.create(ppt2);
        
        Lesson lesson3 = new Lesson();
        lesson3.setName("Travel Trial Lesson");
        lesson3.setSerialNumber("T1-U1-LC1-L3");
        lesson3.setSequence(3);
        lesson3.setLearningCycle(learningCycle);
		lessonAccessor.create(lesson3);
		
		Activity activity3 = new Activity();
		activity3.setName("Travel Trial Activity");
		activity3.setLesson(lesson3);
		activityAccessor.create(activity3);
	
		Resource resource3 = new Resource();
		resource3.setName("Travel Trial PPT");
		resource3.setType(Type.PPT);
		resource3.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson3.getSerialNumber()));
		List<Activity> activities3 = new ArrayList<Activity>();
	    activities3.add(activity3);
	    resource3.setActivities(activities3);
		resourceAccessor.create(resource3);
		
		PPT ppt3 = new PPT();
		ppt3.setResource(resource3);
        pptAccessor.create(ppt3);
        
		// 创建试听课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("Trial Product");
		product.setDescription("Trial Product");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}
	
	private void createAssessment() {
		// 创建测评课程
		Course course = new Course();
		course.setSerialNumber("A1");
		course.setMode(Mode.ONE_ON_ONE);
		course.setName("Assessment");
		course.setDescription("Assessment Course");
		course.setNeedBackupTeacher(false);
		course.setSequential(false);
		course.setFree(true);
		course.setType(Course.Type.DEMO);
		courseAccessor.create(course);
		
		Unit unit = new Unit();
		unit.setName("Assessment Unit");
		unit.setSerialNumber("A1-U1");
		unit.setLevel(Level.LEVEL_0);
		unit.setNameInLevel("Assessment Unit");
		unit.setSequence(1);
		unit.setCourse(course);
		unitAccessor.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("Assessment Learning Cycle");
		learningCycle.setSerialNumber("A1-U1-LC1");
		learningCycle.setSequence(1);
		learningCycle.setUnit(unit);
		learningCycleAccessor.create(learningCycle);
		
		Lesson lesson1 = new Lesson();
		lesson1.setName("Assessment Lesson");
		lesson1.setSerialNumber("A1-U1-LC1-L1");
		lesson1.setSequence(1);
		lesson1.setLearningCycle(learningCycle);
		lesson1.setDbyDocument("53df1ba5-15e5-4f29-b04d-90572362ad75");
		lessonAccessor.create(lesson1);
		
		Activity activity1 = new Activity();
		activity1.setName("Assessment Activity");
		activity1.setLesson(lesson1);
		activityAccessor.create(activity1);
	
		Resource resource1 = new Resource();
		resource1.setName("Assessment PPT");
		resource1.setType(Type.PPT);
		resource1.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson1.getSerialNumber()));
		List<Activity> activities1 = new ArrayList<Activity>();
	    activities1.add(activity1);
	    resource1.setActivities(activities1);
		resourceAccessor.create(resource1);
		
		PPT ppt1 = new PPT();
		ppt1.setResource(resource1);
        pptAccessor.create(ppt1);
        
		// 创建测评课商品
		Product product = new Product();
		product.setStatus(Product.Status.ON_SALE);
		product.setName("Assessment Product");
		product.setDescription("Assessment Product");
		product.setCourse(course);
		product.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		product.setUnits(units);
		productAccessor.create(product);
	}
}
