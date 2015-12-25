package com.vipkid.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.AirCraftTheme;
import com.vipkid.service.AirCraftService;
import com.vipkid.service.AirCraftThemeService;

@RestController
@RequestMapping(value="/api/service/private/airCraftThemes")
public class AirCraftThemeController {
	
	private Logger logger = LoggerFactory.getLogger(AirCraftThemeController.class.getSimpleName());
	
	@Resource
	private AirCraftThemeService airCraftThemeService;
	
	@Resource
	private AirCraftService airCraftService;
	
	@RequestMapping(value="/findByAirCraftId", method=RequestMethod.GET)
	public List<AirCraftTheme> findByAirCraftId(@RequestParam("aircraftId") long aircraftId) {
		logger.info("find student info by id = {}", aircraftId);
		return airCraftThemeService.findByAirCraftId(aircraftId);
	}
	
	@RequestMapping(value="/findByAirCraftIdAndLevel", method=RequestMethod.GET)
	public List<AirCraftTheme> findByAirCraftIdAndLevel(@RequestParam("aircraftId") long aircraftId,@RequestParam("level") int level) {
		logger.info("find student info by id = {},level = {}", aircraftId,level);
		return airCraftThemeService.findByAirCraftIdAndLevel(aircraftId,level);
	}
	
	@RequestMapping(value="/findCurrentByStudentId", method=RequestMethod.GET)
	public AirCraftTheme findCurrentByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find student current airship by id = {}", studentId);
		
		return airCraftThemeService.findCurrentByStudentId(studentId);
	}
	
	@RequestMapping(value="/findCurrentByAirCraftId", method=RequestMethod.GET)
	public List<AirCraftTheme> findCurrentByAirCraftId(@RequestParam("aircraftId") long aircraftId) {
		logger.info("find student current airship by id = {}", aircraftId);
		return airCraftThemeService.findCurrentByAirCraftId(aircraftId);
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public AirCraftTheme update(@RequestBody AirCraftTheme aircraftTheme) {
		logger.info("update aircraftTheme: {}", aircraftTheme);
		airCraftThemeService.update(aircraftTheme);
		return aircraftTheme;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public AirCraftTheme create(@RequestBody AirCraftTheme aircraftTheme) {
		logger.info("create aircraftTheme: {}", aircraftTheme);
		aircraftTheme.setCreateDateTime(new Date());
		airCraftThemeService.create(aircraftTheme);
		return aircraftTheme;
	}
	
}
