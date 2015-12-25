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

import com.vipkid.model.LearningCycle;
import com.vipkid.service.LearningCycleService;

@RestController
@RequestMapping("/api/service/private/learningCycles")
public class LearningCycleController {
	private Logger logger = LoggerFactory.getLogger(LearningCycleController.class.getSimpleName());

	@Resource
	private LearningCycleService learningCycleService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public LearningCycle find(@RequestParam("id") long id) {
		logger.debug("find learningCycle for id = {}", id);
		return learningCycleService.find(id);
	}
	
	@RequestMapping(value = "/findByUnitId", method = RequestMethod.GET)
	public List<LearningCycle> findByUnitId(@RequestParam("unitId") long unitId) {
		logger.debug("find learningCycles for unitId = {}", unitId);
		return learningCycleService.findByUnitId(unitId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public LearningCycle update(@RequestBody LearningCycle learningCycle) {
		logger.debug("update learningCycle: {}", learningCycle);
		return learningCycleService.update(learningCycle);
		
	}
}
