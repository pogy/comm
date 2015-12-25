package com.vipkid.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Parent;
import com.vipkid.repository.UpdateStarsRepository;

@Service
public class UpdateStarsService {
	
	@Resource
	private UpdateStarsRepository updateStarsRepository;
	
	private Logger logger = LoggerFactory.getLogger(UpdateStarsService.class.getSimpleName());
	
	public Parent findParentByMobile(String mobile){
		logger.info("findParentByMobile");
		return updateStarsRepository.findParentByMobile(mobile);
		
	}
	public void updateStarsByStudentId(long id,int num){
		logger.info("updateStarsByStudentId");
		updateStarsRepository.updateStarsByStudentId(id, num);
	}

}
