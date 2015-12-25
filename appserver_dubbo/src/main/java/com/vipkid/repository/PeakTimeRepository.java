package com.vipkid.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.PeakTime;

@Repository
public class PeakTimeRepository extends BaseRepository<PeakTime> {

	public PeakTimeRepository() {
		super(PeakTime.class);
	}

	public PeakTime findByTimePoint(Date time) {
		String sql = "SELECT p FROM PeakTime p where p.timePoint = :time";
		TypedQuery<PeakTime> typedQuery = entityManager.createQuery(sql, PeakTime.class);
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
		String sql = "DELETE FROM PeakTime p WHERE p.timePoint >= :dayBeginning AND p.timePoint <= :dayEnding";
		TypedQuery<PeakTime> typedQuery = entityManager.createQuery(sql, PeakTime.class);
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

	public List<PeakTime> getTimeByDate(Date date) {
		String sql = "SELECT p FROM PeakTime p WHERE DATE(p.timePoint) = Date(:date)";
		TypedQuery<PeakTime> typedQuery = entityManager.createQuery(sql, PeakTime.class);
		typedQuery.setParameter("date", date);
		
		return typedQuery.getResultList();
	}

	public List<PeakTime> getByTimeRange(Date start, Date end) {
		String sql = "SELECT p FROM PeakTime p WHERE p.timePoint >= :start AND p.timePoint <= :end";
		TypedQuery<PeakTime> typedQuery = entityManager.createQuery(sql, PeakTime.class);
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
