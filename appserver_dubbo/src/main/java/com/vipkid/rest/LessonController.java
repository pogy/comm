package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import com.vipkid.handler.LessonHandler;
import com.vipkid.model.Course;
import com.vipkid.rest.vo.query.LessonVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.service.LessonService;

@RestController
@RequestMapping("/api/service/private/lessons")
public class LessonController {
	private Logger logger = LoggerFactory.getLogger(LessonController.class.getSimpleName());

	@Resource
	private LessonService lessonService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Lesson find(@RequestParam("id") long id) {
		logger.info("find lesson for id = {}", id);
		return lessonService.find(id);
	}
	
	@RequestMapping(value = "/findNextByCourseIdAndSequence", method = RequestMethod.GET)
	public Lesson findNextByCourseIdAndSequence(@RequestParam("courseId") long courseId, @RequestParam("sequence") int sequence) {
		logger.info("find next lesson by courseid and sequence , courseid = {} and sequence = {}",courseId, sequence);
		return lessonService.findNextByCourseIdAndSequence(courseId, sequence);
	}
	
	@RequestMapping(value = "/findPrevByCourseIdAndSequence", method = RequestMethod.GET)
	public Lesson findPrevByCourseIdAndSequence(@RequestParam("courseId") long courseId, @RequestParam("sequence") int sequence) {
		logger.info("find prev lesson by courseid and sequence , courseid = {} and sequence = {}",courseId, sequence);
		return lessonService.findPrevByCourseIdAndSequence(courseId, sequence);
	}
	
	@RequestMapping(value = "/findByLearningCycleId", method = RequestMethod.GET)
	public List<Lesson> findByLearningCycleId(@RequestParam("learningCycleId") long learningCycleId) {
		logger.info("find lessons for learningCycleId = {}", learningCycleId);
		return lessonService.findByLearningCycleId(learningCycleId);
	}
	
	@RequestMapping(value = "/findFirstByLearningCycleId", method = RequestMethod.GET)
	public Lesson findFirstByLearningCycleId(@RequestParam("learningCycleId") long learningCycleId) {
		logger.info("find first lesson for learningCycleId = {}", learningCycleId);
		return lessonService.findFirstByLearningCycleId(learningCycleId);
	}
	
	@RequestMapping(value = "/findByUnitId", method = RequestMethod.GET)
	public List<Lesson> findByUnitId(@RequestParam("unitId") long unitId) {
		logger.info("find lessons for unitId = {}", unitId);
		return lessonService.findByUnitId(unitId);
	}
	
	@RequestMapping(value = "/findFirstByUnitId", method = RequestMethod.GET)
	public Lesson findFirstByUnitId(@RequestParam("unitId") long unitId) {
		logger.info("find first lesson for unitId = {}", unitId);
		return lessonService.findFirstByUnitId(unitId);
	}
	
	@RequestMapping(value = "/findByCourseId", method = RequestMethod.GET)
	public List<Lesson> findByCourseId(@RequestParam("courseId") long courseId) {
		logger.info("find lessons for courseId = {}", courseId);
		return lessonService.findByCourseId(courseId);
	}
    @RequestMapping(value = "/filterByCourseId", method = RequestMethod.GET)
    public List<LessonVO> filterByCourseId(@RequestParam("courseId") long courseId) {
        logger.info("find lessons for courseId = {}", courseId);
        List<Lesson> lessonList = lessonService.findByCourseId(courseId);
        return LessonHandler.conver2VOList(lessonList);
    }
	
	@RequestMapping(value = "/findFirstByCourseId", method = RequestMethod.GET)
	public Lesson findFirstByCourseId(@RequestParam("courseId") long courseId) {
		logger.info("find first lesson for courseId = {}", courseId);
		return lessonService.findFirstByCourseId(courseId);
	}
	
	@RequestMapping(value = "/findFirstByCourseIdAndLevel", method = RequestMethod.GET)
	public Lesson findFirstByCourseIdAndLevel(@RequestParam("courseId") long courseId, @RequestParam("level") Level level) {
		logger.info("find first lesson for courseId = {} and level = {}", courseId, level);
		return lessonService.findFirstByCourseIdAndLevel(courseId, level);
	}
	
	@RequestMapping(value = "/findLessonAndClassByUnitId", method = RequestMethod.GET)
	public List<Lesson> findLessonAndClassByUnitId(@RequestParam("studentId") long studentId, @RequestParam("unitId") long unitId) {
		logger.info("find student info by id = {}", studentId);
		return lessonService.findLessonAndClassByUnitId(studentId, unitId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Lesson update(@RequestBody Lesson lesson) {
		logger.info("update lesson: {}", lesson);
		return lessonService.update(lesson);
	}

    @RequestMapping("/findNextByLessonIdAndSequence")
    public Lesson findNextByLessonIdAndSequence(@RequestParam("lessonId") long lessonId, @RequestParam("sequence") int sequence) {
        Course course = findCourseByLessonId(lessonId);

        if (null != course) {
        	return findNextByCourseIdAndSequence(course.getId(), sequence);
        } else {
        	throw new NullPointerException();
        }
    }
    
    @RequestMapping("/findPrevByLessonIdAndSequence")
    public Lesson findPrevByLessonIdAndSequence(@RequestParam("lessonId") long lessonId, @RequestParam("sequence") int sequence) {
        Course course = findCourseByLessonId(lessonId);

        if (null != course) {
        	return findPrevByCourseIdAndSequence(course.getId(), sequence);
        } else {
        	throw new NullPointerException();
        }   
    }
    
    private Course findCourseByLessonId(long lessonId) {
        return lessonService.find(lessonId).getLearningCycle().getUnit().getCourse();
    }
}
