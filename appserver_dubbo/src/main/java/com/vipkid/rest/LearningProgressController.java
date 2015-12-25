package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import com.vipkid.handler.LearningProgressHandler;
import com.vipkid.rest.vo.Response;
import com.vipkid.rest.vo.query.LearningProgressVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.LearningProgress;
import com.vipkid.service.LearningProgressService;

@RestController
@RequestMapping("/api/service/private/learningProgresses")
public class LearningProgressController {
	private Logger logger = LoggerFactory.getLogger(LearningProgressController.class.getSimpleName());
	
	@Resource
	private LearningProgressService learningProgressService;

	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<LearningProgressVO> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find learningProgress for studentId = {}",  studentId);
		List<LearningProgress> learningProgressList = learningProgressService.findByStudentId(studentId);
        return LearningProgressHandler.convertVOList(learningProgressList);
		
	}
    @RequestMapping(value = "/findStartedByStudentId", method = RequestMethod.GET)
    public List<LearningProgressVO> findStartedByStudentId(@RequestParam("studentId") long studentId) {
        logger.info("find learningProgress for studentId = {}",  studentId);
        List<LearningProgress> learningProgressList = learningProgressService.findStartedByStudentId(studentId);
        return LearningProgressHandler.convertVOList(learningProgressList);

    }
	
	@RequestMapping(value = "/findByStudentIdAndCourseId", method = RequestMethod.GET)
	public LearningProgress findByStudentId(@RequestParam("studentId") long studentId, @RequestParam("courseId") long courseId) {
		logger.info("Find learningProgress by studentId = {}, courseId={} ", studentId, courseId);
		return learningProgressService.findByStudentId(studentId, courseId);
	}
	
	@RequestMapping(value = "/findNotMajorByStudentId", method = RequestMethod.GET)
	public List<LearningProgress> findNotMajorByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("Find learningProgress by studentId = {}", studentId);
		return learningProgressService.findNotMajorByStudentId(studentId);
	}
	
	@RequestMapping(value = "/findMajorByStudentId", method = RequestMethod.GET)
	public List<LearningProgress> findMajorByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("Find learningProgress by studentId = {}", studentId);
		return learningProgressService.findMajorByStudentId(studentId);
	}
	
	@RequestMapping(value = "/findLeftClassHourByStudentIdWithoutTestClass", method = RequestMethod.GET)
	public List<LearningProgress> findLeftClassHourByStudentIdWithoutTestClass(@RequestParam("studentId") long studentId) {
		logger.info("Find learningProgress by studentId = {}", studentId);
		return learningProgressService.findLeftClassHourByStudentIdWithoutTestClass(studentId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public LearningProgress update(@RequestBody LearningProgress learningProgress) {		
		return learningProgressService.update(learningProgress);
	}

    @RequestMapping(value = "/resetPracticumLearningProgress" ,method = RequestMethod.PUT)
    public Response resetPracticumLearningProgress(long studentId){
        return learningProgressService.findPracticumByStudentId(studentId);
    }
	
	@RequestMapping(value = "/reSchedule", method = RequestMethod.PUT)
	public LearningProgress reSchedule(@RequestBody LearningProgress learningProgress){
		return learningProgressService.doReSchedule(learningProgress);
	}
	
}
