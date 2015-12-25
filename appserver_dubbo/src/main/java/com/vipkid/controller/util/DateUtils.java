package com.vipkid.controller.util;

import java.util.Date;

import com.vipkid.controller.parent.model.Week;
import com.vipkid.util.DateTimeUtils;

public class DateUtils {
	
	public static Week getThisWeek(){
		Week week = new Week();
		week.setMonday(DateTimeUtils.format(DateTimeUtils.getThisMonday(), DateTimeUtils.DATE_FORMAT4));
		
		Date day2 = DateTimeUtils.getNextDay(DateTimeUtils.getThisMonday());
		week.setTuesday(DateTimeUtils.format(day2, DateTimeUtils.DATE_FORMAT4));
		
		Date day3 = DateTimeUtils.getNextDay(day2);
		week.setWednesday(DateTimeUtils.format(day3, DateTimeUtils.DATE_FORMAT4));
		
		Date day4 = DateTimeUtils.getNextDay(day3);
		week.setThursday(DateTimeUtils.format(day4, DateTimeUtils.DATE_FORMAT4));
		
		Date day5 = DateTimeUtils.getNextDay(day4);
		week.setFriday(DateTimeUtils.format(day5, DateTimeUtils.DATE_FORMAT4));
		
		Date day6 = DateTimeUtils.getNextDay(day5);
		week.setSaturday(DateTimeUtils.format(day6, DateTimeUtils.DATE_FORMAT4));
		
		Date day7 = DateTimeUtils.getNextDay(day6);
		week.setSunday(DateTimeUtils.format(day7, DateTimeUtils.DATE_FORMAT4));
		return week;
	}
	
	public static Week getLaskWeek(){
		Week week = new Week();
		week.setMonday(DateTimeUtils.format(DateTimeUtils.getLastMonday(), DateTimeUtils.DATE_FORMAT4));
		
		Date day2 = DateTimeUtils.getNextDay(DateTimeUtils.getLastMonday());
		week.setTuesday(DateTimeUtils.format(day2, DateTimeUtils.DATE_FORMAT4));
		
		Date day3 = DateTimeUtils.getNextDay(day2);
		week.setWednesday(DateTimeUtils.format(day3, DateTimeUtils.DATE_FORMAT4));
		
		Date day4 = DateTimeUtils.getNextDay(day3);
		week.setThursday(DateTimeUtils.format(day4, DateTimeUtils.DATE_FORMAT4));
		
		Date day5 = DateTimeUtils.getNextDay(day4);
		week.setFriday(DateTimeUtils.format(day5, DateTimeUtils.DATE_FORMAT4));
		
		Date day6 = DateTimeUtils.getNextDay(day5);
		week.setSaturday(DateTimeUtils.format(day6, DateTimeUtils.DATE_FORMAT4));
		
		Date day7 = DateTimeUtils.getNextDay(day6);
		week.setSunday(DateTimeUtils.format(day7, DateTimeUtils.DATE_FORMAT4));
		return week;
	}
	
	public static Week getNextWeek(){
		Week week = new Week();
		week.setMonday(DateTimeUtils.format(DateTimeUtils.getNextMonday(), DateTimeUtils.DATE_FORMAT4));
		
		Date day2 = DateTimeUtils.getNextDay(DateTimeUtils.getNextMonday());
		week.setTuesday(DateTimeUtils.format(day2, DateTimeUtils.DATE_FORMAT4));
		
		Date day3 = DateTimeUtils.getNextDay(day2);
		week.setWednesday(DateTimeUtils.format(day3, DateTimeUtils.DATE_FORMAT4));
		
		Date day4 = DateTimeUtils.getNextDay(day3);
		week.setThursday(DateTimeUtils.format(day4, DateTimeUtils.DATE_FORMAT4));
		
		Date day5 = DateTimeUtils.getNextDay(day4);
		week.setFriday(DateTimeUtils.format(day5, DateTimeUtils.DATE_FORMAT4));
		
		Date day6 = DateTimeUtils.getNextDay(day5);
		week.setSaturday(DateTimeUtils.format(day6, DateTimeUtils.DATE_FORMAT4));
		
		Date day7 = DateTimeUtils.getNextDay(day6);
		week.setSunday(DateTimeUtils.format(day7, DateTimeUtils.DATE_FORMAT4));
		return week;
	}
}
