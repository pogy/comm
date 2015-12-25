package com.vipkid.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.model.Activity;
import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.model.AssessmentReport;
import com.vipkid.model.Audit;
import com.vipkid.model.Channel;
import com.vipkid.model.Country;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.DemoReport;
import com.vipkid.model.EducationalComment;
import com.vipkid.model.Family;
import com.vipkid.model.FollowUp;
import com.vipkid.model.Gender;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.Medal;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.Order;
import com.vipkid.model.OrderItem;
import com.vipkid.model.PPT;
import com.vipkid.model.Parent;
import com.vipkid.model.Partner;
import com.vipkid.model.Payroll;
import com.vipkid.model.PayrollItem;
import com.vipkid.model.Permission;
import com.vipkid.model.Pet;
import com.vipkid.model.Product;
import com.vipkid.model.Resource;
import com.vipkid.model.Role;
import com.vipkid.model.RolePermission;
import com.vipkid.model.Slide;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Student.MarketActivities;
import com.vipkid.model.Student.Source;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.Certificate;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.RecruitmentChannel;
import com.vipkid.model.Teacher.Type;
import com.vipkid.model.TeacherApplication;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.Unit;
import com.vipkid.model.User;
import com.vipkid.repository.ActivityRepository;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.repository.AssessmentReportRepository;
import com.vipkid.repository.AuditRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.DemoReportRepository;
import com.vipkid.repository.EducationalCommentRepository;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.FollowUpRepository;
import com.vipkid.repository.LearningCycleRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.MedalRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderItemRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.PPTRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.PartnerRepository;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PayrollRepository;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.ProductRepository;
import com.vipkid.repository.ResourceRepository;
import com.vipkid.repository.RolePermissionRepository;
import com.vipkid.repository.SlideRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherApplicationRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.UnitRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.security.TokenGenerator;
import com.vipkid.util.Configurations;
import com.vipkid.util.DateTimeUtils;
import com.vipkid.util.TextUtils;

@Service
public class InitializeService {
	private Logger logger = LoggerFactory.getLogger(InitializeService.class.getSimpleName());
	
	@javax.annotation.Resource
	private StaffRepository staffRepository;
	
	@javax.annotation.Resource
	private TeacherRepository teacherRepository;
	
	@javax.annotation.Resource
	private CourseRepository courseRepository;
	
	@javax.annotation.Resource
	private UnitRepository unitRepository;
	
	@javax.annotation.Resource
	private AirCraftRepository aircraftRepository;
	
	@javax.annotation.Resource
	private AirCraftThemeRepository aircraftthemeRepository;
	
	@javax.annotation.Resource
	private LearningCycleRepository learningCycleRepository;
	
	@javax.annotation.Resource
	private LearningProgressRepository learningProgressRepository;
	
	@javax.annotation.Resource
	private LessonRepository lessonRepository;
	
	@javax.annotation.Resource
	private ActivityRepository activityRepository;
	
	@javax.annotation.Resource
	private ResourceRepository resourceRepository;
	
	@javax.annotation.Resource
	private PPTRepository pptRepository;
	
	@javax.annotation.Resource
	private SlideRepository slideRepository;
	
	@javax.annotation.Resource
	private FamilyRepository familyRepository;
	
	@javax.annotation.Resource
	private StudentRepository studentRepository;
	
	@javax.annotation.Resource
	private TeacherApplicationRepository applicationRepository;
	
	@javax.annotation.Resource
	private OrderRepository orderRepository;
	
	@javax.annotation.Resource
	private OnlineClassRepository onlineClassRepository;
	
	@javax.annotation.Resource
	private ParentRepository parentRepository;
	
	@javax.annotation.Resource
	private PetRepository petRepository;
	
	@javax.annotation.Resource
	private TeacherCommentRepository teacherCommentRepository;
	
	@javax.annotation.Resource
	private EducationalCommentRepository academicAdvisorCommentRepository;
	
	@javax.annotation.Resource
	private ProductRepository productRepository;
	
	@javax.annotation.Resource
	private OrderItemRepository orderItemRepository;
	
	@javax.annotation.Resource
	private FollowUpRepository followUpRepository;
	
	@javax.annotation.Resource
	private PayrollItemRepository payrollItemRepository;
	
	@javax.annotation.Resource
	private PayrollRepository payrollRepository;
	
	@javax.annotation.Resource
	private DemoReportRepository demoReportRepository;
	
	@javax.annotation.Resource
	private AssessmentReportRepository assessmentReportRepository;
	
	@javax.annotation.Resource
	private PartnerRepository partnerRepository;
	
	@javax.annotation.Resource
	private RolePermissionRepository rolePermissionRepository;
	
	@javax.annotation.Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;
	
	@javax.annotation.Resource
	private ParentService parentService;
	
	@javax.annotation.Resource
	private AirCraftThemeRepository aircraftThemeRepository;
	
	@javax.annotation.Resource
	private ChannelService channelService;
	
	@javax.annotation.Resource
	private OrderService orderService;
	
	@javax.annotation.Resource
	private BillNoService billNoService;
	
	
	private Lesson startLesson = null;
    private Lesson endLesson = null;
    private Lesson lastFinishLesson  = null;
    private Lesson lastScheduledLesson  = null;
    
    private Course itTestCourse = null;
    private Course recruitmentCourse = null;
    private Course demoCourse = null;
    private Course mainCourse = null;
    private Course prcticumCourse = null;
    
    
    private Product itTestProduct = null;
    private Product demoProduct = null;
    private Product practicumpProduct = null;
    
    private Teacher teacher4Recruitment = null;
    private OnlineClass onlineClass4Recruitment = null;
    private Lesson lesson4Recruitment = null;
    
    private Teacher teacher4Practicum = null;
    private OnlineClass onlineClass4Practicum = null;
    private Lesson lesson4Practicum = null;
    

    
    private LearningProgress learningProgress1;
    private LearningProgress learningProgress2;
    private int counter;
	
	public enum Day{
		YESTERDAY,
		TODAY,
		TOMORROW,
		FARFUTURE,
	}

	@javax.annotation.Resource
	private AuditRepository auditRepository;
	
	@javax.annotation.Resource
	private MedalRepository medalRepository;
	
	@javax.annotation.Resource
	private ExcelToXMLReaderService excelToXMLReaderService;
	
	public Response doInit() throws JDOMException, IOException {
		initDB();
		excelToXMLReaderService.doInitForCourse();
		//excelToXMLReaderService.initForSlideForTest();
		return new Response(HttpStatus.OK.value());
	}
	
	public Response initDate(int num){
		String mobilePre = "135000";
		for(int i = 0;i<num;i++){
			int mobileback = (int) Math.floor(Math.random()*10000 + 10000);
			Parent parent = new Parent();
            parent.setMobile(mobilePre+String.valueOf(mobileback));
            parent.setPassword("vipkid");
            parent.setName("testparent"+parent.getMobile());
            
            // 新建家庭
            Family family = new Family();
            familyRepository.create(family);
            
            Student student = new Student();
            student.setEnglishName("teststudent"+parent.getMobile());
            student.setName("测试数据"+parent.getMobile());
            
           try {
        	    Parent findParent = parentService.findByUsername(parent.getMobile());
	           	if(findParent == null) {
	           		doCreateSignup(parent, student, family, "http://www.vipkid.com.cn");
	           	}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		
	    return new Response(HttpStatus.OK.value());
	}
	
	private Parent doCreateSignup(Parent parent, Student student, Family family, String url) {
        logger.info("do Create SignUp: url: {}", url);
        MarketActivities currentActivity = null;
        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);

        if (student != null) {
            // 新建学生
            if (student.getAttendedActivities() != null) {
                //Find current activity.
                for (MarketActivities activity : MarketActivities.values()) {
                    //Check the activity(from web) is valid.
                    if (student.getAttendedActivities().indexOf(activity.toString()) > 0) {
                        currentActivity = activity;
                    }
                }
            }

            Source registerSource = null;
            if (student.getSource() != null) {
                //Find current register source.

                for (Source source : Source.values()) {
                    //Check the activity(from web) is valid.
                    if (source.equals(student.getSource())) {
                        registerSource = source;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(url)) {
                Channel channel = channelService.findChannelByURL(url);
                if (channel != null) {
                    logger.info("Channel source name: {}", channel.getSourceName());
                    student.setChannel(channel);
                }
                if (url.indexOf(channelService.CHANNEL_KEYWORD) > 0) {
                    String kw = StringUtils.substring(url, StringUtils.indexOf(url, channelService.CHANNEL_KEYWORD) + channelService.CHANNEL_KEYWORD.length());
                    if (!StringUtils.isEmpty(kw)) {
                        student.setChannelKeyword(kw);
                    }
                }
            }

            String studentPassword = PasswordGenerator.generate();
            student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
            student.setPassword(studentPassword);
            //		student.setSource(Source.WEBSITE);
            student.setWelcome(true);
            student.setFamily(family);
            student.setCreater(parent);
            student.setLifeCycle(Student.LifeCycle.SIGNUP);
            student.setAvatar("boy_3");
            if (registerSource != null) {
                student.setSource(registerSource);
            } else {
                student.setSource(Source.WEBSITE);
            }
            if (currentActivity != null) {
                student.addAttendedActivity(currentActivity);
            }
            student.setStatus(Student.Status.TEST);
            student.setAccountType(Student.AccountType.TEST);

            studentRepository.create(student);
			studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
            List<Student> students = new ArrayList<Student>();
            students.add(student);
            family.setStudents(students);

            //					// 更新家庭
            //					family.setCreater(parent);
            //					familyAccessor.update(family);

            // 新建ITTest课学习进度
            Course itTestCourse = courseRepository.findByCourseType(Course.Type.IT_TEST);
            LearningProgress itTestLearningProgress = new LearningProgress();
            itTestLearningProgress.setStudent(student);
            itTestLearningProgress.setStatus(LearningProgress.Status.STARTED);
            itTestLearningProgress.setCourse(itTestCourse);
            itTestLearningProgress.setLeftClassHour(1);
            itTestLearningProgress.setTotalClassHour(1);
            learningProgressRepository.create(itTestLearningProgress);

            // 新建试听课学习进度
            Course demoCourse = courseRepository.findByCourseType(Course.Type.TRIAL);
            LearningProgress demoLearningProgress = new LearningProgress();
            demoLearningProgress.setStudent(student);
            demoLearningProgress.setStatus(LearningProgress.Status.STARTED);
            demoLearningProgress.setCourse(demoCourse);
            demoLearningProgress.setLeftClassHour(1);
            demoLearningProgress.setTotalClassHour(1);
            learningProgressRepository.create(demoLearningProgress);
            
            List<Unit>units = unitRepository.findByCourseType(Course.Type.MAJOR);
            OrderItem orderItem = new OrderItem();
            Product product = new Product();
            List<Product> products = productRepository.findByCourseType(Course.Type.MAJOR);
            product = products.get(0);
            orderItem.setUnits(units);
            orderItem.setClassHour(500);
            orderItem.setClassHourPrice(0);
            orderItem.setComment("test");
            orderItem.setDealPrice(0);
            orderItem.setPrice(0);
            orderItem.setProduct(product);
            orderItem.setStartUnit(units.get(0));
            
            User user = new User();
            user.setId(2);
            
            Order order = new Order();
            order.setComment("test");
            order.setSerialNumber(System.currentTimeMillis()+"-001");
            order.setCreater(user);
            order.setFamily(family);
            order.setStudent(student);
            order.setTotalDealPrice(0);
            order.setCreateDateTime(new Date());
			order.setStatus(Order.Status.TO_PAY);
			order.setCreateDateTime(new Date());
			order.setSerialNumber(billNoService.doGetNextOrderNo());
			
			order = orderRepository.create(order);
			orderItem.setOrder(order);
			orderItem=orderItemRepository.create(orderItem);
			
			List<OrderItem> orderItems = new ArrayList<OrderItem>();
            orderItems.add(orderItem);
            order.setOrderItems(orderItems);
			order = orderRepository.update(order);
            
            order.setConfirmer(user);
            orderService.doConfirm(order);
            

            //给学生一个默认的飞机
            AirCraft airCraft = new AirCraft();
            airCraft.setStudent(student);
            airCraft.setSequence(1);
            aircraftRepository.create(airCraft);

            AirCraftTheme airCraftTheme = new AirCraftTheme();
            airCraftTheme.setAirCraft(airCraft);
            airCraftTheme.setCurrent(true);
            airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
            airCraftTheme.setLevel(1);
            airCraftTheme.setName("阿波罗号");
            airCraftTheme.setPrice(0);
            airCraftTheme.setUrl("ac1_1");
            aircraftThemeRepository.create(airCraftTheme);

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
        }

       /* // 发送新家长注册短信
        if (student != null) {
            SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
        }

        // 发送新家长注册统计邮件
        EMail.sendNewParentSignupEmail(parent.getMobile());*/

        return parent;
    }
	
	public Response doInitForParentPortal() {
		// 创建超级管理员用户
		List<Student>students =  new ArrayList<Student>();
		Staff staff = new Staff();
		staff.setUsername("huozhenzhong@vipkid.com.cn");
		staff.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff.addRole(Role.STAFF_CXO);
		staff.addRole(Role.STAFF_ADMIN);
		staff.setEmail("huozhenzhong@vipkid.com.cn");
		staff.setMobile("18501361687");
		staff.setName("霍振中");
		staff.setEnglishName("Forest Huo");
		staffRepository.create(staff);

		Staff staff1 = new Staff();
		staff1.setUsername("zhaohongliang@vipkid.com.cn");
		staff1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff1.addRole(Role.STAFF_ADMIN);
		staff1.setEmail("zhaohongliang@vipkid.com.cn");
		staff1.setMobile("18501361688");
		staff1.setName("赵红亮");
		staff1.setEnglishName("Lion Zhao");
		staffRepository.create(staff1);

		Staff staff2 = new Staff();
		staff2.setUsername("wangqing@vipkid.com.cn");
		staff2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff2.addRole(Role.STAFF_ADMIN);
		staff2.addRole(Role.STAFF_OPERATION);
		staff2.setEmail("wangqing@vipkid.com.cn");
		staff2.setMobile("18501361689");
		staff2.setName("王青");
		staff2.setEnglishName("Alex Wang");
		staffRepository.create(staff2);

		Staff staff3 = new Staff();
		staff3.setUsername("gaoyuan1@vipkid.com.cn");
		staff3.setPassword(PasswordEncryptor.encrypt("123456"));
		staff3.addRole(Role.STAFF_ADMIN);
		staff3.addRole(Role.STAFF_OPERATION);
		staff3.setEmail("gaoyuan@vipkid.com.cn");
		staff3.setMobile("18511863246");
		staff3.setName("高源");
		staff3.setEnglishName("Adam Gao");
		staffRepository.create(staff3);

		Family family1 = new Family();
		family1.setProvince("河北省");
		family1.setCity("邯郸市");

		family1.setCreater(staff1);
		family1.setLastEditor(staff1);
		familyRepository.create(family1);
		
		Parent parent1 = new Parent();
		parent1.setUsername("Test Parent1");
		parent1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_PARENT_PASSWORD));
		parent1.setName("家长1");
		parent1.setEmail("parent1@vipkid.com.cn");
		parent1.setMobile("13800138000");
		parent1.setStatus(User.Status.NORMAL);
		parent1.setWechatOpenId("woshidatiancai");
		parent1.setRelation(Parent.Relation.FATHER);
		parent1.setGender(Gender.MALE);
		parent1.setFamily(family1);
		parentRepository.create(parent1);

		Parent parent2 = new Parent();
		parent2.setUsername("Test Parent2");
		parent2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_PARENT_PASSWORD));
		parent2.setName("家长2");
		parent2.setEmail("parent1@vipkid.com.cn");
		parent2.setMobile("13800138000");
		parent2.setStatus(User.Status.NORMAL);
		parent2.setWechatOpenId("woshidatiancai");
		parent2.setRelation(Parent.Relation.MOTHER);
		parent2.setGender(Gender.FEMALE);
		parent2.setFamily(family1);
		parentRepository.create(parent2);
		
