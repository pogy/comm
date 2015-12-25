package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.MarketActivity;
import com.vipkid.model.Medal;
import com.vipkid.repository.MedalRepository;

@Service
public class MedalService {

	private Logger logger = LoggerFactory.getLogger(StudentService.class.getSimpleName());
	
	@Resource
	private MedalRepository medalRepository;
	
	public Medal find(long id){
		return medalRepository.find(id);
	}
	
	public List<Medal> findByStudentId(long studentId){
		logger.debug("find StudentId: {}", studentId);
		return medalRepository.findByStudentId(studentId);
	}
	
	public List<Medal> findByStudentIdAndWellcome(long studentId, MarketActivity activity){
		logger.debug("find StudentId: {} activity: {}", studentId,activity);
		return medalRepository.findByStudentIdAndWellcome(studentId,activity);
	}
	
	public Medal update(Medal medal) {
		logger.debug("update medal: {}", medal);
		
		medalRepository.update(medal);
		
		return medal;
	}
	
	public Medal create(Medal medal) {
		logger.debug("create medal: {}", medal);
		
		medalRepository.create(medal);
		
		return medal;
	}
	
	public long count(long studentId){
		logger.info("find medal count: {}", studentId);
		return medalRepository.count(studentId);
	}
	
}
