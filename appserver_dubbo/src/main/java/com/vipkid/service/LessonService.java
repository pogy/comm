package com.vipkid.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Course;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.PPT;
import com.vipkid.model.Resource;
import com.vipkid.model.TeacherComment;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.PPTRepository;
import com.vipkid.repository.TeacherCommentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.LessonClassOfUnitView;

@Service
public class LessonService {
	private Logger logger = LoggerFactory.getLogger(LessonService.class.getSimpleName());

	@javax.annotation.Resource
	private LessonRepository lessonRepository;
	
	@javax.annotation.Resource
	private OnlineClassRepository onlineClassRepository;
	
	@javax.annotation.Resource
	private TeacherCommentRepository teacherCommentRepository;
	
	@javax.annotation.Resource
	private PPTRepository pptRepository;
	
	@javax.annotation.Resource
	private CourseRepository courseRepository;
	
	@javax.annotation.Resource
	private SecurityService securityService;
	
	public Lesson find(long id) {
		logger.debug("find lesson for id = {}", id);
		Lesson lesson = lessonRepository.find(id);
		//PPT ppt = pptRepository.findByLessonIdAndType(lesson.getId(), Resource.Type.PPT);
		//lesson.setPpt(ppt);
		return lesson;
	}
	
	public Lesson findNextByCourseIdAndSequence(long courseId, int sequence) {
		Lesson lesson = lessonRepository.findByCourseIdAndSequence(courseId, sequence+1);
		if (lesson == null) {
			lesson = lessonRepository.findByCourseIdAndSequence(courseId, sequence);
		}
		
		return lesson;
	}
	
	public Lesson findPrevByCourseIdAndSequence(long courseId, int sequence) {
		Lesson lesson = lessonRepository.findByCourseIdAndSequence(courseId, sequence-1);
		if (lesson == null) {
			lesson = lessonRepository.findByCourseIdAndSequence(courseId, sequence);
		}
		
		return lesson;
	}
	
	private Course findCourseByLessonId(long lessonId) {
		return lessonRepository.find(lessonId).getLearningCycle().getUnit().getCourse();
	}
	
	public Lesson findNextByLessonIdAndSequence(long lessonId, int sequence) {
		Course course = findCourseByLessonId(lessonId);
		
		return findNextByCourseIdAndSequence(course.getId(), sequence);
	}
	
	public Lesson findPrevByLessonIdAndSequence(long lessonId, int sequence) {
		Course course = findCourseByLessonId(lessonId);
		
		return findPrevByCourseIdAndSequence(course.getId(), sequence);
	}
	
	public List<Lesson> findByLearningCycleId(long learningCycleId) {
		logger.debug("find lessons for learningCycleId = {}", learningCycleId);
		List<Lesson> lessons = lessonRepository.findByLearningCycleId(learningCycleId);
        if (CollectionUtils.isNotEmpty(lessons)) {
            for(Lesson lesson : lessons) {
                PPT ppt = pptRepository.findByLessonIdAndType(lesson.getId(), Resource.Type.PPT);
                lesson.setPpt(ppt);
            }
        }
		return lessons;
	}
	
	public Lesson findFirstByLearningCycleId(long learningCycleId) {
		logger.debug("find first lesson for learningCycleId = {}", learningCycleId);
		
		List<Lesson> lessons = lessonRepository.findByLearningCycleId(learningCycleId);
		if(lessons.isEmpty()) {
			return null;
		}else {
			return lessons.get(0);
		}
	}
	
	public List<Lesson> findByUnitId(long unitId) {
		logger.debug("find lessons for unitId = {}", unitId);
		return lessonRepository.findByUnitId(unitId);
	}
	
	public Lesson findFirstByUnitId(long unitId) {
		logger.debug("find lesson for unitId = {}", unitId);
		List<Lesson> lessons = lessonRepository.findByUnitId(unitId);
		if(lessons.isEmpty()) {
			return null;
		}else {
			return lessons.get(0);
		}
	}
	
	public List<Lesson> findByCourseId(long courseId) {
		logger.debug("find lessons for courseId = {}", courseId);
		return lessonRepository.findByCourseId(courseId);
	}
	
	public Lesson findFirstByCourseId(long courseId) {
		logger.debug("find first lesson for courseId = {}", courseId);
		List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
		if(lessons.isEmpty()) {
			return null;
		}else {
			return lessons.get(0);
		}
	}
	
	public Lesson findFirstByCourseIdAndLevel(long courseId, Level level) {
		logger.debug("find first lesson for courseId = {} and level = {}", courseId, level);
		return lessonRepository.findFirstByCourseIdAndLevel(courseId, level);
	}
	
	public List<Lesson> findLessonAndClassByUnitId(long studentId, long unitId) {
		logger.debug("find student info by id = {}", studentId);
		List<Lesson> lessons = lessonRepository.findByUnitId(unitId);
		
		List<OnlineClass> onlineClasses = onlineClassRepository.findBookedAndFinishedByStudentIdAndUnitId(studentId, unitId);
		Map<Long, OnlineClass> onlineClassByLessonMap = initLatestOnlineClassByLesson(onlineClasses);
		for (Lesson lesson : lessons){
			OnlineClass onlineClass = onlineClassByLessonMap.get(lesson.getId());
			if (onlineClass != null){
				LessonClassOfUnitView lessonClassOfUnitView = new LessonClassOfUnitView();
				lessonClassOfUnitView.setOnlineClassId(onlineClass.getId());
				lessonClassOfUnitView.setName(lesson.getName());
				lessonClassOfUnitView.setSequence(lesson.getSequence());
				lessonClassOfUnitView.setNumber(lesson.getNumber());
				
				TeacherComment currentComment = teacherCommentRepository.findByOnlineClassIdAndStudentId(onlineClass.getId(), studentId);
				if(null != currentComment){
					lessonClassOfUnitView.setStars(currentComment.getStars());
				}
				
				if(onlineClass.getStatus().equals(Status.FINISHED)){
					lessonClassOfUnitView.setFinished(true);
				} else {
					lessonClassOfUnitView.setFinished(false);
				}
				lesson.setLessonClassOfUnitView(lessonClassOfUnitView);	
			}
		}
		return lessons;
	}
	

	public Lesson update(Lesson lesson) {
		logger.debug("update lesson: {}", lesson);
		lessonRepository.update(lesson);
		StringBuffer strbuf = new StringBuffer(lesson.getSerialNumber());//课程的序列号不会更改，所以以他为基准。
		securityService.logAudit(com.vipkid.model.Audit.Level.INFO, Category.LESSON_BASIC_INFO_UPDATE, "Update: The "+strbuf.toString()+" Lesson has been updated！" );
		return lesson;
	}
	
	private Map<Long, OnlineClass> initLatestOnlineClassByLesson(final List<OnlineClass> onlineClasses){
		Map<Long, OnlineClass> latestOnlineClassMap = new HashMap<Long, OnlineClass>();
		for(OnlineClass onlineClass : onlineClasses){
			long lessonId = onlineClass.getLesson().getId();
			OnlineClass mappedOnlineClass = latestOnlineClassMap.get(lessonId);
			if (mappedOnlineClass == null){
				latestOnlineClassMap.put(lessonId, onlineClass);
			} else{
				if (mappedOnlineClass.getScheduledDateTime().getTime() < onlineClass.getScheduledDateTime().getTime()){
					latestOnlineClassMap.put(lessonId, onlineClass);
				}
			}
		}
		return latestOnlineClassMap;
	}
}
