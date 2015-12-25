package com.vipkid.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.TrialThreshold;

@Repository
public class TrialThresholdRepository extends BaseRepository<TrialThreshold> {

	public TrialThresholdRepository() {
		super(TrialThreshold.class);
	}

	public TrialThreshold findByTimePoint(Date time) {
		String sql = "SELECT t FROM TrialThreshold t where t.timePoint = :time";
		TypedQuery<TrialThreshold> typedQuery = entityManager.createQuery(sql, TrialThreshold.class);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		if (calendar.get(Calendar.MINUTE) < 30) {
			calendar.set(Calendar.MINUTE, 0);
		} else {
			calendar.set(Calendar.MINUTE, 30);
		}
		typedQuery.setParameter("time", calendar.getTime());
		
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public void deleteByDate(Calendar calendar) {
		String sql = "DELETE FROM TrialThreshold t WHERE t.timePoint >= :dayBeginning AND t.timePoint <= :dayEnding";
		TypedQuery<TrialThreshold> typedQuery = entityManager.createQuery(sql, TrialThreshold.class);
		Calendar dayBeginningCalendar = Calendar.getInstance();
		dayBeginningCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		dayBeginningCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		dayBeginningCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
		dayBeginningCalendar.set(Calendar.HOUR_OF_DAY, 0);
		dayBeginningCalendar.set(Calendar.MINUTE, 0);
		dayBeginningCalendar.set(Calendar.SECOND, 0);
		dayBeginningCalendar.set(Calendar.MILLISECOND, 0);
		Calendar dayEndingCalendar = Calendar.getInstance();
		dayEndingCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		dayEndingCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		dayEndingCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
		dayEndingCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dayEndingCalendar.set(Calendar.MINUTE, 59);
		dayEndingCalendar.set(Calendar.SECOND, 59);
		dayEndingCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("dayBeginning", dayBeginningCalendar.getTime());
		typedQuery.setParameter("dayEnding", dayEndingCalendar.getTime());

		typedQuery.executeUpdate();
	}

	public List<TrialThreshold> getTimeByDate(Date date) {
		String sql = "SELECT t FROM TrialThreshold t WHERE DATE(t.timePoint) = DATE(:date)";
		TypedQuery<TrialThreshold> typedQuery = entityManager.createQuery(sql, TrialThreshold.class);
		typedQuery.setParameter("date", date);
		
		return typedQuery.getResultList();
	}

	public List<TrialThreshold> getByTimeRange(Date start, Date end) {
		String sql = "SELECT t FROM TrialThreshold t WHERE t.timePoint >= :start AND t.timePoint <= :end";
		TypedQuery<TrialThreshold> typedQuery = entityManager.createQuery(sql, TrialThreshold.class);
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		startCalendar.set(Calendar.SECOND, 0);
		startCalendar.set(Calendar.MILLISECOND, 0);
		endCalendar.setTime(end);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("start", startCalendar.getTime());
		typedQuery.setParameter("end", endCalendar.getTime());
		
		return typedQuery.getResultList();
	}

}
