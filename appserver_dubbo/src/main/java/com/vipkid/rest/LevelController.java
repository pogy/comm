package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.CourseLevel;
import com.vipkid.service.LevelService;


@RestController
@RequestMapping("/api/service/private/levels")
public class LevelController {
	private Logger logger = LoggerFactory.getLogger(LevelController.class.getSimpleName());

	@Resource
	private LevelService levelService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public CourseLevel find(@RequestParam("id") long id) {
		logger.debug("find courselevel for id = {}", id);
		
		return levelService.find(id);
	}

	@RequestMapping(value = "/findByCourseId", method = RequestMethod.GET)
	public List<CourseLevel> findByCourseId(@RequestParam("courseId") long courseId) {
		logger.debug("find courselevel for courseId = {}", courseId);
		return levelService.findByCourseId(courseId);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public CourseLevel update(@RequestBody CourseLevel level) {
		logger.info("update lesson: {}", level);
		return levelService.update(level);
	}
}