		Student student = new Student();
		student.setUsername("201500001");
		student.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student.setName("徐磊");
		student.setEnglishName("xulei");
		student.setGender(Gender.MALE);
		student.setStars(500);
		student.setFamily(family1);
		student.setLifeCycle(Student.LifeCycle.LEARNING);
		student.setCreater(staff);
		student.setQq("11111111");
		student.setLastEditor(staff);
		student.setAvatar("boy_1");
		studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
		
		//create course
		logger.info("Create Course");
		Course course1 = new Course();
		course1.setSerialNumber("C10");
		course1.setName("PRE-K 教学方案");
		course1.setMode(Mode.ONE_ON_ONE);
		course1.setType(Course.Type.NORMAL);
		course1.setSequential(true);
		course1.setNeedBackupTeacher(true);
		course1.setBaseClassSalary(10);
		courseRepository.create(course1);

		Course course2 = new Course();
		course2.setSerialNumber("C2");
		course2.setName("环球大冒险");
		course2.setMode(Mode.ONE_TO_MANY);
		course2.setType(Course.Type.MAJOR);
		course2.setSequential(false);
		course2.setNeedBackupTeacher(false);
		course2.setBaseClassSalary(11);
		courseRepository.create(course2);

		Course course3 = new Course();
		course3.setSerialNumber("C30");
		course3.setName("PRE-K 教学方案3");
		course3.setMode(Mode.ONE_ON_ONE);
		course3.setType(Course.Type.NORMAL);
		course3.setSequential(true);
		course3.setNeedBackupTeacher(false);
		course3.setBaseClassSalary(12);
		courseRepository.create(course3);
		
		logger.info("Create Unit");
		Unit unit1 = new Unit();
		unit1.setSerialNumber("C10-U1");
		unit1.setSequence(1);
		unit1.setName("Letter");
		unit1.setObjective("Objective, Objective, Objective, Objective, Objective, Objective, Objective, Objective.");
		unit1.setTopic("topic");
		unit1.setLevel(Level.LEVEL_0);
		unit1.setCourse(course1);
		unitRepository.create(unit1);

		logger.info("Create Unit for Course 2");
		Unit unit4C2 = new Unit();
		unit4C2.setSerialNumber("C20-U1");
		unit4C2.setSequence(1);
		unit4C2.setName("Letter");
		unit4C2.setObjective("Objective");
		unit4C2.setTopic("topic");
		unit4C2.setCourse(course2);
		unit4C2.setLevel(Level.LEVEL_0);
		unit4C2.setNameInLevel("Level 0");
		unitRepository.create(unit4C2);

		logger.info("Create LearningCycle1");
		LearningCycle learningCycle1 = new LearningCycle();
		learningCycle1.setName("Learning Cycle1");
		learningCycle1.setSerialNumber("C10-U1-LC1");
		learningCycle1.setSequence(1);
		learningCycle1.setUnit(unit1);
		learningCycle1.setTopic("topic");
		learningCycle1.setObjective("Learing Cycle Objective");
		learningCycleRepository.create(learningCycle1);

		logger.info("Create LearningCycle for C20-U1");
		LearningCycle learningCycle4C2 = new LearningCycle();
		learningCycle4C2.setName("Learning Cycle1");
		learningCycle4C2.setSerialNumber("C20-U1-LC1");
		learningCycle4C2.setSequence(1);
		learningCycle4C2.setUnit(unit4C2);
		learningCycle4C2.setTopic("topic");
		learningCycle4C2.setObjective("Learing Cycle Objective");
		learningCycleRepository.create(learningCycle4C2);

		logger.info("Create Lessons for C20-U1-LC1");
		for (int i=0; i<5; i++){
			Lesson lesson4C2 = new Lesson();
			lesson4C2.setName("I'm Special, Letter A" + (i + 1));
			lesson4C2.setSerialNumber("C20-U1-LC1-L"+ (i + 1));
			lesson4C2.setSequence(i + 1);
			lesson4C2.setLearningCycle(learningCycle4C2);
			lesson4C2.setObjective("Lesson Objective: This is a objective. Lesson Objective: This is a objective. "
					+ "Lesson Objective: This is a objective. Lesson Objective: This is a objective. ");
			lesson4C2.setTopic("Lesson topic " + (i + 1));
			lesson4C2.setDomain("Lesson domain " + (i + 1));
			lesson4C2.setGoal("Goal:" + (i+1) + " Learn letter A.");
			lessonRepository.create(lesson4C2);
		}
		
		Audit audit1 = new Audit();
		audit1.setCategory(Audit.Category.ONLINE_CLASS_BOOK);
		audit1.setLevel(Audit.Level.INFO);
		audit1.setOperation("chulaiba");	
		audit1.setExecuteDateTime(new Date());
		audit1.setOperator(staff1.getName());
		auditRepository.create(audit1);

		Audit audit2 = new Audit();
		audit2.setCategory(Audit.Category.ONLINE_CLASS_CANCEL);
		audit2.setLevel(Audit.Level.ERROR);
		audit2.setOperation("chulaiba");	
		audit2.setExecuteDateTime(new Date());
		audit2.setOperator(staff1.getName());
		auditRepository.create(audit2);

		Audit audit3 = new Audit();
		audit3.setCategory(Audit.Category.STAFF_CREATE);
		audit3.setLevel(Audit.Level.INFO);
		audit3.setOperation("chulaiba");	
		audit3.setExecuteDateTime(new Date());
		audit3.setOperator(staff2.getName());
		auditRepository.create(audit3);

		Audit audit4 = new Audit();
		audit4.setCategory(Audit.Category.STAFF_CREATE);
		audit4.setLevel(Audit.Level.INFO);
		audit4.setOperation("chulaiba");	
		audit4.setExecuteDateTime(new Date());
		audit4.setOperator(staff2.getName());
		auditRepository.create(audit4);

		Audit audit5 = new Audit();
		audit5.setCategory(Audit.Category.STAFF_UPDATE);
		audit5.setLevel(Audit.Level.INFO);
		audit5.setOperation("chulaiba");	
		audit5.setExecuteDateTime(new Date());
		audit5.setOperator(staff3.getName());
		auditRepository.create(audit5);

		Audit audit6 = new Audit();
		audit6.setCategory(Audit.Category.STUDENT_CREATE);
		audit6.setLevel(Audit.Level.WARNING);
		audit6.setOperation("chulaiba");	
		audit6.setExecuteDateTime(new Date());
		audit6.setOperator(staff3.getName());
		auditRepository.create(audit6);

		Audit audit7 = new Audit();
		audit7.setCategory(Audit.Category.STUDENT_UPDATE);
		audit7.setLevel(Audit.Level.WARNING);
		audit7.setOperation("chulaiba");	
		audit7.setExecuteDateTime(new Date());
		audit7.setOperator(staff3.getName());
		auditRepository.create(audit7);

		Audit audit8 = new Audit();
		audit8.setCategory(Audit.Category.TEACHER_CREATE);
		audit8.setLevel(Audit.Level.WARNING);
		audit8.setOperation("chulaiba");	
		audit8.setExecuteDateTime(new Date());
		audit8.setOperator(staff3.getName());
		auditRepository.create(audit8);

