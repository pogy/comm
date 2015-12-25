package com.vipkid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Channel;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Family;
import com.vipkid.model.Gender;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.LearningProgress.Status;
import com.vipkid.model.Parent;
import com.vipkid.model.Pet;
import com.vipkid.model.Student;
import com.vipkid.model.Student.StudentType;
import com.vipkid.repository.AgentRepository;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.MarketingActivityRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.security.SecurityService;
import com.vipkid.security.TokenGenerator;
import com.vipkid.util.TextUtils;

@Service
public class ImportExcelService {

	@Resource
	private ParentRepository parentRepository;

	@Resource
	private FamilyRepository familyRepository;

	@Resource
	private StudentRepository studentRepository;

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private LearningProgressRepository learingProgressRepository;

	@Resource
	private AirCraftRepository aircraftRepository;

	@Resource
	private AirCraftThemeRepository aircraftThemeRepository;

	@Resource
	private PetRepository petRepository;

	@Resource
	private MarketingActivityRepository marketingActivityRepository;

	@Resource
	private AgentRepository agentRepository;

	@Resource
	private SecurityService securityService;

	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;
	
	@Resource
	private ChannelService channelService;
	
	@Resource
	private OnlineClassService onlineClassService;

	public String doUpdate(String parentName, String phone, String notes, String source, String channel, Parent findParent) {
		List<Student> students = studentRepository.findByParentId(findParent.getId());
		if (parentName != null && !parentName.equals("")) {
			findParent.setName(parentName);
			parentRepository.update(findParent);
		}
		int sign = 0;
		Channel channelFound = channelService.findBySourceName(channel);
		if (channelFound == null) {
			securityService.logAudit(Level.ERROR, Category.IMPORT_EXCEL_DATA, "fault format student " + phone);
			return null;
		}
		for (Student student : students) {
			StringBuffer operation = new StringBuffer();
			operation.append("import excel data,update,");
			operation.append("phone=");
			operation.append(phone);
			operation.append("studentId=");
			operation.append(student.getId());
			operation.append(",sourceFromExcel=");
			operation.append(source);
			operation.append(",channelFromExcel");
			operation.append(channel);

			if (student.getChannel() == null) {
				student.setChannel(channelFound);
			}
			operation.append(",channelEndUpdate");
			operation.append(channel);
			securityService.logAudit(Level.INFO, Category.IMPORT_EXCEL_DATA, operation.toString());

			if (sign == 0) {
				if (notes != null && !notes.equals("")) {
					student.setNotes(student.getNotes() + "  " + notes);
				}
			}
			studentRepository.update(student);
			sign++;
		}
		return channel;
	}

	private Hashtable<String, Channel> channelTable = new Hashtable<String, Channel>();

	private Channel getChannelBySourceName(String name) {
		if (channelTable.containsKey(name)) {
			return channelTable.get(name);
		} else {
			Channel channel = channelService.findBySourceName(name);
			channelTable.put(name, channel);

			return channel;
		}
	}

