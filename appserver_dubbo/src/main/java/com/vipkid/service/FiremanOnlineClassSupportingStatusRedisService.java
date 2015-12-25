package com.vipkid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.OnlineClass;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.exception.RedisException;
import com.vipkid.service.exception.RedisIOFailedException;
import com.vipkid.service.pojo.FiremanLogRedisView;
import com.vipkid.service.pojo.LongList;

@Service
public class FiremanOnlineClassSupportingStatusRedisService {
	private Logger logger = LoggerFactory.getLogger(FiremanOnlineClassSupportingStatusRedisService.class.getSimpleName());
	
	@Resource
	private OnlineClassRepository onlinClassRepository;
	
	private static final int timeout = 3600;
	
	private String createRedisKey(Long onlineClassId) {
		return "FIREMAN:" + onlineClassId;
	}
	
	public Response setTeacherHavingProblem(Long onlineClassId) {
		logger.error("[FIREMAN] set teacher having problem for onlineclass {}", onlineClassId);
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && !firemanLog.isTeacherHavingProblem()) {
				firemanLog.setTeacherHavingProblem(true);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(false); // we don't know whether student in the classroom, so we just say not.
				firemanLog.setTeacherInTheClassroom(true); // teacher must be in when he/she can submit a help request.
				firemanLog.setStudentHavingProblem(false);
				firemanLog.setTeacherHavingProblem(true);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public Response setTeacherResolvedProblem(Long onlineClassId) {
		logger.error("[FIREMAN] set teacher having problem for onlineclass {}", onlineClassId);
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && firemanLog.isTeacherHavingProblem()) {
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(false); // we don't know whether student in the classroom, so we just say not.
				firemanLog.setTeacherInTheClassroom(true); // teacher must be in when he/she can submit a help request.
				firemanLog.setStudentHavingProblem(false); 
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public Response setStudentHavingProblem(Long onlineClassId) {
		logger.error("[FIREMAN] set student having problem for onlineclass {}", onlineClassId);
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && !firemanLog.isStudentHavingProblem()) {
				firemanLog.setStudentHavingProblem(true);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(false); // we don't know whether student in the classroom, so we just say not.
				firemanLog.setTeacherInTheClassroom(true); // teacher must be in when he/she can submit a help request.
				firemanLog.setStudentHavingProblem(true);
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public Response setStudentResolvedProblem(Long onlineClassId) {
		logger.error("[FIREMAN] set student having problem for onlineclass {}", onlineClassId);

		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && firemanLog.isStudentHavingProblem()) {
				firemanLog.setStudentHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(false); // we don't know whether student in the classroom, so we just say not.
				firemanLog.setTeacherInTheClassroom(true); // teacher must be in when he/she can submit a help request.
				firemanLog.setStudentHavingProblem(false); 
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public Response setStudentInClassroom(Long onlineClassId) {
		logger.error("[FIREMAN] set student in the classroom for onlineclass {}", onlineClassId);
		
		OnlineClass onlineClass = onlinClassRepository.find(onlineClassId);
		
		if (onlineClass == null) {
			throw new RedisIOFailedException();
		}
		
		if (onlineClass.getStudentEnterClassroomDateTime() == null) {
			onlineClass.setStudentEnterClassroomDateTime(Calendar.getInstance().getTime());
			onlinClassRepository.updateWithException(onlineClass) ;
		}
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && !firemanLog.isStudentInTheClassroom()) {
				firemanLog.setStudentInTheClassroom(true);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(true);
				firemanLog.setTeacherInTheClassroom(false);
				firemanLog.setStudentHavingProblem(false); 
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public Response setTeacherInClassroom(Long onlineClassId) {
		logger.error("[FIREMAN] set student in the classroom for onlineclass {}", onlineClassId);
		
		OnlineClass onlineClass = onlinClassRepository.find(onlineClassId);
		
		if (onlineClass == null) {
			throw new RedisIOFailedException();
		}
		
		if (onlineClass.getTeacherEnterClassroomDateTime() == null) {
			onlineClass.setTeacherEnterClassroomDateTime(Calendar.getInstance().getTime());
			onlinClassRepository.updateWithException(onlineClass);
		}
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(onlineClassId));
			if (firemanLog != null && !firemanLog.isTeacherInTheClassroom()) {
				firemanLog.setTeacherInTheClassroom(true);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
			} else if (firemanLog == null) { // this should not happen
				firemanLog = new FiremanLogRedisView();
				firemanLog.setOnlineClassId(onlineClassId);
				firemanLog.setStudentInTheClassroom(false);
				firemanLog.setTeacherInTheClassroom(true);
				firemanLog.setStudentHavingProblem(false); 
				firemanLog.setTeacherHavingProblem(false);
				redisClient.setObject(createRedisKey(onlineClassId), firemanLog);
				redisClient.expire(createRedisKey(onlineClassId), timeout);
			}
		} catch (RedisException e) {
			throw new RedisIOFailedException();
		}
		
		return new Response(200);
	}
	
	public List<FiremanLogRedisView> list(LongList ids) {
		logger.error("[FIREMAN] looking for fireman log status for onlineclasses");
		
		try {
			RedisClient redisClient = RedisClient.getInstance();
			
			List<Long> onlineClassIds = ids.getIds();
			
			List<FiremanLogRedisView> firemanLogs = new ArrayList<FiremanLogRedisView>();

			for (Long id: onlineClassIds) {
				FiremanLogRedisView firemanLog = (FiremanLogRedisView)redisClient.getObject(createRedisKey(id));
				if (firemanLog == null) {
					firemanLog = new FiremanLogRedisView();
					firemanLog.setOnlineClassId(id);
					firemanLog.setStudentHavingProblem(false);
					firemanLog.setStudentInTheClassroom(false);
					firemanLog.setTeacherHavingProblem(false);
					firemanLog.setTeacherInTheClassroom(false);
				}
				firemanLogs.add(firemanLog);
			}
			
			return firemanLogs;
		} catch (RedisException re) {
			throw new RedisIOFailedException();
		}
	}
}