		Audit audit9 = new Audit();
		audit9.setCategory(Audit.Category.TEACHER_UPDATE);
		audit9.setLevel(Audit.Level.WARNING);
		audit9.setOperation("chulaiba");	
		audit9.setExecuteDateTime(new Date());
		audit9.setOperator(staff3.getName());
		auditRepository.create(audit9);

		//create medal    --- add by xulei
		Medal medal = new Medal();
		medal.setName("Unit1");
		medal.setDescription("Unit1 medal");
		medal.setUnit(unit1);
		medal.setStudent(student);
		medal.setPristine(true);
		medalRepository.create(medal);

		Teacher teacherA = new Teacher();
		teacherA.setUsername("teacherA@vipkid.com.cn");
		teacherA.setPassword(PasswordEncryptor.encrypt("123456"));
		teacherA.setEmail("teacherA@vipkid.com.cn");
		teacherA.setName("teacherA");
		teacherA.setAddress("teacherA address");
		teacherA.setBankAccountName("teacherA bank account name");
		teacherA.setBankAddress("teacherA bank address");
		teacherA.setBankCardNumber("123456");
		teacherA.setBankName("bank");
		teacherA.setBankSWIFTCode("1234");
		teacherA.setBirthday(new Date());
		teacherA.setLifeCycle(LifeCycle.REGULAR);
		teacherA.setType(Type.PART_TIME);
		teacherA.setCountry(Country.AUSTRALIA);
		teacherA.setCreater(staff1);
		teacherA.setLastEditor(staff3);
		teacherA.setSummary("This is a pretty good teacher");
		List<Course> certificatedCoursesA = new ArrayList<Course>();
		certificatedCoursesA.add(course1);
		teacherA.setCertificatedCourses(certificatedCoursesA);
		teacherA.setCertificates(Certificate.TEFL.name());
		teacherA.setGender(Gender.MALE);
		teacherA.setSerialNumber("SNA");
		teacherA.setSkype("skype");
		teacherA.setMobile("15811082306");
		teacherA.setContractStartDate(new Date());
		teacherA.setContractEndDate(new Date());
		teacherA.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacherA.setPayPalAccount("payPalAccount");

		//				List<Student> favoredByStudentsA = new ArrayList<Student>();
		//				favoredByStudentsA.add(student1);
		//				teacherA.setFavoredByStudents(favoredByStudentsA);
		teacherA.addFavoredByStudent(student);

		teacherRepository.create(teacherA);

		Teacher teacherB = new Teacher();
		teacherB.setUsername("teacherB@vipkid.com.cn");
		teacherB.setPassword(PasswordEncryptor.encrypt("123456"));
		teacherB.setEmail("teacherB@vipkid.com.cn");
		teacherB.setName("teacherB");
		teacherB.setAddress("teacherB address");
		teacherB.setBankAccountName("teacherB bank account name");
		teacherB.setBankAddress("teacherB bank address");
		teacherB.setBankCardNumber("123456");
		teacherB.setBankName("bank");
		teacherB.setBankSWIFTCode("1234");
		teacherB.setBirthday(new Date());
		teacherB.setLifeCycle(LifeCycle.REGULAR);
		teacherB.setType(Type.PART_TIME);
		teacherB.setCountry(Country.AUSTRALIA);
		teacherB.setCreater(staff1);
		teacherB.setLastEditor(staff3);
		teacherB.setSummary("This is a pretty good teacher");
		List<Course> certificatedCoursesB = new ArrayList<Course>();
		certificatedCoursesB.add(course1);
		teacherB.setCertificatedCourses(certificatedCoursesB);
		teacherB.setCertificates(Certificate.TEFL.name());
		teacherB.setGender(Gender.MALE);
		teacherB.setSerialNumber("SNB");
		teacherB.setSkype("skype");
		teacherB.setMobile("15811082316");
		teacherB.setContractStartDate(new Date());
		teacherB.setContractEndDate(new Date());
		teacherB.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacherB.setPayPalAccount("payPalAccount");

		//				List<Student> favoredByStudentsB = new ArrayList<Student>();
		//				favoredByStudentsB.add(student1);
		//				teacherB.setFavoredByStudents(favoredByStudentsB);
		teacherB.addFavoredByStudent(student);

		teacherRepository.create(teacherB);

		Teacher teacherC = new Teacher();
		teacherC.setUsername("teacherC@vipkid.com.cn");
		teacherC.setPassword(PasswordEncryptor.encrypt("123456"));
		teacherC.setEmail("teacherC@vipkid.com.cn");
		teacherC.setName("teacherC");
		teacherC.setAddress("teacherC address");
		teacherC.setBankAccountName("teacherC bank account name");
		teacherC.setBankAddress("teacherC bank address");
		teacherC.setBankCardNumber("123456");
		teacherC.setBankName("bank");
		teacherC.setBankSWIFTCode("1234");
		teacherC.setBirthday(new Date());
		teacherC.setLifeCycle(LifeCycle.REGULAR);
		teacherC.setType(Type.PART_TIME);
		teacherC.setCountry(Country.AUSTRALIA);
		teacherC.setCreater(staff1);
		teacherC.setLastEditor(staff3);
		teacherC.setSummary("This is a pretty good teacher");
		List<Course> certificatedCoursesC = new ArrayList<Course>();
		certificatedCoursesC.add(course1);
		teacherC.setCertificatedCourses(certificatedCoursesC);
		teacherC.setCertificates(Certificate.TEFL.name());
		teacherC.setGender(Gender.MALE);
		teacherC.setSerialNumber("SNC");
		teacherC.setSkype("skype");
		teacherC.setMobile("15811082326");
		teacherC.setContractStartDate(new Date());
		teacherC.setContractEndDate(new Date());
		teacherC.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacherC.setPayPalAccount("payPalAccount");

		teacherRepository.create(teacherC);

		Teacher teacherD = new Teacher();
		teacherD.setUsername("teacherD@vipkid.com.cn");
		teacherD.setPassword(PasswordEncryptor.encrypt("123456"));
		teacherD.setEmail("teacherD@vipkid.com.cn");
		teacherD.setName("teacherD");
		teacherD.setAddress("teacherD address");
		teacherD.setBankAccountName("teacherD bank account name");
		teacherD.setBankAddress("teacherD bank address");
		teacherD.setBankCardNumber("123456");
		teacherD.setBankName("bank");
		teacherD.setBankSWIFTCode("1234");
		teacherD.setBirthday(new Date());
		teacherD.setLifeCycle(LifeCycle.REGULAR);
		teacherD.setType(Type.PART_TIME);
		teacherD.setCountry(Country.AUSTRALIA);
		teacherD.setCreater(staff1);
		teacherD.setLastEditor(staff3);
		teacherD.setSummary("This is a pretty good teacher");
		List<Course> certificatedCoursesD = new ArrayList<Course>();
		certificatedCoursesD.add(course1);
		teacherD.setCertificatedCourses(certificatedCoursesD);
		teacherD.setCertificates(Certificate.TEFL.name());
		teacherD.setGender(Gender.MALE);
		teacherD.setSerialNumber("SND");
		teacherD.setSkype("skype");
		teacherD.setMobile("15811082336");
		teacherD.setContractStartDate(new Date());
		teacherD.setContractEndDate(new Date());
		teacherD.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacherD.setPayPalAccount("payPalAccount");
		teacherRepository.create(teacherD);
		
		
		
		
		//create product
     	Product product1 = new Product();
     	product1.setName("商品1");
     	product1.setStatus(Product.Status.ON_SALE);
     	product1.setDescription("商品描述");
     	product1.setCourse(course1);
     	product1.setClassHourPrice(300);
     	List<Unit> units = new ArrayList<Unit>();
     	units.add(unit1);
     	units.add(unit4C2);
     	product1.setUnits(units);
     	productRepository.create(product1);
     	
      	Product product2 = new Product();
      	product2.setName("商品2");
      	product2.setStatus(Product.Status.ON_SALE);
      	product2.setDescription("商品描述2");
      	product2.setCourse(course2);
      	product2.setClassHourPrice(300);
     	List<Unit> units4C2 = new ArrayList<Unit>();
     	units4C2.add(unit4C2);
     	product2.setUnits(units4C2);
     	productRepository.create(product2);
		
     	
		learningProgress1 = new LearningProgress();
        learningProgress2 = new LearningProgress();
		// Create learning progress
		learningProgress1.setCourse(course1);
		learningProgress1.setStatus(LearningProgress.Status.STARTED);
		learningProgress1.setNextShouldTakeLesson(startLesson);
		learningProgress1.setStudent(student);
		learningProgressRepository.create(learningProgress1);

		learningProgress2.setCourse(course1);
		learningProgress2.setStatus(LearningProgress.Status.STARTED);
		learningProgress2.setNextShouldTakeLesson(startLesson);
		learningProgress2.setStudent(student);
		learningProgressRepository.create(learningProgress2);		
		
		initOnlineClass(Day.FARFUTURE, teacherA, students, Status.AVAILABLE, FinishType.AS_SCHEDULED, learningCycle1, 5, 0,product1);
		initOnlineClass(Day.FARFUTURE, teacherB, students, Status.CANCELED, FinishType.AS_SCHEDULED, learningCycle1, 5, 0,product1);
		initOnlineClass(Day.FARFUTURE, teacherC, students, Status.AVAILABLE, FinishType.AS_SCHEDULED, learningCycle1, 5, 0,product1);
		initOnlineClass(Day.FARFUTURE, teacherC, students, Status.CANCELED, FinishType.AS_SCHEDULED, learningCycle1, 1, 0,product2);
		initOnlineClass(Day.FARFUTURE, teacherD, students, Status.CANCELED, FinishType.AS_SCHEDULED, learningCycle1, 5, 0,product2);
	
