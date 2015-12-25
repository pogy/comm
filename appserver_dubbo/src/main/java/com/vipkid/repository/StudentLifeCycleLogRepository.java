package com.vipkid.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Student;
import com.vipkid.model.StudentLifeCycleLog;

@Repository
public class StudentLifeCycleLogRepository extends BaseRepository<StudentLifeCycleLog>{

	private Logger logger = LoggerFactory.getLogger(StudentLifeCycleLogRepository.class);

	public List<StudentLifeCycleLog> findToBeRenewedMoreThanNintyDays(int toBeRenewedDucation) {
		logger.debug("find studentlogs with to be renewed for {} days", toBeRenewedDucation);
		
		String sql = "SELECT s FROM StudentLifeCycleLog s WHERE s.createdDateTime = (SELECT MAX(ss.createdDateTime) FROM StudentLifeCycleLog ss WHERE ss.student.id = s.student.id) "
				+ "AND s.student.lifeCycle = :lifeCycle AND s.createdDateTime < :someDaysAgo";
		TypedQuery<StudentLifeCycleLog> typedQuery = entityManager.createQuery(sql, StudentLifeCycleLog.class);
		typedQuery.setParameter("lifeCycle", Student.LifeCycle.TO_BE_RENEWED);
		Calendar toBeRenewedDucationDaysAgoCalendar = Calendar.getInstance();
		toBeRenewedDucationDaysAgoCalendar.add(Calendar.DATE, 0-toBeRenewedDucation);
		typedQuery.setParameter("someDaysAgo", toBeRenewedDucationDaysAgoCalendar.getTime());
		
		return typedQuery.getResultList();
	}
	
}
