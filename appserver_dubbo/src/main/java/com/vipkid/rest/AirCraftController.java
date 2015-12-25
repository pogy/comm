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

import com.vipkid.model.AirCraft;
import com.vipkid.service.AirCraftService;

@RestController
@RequestMapping(value="/api/service/private/airCrafts")
public class AirCraftController {
	
	private Logger logger = LoggerFactory.getLogger(AirCraftController.class.getSimpleName());
	
	@Resource
	private AirCraftService airCraftService;
	
	@RequestMapping(value="/findByStudentId", method=RequestMethod.GET)
	public List<AirCraft> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.debug("find student info by id = {}", studentId);
		
		return airCraftService.findByStudentId(studentId);
	}
	
	@RequestMapping(value="/findCurrentByStudentId", method=RequestMethod.GET)
	public List<AirCraft> findCurrentByStudentId(@RequestParam("studentId") long studentId) {
		logger.debug("find student current airship by id = {}", studentId);
		return airCraftService.findCurrentByStudentId(studentId);
	}
	
	@RequestMapping(value="/findAircraftBySequenceAndStudentId", method=RequestMethod.GET)
	public AirCraft findAircraftBySequenceAndStudentId(@RequestParam("studentId") long studentId,@RequestParam("sequence") int sequence) {
		logger.debug("find Aircraft by id= {},sequence={}", studentId,sequence);
		return airCraftService.findAircraftBySequenceAndStudentId(studentId,sequence);
	}
	
	@RequestMapping(value="/update", method=RequestMethod.PUT)
	public AirCraft update(@RequestBody AirCraft aircraft) {
		logger.debug("update aircraft: {}", aircraft);
		
		airCraftService.update(aircraft);
		
		return aircraft;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public AirCraft create(@RequestBody AirCraft aircraft) {
		logger.debug("create aircraft: {}", aircraft);
		
		airCraftService.create(aircraft);
		
		return aircraft;
	}
	
	
	
}
