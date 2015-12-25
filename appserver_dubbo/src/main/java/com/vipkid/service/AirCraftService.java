package com.vipkid.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.service.pojo.AirCraftThemeView;

@Service
public class AirCraftService {
	
	private Logger logger = LoggerFactory.getLogger(AirCraftService.class.getSimpleName());
	
	@Resource
	private AirCraftRepository airCraftRepository;
	
	@Resource
	private AirCraftThemeRepository airCraftThemeAccessor;

	public List<AirCraft> findByStudentId(long studentId) {
		logger.debug("find student info by id = {}", studentId);
		List<AirCraft> aircrafts = airCraftRepository.findByStudentId(studentId);
		for (AirCraft airCraft : aircrafts){
			List<AirCraftTheme> aircraftThemes = airCraftThemeAccessor.findByAirCraftId(airCraft.getId());
			List<AirCraftThemeView> aircraftThemeViews = new ArrayList<AirCraftThemeView>();
			for(AirCraftTheme airCraftTheme : aircraftThemes){
				AirCraftThemeView airCraftThemeView = new AirCraftThemeView();
				airCraftThemeView.setName(airCraftTheme.getName());
				airCraftThemeView.setIntroduction(airCraftTheme.getIntroduction());
				airCraftThemeView.setLevel(airCraftTheme.getLevel());
				airCraftThemeView.setPrice(airCraftTheme.getPrice());
				airCraftThemeView.setUrl(airCraftTheme.getUrl());
				airCraftThemeView.setCurrent(airCraftTheme.isCurrent());
				aircraftThemeViews.add(airCraftThemeView);
			}
			airCraft.setAirCraftThemes(aircraftThemeViews);
		}
		
		return aircrafts;
	}
	
	public List<AirCraft> findCurrentByStudentId(long studentId) {
		logger.debug("find student current airship by id = {}", studentId);
		return airCraftRepository.findCurrentByStudentId(studentId);
	}

	public AirCraft findAircraftBySequenceAndStudentId(long studentId, int sequence) {
		logger.debug("find Aircraft by id= {},sequence={}", studentId,sequence);
		List<AirCraft> aircraftList = airCraftRepository.findAircraftBySequenceAndStudentId(studentId,sequence);
		
		if(aircraftList != null && aircraftList.size() == 1){
			return aircraftList.get(0);
		}
		else {
			throw new IllegalStateException("The aircraft can not be nullor mutiple");
		}
		
	}

	public AirCraft update(AirCraft aircraft) {
		logger.debug("update aircraft: {}", aircraft);
		
		airCraftRepository.update(aircraft);
		
		return aircraft;
	}

	public AirCraft create(AirCraft aircraft) {
		logger.debug("create aircraft: {}", aircraft);
		
		airCraftRepository.create(aircraft);
		
		return aircraft;
	}
	
}
