package com.vipkid.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Activity;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.PPT;
//import com.vipkid.model.Resource;
import com.vipkid.model.Resource.Type;
import com.vipkid.model.Unit;
import com.vipkid.repository.ActivityRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.LearningCycleRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.PPTRepository;
import com.vipkid.repository.ResourceRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.UnitRepository;
import com.vipkid.util.Configurations;

@Service
public class TestDataInitializeService {

	private Logger logger = LoggerFactory.getLogger(InitializeService.class.getSimpleName());

	@Resource
	private TeacherRepository teacherRepository;

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private UnitRepository unitRepository;

	@Resource
	private LearningCycleRepository learningCycleRepository;

	@Resource
	private LearningProgressRepository learningProgressRepository;

	@Resource
	private OnlineClassRepository onlineClassRepository;

	@Resource
	private LessonRepository lessonRepository;

	@Resource
	private ActivityRepository activityRepository;

	@Resource
	private ResourceRepository resourceRepository;

	@Resource
	private PPTRepository pptRepository;

	public Response doInitCourse() {

		// create course
		logger.info("Create Course");
		Course course = new Course();
		course.setSerialNumber("C30");
		course.setName("TEST课程");
		course.setMode(Mode.ONE_ON_ONE);
		course.setType(Course.Type.TEST);
		course.setSequential(true);
		course.setNeedBackupTeacher(true);
		course.setBaseClassSalary(10);
		courseRepository.create(course);

		logger.info("Create Unit");
		Unit unit1 = new Unit();
		unit1.setSerialNumber("C30-U1");
		unit1.setSequence(1);
		unit1.setName("Letter1");
		unit1.setObjective("Objective, Objective, Objective, Objective, Objective, Objective, Objective, Objective.");
		unit1.setTopic("topic");
		unit1.setLevel(Level.LEVEL_0);
		unit1.setCourse(course);
		unitRepository.create(unit1);

		logger.info("Create Unit2 for Course");
		Unit unit2 = new Unit();
		unit2.setSerialNumber("C30-U2");
		unit2.setSequence(2);
		unit2.setName("Letter2");
		unit2.setObjective("Objective");
		unit2.setTopic("topic");
		unit2.setCourse(course);
		unit2.setLevel(Level.LEVEL_1);
		unit2.setNameInLevel("Level_1");
		unitRepository.create(unit2);

		logger.info("Create LearningCycle1");
		LearningCycle learningCycle1 = new LearningCycle();
		learningCycle1.setName("Learning Cycle1");
		learningCycle1.setSerialNumber("C30-U1-LC1");
		learningCycle1.setSequence(1);
		learningCycle1.setUnit(unit1);
		learningCycle1.setTopic("topic");
		learningCycle1.setObjective("Learing Cycle Objective");
		learningCycleRepository.create(learningCycle1);

		logger.info("Create LearningCycle for C10-U2");
		LearningCycle learningCycle2 = new LearningCycle();
		learningCycle2.setName("Learning Cycle1");
		learningCycle2.setSerialNumber("C30-U2-LC1");
		learningCycle2.setSequence(1);
		learningCycle2.setUnit(unit2);
		learningCycle2.setTopic("topic");
		learningCycle2.setObjective("Learing Cycle Objective");
		learningCycleRepository.create(learningCycle2);

		logger.info("Create Lessons for C30-U1-LC1");
		for (int i = 0; i < 5; i++) {
			Lesson lesson1 = new Lesson();
			lesson1.setName("I'm Special, Letter A" + (i + 1));
			lesson1.setSerialNumber("C30-U1-LC1-L" + (i + 1));
			lesson1.setSequence(i + 1);
			lesson1.setLearningCycle(learningCycle1);
			lesson1.setObjective("Lesson Objective: This is a objective. Lesson Objective: This is a objective. " + "Lesson Objective: This is a objective. Lesson Objective: This is a objective. ");
			lesson1.setTopic("Lesson topic " + (i + 1));
			lesson1.setDomain("Lesson domain " + (i + 1));
			lesson1.setGoal("Goal:" + (i + 1) + " Learn letter A.");
			lessonRepository.create(lesson1);

			Activity activity = new Activity();
			activity.setName("TEST课程活动");
			activity.setLesson(lesson1);
			activityRepository.create(activity);

			com.vipkid.model.Resource resource = new com.vipkid.model.Resource();
			resource.setName("TEST课程PPT");
			resource.setType(Type.PPT);
			resource.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson1.getSerialNumber()));
			List<Activity> activities = new ArrayList<Activity>();
			activities.add(activity);
			resource.setActivities(activities);
			resourceRepository.create(resource);

			PPT ppt = new PPT();
			ppt.setResource(resource);
			pptRepository.create(ppt);
		}

		logger.info("Create Lessons for C30-U2-LC1");
		for (int i = 0; i < 5; i++) {
			Lesson lesson2 = new Lesson();
			lesson2.setName("I'm Special, Letter A" + (i + 1));
			lesson2.setSerialNumber("C30-U2-LC1-L" + (i + 1));
			lesson2.setSequence(i + 1);
			lesson2.setLearningCycle(learningCycle2);
			lesson2.setObjective("Lesson Objective: This is a objective. Lesson Objective: This is a objective. " + "Lesson Objective: This is a objective. Lesson Objective: This is a objective. ");
			lesson2.setTopic("Lesson topic " + (i + 1));
			lesson2.setDomain("Lesson domain " + (i + 1));
			lesson2.setGoal("Goal:" + (i + 1) + " Learn letter A.");
			lessonRepository.create(lesson2);

			Activity activity = new Activity();
			activity.setName("TEST课程活动");
			activity.setLesson(lesson2);
			activityRepository.create(activity);

			com.vipkid.model.Resource resource = new com.vipkid.model.Resource();
			resource.setName("TEST课程PPT");
			resource.setType(Type.PPT);
			resource.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson2.getSerialNumber()));
			List<Activity> activities = new ArrayList<Activity>();
			activities.add(activity);
			resource.setActivities(activities);
			resourceRepository.create(resource);

			PPT ppt = new PPT();
			ppt.setResource(resource);
			pptRepository.create(ppt);
		}

		return Response.ok("succeed!!").build();
	}
}