		//student1 like teacherA and teacherB, teacherA and teacherC have available
        return new Response(HttpStatus.OK.value());
	}
	
	private void initDB() {	
		List<Student>students = new ArrayList<Student>();
		
		createITTest();
		Lesson demoLesson = createDemo();

		createRecruitmentCourse();
		createPracticumCourse();
		
		RolePermission rolePermission = new RolePermission();
		rolePermission.setRole(Role.STAFF_ADMIN.toString());
		StringBuilder permissionsStringBuilder = new StringBuilder();
		for(Permission permission : Permission.values()) {
			permissionsStringBuilder.append(permission.toString()).append(TextUtils.SPACE);
		}
		if(permissionsStringBuilder != null) {
			rolePermission.setPermissions(permissionsStringBuilder.toString().trim());
		}	
		rolePermissionRepository.create(rolePermission);
		
		RolePermission rolePermission2 = new RolePermission();
		rolePermission2.setRole(Role.STAFF_OPERATION.toString());
		rolePermission2.setPermissions("ONLINE_CLASS_BOOK");
		rolePermissionRepository.create(rolePermission2);
		
		RolePermission rolePermission3 = new RolePermission();
		rolePermission3.setRole(Role.PARTNER.toString());
		rolePermission3.setPermissions("ONLINE_CLASS_CLASSROOM_CREATE");
		rolePermissionRepository.create(rolePermission3);
		
		Partner partner1 = new Partner();
		partner1.setUsername("partner1@vipkid.com.cn");
		partner1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		partner1.setEmail("partner1@vipkid.com.cn");
		partner1.setName("霍振中");
		partner1.setGender(Gender.FEMALE);
		partner1.setType(Partner.Type.TEACHER_RECRUITMENT);
		partnerRepository.create(partner1);
		
		Partner partner2 = new Partner();
		partner2.setUsername("partner2@vipkid.com.cn");
		partner2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		partner2.setEmail("partner2@vipkid.com.cn");
		partner2.setName("霍二中");
		partner2.setGender(Gender.FEMALE);
		partner2.setType(Partner.Type.TEACHER_RECRUITMENT);
		partnerRepository.create(partner2);
		
		// 创建超级管理员用户
		Staff staff = new Staff();
		staff.setUsername("huozhenzhong@vipkid.com.cn");
		staff.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff.addRole(Role.STAFF_CXO);
		staff.addRole(Role.STAFF_ADMIN);
		staff.setEmail("huozhenzhong@vipkid.com.cn");
		staff.setMobile("18501361687");
		staff.setName("霍振中");
		staff.setEnglishName("Forest Huo");
		staffRepository.create(staff);
		
		Staff system = new Staff();
		system.setUsername("system@vipkid.com.cn");
		system.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		system.addRole(Role.STAFF_ADMIN);
		system.setEmail("system@vipkid.com.cn");
		system.setMobile("18501361687");
		system.setName("system");
		system.setEnglishName("system");
		system.setStatus(User.Status.LOCKED);
		staffRepository.create(system);
		
		Staff staff1 = new Staff();
		staff1.setUsername("zhaohongliang@vipkid.com.cn");
		staff1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff1.addRole(Role.STAFF_ADMIN);
		staff1.setEmail("zhaohongliang@vipkid.com.cn");
		staff1.setMobile("18501361688");
		staff1.setName("赵红亮");
		staff1.setEnglishName("Lion Zhao");
		staffRepository.create(staff1);
		
		Staff staff2 = new Staff();
		staff2.setUsername("wangqing@vipkid.com.cn");
		staff2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		staff2.addRole(Role.STAFF_OPERATION);
		staff2.setEmail("wangqing@vipkid.com.cn");
		staff2.setMobile("18501361689");
		staff2.setName("王青");
		staff2.setEnglishName("Alex Wang");
		staffRepository.create(staff2);
	
		Staff staff3 = new Staff();
		staff3.setUsername("gaoyuan1@vipkid.com.cn");
		staff3.setPassword(PasswordEncryptor.encrypt("123456"));
		staff3.addRole(Role.STAFF_ADMIN);
		staff3.addRole(Role.STAFF_OPERATION);
		staff3.setEmail("gaoyuan1@vipkid.com.cn");
		staff3.setMobile("18511863246");
		staff3.setName("高源");
		staff3.setEnglishName("Adam Gao");
		staffRepository.create(staff3);
		
		//创建申请教师者
		createApplicants();
		
		Family family1 = new Family();
		family1.setProvince("河北省");
		family1.setCity("邯郸市");

		family1.setCreater(staff1);
		family1.setLastEditor(staff1);
		familyRepository.create(family1);
		
		//add by xulei
		Student student = new Student();
		student.setUsername("201500001");
		student.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student.setName("徐磊");
		student.setEnglishName("xulei");
		student.setGender(Gender.MALE);
		student.setFamily(family1);
		student.setStars(500);
		student.setCreater(staff1);
		student.setLastEditor(staff1);
		student.setWelcome(true);
		student.setAvatar("boy_1");
		studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
		students.add(student);
		
		AssessmentReport report1 = new AssessmentReport();
		report1.setName("hehe");
		report1.setReaded(false);
		report1.setStudent(student);
		report1.setUrl("http://vipkid.oss-cn-beijing.aliyuncs.com/IMAGE/024d6de0-d448-4da3-a9d0-daf7d8c174e6.png");
		assessmentReportRepository.create(report1);
		
		AssessmentReport report2 = new AssessmentReport();
		report2.setName("heihei");
		report2.setReaded(false);
		report2.setStudent(student);
		report2.setUrl("http://ww1.sinaimg.cn/mw600/97f224aajw1eo8sbkyoqij20i646tty6.jpg");
		assessmentReportRepository.create(report2);
		
		Order order = new Order();
		order.setSerialNumber("2015010600001");
		order.setStudent(student);
		order.setFamily(family1);
		order.setStatus(Order.Status.TO_PAY);
		order.setCreateDateTime(new Date());
		order.setCreater(staff1);
		order.setConfirmer(staff2);
		order.setTotalDealPrice(10000);
		orderRepository.create(order);
		
		
		// student username 为学号，根据注册时间生成，在init手动setUsername时候会和时间冲突，所以学号设置间隔较大以绕过冲突，此冲突只在init data后会有，正式环境不会有此问题
		Student student1 = new Student();
		student1.setUsername("201520002");
		student1.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student1.setName("小明");
		student1.setEnglishName("James");
		student1.setLifeCycle(Student.LifeCycle.LEARNING);
		student1.setGender(Gender.MALE);
		student1.setFamily(family1);
		student1.setAvatar("boy_1");
		student1.setCreater(staff1);
		student1.setLastEditor(staff1);
		student1.setWelcome(true);
		studentRepository.create(student1);
		studentLifeCycleLogService.doChangeLifeCycle(student1, null, Student.LifeCycle.SIGNUP);
		students.add(student1);
		
		Student student2 = new Student();
		student2.setUsername("201530003");
		student2.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student2.setName("小可");
		student2.setEnglishName("Romantic");
		student2.setLifeCycle(Student.LifeCycle.LEARNING);
		student2.setGender(Gender.FEMALE);
		student2.setFamily(family1);
		student2.setCreater(staff1);
		student2.setAvatar("girl_1");
		student2.setLastEditor(staff1);
		student2.setWelcome(true);
		studentRepository.create(student2);
		studentLifeCycleLogService.doChangeLifeCycle(student2, null, Student.LifeCycle.SIGNUP);
		students.add(student2);
		
		Student student3 = new Student();
		student3.setUsername("201540004");
		student3.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student3.setName("小兵");
		student3.setEnglishName("Test");
		student3.setLifeCycle(Student.LifeCycle.LEARNING);
		student3.setGender(Gender.FEMALE);
		student3.setFamily(family1);
		student3.setCreater(staff1);
		student3.setAvatar("girl_1");
		student3.setLastEditor(staff1);
		student3.setWelcome(true);
		studentRepository.create(student3);
		studentLifeCycleLogService.doChangeLifeCycle(student3, null, Student.LifeCycle.SIGNUP);
		students.add(student3);
		
		
		Student student4 = new Student();
		student4.setUsername("201550005");
		student4.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student4.setName("小斗");
		student4.setEnglishName("Ko");
		student4.setLifeCycle(Student.LifeCycle.LEARNING);
		student4.setGender(Gender.FEMALE);
		student4.setFamily(family1);
		student4.setCreater(staff1);
		student4.setAvatar("girl_1");
		student4.setLastEditor(staff1);
		student4.setWelcome(true);
		studentRepository.create(student4);
		studentLifeCycleLogService.doChangeLifeCycle(student4, null, Student.LifeCycle.SIGNUP);
		students.add(student4);
		
		Student student5 = new Student();
		student5.setUsername("201560006");
		student5.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student5.setName("小逗比");
		student5.setEnglishName("alan");
		student5.setLifeCycle(Student.LifeCycle.LEARNING);
		student5.setGender(Gender.FEMALE);
		student5.setFamily(family1);
		student5.setCreater(staff1);
		student5.setAvatar("girl_1");
		student5.setLastEditor(staff1);
		student5.setWelcome(true);
		student.setLifeCycle(Student.LifeCycle.SIGNUP);
		studentLifeCycleLogService.doChangeLifeCycle(student5, null, Student.LifeCycle.SIGNUP);
		students.add(student5);
		
		Student student6 = new Student();
		student6.setUsername("201570007");
		student6.setPassword(Configurations.Auth.DEFAULT_STUDENT_PASSWORD);
		student6.setName("大豆比");
		student6.setEnglishName("Alex");
		student6.setLifeCycle(Student.LifeCycle.LEARNING);
		student6.setGender(Gender.FEMALE);
		student6.setFamily(family1);
		student6.setCreater(staff1);
		student6.setAvatar("girl_1");
		student6.setLastEditor(staff1);
		student6.setWelcome(true);
		studentLifeCycleLogService.doChangeLifeCycle(student6, null, Student.LifeCycle.SIGNUP);
		studentRepository.create(student6);
		
		//add by Siyue
		AirCraft aircraft1 = new AirCraft();
		aircraft1.setStudent(student);
		aircraft1.setSequence(7);
		aircraftRepository.create(aircraft1);
		
		AirCraft aircraft2 = new AirCraft();
		aircraft2.setStudent(student);
		aircraft2.setSequence(1);
		aircraftRepository.create(aircraft2);
		
		AirCraft aircraft3 = new AirCraft();
		aircraft3.setStudent(student);
		aircraft3.setSequence(15);
		aircraftRepository.create(aircraft3);
		
		//add by Siyue
		AirCraftTheme aircrafttheme1 = new AirCraftTheme();
		aircrafttheme1.setAirCraft(aircraft1);
		aircrafttheme1.setIntroduction("宇宙号具有坚固的钛金属外壳，可以支持在太阳系中遨游。");
		aircrafttheme1.setName("宇宙号");
		aircrafttheme1.setCurrent(true);
		aircrafttheme1.setLevel(1);
		aircrafttheme1.setPrice(15);
		aircrafttheme1.setUrl("ac7_1");
		aircraftthemeRepository.create(aircrafttheme1);
		
		//add by Siyue
		AirCraftTheme aircrafttheme2 = new AirCraftTheme();
		aircrafttheme2.setAirCraft(aircraft1);
		aircrafttheme2.setIntroduction("超级宇宙号具有更加坚固的外壳，可以在银河系中遨游。");
		aircrafttheme2.setName("超级宇宙号");
		aircrafttheme2.setCurrent(false);
		aircrafttheme2.setLevel(2);
		aircrafttheme2.setPrice(17);
		aircrafttheme2.setUrl("ac7_2");
		aircraftthemeRepository.create(aircrafttheme2);
		
		//add by Siyue
		AirCraftTheme aircrafttheme3 = new AirCraftTheme();
		aircrafttheme3.setAirCraft(aircraft2);
		aircrafttheme3.setIntroduction("地球号可以支持围绕地球表面飞行。");
		aircrafttheme3.setName("地球号");
		aircrafttheme3.setCurrent(false);
		aircrafttheme3.setLevel(1);
		aircrafttheme3.setPrice(15);
		aircrafttheme3.setUrl("ac1_1");
		aircraftthemeRepository.create(aircrafttheme3);
		
		//add by Siyue
		AirCraftTheme aircrafttheme4 = new AirCraftTheme();
		aircrafttheme4.setAirCraft(aircraft3);
		aircrafttheme4.setIntroduction("火星号可以围绕火星表面飞行。");
		aircrafttheme4.setName("火星号");
		aircrafttheme4.setCurrent(false);
		aircrafttheme4.setLevel(1);
		aircrafttheme4.setPrice(15);
		aircrafttheme4.setUrl("ac15_1");
		aircraftthemeRepository.create(aircrafttheme4);
		
		//add by Siyue
		Pet pet = new Pet();
		pet.setStudent(student);
		pet.setName("cat");
		pet.setSequence(1);
		pet.setPrice(30);
		pet.setUrl("pet1");
		pet.setCurrent(true);
		pet.setIntroduction("可爱的猫咪可以伴你遨游太空。");
		petRepository.create(pet);
		
		Parent parent1 = new Parent();
		parent1.setUsername("13800138000");
		parent1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_PARENT_PASSWORD));
		parent1.setName("家长1");
		parent1.setEmail("parent1@vipkid.com.cn");
		parent1.setMobile("13800138000");
		parent1.setStatus(User.Status.NORMAL);
		parent1.setWechatOpenId("woshidatiancai");
		parent1.setRelation(Parent.Relation.FATHER);
		parent1.setGender(Gender.MALE);
		parent1.setFamily(family1);
		parentRepository.create(parent1);
		
		Parent parent2 = new Parent();
		parent2.setUsername("Test Parent2");
		parent2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_PARENT_PASSWORD));
		parent2.setName("家长2");
		parent2.setEmail("parent1@vipkid.com.cn");
		parent2.setMobile("13800138001");
		parent2.setStatus(User.Status.NORMAL);
		parent2.setRelation(Parent.Relation.MOTHER);
		parent2.setGender(Gender.FEMALE);
		parent2.setFamily(family1);
		parentRepository.create(parent2);
		
		
		//create course
		logger.info("Create Course");
		Course course0 = new Course();
		course0.setSerialNumber("C00");
		course0.setName("新生指导课");
		course0.setMode(Mode.ONE_ON_ONE);
		course0.setType(Course.Type.GUIDE);
		course0.setNeedBackupTeacher(true);
		course0.setSequential(true);
		course0.setBaseClassSalary(10);
        courseRepository.create(course0);
        
        Unit unit0 = new Unit();
        unit0.setSerialNumber("C00-U1");
        unit0.setSequence(6001);
        unit0.setName("新生指导课");
        unit0.setObjective("Objective");
        unit0.setTopic("topic");
        unit0.setLevel(Level.LEVEL_0);
        unit0.setCourse(course0);
        unitRepository.create(unit0);
        
        LearningCycle learningCycle0 = new LearningCycle();
        learningCycle0.setName("新生指导课");
        learningCycle0.setSerialNumber("C00-U1-LC1");
        learningCycle0.setSequence(7001);
        learningCycle0.setUnit(unit0);
        learningCycle0.setTopic("topic");
        learningCycle0.setObjective("Learing Cycle Objective");
        learningCycleRepository.create(learningCycle0);
        
        Lesson lesson0 = new Lesson();
        lesson0.setName("新生指导课");
        lesson0.setSerialNumber("C00-U1-LC1-L1");
        lesson0.setSequence(8001);
        lesson0.setLearningCycle(learningCycle0);
        lesson0.setObjective("Lesson Objective");
        lesson0.setTopic("Lesson topic ");
        lesson0.setDomain("Lesson domain ");
    	lesson0.setGoal("Goal:");
	    lessonRepository.create(lesson0);
	    
	    Resource resource0 = new Resource();
	    resource0.setName("demo0");
	    resource0.setType(Resource.Type.PPT);
	    resource0.setUrl("http://resource.vipkid.com.cn/static/slides/baoshan/index.html");
        resourceRepository.create(resource0);
        
        logger.info("Create PPT 0");
        PPT ppt0 = new PPT();
        ppt0.setResource(resource0);
        pptRepository.create(ppt0);
        
		Course course1 = new Course();
		course1.setSerialNumber("C10");
		course1.setName("PRE-K 教学方案");
		course1.setMode(Mode.ONE_ON_ONE);
		course1.setType(Course.Type.NORMAL);
		course1.setNeedBackupTeacher(true);
		course1.setSequential(true);
		course1.setBaseClassSalary(10);
        courseRepository.create(course1);
        
		Course course2 = new Course();
		course2.setSerialNumber("C20");
		course2.setName("环球大冒险");
		course2.setMode(Mode.ONE_TO_MANY);
		course2.setType(Course.Type.MAJOR);
		course2.setBaseClassSalary(11);
		course2.setSequential(false);
        courseRepository.create(course2);
        
		Course course3 = new Course();
		course3.setSerialNumber("C31");
		course3.setName("PRE-K 教学方案3");
		course3.setMode(Mode.ONE_ON_ONE);
		course3.setType(Course.Type.NORMAL);
		course3.setSequential(true);
		course3.setBaseClassSalary(12);
        courseRepository.create(course3);
        
        Teacher teacher1 = new Teacher();
		teacher1.setUsername("both@vipkid.com.cn");
		teacher1.setPassword(PasswordEncryptor.encrypt("123456"));
		teacher1.setEmail("both@vipkid.com.cn");
		teacher1.setName("Both");
		teacher1.setRealName("Both");
		teacher1.setAddress("teacher address");
		teacher1.setBankAccountName("teacher bank account name");
		teacher1.setQq("33021111");
		teacher1.setBankAddress("banck address");
		teacher1.setBankCardNumber("123456");
		teacher1.setBankName("bank");
		teacher1.setBankSWIFTCode("1234");
		teacher1.setBirthday(new Date());
		teacher1.setLifeCycle(LifeCycle.REGULAR);
		teacher1.setType(Type.FULL_TIME);
		teacher1.setCountry(Country.AUSTRALIA);
		teacher1.setCreater(staff1);
		teacher1.setLastEditor(staff3);
