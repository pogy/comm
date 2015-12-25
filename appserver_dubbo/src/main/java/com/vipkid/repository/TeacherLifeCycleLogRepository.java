package com.vipkid.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.User;

@Repository
public class TeacherLifeCycleLogRepository extends BaseRepository<TeacherLifeCycleLog> {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(TeacherLifeCycleLogRepository.class.getSimpleName());
	
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private OnlineClassRepository OnlineClassRepository;

	public TeacherLifeCycleLogRepository() {
		super(TeacherLifeCycleLog.class);
	}
	
	public Date getPassedPreviousPhaseDateTime(long teacherId, LifeCycle currentPhase){
		logger.debug("get passedPreviousPhaseDateTime with params: teacherId = {}, lifeCycle = {}.",teacherId,currentPhase);
		String sql = "SELECT tll FROM TeacherLifeCycleLog tll WHERE tll.teacher.id = :teacherId AND tll.toStatus = :toStatus ORDER BY tll.createDateTime DESC";
		TypedQuery<TeacherLifeCycleLog> typedQuery = entityManager.createQuery(sql, TeacherLifeCycleLog.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("toStatus", currentPhase.toString());
		List<TeacherLifeCycleLog> TeacherLifeCycleLogs = typedQuery.getResultList();
		if(TeacherLifeCycleLogs.isEmpty()){
			return null;
		}else{
			return TeacherLifeCycleLogs.get(0).getCreateDateTime();
		}
	}

	public TeacherLifeCycleLog getOperateInfoWithTeacherIdCurrentPhase(	long teacherId, LifeCycle lifeCycle ) {
		logger.debug("getOperateInfoWithTeacherIdCurrentPhase with params: teacherId = {}, lifeCycle = {}.",teacherId,lifeCycle);
		String sql = "SELECT tll FROM TeacherLifeCycleLog tll WHERE tll.teacher.id = :teacherId AND tll.toStatus = :toStatus ORDER BY tll.createDateTime DESC";
		TypedQuery<TeacherLifeCycleLog> typedQuery = entityManager.createQuery(sql, TeacherLifeCycleLog.class);
		typedQuery.setParameter("teacherId", teacherId);
		typedQuery.setParameter("toStatus", lifeCycle.toString());
		List<TeacherLifeCycleLog> TeacherLifeCycleLogs = typedQuery.getResultList();
		if(TeacherLifeCycleLogs.isEmpty()){
			return null;
		}else{
			return TeacherLifeCycleLogs.get(0);
		}
	}

	public List<User> getOperatorOptions(LifeCycle lifeCycle) {
		logger.debug("getOperatorOptions with params: lifeCycle = {}.", lifeCycle);
		String sql = "SELECT DISTINCT tll.operator FROM TeacherLifeCycleLog tll WHERE tll.toStatus = :toStatus ORDER BY tll.createDateTime DESC";
		
		TypedQuery<User> typedQuery = entityManager.createQuery(sql, User.class);
		typedQuery.setParameter("toStatus", lifeCycle.toString());
		List<User> TeacherLifeCycleLogs = typedQuery.getResultList();
		if(TeacherLifeCycleLogs.isEmpty()){
			return null;
		}else{
			return TeacherLifeCycleLogs;
		}
	}

		
}
