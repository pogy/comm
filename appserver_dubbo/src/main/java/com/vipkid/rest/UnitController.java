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

import com.vipkid.model.Course.Type;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Unit;
import com.vipkid.service.UnitService;

@RestController
@RequestMapping("/api/service/private/units")
public class UnitController {
private Logger logger = LoggerFactory.getLogger(UnitController.class.getSimpleName());
	
	@Resource
	private UnitService unitService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Unit find(@RequestParam("id") long id) {
		logger.info("find unit for id = {}", id);
		Unit unit = unitService.find(id);
		return unit;
	}
	
	@RequestMapping(value = "/findByCourseId", method = RequestMethod.GET)
	public List<Unit> findByCourseId(@RequestParam("courseId") long courseId) {
		logger.info("find units with params: courseId = {}", courseId);
		return unitService.findByCourseId(courseId);
	}
	
	@RequestMapping(value = "/findByLevelId", method = RequestMethod.GET)
	public List<Unit> findByLevelId(@RequestParam("levelId") long levelId) {
		logger.info("find levels with params: levelId = {}", levelId);
		return unitService.findByLevelId(levelId);
	}
	
	@RequestMapping(value = "/findByCourseType", method = RequestMethod.GET)
	public List<Unit> findByCourseType(@RequestParam("type") Type type) {
		logger.info("find units with params: type = {}", type);
		return unitService.findByCourseType(type);
	}
	
	@RequestMapping(value = "/findByCourseIdAndLevel", method = RequestMethod.GET)
	public List<Unit> findByCourseIdAndLevel(@RequestParam("courseId") long courseId, @RequestParam("level") Level level) {
		logger.info("find units with params: courseId = {} level = {} ", courseId, level);
		return unitService.findByCourseIdAndLevel(courseId, level);
	}
	
	// 以下两个方法都调用的service的同一个方法，是否应该精简？
	@RequestMapping(value = "/findStartedUnits", method = RequestMethod.GET)
	public List<Unit> findStartedUnits(@RequestParam("courseId") long courseId) {
		logger.info("find started units with params: courseId = {}", courseId);
		return unitService.findUnitsByStatus(OnlineClass.Status.FINISHED.toString(), courseId);
	}
	@RequestMapping(value = "/findStartingUnits", method = RequestMethod.GET)
	public List<Unit> findStartingUnits(@RequestParam("courseId") long courseId) {
		logger.info("find starting units with params: courseId = {}", courseId);
		return unitService.findUnitsByStatus(OnlineClass.Status.BOOKED.toString(), courseId);
	}
	
	@RequestMapping(value = "/findBySequenceRangeAndCourseId", method = RequestMethod.GET)
	public List<Unit> findBySequenceRangeAndCourseId(@RequestParam("firstSequence") int firstSequence, @RequestParam("lastSequence") int lastSequence, @RequestParam("courseId") long courseId){
		logger.info("find units with params: firstSequence = {}, lastSequence = {}, courseId = {}", firstSequence, lastSequence, courseId);
		return unitService.findBySequenceRangeAndCourseId(firstSequence, lastSequence, courseId);
	}
	
	@RequestMapping(value = "/findByFirstSequenceAndCourseId", method = RequestMethod.GET)
	public List<Unit> findByFirstSequenceAndCourseId(@RequestParam("firstSequence") int firstSequence, @RequestParam("courseId") long courseId){
		logger.info("find units with params: firstSequence = {}, courseId = {}", firstSequence, courseId);
		return unitService.findByFirstSequenceAndCourseId(firstSequence, courseId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Unit update(@RequestBody Unit unit) {
		logger.info("update unit: {}", unit);
		return unitService.update(unit);
	}
}