//		teacher1.setSummary("This is a pretty good teacher");
		List<Course> certificatedCourses = new ArrayList<Course>();
		certificatedCourses.add(itTestCourse);
		certificatedCourses.add(demoCourse);
		certificatedCourses.add(recruitmentCourse);
		certificatedCourses.add(prcticumCourse);
		
		certificatedCourses.add(course1);
		certificatedCourses.add(course2);
		teacher1.setCertificatedCourses(certificatedCourses);
		teacher1.setCertificates(Certificate.TEFL.name());
		teacher1.setGender(Gender.MALE);
		teacher1.setSerialNumber("SN");
		teacher1.setSkype("skype");
		teacher1.setMobile("15811082326");
		teacher1.setContractStartDate(new Date());
		teacher1.setContractEndDate(new Date());
		teacher1.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacher1.setPayPalAccount("payPalAccount");
		teacher1.setExtraClassSalary(2);
		teacherRepository.create(teacher1);
		
		teacher4Recruitment = teacher1;
		teacher4Practicum = teacher1;
		
		Teacher teacher2 = new Teacher();
		teacher2.setUsername("teacher2@vipkid.com.cn");
		teacher2.setPassword(PasswordEncryptor.encrypt("123456"));
		teacher2.setEmail("teacher2@vipkid.com.cn");
		teacher2.setName("Teacher2");
		teacher2.setRealName("Teacher2");
		teacher2.setAddress("teacher address");
		teacher2.setBankAccountName("teacher bank account name");
		teacher2.setBankAddress("banck address");
		teacher2.setBankCardNumber("123456");
		teacher2.setBankName("bank");
		teacher2.setBankSWIFTCode("1234");
		teacher2.setBirthday(new Date());
		teacher2.setLifeCycle(LifeCycle.REGULAR);
		teacher2.setType(Type.PART_TIME);
		teacher2.setCountry(Country.AUSTRALIA);
