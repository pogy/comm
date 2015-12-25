package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;

@Service
public class AirCraftThemeService {
	
	private Logger logger = LoggerFactory.getLogger(AirCraftThemeService.class.getSimpleName());
	
	@Resource
	private AirCraftThemeRepository airCraftThemeRepository;
	
	@Resource
	private AirCraftRepository airCraftRepository;
	
	public List<AirCraftTheme> findByAirCraftId(long aircraftId) {
		logger.debug("find student info by id = {}", aircraftId);
		return airCraftThemeRepository.findByAirCraftId(aircraftId);
	}
	
	public List<AirCraftTheme> findByAirCraftIdAndLevel(long aircraftId, int level) {
		logger.debug("find student info by id = {},level = {}", aircraftId,level);
		return airCraftThemeRepository.findByAirCraftIdAndLevel(aircraftId,level);
	}
	
	public AirCraftTheme findCurrentByStudentId(long studentId) {
		logger.debug("find student current airship by id = {}", studentId);
		
		List<AirCraft> airCrafts = airCraftRepository.findByStudentId(studentId);
		
		for(AirCraft airCraft : airCrafts){
			if(airCraftThemeRepository.findCurrentByAirCraftId(airCraft.getId()).size() > 0){
				 return airCraftThemeRepository.findCurrentByAirCraftId(airCraft.getId()).get(0);
			}
		}
		
		return null;
	}

	public List<AirCraftTheme> findCurrentByAirCraftId(long aircraftId) {
		logger.debug("find student current airship by id = {}", aircraftId);
		return airCraftThemeRepository.findCurrentByAirCraftId(aircraftId);
	}
	
	public AirCraftTheme update(AirCraftTheme aircraftTheme) {
		logger.debug("update aircraftTheme: {}", aircraftTheme);
		airCraftThemeRepository.update(aircraftTheme);
		return aircraftTheme;
	}

	public AirCraftTheme create(AirCraftTheme aircraftTheme) {
		logger.debug("create aircraftTheme: {}", aircraftTheme);
		airCraftThemeRepository.create(aircraftTheme);
		return aircraftTheme;
	}

	public long findStarsByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		return airCraftThemeRepository.findStarsByStudentIdAndTimeRange(studentId, startDate, endDate);
	}
	
}
