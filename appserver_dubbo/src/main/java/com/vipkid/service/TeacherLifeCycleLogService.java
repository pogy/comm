package com.vipkid.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.User;
import com.vipkid.repository.TeacherLifeCycleLogRepository;
import com.vipkid.repository.TeacherRepository;

@Service
public class TeacherLifeCycleLogService {

	private Logger logger = LoggerFactory.getLogger(TeacherLifeCycleLogService.class.getSimpleName());


	@Resource
	private TeacherLifeCycleLogRepository teacherLifeCycleLogRepository;

	@Resource
	private TeacherRepository teacherRepository;

	public TeacherLifeCycleLog update(TeacherLifeCycleLog teacherLifeCycleLog) {
		return teacherLifeCycleLogRepository.update(teacherLifeCycleLog);
	}
	           
	public Date getPassedPreviousPhaseDateTime(long teacherId, LifeCycle lifeCycle) {
		logger.debug("get passedPreviousPhaseDateTime with params: teacherId = {}, lifeCycle = {}.",teacherId,lifeCycle);
		return teacherLifeCycleLogRepository.getPassedPreviousPhaseDateTime(teacherId,lifeCycle);
	}



	public TeacherLifeCycleLog getOperateInfoWithTeacherIdCurrentPhase(long teacherId, LifeCycle lifeCycle) {
		logger.debug("get getOperateInfoWithTeacherIdCurrentPhase with params: teacherId = {}, lifeCycle = {}.",teacherId,lifeCycle);
		return teacherLifeCycleLogRepository.getOperateInfoWithTeacherIdCurrentPhase(teacherId,lifeCycle);
	}
	
	public List<User> getOperatorOptions(LifeCycle lifeCycle) {
		logger.debug("getOperatorOptions with params: lifeCycle = {}.",lifeCycle);
		return teacherLifeCycleLogRepository.getOperatorOptions(lifeCycle);
	}
}