//		teacher2.setSummary("This teacher is a dog");
		List<Course> certificatedCoursesForTeacher2 = new ArrayList<Course>();
		certificatedCoursesForTeacher2.add(course1);
		certificatedCoursesForTeacher2.add(course2);
		teacher2.setCertificatedCourses(certificatedCoursesForTeacher2);
		teacher2.setCertificates("ETS");
		teacher2.setGender(Gender.MALE);
		teacher2.setSerialNumber("SN2");
		teacher2.setSkype("skype");
		teacher2.setMobile("111");
		teacher2.setRecruitmentChannel(RecruitmentChannel.CHEGG);
		teacher2.setPayPalAccount("payPalAccount");
		teacher2.setCreater(staff1);
		teacher2.setLastEditor(staff3);
		teacher2.setExtraClassSalary(3);
		teacherRepository.create(teacher2);
		
		createPartners();

        logger.info("Create Unit1");
        Unit unit1 = new Unit();
        unit1.setSerialNumber("C10-U1");
        unit1.setSequence(1);
        unit1.setName("Letter");
        unit1.setObjective("Objective");
        unit1.setTopic("topic");
        unit1.setLevel(Level.LEVEL_0);
        unit1.setCourse(course1);
        unitRepository.create(unit1);
        
        logger.info("Create Unit2");
        Unit unit2 = new Unit();
        unit2.setSerialNumber("C10-U2");
        unit2.setSequence(2);
        unit2.setName("Letter");
        unit2.setObjective("Objective");
        unit2.setTopic("topic");
        unit2.setLevel(Level.LEVEL_0);
        unit2.setCourse(course1);
        unitRepository.create(unit2);
        
        logger.info("Create Unit for Course 2");
        Unit unit4C2 = new Unit();
        unit4C2.setSerialNumber("C20-U1");
        unit4C2.setSequence(3);
        unit4C2.setName("Letter");
        unit4C2.setObjective("Objective");
        unit4C2.setTopic("topic");
        unit4C2.setCourse(course2);
        unit4C2.setLevel(Level.LEVEL_0);
        unit4C2.setNameInLevel("Level 0");
        unitRepository.create(unit4C2);
        
        logger.info("Create LearningCycle1");
        LearningCycle learningCycle1 = new LearningCycle();
        learningCycle1.setName("Learning Cycle1");
        learningCycle1.setSerialNumber("C10-U1-LC1");
        learningCycle1.setSequence(1001);
        learningCycle1.setUnit(unit1);
        learningCycle1.setTopic("topic");
        learningCycle1.setObjective("Learing Cycle Objective");
        learningCycleRepository.create(learningCycle1);
        
        logger.info("Create LearningCycle2");
        LearningCycle learningCycle2 = new LearningCycle();
        learningCycle2.setName("Learning Cycle2");
        learningCycle2.setSerialNumber("C10-U1-LC2");
        learningCycle2.setSequence(1002);
        learningCycle2.setUnit(unit1);
        learningCycle2.setTopic("topic");
        learningCycle2.setObjective("Learing Cycle Objective");
        learningCycleRepository.create(learningCycle2);
        
        logger.info("Create LearningCycle3");
        LearningCycle learningCycle3 = new LearningCycle();
        learningCycle3.setName("Learning Cycle3");
        learningCycle3.setSerialNumber("C10-U2-LC1");
        learningCycle3.setSequence(1003);
        learningCycle3.setUnit(unit2);
        learningCycle3.setTopic("topic");
        learningCycle3.setObjective("Learing Cycle Objective");
        learningCycleRepository.create(learningCycle3);
        
        
        logger.info("Create LearningCycle for C2U1");
        LearningCycle learningCycle4C2 = new LearningCycle();
        learningCycle4C2.setName("Learning Cycle1");
        learningCycle4C2.setSerialNumber("C20-U1-LC1");
        learningCycle4C2.setSequence(1004);
        learningCycle4C2.setUnit(unit4C2);
        learningCycle4C2.setTopic("topic");
        learningCycle4C2.setObjective("Learing Cycle Objective");
        learningCycleRepository.create(learningCycle4C2);
        
        logger.info("Create Lessons for C2U1LC1");
        for (int i=0; i<5; i++){
        	Lesson lesson4C2 = new Lesson();
        	lesson4C2.setName("I'm Special, Letter A" + (i + 1));
        	lesson4C2.setSerialNumber("C20-U1-LC1-L"+ (i + 1));
        	lesson4C2.setSequence(i + 1);
        	lesson4C2.setLearningCycle(learningCycle4C2);
        	lesson4C2.setObjective("Lesson Objective");
        	lesson4C2.setTopic("Lesson topic " + (i + 1));
        	lesson4C2.setDomain("Lesson domain " + (i + 1));
        	lesson4C2.setGoal("Goal:" + (i+1));
 	        lessonRepository.create(lesson4C2);
 	        
 	        OnlineClass onlineClass4C2 = new OnlineClass();
 	        onlineClass4C2.setMaxStudentNumber(3);
 	        onlineClass4C2.setMinStudentNumber(1);
 	        onlineClass4C2.setStatus(Status.AVAILABLE);
 	        onlineClass4C2.setLesson(lesson4C2);
 	        onlineClass4C2.setTeacher(teacher1);
 	        onlineClass4C2.setScheduledDateTime(DateTimeUtils.getToday(10 + i));
	        onlineClass4C2.setCourse(demoCourse);
	        onlineClassRepository.create(onlineClass4C2);
	        
	        TeacherComment teacherComment1 = new TeacherComment();
	        teacherComment1.setAbilityToFollowInstructions(1);
	        teacherComment1.setActivelyInteraction(2);
	        teacherComment1.setClearPronunciation(3);
	        teacherComment1.setOnlineClass(onlineClass4C2);
	        teacherComment1.setStudent(students.get(0));
	        teacherComment1.setTeacher(teacher1);
	        teacherComment1.setStars(3);
	        teacherComment1.setReadingSkills(4);
	        teacherComment1.setRepetition(5);
	        teacherComment1.setSpellingAccuracy(1);
	        teacherComment1.setTeacherFeedback("Todays lesson was not as good as out last one. I think Lovely was very tired and was having trouble concentrating or understanding what was asked of her. She did do well when were started writing her name.");
	        teacherComment1.setTipsForOtherTeachers("Today was her 5th birthday so she is quite young so go extra slow with her when prompting for answers.");
	        teacherCommentRepository.create(teacherComment1);


			Payroll payroll = new Payroll();
            payroll.setTeacher(teacher1);
            payrollRepository.create(payroll);

			PayrollItem payrollItem = new PayrollItem();
            payrollItem.setOnlineClass(onlineClass4C2);
            payrollItem.setPayroll(payroll);
            payrollItemRepository.create(payrollItem);
			payrollItem.setSalary(120);

			onlineClass4C2.setPayrollItem(payrollItem);
			onlineClassRepository.update(onlineClass4C2);
        }
        
        createOnlineClass4Recruitment();
        createOnlineClass4Practicum();
        
        //create product
     	Product product1 = new Product();
     	product1.setName("商品1");
     	product1.setStatus(Product.Status.ON_SALE);
     	product1.setDescription("商品描述");
     	product1.setCourse(course1);
     	product1.setClassHourPrice(300);
     	List<Unit> units = new ArrayList<Unit>();
     	units.add(unit1);
     	units.add(unit2);
     	units.add(unit4C2);
     	product1.setUnits(units);
     	productRepository.create(product1);
     	
      	Product product2 = new Product();
      	product2.setName("商品2");
      	product2.setStatus(Product.Status.ON_SALE);
      	product2.setDescription("商品描述2");
      	product2.setCourse(course2);
      	product2.setClassHourPrice(300);
     	List<Unit> units4C2 = new ArrayList<Unit>();
     	units4C2.add(unit4C2);
     	product2.setUnits(units4C2);
     	productRepository.create(product2);
        
        LearningProgress lp1 = new LearningProgress();
        lp1.setLeftClassHour(10);	
        lp1.setTotalClassHour(30);
        lp1.setCourse(course2);
        lp1.setStudent(student6);
        lp1.setStatus(LearningProgress.Status.STARTED);
        learningProgressRepository.create(lp1);
        
        LearningProgress lp2 = new LearningProgress();
        lp2.setLeftClassHour(10);
        lp2.setTotalClassHour(30);
        lp2.setCourse(course2);
        lp2.setStatus(LearningProgress.Status.STARTED);
        lp2.setStudent(student5);
        learningProgressRepository.create(lp2);
        
        LearningProgress lp3 = new LearningProgress();
        lp3.setLeftClassHour(10);
        lp3.setTotalClassHour(30);
        lp3.setCourse(course2);
        lp3.setStatus(LearningProgress.Status.STARTED);
        lp3.setStudent(student4);
        learningProgressRepository.create(lp3);
        
       
        
        //init for one-on-one class
        learningProgress1 = new LearningProgress();
        learningProgress2 = new LearningProgress();

        
		Audit audit1 = new Audit();
		audit1.setCategory(Audit.Category.ONLINE_CLASS_BOOK);
		audit1.setLevel(Audit.Level.INFO);
		audit1.setOperation("chulaiba");	
		audit1.setExecuteDateTime(new Date());
		audit1.setOperator(staff1.getName());
		auditRepository.create(audit1);
		
		Audit audit2 = new Audit();
		audit2.setCategory(Audit.Category.ONLINE_CLASS_CANCEL);
		audit2.setLevel(Audit.Level.ERROR);
		audit2.setOperation("chulaiba");	
		audit2.setExecuteDateTime(new Date());
		audit2.setOperator(staff1.getName());
		auditRepository.create(audit2);
		
		Audit audit3 = new Audit();
		audit3.setCategory(Audit.Category.STAFF_CREATE);
		audit3.setLevel(Audit.Level.INFO);
		audit3.setOperation("chulaiba");	
		audit3.setExecuteDateTime(new Date());
		audit3.setOperator(staff2.getName());
		auditRepository.create(audit3);
		
		Audit audit4 = new Audit();
		audit4.setCategory(Audit.Category.STAFF_CREATE);
		audit4.setLevel(Audit.Level.INFO);
		audit4.setOperation("chulaiba");	
		audit4.setExecuteDateTime(new Date());
		audit4.setOperator(staff2.getName());
		auditRepository.create(audit4);
		
		Audit audit5 = new Audit();
		audit5.setCategory(Audit.Category.STAFF_UPDATE);
		audit5.setLevel(Audit.Level.INFO);
		audit5.setOperation("chulaiba");	
		audit5.setExecuteDateTime(new Date());
		audit5.setOperator(staff3.getName());
		auditRepository.create(audit5);
		
		Audit audit6 = new Audit();
		audit6.setCategory(Audit.Category.STUDENT_CREATE);
		audit6.setLevel(Audit.Level.WARNING);
		audit6.setOperation("chulaiba");	
		audit6.setExecuteDateTime(new Date());
		audit6.setOperator(staff3.getName());
		auditRepository.create(audit6);
		
		Audit audit7 = new Audit();
		audit7.setCategory(Audit.Category.STUDENT_UPDATE);
		audit7.setLevel(Audit.Level.WARNING);
		audit7.setOperation("chulaiba");	
		audit7.setExecuteDateTime(new Date());
		audit7.setOperator(staff3.getName());
		auditRepository.create(audit7);
		
		Audit audit8 = new Audit();
		audit8.setCategory(Audit.Category.TEACHER_CREATE);
		audit8.setLevel(Audit.Level.WARNING);
		audit8.setOperation("chulaiba");	
		audit8.setExecuteDateTime(new Date());
		audit8.setOperator(staff3.getName());
		auditRepository.create(audit8);
		
		Audit audit9 = new Audit();
		audit9.setCategory(Audit.Category.TEACHER_UPDATE);
		audit9.setLevel(Audit.Level.WARNING);
		audit9.setOperation("chulaiba");	
		audit9.setExecuteDateTime(new Date());
		audit9.setOperator(staff3.getName());
		auditRepository.create(audit9);
		
     	//create medal
     	Medal medal = new Medal();
     	medal.setName("Unit1");
     	medal.setDescription("Unit1 medal");
     	medal.setUnit(unit1);
     	medal.setGainTime(new Date());
     	medal.setStudent(student);
     	medal.setPristine(true);
     	medalRepository.create(medal);
     	
     	FollowUp followUp1 = new FollowUp();
     	followUp1.setStatus(FollowUp.Status.CREATED);
     	followUp1.setCategory(FollowUp.Category.COMMENT);
     	followUp1.setCreater(staff1);
     	followUp1.setContent("hai bu cuo");
     	followUp1.setStakeholder(student1);
     	followUpRepository.create(followUp1);
		
     	// Create orderItem
     	OrderItem orderItem = new OrderItem();
     	orderItem.setClassHour(20);
     	orderItem.setClassHourPrice(300);
     	orderItem.setPrice(6000);
     	orderItem.setDealPrice(5000);
     	orderItem.setOrder(order);
     	orderItem.setProduct(product1);
     	orderItemRepository.create(orderItem);
     	
     	OrderItem orderItem2 = new OrderItem();
     	orderItem2.setClassHour(10);
     	orderItem2.setClassHourPrice(300);
     	orderItem2.setPrice(3000);
     	orderItem2.setDealPrice(2000);
     	orderItem2.setOrder(order);
     	orderItem2.setProduct(product2);
     	orderItemRepository.create(orderItem2);
     	
     	
        initOnlineClass(Day.YESTERDAY, teacher1, students, Status.FINISHED, FinishType.AS_SCHEDULED, learningCycle1, 6, 5,product1);
        initOnlineClass(Day.TODAY, teacher1, students, Status.BOOKED, null, learningCycle2, 6, 0,product2);
        initOnlineClass(Day.TOMORROW, teacher1, students, Status.AVAILABLE, null, learningCycle3, 6, 0,product1);
        
        
        // create a demo report and a demo online class
        OnlineClass demoOnlineClass = new OnlineClass();
        demoOnlineClass.setTeacher(teacher1);
        List<Student> studentsForDemoOnlineClass = new ArrayList<Student>();
        studentsForDemoOnlineClass.add(student);
        demoOnlineClass.setStudents(studentsForDemoOnlineClass);
        demoOnlineClass.setStatus(Status.FINISHED);
        demoOnlineClass.setFinishType(FinishType.AS_SCHEDULED);
        Date scheduledDateTime = DateTimeUtils.getYesterday(19);
        demoOnlineClass.setScheduledDateTime(scheduledDateTime); 
        demoOnlineClass.setLesson(demoLesson);
        onlineClassRepository.create(demoOnlineClass);
        
        DemoReport demoReport = new DemoReport();
        demoReport.setTeacher(teacher1);
        demoReport.setStudent(student);
        demoReport.setLifeCycle(DemoReport.LifeCycle.SUBMITTED);
        demoReport.setCreateDateTime(scheduledDateTime);
        demoReport.setOnlineClass(demoOnlineClass);              
        demoReportRepository.create(demoReport);
        
        demoOnlineClass.setDemoReport(demoReport);
        onlineClassRepository.update(demoOnlineClass);
     	
		 // Create learning progress
     	learningProgress1.setCourse(course1);
     	learningProgress1.setStatus(LearningProgress.Status.STARTED);
     	learningProgress1.setNextShouldTakeLesson(lessonRepository.findByCourseIdAndSequence(lastFinishLesson.getLearningCycle().getUnit().getCourse().getId(), lastFinishLesson.getSequence() + 1));
     	learningProgress1.setStudent(student);
     	learningProgress1.setTotalClassHour(24);
     	learningProgress1.setLeftClassHour(10);
     	learningProgress1.setLastScheduledLesson(lastScheduledLesson);
     	learningProgressRepository.create(learningProgress1);

     	learningProgress2.setCourse(course1);
     	learningProgress2.setStatus(LearningProgress.Status.STARTED);
     	learningProgress2.setNextShouldTakeLesson(lessonRepository.findByCourseIdAndSequence(lastFinishLesson.getLearningCycle().getUnit().getCourse().getId(), lastFinishLesson.getSequence() + 1));
     	learningProgress2.setStudent(student2);
     	learningProgress2.setTotalClassHour(10);
     	learningProgress2.setLeftClassHour(10);
     	learningProgress2.setLastScheduledLesson(lastScheduledLesson);
     	learningProgressRepository.create(learningProgress2);
     	
     	LearningProgress itTestLearningProgress = new LearningProgress();
     	itTestLearningProgress.setCourse(itTestCourse);
     	itTestLearningProgress.setStatus(LearningProgress.Status.STARTED);
     	itTestLearningProgress.setStudent(student);
     	itTestLearningProgress.setTotalClassHour(24);
     	itTestLearningProgress.setLeftClassHour(10);
     	learningProgressRepository.create(itTestLearningProgress);
     	
     	LearningProgress demoLearningProgress = new LearningProgress();
     	demoLearningProgress.setCourse(demoCourse);
     	demoLearningProgress.setStatus(LearningProgress.Status.STARTED);
     	demoLearningProgress.setStudent(student);
     	demoLearningProgress.setTotalClassHour(24);
     	demoLearningProgress.setLeftClassHour(10);
     	learningProgressRepository.create(demoLearningProgress);
     	
     	LearningProgress learningProgress4C2 = new LearningProgress();
     	learningProgress4C2.setCourse(course2);
     	learningProgress4C2.setStatus(LearningProgress.Status.STARTED);
     	learningProgress4C2.setStudent(student);
     	learningProgress4C2.setTotalClassHour(10);
     	learningProgressRepository.create(learningProgress4C2);
     	
     	// create assessment report
 		AssessmentReport assessmentReport1 = new AssessmentReport();
 		assessmentReport1.setName("周报");
 		assessmentReport1.setStudent(student);
 		assessmentReport1.setUrl("http://photocdn.sohu.com/20120621/Img346253546.jpg");
 		assessmentReportRepository.create(assessmentReport1);
     		
 		AssessmentReport assessmentReport2 = new AssessmentReport();
 		assessmentReport2.setName("月报");
 		assessmentReport2.setStudent(student);
 		assessmentReport2.setUrl("http://www.fad123.com/uploads/141222/18-1412221300130-L.jpg?135_170");
 		assessmentReportRepository.create(assessmentReport2);	

	}
	
	private void initOnlineClass(Day whichDay, final Teacher teacher, final List<Student> students, final Status status, final FinishType finishType, final LearningCycle learningCycle, final int length, int stars, Product product){
		
		int end = counter + length;
		for (int i=counter; i<end; i++){        
	        logger.info("Create Lesson" + (i + 1));
	        Lesson lesson1 = new Lesson();
	        if (i == 0){
	        	startLesson = lesson1;
	        }
	        if (i == end - 1){
	        	endLesson = lesson1;
	        }
	        lesson1.setName("I'm Special, Letter A" + (i + 1));
	        lesson1.setSerialNumber(learningCycle.getSerialNumber() + "-L" +  (i + 1));
	        lesson1.setSequence(i + 1 + 1000);
	        lesson1.setLearningCycle(learningCycle);
	        lesson1.setObjective("Lesson Objective");
	        lesson1.setTopic("Lesson topic " + (i + 1));
	        lesson1.setDomain("Lesson domain " + (i + 1));
	        lesson1.setGoal("Goal:" + (i+1));
//	        lesson1.setTopic("Lesson topic");
	        lessonRepository.create(lesson1);
	        
	        logger.info("Create Activity" + (i + 1) );
	        Activity activity1 = new Activity();
	        activity1.setName("Activity " + (i + 1));
	        activity1.setLesson(lesson1);
	        activityRepository.create(activity1);
	        
	        logger.info("Create Resource 1" + (i + 1));
	        List<Activity> activities = new ArrayList<Activity>();
	        activities.add(activity1);
	        Resource resource1 = new Resource();
	        resource1.setName("demo" + (i + 1));
	        resource1.setType(Resource.Type.PPT);
	        resource1.setActivities(activities);
	        resource1.setUrl("http://resource.vipkid.com.cn/static/slides/baoshan/index.html");
	        resourceRepository.create(resource1);
	        
	        logger.info("Create PPT 1" + (i + 1));
	        PPT ppt1 = new PPT();
	        ppt1.setResource(resource1);
	        pptRepository.create(ppt1);
	        
	       
	        for (int j=0; j<16; j++){
	        	 logger.debug("Create slide {}", j) ;
	             Slide slide = new Slide();
	             slide.setPage(j + 1);
	             slide.setPPT(ppt1);
	             slide.setSlideImageUrl("http://resource.vipkid.com.cn/static/slides/baoshan/images/slide_" + (j+1) + ".JPG");
	             slideRepository.create(slide);
	        }     
	        
	        OnlineClass onlineClass1 = new OnlineClass(); 
	        onlineClass1.setComments("这个学生英语基础非常好，请老师多注意。这个学生英语基础非常好，请老师多注意。");
	        Date scheduledDateTime = null;
	        switch(whichDay){
	        case YESTERDAY:
	        	scheduledDateTime = DateTimeUtils.getYesterday(10 + i);
	        	break;
	        case TODAY:
	        	scheduledDateTime = DateTimeUtils.getToday(10 + (i - counter));
	        	break;
	        case TOMORROW:
	        	scheduledDateTime = DateTimeUtils.getTomorrow(10 + (i - counter));
	        	break;
	        case FARFUTURE:
	        	scheduledDateTime = DateTimeUtils.getNextMonday();
	        	Calendar scheduledCalendar = Calendar.getInstance();
	        	scheduledCalendar.setTime(scheduledDateTime);
	        	scheduledCalendar.add(Calendar.DAY_OF_MONTH, i - counter + 1);
	        	scheduledCalendar.set(Calendar.MINUTE, 0);
	        	scheduledCalendar.set(Calendar.SECOND, 0);
	        	scheduledCalendar.set(Calendar.MILLISECOND, 0);
	        	scheduledCalendar.set(Calendar.HOUR_OF_DAY, 10);
	        	scheduledDateTime = scheduledCalendar.getTime();
	        	break;
	        }
	        
	        onlineClass1.setScheduledDateTime(scheduledDateTime);
	        if(status == Status.BOOKED || status == Status.FINISHED){
	        	onlineClass1.setLesson(lesson1);
	        	if(students.size()>0){
	        		onlineClass1.addStudent(students.get(0));
	        	}
//	        	for (Student s : students) {
//	        		onlineClass1.addStudent(s);
//				}
	        	
	        }  
	        
	        onlineClass1.setTeacher(teacher);
	        onlineClass1.setStatus(status);
	        
	        if (status == Status.BOOKED){
	        	lastScheduledLesson = lesson1;
	        }
	        
	        if (status == Status.FINISHED){
		        onlineClass1.setFinishType(finishType);
		        if (i == end - 1){
		        	 onlineClass1.setCanUndoFinish(true);
		        }
		        lastFinishLesson = lesson1;
	        }
	       // onlineClass1.setCourse(course);
	        onlineClassRepository.create(onlineClass1);  
	        if(status == Status.FINISHED){
	        	learningProgress1.addCompletedOnlineClass(onlineClass1);
		        learningProgress2.addCompletedOnlineClass(onlineClass1);
	        }
	        
	        
	        
	        Payroll payroll = new Payroll();
            payroll.setTeacher(teacher);
            payrollRepository.create(payroll);

			PayrollItem payrollItem = new PayrollItem();
            payrollItem.setOnlineClass(onlineClass1);
            payrollItem.setPayroll(payroll);
            payrollItemRepository.create(payrollItem);

			onlineClass1.setPayrollItem(payrollItem);
			onlineClassRepository.create(onlineClass1);

	        TeacherComment teacherComment1 = new TeacherComment();
	        teacherComment1.setAbilityToFollowInstructions(1);
	        teacherComment1.setActivelyInteraction(2);
	        teacherComment1.setClearPronunciation(3);
	        teacherComment1.setOnlineClass(onlineClass1);
	        teacherComment1.setStudent(students.get(0));
	        teacherComment1.setTeacher(teacher);
	        teacherComment1.setStars(3);
	        teacherComment1.setReadingSkills(4);
	        teacherComment1.setRepetition(5);
	        teacherComment1.setSpellingAccuracy(1);
	        teacherComment1.setStars(3);
	        teacherComment1.setTeacherFeedback("Todays lesson was not as good as out last one. I think Lovely was very tired and was having trouble concentrating or understanding what was asked of her. She did do well when were started writing her name.");
	        teacherComment1.setTipsForOtherTeachers("Today was her 5th birthday so she is quite young so go extra slow with her when prompting for answers.");
	        teacherCommentRepository.create(teacherComment1);
	        
	        TeacherComment teacherComment2 = new TeacherComment();
	        teacherComment2.setAbilityToFollowInstructions(1);
	        teacherComment2.setActivelyInteraction(2);
	        teacherComment2.setClearPronunciation(3);
	        teacherComment2.setOnlineClass(onlineClass1);
	        teacherComment2.setStudent(students.get(0));
	        teacherComment2.setTeacher(teacher);
	        teacherComment2.setStars(3);
	        teacherComment2.setReadingSkills(4);
	        teacherComment2.setRepetition(5);
	        teacherComment2.setSpellingAccuracy(1);
	        teacherComment2.setStars(3);
	        teacherComment2.setTeacherFeedback("Todays lesson was not as good as out last one. I think Lovely was very tired and was having trouble concentrating or understanding what was asked of her. She did do well when were started writing her name.");
	        teacherComment2.setTipsForOtherTeachers("Today was her 5th birthday so she is quite young so go extra slow with her when prompting for answers.");
	        teacherCommentRepository.create(teacherComment2);
	        
	        TeacherComment teacherComment3 = new TeacherComment();
	        teacherComment3.setAbilityToFollowInstructions(1);
	        teacherComment3.setActivelyInteraction(2);
	        teacherComment3.setClearPronunciation(3);
	        teacherComment3.setOnlineClass(onlineClass1);
	        teacherComment3.setStudent(students.get(0));
	        teacherComment3.setTeacher(teacher);
	        teacherComment3.setStars(3);
	        teacherComment3.setReadingSkills(4);
	        teacherComment3.setRepetition(5);
	        teacherComment3.setSpellingAccuracy(1);
	        teacherComment3.setStars(3);
	        teacherComment3.setTeacherFeedback("Todays lesson was not as good as out last one. I think Lovely was very tired and was having trouble concentrating or understanding what was asked of her. She did do well when were started writing her name.");
	        teacherComment3.setTipsForOtherTeachers("Today was her 5th birthday so she is quite young so go extra slow with her when prompting for answers.");
	        teacherCommentRepository.create(teacherComment3);
	        
	        //创建AA评价
	        EducationalComment aaComment1 = new EducationalComment();
	        aaComment1.setOnlineClass(onlineClass1);
	        aaComment1.setStudent(students.get(0));
	        aaComment1.setContent("Todays lesson was not as good as out last one. I think Lovely was very tired and was having trouble concentrating or understanding what was asked of her. She did do well when were started writing her name.");
	        academicAdvisorCommentRepository.create(aaComment1);
		} 
		counter = end;
	}
	
	private void createPartners(){
		Partner partner1 = new Partner();
		partner1.setName("CHEGG");
		partner1.setEmail("CHEGG@vipkid.com.cn");
		partner1.setUsername(partner1.getEmail());
		partner1.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partner1.setType(Partner.Type.TEACHER_RECRUITMENT);
		partnerRepository.create(partner1);
		
		Partner partner2 = new Partner();
		partner2.setName("TEACHER_REFERAL");
		partner2.setEmail("TEACHER_REFERAL@vipkid.com.cn");
		partner2.setUsername(partner2.getEmail());
		partner2.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partner2.setType(Partner.Type.TEACHER_RECRUITMENT);
		partnerRepository.create(partner2);
		
		Partner partner3 = new Partner();
		partner3.setName("SELF_REFERAL");
		partner3.setEmail("SELF_REFERAL@vipkid.com.cn");
		partner3.setUsername(partner3.getEmail());
		partner3.setType(Partner.Type.TEACHER_RECRUITMENT);
		partner3.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partnerRepository.create(partner3);
		
		Partner partner4 = new Partner();
		partner4.setName("Jon");
		partner4.setEmail("JON@vipkid.com.cn");
		partner4.setUsername(partner4.getEmail());
		partner4.setType(Partner.Type.TEACHER_RECRUITMENT);
		partner4.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partnerRepository.create(partner4);
		
		Partner partner5 = new Partner();
		partner5.setName("Leon");
		partner5.setEmail("xueyejingfeng@163.com");
		partner5.setUsername(partner5.getEmail());
		partner5.setType(Partner.Type.TEACHER_RECRUITMENT);
		partner5.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partnerRepository.create(partner5);
		
		Partner partner6 = new Partner();
		partner6.setName("Other");
		partner6.setEmail("other@vipkid.com.cn");
		partner6.setUsername(partner6.getEmail());
		partner6.setType(Partner.Type.TEACHER_RECRUITMENT);
		partner6.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partnerRepository.create(partner6);

	}
	
	private void createOnlineClass4Recruitment(){
		onlineClass4Recruitment = new OnlineClass();
		onlineClass4Recruitment.setMaxStudentNumber(1);
		onlineClass4Recruitment.setMinStudentNumber(1);
		onlineClass4Recruitment.setStatus(Status.BOOKED);
		onlineClass4Recruitment.setLesson(this.lesson4Recruitment);
		onlineClass4Recruitment.setTeacher(this.teacher4Recruitment);
		onlineClass4Recruitment.setScheduledDateTime(DateTimeUtils.getToday(10));
		onlineClass4Recruitment.setCourse(this.recruitmentCourse);
        onlineClassRepository.create(onlineClass4Recruitment);
	}
	
	private void createOnlineClass4Practicum(){
		onlineClass4Practicum = new OnlineClass();
		onlineClass4Practicum.setMaxStudentNumber(1);
		onlineClass4Practicum.setMinStudentNumber(1);
		onlineClass4Practicum.setStatus(Status.BOOKED);
		onlineClass4Practicum.setLesson(this.lesson4Practicum);
		onlineClass4Practicum.setTeacher(this.teacher4Practicum);
		onlineClass4Practicum.setScheduledDateTime(DateTimeUtils.getToday(10));
		onlineClass4Practicum.setCourse(this.prcticumCourse);
        onlineClassRepository.create(onlineClass4Practicum);
	}
	
	private void createApplicants(){
		Teacher applicant = new Teacher();
		applicant.setLifeCycle(LifeCycle.SIGNUP);
		applicant.setName("Applicant.zhao");
		applicant.setUsername("applicant@vipkid.com.cn");
		applicant.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
		applicant.setEmail("applicant@vipkid.com.cn");
		applicant.setAddress("address");
		applicant.setCountry(Country.USA);
		//applicant.setOffHoursPerWeek(10);
		applicant.setJob("teacher");
		applicant.setSerialNumber("APPLICANT-1");
		applicant.setRecruitmentChannel(RecruitmentChannel.SELF_REFERAL);
		teacherRepository.create(applicant);
		
		TeacherApplication application = new TeacherApplication();
		application.setTeacher(applicant);
		application.setApplyDateTime(new Date());
		application.setCurrent(true);
		application.setStatus(TeacherApplication.Status.SIGNUP);
		applicationRepository.create(application);
		
		TeacherApplication interview = new TeacherApplication();
		interview.setTeacher(applicant);
		interview.setApplyDateTime(new Date());
		interview.setCurrent(true);
		interview.setOnlineClass(onlineClass4Recruitment);
		interview.setStatus(TeacherApplication.Status.INTERVIEW);
		applicationRepository.create(interview);
	}
	
	
	private void createRecruitmentCourse(){
		// 创建教室招聘课程
		recruitmentCourse = new Course();
		recruitmentCourse.setSerialNumber("R1");
		recruitmentCourse.setMode(Mode.ONE_TO_MANY);
		recruitmentCourse.setName("教师招聘课程");
		recruitmentCourse.setDescription("面试教师。");
		recruitmentCourse.setNeedBackupTeacher(false);
		recruitmentCourse.setSequential(false);
		recruitmentCourse.setFree(true);
		recruitmentCourse.setType(Course.Type.TEACHER_RECRUITMENT);
		courseRepository.create(recruitmentCourse);
		
		Unit unit = new Unit();
		unit.setName("教师招聘测试单元");
		unit.setNameInLevel("R1-U1");
		unit.setSerialNumber("R1-U1");
		unit.setCourse(recruitmentCourse);
		unit.setLevel(Level.LEVEL_0);
		unitRepository.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("教师招聘学习闭环");
		learningCycle.setSerialNumber("R1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleRepository.create(learningCycle);
		
		lesson4Recruitment = new Lesson();
		lesson4Recruitment.setName("教师招聘测试课");
		lesson4Recruitment.setSerialNumber("R1-U1-LC1-L1");
		lesson4Recruitment.setLearningCycle(learningCycle);
		lessonRepository.create(lesson4Recruitment);
		
		Activity activity = new Activity();
		activity.setName("教师招聘活动");
		activity.setLesson(lesson4Recruitment);
		activityRepository.create(activity);
		
		Resource resource = new Resource();
		resource.setName("教师招聘PPT");
		resource.setType(Resource.Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceRepository.create(resource);
		
	  
        PPT ppt1 = new PPT();
        ppt1.setResource(resource);
        pptRepository.create(ppt1);
		
		// 创建教师招聘课商品
		Product recruitmentProduct = new Product();
		recruitmentProduct.setName("教师招聘课");
		recruitmentProduct.setDescription("面试教师。");
		recruitmentProduct.setStatus(Product.Status.ON_SALE);
		recruitmentProduct.setCourse(recruitmentCourse);
		recruitmentProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		itTestProduct.setUnits(units);
		productRepository.create(recruitmentProduct);
	}
	
	private void createPracticumCourse(){
		// 创建教室招聘课程
		prcticumCourse = new Course();
		prcticumCourse.setSerialNumber("P1");
		prcticumCourse.setMode(Mode.ONE_TO_MANY);
		prcticumCourse.setName("教师试讲课程");
		prcticumCourse.setDescription("试讲教师。");
		prcticumCourse.setNeedBackupTeacher(false);
		prcticumCourse.setSequential(false);
		prcticumCourse.setFree(true);
		prcticumCourse.setType(Course.Type.PRACTICUM);
		courseRepository.create(prcticumCourse);
		
		Unit unit = new Unit();
		unit.setName("教师招聘试讲单元");
		unit.setNameInLevel("P1-U1");
		unit.setSerialNumber("P1-U1");
		unit.setCourse(prcticumCourse);
		unit.setLevel(Level.LEVEL_0);
		unitRepository.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("教师招聘试讲闭环");
		learningCycle.setSerialNumber("P1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleRepository.create(learningCycle);
		
		lesson4Practicum = new Lesson();
		lesson4Practicum.setName("教师试讲测试课");
		lesson4Practicum.setSerialNumber("P1-U1-LC1-L1");
		lesson4Practicum.setLearningCycle(learningCycle);
		lessonRepository.create(lesson4Practicum);
		
		Activity activity = new Activity();
		activity.setName("教师试讲活动");
		activity.setLesson(lesson4Practicum);
		activityRepository.create(activity);
		
		Resource resource = new Resource();
		resource.setName("教师试讲PPT");
		resource.setType(Resource.Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceRepository.create(resource);
		
	  
        PPT ppt1 = new PPT();
        ppt1.setResource(resource);
        pptRepository.create(ppt1);
		
		// 创建教师招聘课商品
		Product practicumProduct = new Product();
		practicumProduct.setName("教师试讲课");
		practicumProduct.setDescription("面试教师。");
		practicumProduct.setStatus(Product.Status.ON_SALE);
		practicumProduct.setCourse(recruitmentCourse);
		practicumProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		practicumProduct.setUnits(units);
		productRepository.create(practicumProduct);
	}
	
	private void createITTest() {
		// 创建IT测试课程
        itTestCourse = new Course();
        itTestCourse.setSerialNumber("IT1");
        itTestCourse.setMode(Mode.ONE_ON_ONE);
        itTestCourse.setName("IT测试课程");
        itTestCourse.setDescription("保障家长可以通过在线教室正常上课。");
        itTestCourse.setNeedBackupTeacher(false);
        itTestCourse.setSequential(false);
        itTestCourse.setFree(true);
        itTestCourse.setType(Course.Type.IT_TEST);
		courseRepository.create(itTestCourse);
		
		Unit unit = new Unit();
		unit.setName("IT测试单元");
		unit.setNameInLevel("IT1-U1");
		unit.setSerialNumber("IT1-U1");
		unit.setCourse(itTestCourse);
		unit.setLevel(Level.LEVEL_0);
		unitRepository.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("IT测试学习闭环");
		learningCycle.setSerialNumber("IT1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleRepository.create(learningCycle);
		
		Lesson lesson = new Lesson();
		lesson.setName("IT测试课");
		lesson.setSerialNumber("IT1-U1-LC1-L1");
		lesson.setLearningCycle(learningCycle);
		lessonRepository.create(lesson);
		
		Activity activity = new Activity();
		activity.setName("IT测试活动");
		activity.setLesson(lesson);
		activityRepository.create(activity);
		
		Resource resource = new Resource();
		resource.setName("IT测试PPT");
		resource.setType(Resource.Type.PPT);
		resource.setUrl("xxx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceRepository.create(resource);
		
	  
        PPT ppt1 = new PPT();
        ppt1.setResource(resource);
        pptRepository.create(ppt1);
		
		// 创建IT测试课商品
		itTestProduct = new Product();
		itTestProduct.setName("IT测试课");
		itTestProduct.setDescription("用于保障家长可以通过在线教室正常上课。");
		itTestProduct.setStatus(Product.Status.ON_SALE);
		itTestProduct.setCourse(itTestCourse);
		itTestProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		itTestProduct.setUnits(units);
		productRepository.create(itTestProduct);
	}
	
	private Lesson createDemo() {
		// 创建试听课程
		demoCourse = new Course();
		demoCourse.setSerialNumber("DEMO1");
		demoCourse.setMode(Mode.ONE_ON_ONE);
		demoCourse.setName("试听课程");
		demoCourse.setDescription("学生试听课程。");
		demoCourse.setNeedBackupTeacher(false);
		demoCourse.setSequential(false);
		demoCourse.setFree(true);
		demoCourse.setType(Course.Type.DEMO);
		demoCourse.setBaseClassSalary(10);
		courseRepository.create(demoCourse);
		
		Unit unit = new Unit();
		unit.setName("试听单元");
		unit.setNameInLevel("DEMO1-U1");
		unit.setSerialNumber("DEMO1-U1");
		unit.setCourse(demoCourse);
		unit.setLevel(Level.LEVEL_0);
		unitRepository.create(unit);
		
		LearningCycle learningCycle = new LearningCycle();
		learningCycle.setName("试听学习闭环");
		learningCycle.setSerialNumber("DEMO1-U1-LC1");
		learningCycle.setUnit(unit);
		learningCycleRepository.create(learningCycle);
		
		Lesson lesson = new Lesson();
		lesson.setName("试听课");
		lesson.setSerialNumber("DEMO1-U1-LC1-L1");
		lesson.setLearningCycle(learningCycle);
		lessonRepository.create(lesson);
		
		Activity activity = new Activity();
		activity.setName("试听活动");
		activity.setLesson(lesson);
		activityRepository.create(activity);
		
		
		Resource resource = new Resource();
		resource.setName("试听PPT");
		resource.setType(Resource.Type.PPT);
		resource.setUrl("xx");
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		resource.setActivities(activities);
		resourceRepository.create(resource);
		
		PPT ppt1 = new PPT();
        ppt1.setResource(resource);
        pptRepository.create(ppt1);
	        
		// 创建试听课商品
		demoProduct = new Product();
		demoProduct.setStatus(Product.Status.ON_SALE);
		demoProduct.setName("试听课");
		demoProduct.setDescription("学生试听课程。");
		demoProduct.setCourse(demoCourse);
		demoProduct.setClassHourPrice(0);
		List<Unit> units = new ArrayList<Unit>();
		units.add(unit);
		demoProduct.setUnits(units);
		productRepository.create(demoProduct);
		
		return lesson;
				
	}
}