	public Student doCreate(String parentName, String studentName, String englishName, String age, Date birDate, String gender, String phone, String city, String email, String notes, String source,
			String channel) {

		// 新建家庭
		Family family = new Family();
		if (city != null && !city.equals("")) {
			family.setCity(city);
		}
		familyRepository.create(family);
		Channel channelFound = channelService.findBySourceName(channel);
		if (channelFound == null) {
			securityService.logAudit(Level.ERROR, Category.IMPORT_EXCEL_DATA, "fault format student " + phone);
			return null;
		}
		Parent parent = new Parent();
		parent.setMobile(phone);
		if (parentName != null && !parentName.equals("")) {
			parent.setName(parentName);
		}
		if (email != null && !email.equals("")) {
			parent.setEmail(email);
		}

		Student student = new Student();
		StringBuffer operation = new StringBuffer();
		operation.append("import excel data,create family,");
		operation.append("phone=");
		operation.append(phone);
		operation.append(",source=");
		operation.append(source);
		operation.append(",channel=");
		operation.append(source);
		securityService.logAudit(Level.INFO, Category.IMPORT_EXCEL_DATA, operation.toString());
		// MarketingActivity marketingActivity =
		// marketingActivityRepository.findByChannel(channel);
		student.setChannel(channelFound);
		if (studentName != null && !studentName.equals("")) {
			student.setName(studentName);
		}
		if (englishName != null && !englishName.equals("")) {
			student.setEnglishName(englishName);
		}
		if (birDate != null) {
			student.setBirthday(birDate);
		} else if (age != null && !age.equals("")) {
			Calendar today = Calendar.getInstance();
			today.add(Calendar.YEAR, -Integer.valueOf(age));
			student.setBirthday(today.getTime());
		}
		if (gender != null && !gender.equals("")) {
			if (gender.trim().equals("男")) {
				student.setGender(Gender.MALE);
			} else if (gender.trim().equals("女")) {
				student.setGender(Gender.FEMALE);
			}
		}
		if (notes != null && !notes.equals("")) {
			student.setNotes(notes);
		}
		
		student = doCreateSignup(parent, student, family,channel);
		//TODO 以后去掉
		if("悠贝绘本公开课".equals(channel)){//悠贝绘本公开课专属逻辑
			onlineClassService.doBookOnToManyForOpen(1080011, student.getId(), 1216896, "2015-07-29 19:00");
		}
		return student;
	}

	private Student doCreateSignup(Parent parent, Student student, Family family,String channel) {

		// 新建家长
		//TODO 以后只留下else中的逻辑
		if("悠贝绘本公开课".equals(channel)){//悠贝绘本公开课专属逻辑
			parent.setPassword(PasswordEncryptor.encrypt("vipkid"));
		}else{
			parent.setPassword(PasswordEncryptor.encrypt(parent.getMobile()));
		}
		parent.setUsername(parent.getMobile());
		parent.setMobile(parent.getUsername());
		parent.setCreater(securityService.getCurrentUser());
		parent.setFamily(family);
		if (TextUtils.isEmpty(parent.getToken())) {
			parent.setToken(TokenGenerator.generate());
		}
		parentRepository.create(parent);
		List<Parent> parents = new ArrayList<Parent>();
		parents.add(parent);
		family.setParents(parents);

		// 新建学生
		String studentPassword = PasswordGenerator.generate();
		student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
		student.setPassword(studentPassword);
		// student.setSource(Source.WEBSITE);
		student.setWelcome(true);
		student.setFamily(family);
		student.setCreater(securityService.getCurrentUser());
		student.setLifeCycle(Student.LifeCycle.SIGNUP);
		student.setAvatar("boy_3");

		student = studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
		List<Student> students = new ArrayList<Student>();
		students.add(student);
		family.setStudents(students);

		// // 更新家庭
		// family.setCreater(parent);
		// familyAccessor.update(family);

		// 新建ITTest课学习进度
		Course itTestCourse = courseRepository.findByCourseType(Course.Type.IT_TEST);
		LearningProgress itTestLearningProgress = new LearningProgress();
		itTestLearningProgress.setStudent(student);
		itTestLearningProgress.setStatus(Status.STARTED);
		itTestLearningProgress.setCourse(itTestCourse);
		itTestLearningProgress.setLeftClassHour(1);
		itTestLearningProgress.setTotalClassHour(1);
		learingProgressRepository.create(itTestLearningProgress);

		// 新建试听课学习进度
		Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
		LearningProgress demoLearningProgress = new LearningProgress();
		demoLearningProgress.setStudent(student);
		demoLearningProgress.setStatus(Status.STARTED);
		demoLearningProgress.setCourse(demoCourse);
		demoLearningProgress.setLeftClassHour(1);
		demoLearningProgress.setTotalClassHour(1);
		learingProgressRepository.create(demoLearningProgress);

		// 给学生一个默认的飞机
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

		// 给学生一个默认的宠物
		Pet pet = new Pet();
		pet.setStudent(student);
		pet.setName(student.getEnglishName() + "'s Spirit");
		pet.setSequence(10);
		pet.setPrice(0);
		pet.setUrl("pet10");
		pet.setCurrent(true);
		pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
		petRepository.create(pet);

		return student;
	}

}
