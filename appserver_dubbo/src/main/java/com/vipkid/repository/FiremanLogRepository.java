package com.vipkid.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.FiremanLog;
import com.vipkid.model.FiremanLog.Event;

@Repository
public class FiremanLogRepository extends BaseRepository<FiremanLog> {

	private Logger logger = LoggerFactory.getLogger(FiremanLogRepository.class);

	public FiremanLog findRecentLogByOnlineClassIdAndEvent(long onlineClassId, Event event) {
		logger.debug("FiremanLogAccessor.findRecentLogByOnlineClassIdAndEvent");
		
		String sql = "SELECT l FROM FiremanLog l WHERE l.onlineClass.id = :onlineClassId AND l.event = :event ORDER BY l.createdDateTime DESC";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("event", event);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults(1);
		
		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList().get(0);
		}
		return null;
	}
	
	public List<FiremanLog> findOnlineClassSupportingStatus(){
		logger.debug("FiremanLogAccessor.findOnlineClassSupportingStatus");		
		
		String sql = "SELECT f from FiremanLog f WHERE f.status = :status AND f.createdDateTime BETWEEN :startDateTime AND :endDateTime AND f.onlineClass.scheduledDateTime BETWEEN :startDateTime AND :endScheduleTime ORDER BY f.createdDateTime DESC";
//		String sql = "SELECT f from FiremanLog f WHERE f.status = :status AND f.createdDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY f.createdDateTime DESC";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("status", FiremanLog.Status.RESOLVING);
		
		Calendar startDateTimeCalendar = Calendar.getInstance();
		Calendar endDateTimeCalendar = Calendar.getInstance();
		Calendar endScheduleTimeCalendar = Calendar.getInstance();
		int hour = startDateTimeCalendar.get(Calendar.HOUR);
		
		if(startDateTimeCalendar.get(Calendar.MINUTE) < 25){
			startDateTimeCalendar.set(Calendar.HOUR,hour-1);
			startDateTimeCalendar.set(Calendar.MINUTE,30);
			startDateTimeCalendar.set(Calendar.SECOND,0);
			startDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endDateTimeCalendar.set(Calendar.MINUTE,30);
			endDateTimeCalendar.set(Calendar.SECOND,0);
			endDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endScheduleTimeCalendar.set(Calendar.MINUTE,0);
			endScheduleTimeCalendar.set(Calendar.SECOND,0);
			endScheduleTimeCalendar.set(Calendar.MILLISECOND,0);
		}
		else if(startDateTimeCalendar.get(Calendar.MINUTE)<55 && startDateTimeCalendar.get(Calendar.MINUTE)>=25){
			startDateTimeCalendar.set(Calendar.MINUTE, 0);
			startDateTimeCalendar.set(Calendar.SECOND,0);
			startDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endDateTimeCalendar.set(Calendar.HOUR, hour+1);
			endDateTimeCalendar.set(Calendar.MINUTE, 0);
			endDateTimeCalendar.set(Calendar.SECOND,0);
			endDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endScheduleTimeCalendar.set(Calendar.MINUTE,30);
			endScheduleTimeCalendar.set(Calendar.SECOND,0);
			endScheduleTimeCalendar.set(Calendar.MILLISECOND,0);
		}
		else if(startDateTimeCalendar.get(Calendar.MINUTE)>=55){
			startDateTimeCalendar.set(Calendar.MINUTE, 30);
			startDateTimeCalendar.set(Calendar.SECOND,0);
			startDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endDateTimeCalendar.set(Calendar.HOUR, hour+1);
			endDateTimeCalendar.set(Calendar.MINUTE, 30);
			endDateTimeCalendar.set(Calendar.SECOND,0);
			endDateTimeCalendar.set(Calendar.MILLISECOND,0);
			endScheduleTimeCalendar.set(Calendar.HOUR, hour+1);
			endScheduleTimeCalendar.set(Calendar.MINUTE, 0);
			endScheduleTimeCalendar.set(Calendar.SECOND,0);
			endScheduleTimeCalendar.set(Calendar.MILLISECOND,0);
		}
		
		typedQuery.setParameter("startDateTime", startDateTimeCalendar.getTime());
		typedQuery.setParameter("endDateTime", endDateTimeCalendar.getTime());
		typedQuery.setParameter("endScheduleTime", endScheduleTimeCalendar.getTime());
		return typedQuery.getResultList();
	}
	
	public FiremanLog findStudentFiremanLogByOnlineClassId(long onlineClassId){
		logger.debug("FiremanLogAccessor.findStudentFiremanLogByOnlineClassId");
		String sql = "SELECT f from FiremanLog f WHERE f.onlineClass.id = :onlineClassId AND f.event = :event AND f.status = :status";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("event", Event.STUDENT_NEED_HELP);
		typedQuery.setParameter("status", FiremanLog.Status.RESOLVING);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public FiremanLog findTeacherFiremanLogByOnlineClassId(long onlineClassId){
		logger.debug("FiremanLogAccessor.findTeacherFiremanLogByOnlineClassId");
		String sql = "SELECT f from FiremanLog f WHERE f.onlineClass.id = :onlineClassId AND f.event = :event AND f.status = :status";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("event", Event.TEACHER_NEED_HELP);
		typedQuery.setParameter("status", FiremanLog.Status.RESOLVING);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public FiremanLog findFiremanLogByOnlineClassIdAndStudentNotEnter(long onlineClassId){
		logger.debug("FiremanLogAccessor.findFiremanLogByOnlineClassIdAndEvent");
		String sql = "SELECT f from FiremanLog f WHERE f.onlineClass.id = :onlineClassId AND f.event = :event";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("event", Event.STUDENT_NOT_ENTER);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public FiremanLog findFiremanLogByOnlineClassIdAndTeacherNotEnter(long onlineClassId){
		logger.debug("FiremanLogAccessor.findFiremanLogByOnlineClassIdAndEvent");
		String sql = "SELECT f from FiremanLog f WHERE f.onlineClass.id = :onlineClassId AND f.event = :event";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("event", Event.TEACHER_NOT_ENTER);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public FiremanLog findFiremanLogById(long Id){
		logger.debug("FiremanLogAccessor.findFiremanLogById");
		String sql = "SELECT f from FiremanLog f WHERE f.id = :Id";
		TypedQuery<FiremanLog> typedQuery = entityManager.createQuery(sql, FiremanLog.class);
		typedQuery.setParameter("Id", Id);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
