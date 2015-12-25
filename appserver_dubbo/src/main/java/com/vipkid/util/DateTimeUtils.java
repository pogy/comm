package com.vipkid.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtils {
	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class.getSimpleName());

	public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	public final static DateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
	public final static DateFormat DATE_FORMAT3 = new SimpleDateFormat("MMM dd", Locale.US);
	public final static DateFormat DATE_FORMAT4 = new SimpleDateFormat("MM-dd", Locale.getDefault());
	public final static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
	public final static DateFormat TIME_FORMAT2 = new SimpleDateFormat("hh:mma", Locale.US);
	public final static DateFormat TIME_FORMAT3 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
	public final static DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public final static DateFormat DATETIME_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	public final static DateFormat DATETIME_FORMAT3 = new SimpleDateFormat("MMM dd, hh:mma", Locale.US);
	public final static DateFormat DATETIME_FORMAT4 = new SimpleDateFormat("EEEE, MMM dd, hh:mma", Locale.US);

	public static Date parse(String string, DateFormat format) {
		Date date = null;

		try {
			date = format.parse(string);
		} catch (ParseException e) {
			if (format.equals(DATE_FORMAT) || format.equals(DATE_FORMAT2)) {
				logger.error("Error when parsing date from string: " + string);
			} else if (format.equals(TIME_FORMAT)) {
				logger.error("Error when parsing time from string: " + string);
			} else if (format.equals(DATETIME_FORMAT) || format.equals(DATETIME_FORMAT2) ||format.equals(DATETIME_FORMAT3)) {
				logger.error("Error when parsing datetime from string: " + string);
			}
		}

		return date;
	}

	public static String format(Date date, DateFormat format) {
		return (date == null ? null : format.format(date));
	}
	
	public static String format(Date date, DateFormat format, TimeZone tz) {
		if (format!= null && tz != null){
			format.setTimeZone(tz);
		}
		return (date == null ? null : format.format(date));
	}
	
	public static Date getNthMinutesLater(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, n);
		return calendar.getTime();
	}
	
	public static Date getNthMinutesLater(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, n);
		return calendar.getTime();
	}
	
	public static Date getBeginningOfThisMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getBeginningOfNextMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getBeginningOfNextNextMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getBeginningOfTheDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getNthHourLater(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, n);
		return calendar.getTime();
	}

	public static Date getTomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getTomorrow(int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, interval);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getToday(int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, interval);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getPrevMinutes(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -offset);
		return calendar.getTime();
	}
	
	public static Date getYesterday(int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, interval);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getTheDayAfterTomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getNextDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	public static Date getDateByOffset(int dayOffset, int minuteOffset){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, dayOffset);
		calendar.add(Calendar.MINUTE, minuteOffset);
		return calendar.getTime();
	}
	
	public static Date getNextNthMonday(int n) {
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}
		calendar.add(Calendar.WEEK_OF_YEAR, n);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getLastMonday() {
		Calendar calendar = Calendar.getInstance();
		
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.add(Calendar.DATE, -7);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getThisMonday() {
		Calendar calendar = Calendar.getInstance();
		
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getNextMonday() {
		Calendar calendar = Calendar.getInstance();

		if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getNextNextMonday() {
		Calendar calendar = Calendar.getInstance();
		
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		} else {
			calendar.add(Calendar.WEEK_OF_YEAR, 2);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Boolean isSameWeek(Date dateOne, Date dateTwo) {
		Calendar calOne = Calendar.getInstance();
		calOne.setTime(dateOne);
		Calendar calTwo = Calendar.getInstance();
		calTwo.setTime(dateTwo);
		//判断是否是周日，如果是，则其周再-1
		if(calOne.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calOne.add(Calendar.DATE, -1);
		}
		if(calTwo.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calTwo.add(Calendar.DATE, -1);
		}
		if(calOne.get(Calendar.YEAR) == calTwo.get(Calendar.YEAR) &&
				calOne.get(Calendar.WEEK_OF_YEAR) == calTwo.get(Calendar.WEEK_OF_YEAR))
		{
			return true;
		}
		int subYear = calOne.get(Calendar.WEEK_OF_YEAR) - calTwo.get(Calendar.WEEK_OF_YEAR);
		if(subYear == 1 && calTwo.get(Calendar.MONTH)==11) {
			if(calOne.get(Calendar.WEEK_OF_YEAR) == calTwo.get(Calendar.WEEK_OF_YEAR)) {
				return true;
			}
		} else if( subYear == -1 && calOne.get(Calendar.MONTH) == 11) {
			if(calOne.get(Calendar.WEEK_OF_YEAR) == calTwo.get(Calendar.WEEK_OF_YEAR)) {
				return true;
			}
		}
		return false;
	}
	
	public static Date getBeginningOfTheDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getTheDayAfterYesterday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	public static Date getYearOffset(Date date,int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, offset);
		return calendar.getTime();
	}

}
