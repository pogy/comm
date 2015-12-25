package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.ItTest;
import com.vipkid.repository.ItTestRepository;
import com.vipkid.security.SecurityService;

@Service
public class ItTestService {
	private Logger logger = LoggerFactory.getLogger(ItTestService.class.getSimpleName());
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private ItTestRepository itTestRepository;	
	
	public ItTest find(long id) {
		logger.debug("find ItTest for id = {}", id);
		return itTestRepository.find(id);
	}
	
	public List<ItTest> findByFamilyId(long familyId) {
		logger.debug("find ItTest for familyId = {}", familyId);
		return itTestRepository.findByFamilyId(familyId);
	}
	
	public List<ItTest> findByTeacherId(long teacherId) {
		logger.debug("find ItTest for teacherId = {}", teacherId);
		return itTestRepository.findByTeacherId(teacherId);
	}
	
	public List<ItTest> findByStudentId(long studentId) {
		logger.debug("find ItTest for studentId = {}", studentId);
		return itTestRepository.findByStudentId(studentId);
	}
}
