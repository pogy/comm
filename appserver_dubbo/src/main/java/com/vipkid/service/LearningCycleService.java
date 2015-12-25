package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.LearningCycle;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.repository.LearningCycleRepository;
import com.vipkid.security.SecurityService;

@Service
public class LearningCycleService {
	private Logger logger = LoggerFactory.getLogger(LearningCycleService.class.getSimpleName());

	@Resource
	private LearningCycleRepository learningCycleRepository;
	
	@Resource
	private SecurityService securityService;
	
	public LearningCycle find(long id) {
		logger.debug("find learningCycle for id = {}", id);
		return learningCycleRepository.find(id);
	}
	
	public List<LearningCycle> findByUnitId(long unitId) {
		logger.debug("find learningCycles for unitId = {}", unitId);
		return learningCycleRepository.findByUnitId(unitId);
	}
	
	public LearningCycle update(LearningCycle learningCycle) {
		logger.debug("update learningCycle: {}", learningCycle);
		learningCycleRepository.update(learningCycle);
		
		StringBuffer strbuf = new StringBuffer(learningCycle.getSerialNumber());//序列号不会更改，所以以他为基准。
		securityService.logAudit(Level.INFO, Category.LEARNINGCYCLE_BASIC_INFO_UPDATE, "Update: The "+strbuf.toString()+" LearningCycle has been updated！" );
		
		
		return learningCycle;
	}
}
