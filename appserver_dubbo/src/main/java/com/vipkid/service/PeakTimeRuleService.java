package com.vipkid.service;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.PeakTime;
import com.vipkid.model.PeakTimeRule;
import com.vipkid.model.PeakTimeRule.Category;
import com.vipkid.model.PeakTimeRule.Status;
import com.vipkid.repository.PeakTimeRepository;
import com.vipkid.repository.PeakTimeRuleRepository;
import com.vipkid.service.exception.PeakTimeApplyTimeoutException;
import com.vipkid.service.exception.WrongPeakTimeFormatException;

@Service
public class PeakTimeRuleService {
	private Logger logger = LoggerFactory.getLogger(PeakTimeRuleService.class.getSimpleName());
	
	@Resource
	private PeakTimeRuleRepository peakTimeRuleRepository;
	
	@Resource
	private PeakTimeRepository peakTimeRepository;
	
	private final SimpleDateFormat dateSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	private final SimpleDateFormat timeSimpleDateFormat = new SimpleDateFormat("HH:mm");

	public List<PeakTimeRule> findAllRules() {
		logger.debug("find all the peak time rules");
		
		return peakTimeRuleRepository.findAllRules();
	}

	public List<PeakTimeRule> findRecentYearRules() {
		logger.debug("find all the peak time rules from recent year");
		
		return peakTimeRuleRepository.findRecentYearRules();
	}

	public List<PeakTimeRule> applyRules(List<PeakTimeRule> rules) {
		for (PeakTimeRule rule : rules) {
			applyRule(rule);
			
			List<PeakTimeRule> weekDayRules = peakTimeRuleRepository.findWorkingByCategoryAndParentRuleId(Category.WEEKDAY, rule.getId());
			for (PeakTimeRule weekDayRule : weekDayRules) {
				applyRule(weekDayRule);
			}
			
			List<PeakTimeRule> specificDateRules = peakTimeRuleRepository.findWorkingByCategoryAndParentRuleId(Category.SPECIFICDAY, rule.getId());
			for (PeakTimeRule specificDateRule : specificDateRules) {
				applyRule(specificDateRule);
			}
		}
		return findRecentYearRules();
	}
	
	public List<PeakTimeRule> applyAll() {
		List<PeakTimeRule> longTimeRules = peakTimeRuleRepository.findByCategoryAndStatus(Category.LONGTIME, Status.WORKING);
		
		for (PeakTimeRule longTimeRule : longTimeRules) {
			applyRule(longTimeRule);
			
			List<PeakTimeRule> weekDayRules = peakTimeRuleRepository.findWorkingByCategoryAndParentRuleId(Category.WEEKDAY, longTimeRule.getId());
			for (PeakTimeRule weekDayRule : weekDayRules) {
				applyRule(weekDayRule);
			}
			
			List<PeakTimeRule> specificDateRules = peakTimeRuleRepository.findWorkingByCategoryAndParentRuleId(Category.SPECIFICDAY, longTimeRule.getId());
			for (PeakTimeRule specificDateRule : specificDateRules) {
				applyRule(specificDateRule);
			}
		}
		
		return findRecentYearRules();
	}
	
	private void applyRule(PeakTimeRule rule) {
		String timeString = rule.getTime();
		Calendar twoYearLater = Calendar.getInstance();
		twoYearLater.add(Calendar.YEAR, 2);
		twoYearLater.set(Calendar.DATE, 1);
		twoYearLater.set(Calendar.MONTH, Calendar.JANUARY);
		switch (rule.getCategory()) {
		case LONGTIME:
			if (isValidDateRange(timeString)) {
				try {
					Date startDate = dateSimpleDateFormat.parse(timeString.split("-")[0]);
					Date endDate = dateSimpleDateFormat.parse(timeString.split("-")[1]);
					if (startDate.after(endDate) || endDate.after(twoYearLater.getTime())/* || startDate.before(new Date()) */) {
						logger.debug("time string wrong format");
						throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
					}
					
					Calendar startCalendar = Calendar.getInstance();
					startCalendar.setTime(startDate);
					Calendar endCalendar = Calendar.getInstance();
					endCalendar.setTime(endDate);
					Date saveStartDate = new Date();
					Date workingDate = new Date();
					
					while (!startCalendar.after(endCalendar)) {
						applyRuleToDate(rule, startCalendar);
						
						startCalendar.add(Calendar.DATE, 1);
						workingDate = new Date();
						if (workingDate.getTime() - saveStartDate.getTime() > 5*60*1000) {
							throw new PeakTimeApplyTimeoutException("We do not have so much time to do this.");
						}
					}
				} catch (PeakTimeApplyTimeoutException e) {
					throw e; 
				} catch (Exception e) {
					// won't get here
					logger.debug("time string wrong format");
					return;
				}
			} else {
				throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
			}
			break;
		case WEEKDAY:
			if (isValidWeekDay(timeString)) {
				PeakTimeRule parentRule = rule.getParentRule();
				if (parentRule == null) {
					return; // invalid
				}
				try {
					Date startDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[0]);
					Date endDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[1]);
					if (startDate.after(endDate) || endDate.after(twoYearLater.getTime())) {
						logger.debug("time string wrong format");
						throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
					}
					
					Calendar startCalendar = Calendar.getInstance();
					startCalendar.setTime(startDate);
					Calendar endCalendar = Calendar.getInstance();
					endCalendar.setTime(endDate);
					while (!startCalendar.after(endCalendar)) {
						if (isWeekDay(startCalendar.get(Calendar.DAY_OF_WEEK), timeString)) {
							applyRuleToDate(rule, startCalendar);
						}
						startCalendar.add(Calendar.DATE, 1);
					}
				} catch (Exception e) {
					logger.debug("time string wrong format");
					return;
				}
			} else {
				throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
			}
			break;
		case SPECIFICDAY:
			if (isValidDate(timeString)) {
				PeakTimeRule parentRule = rule.getParentRule();
				if (parentRule == null) {
					throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
				}
				try {
					Date startDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[0]);
					Date endDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[1]);
					if (startDate.after(endDate) || endDate.after(twoYearLater.getTime())) {
						logger.debug("time string wrong format");
						return;
					}
					Date singleDate = dateSimpleDateFormat.parse(timeString);
					if (singleDate.before(startDate) || singleDate.after(endDate)) {
						return; // out of bound
					}
					Calendar singleCalendar = Calendar.getInstance();
					singleCalendar.setTime(singleDate);
					
					applyRuleToDate(rule, singleCalendar);
				} catch (Exception e) {
					logger.debug("time string wrong format");
					return;
				}
			} else {
				throw new WrongPeakTimeFormatException("Wrong Peak Time Format");
			}
			break;
		}
		rule.setStatus(PeakTimeRule.Status.APPLIED);
		peakTimeRuleRepository.update(rule);
	}

	private boolean isWeekDay(int weekDayIndex, String weekDayName) throws Exception {
		switch(weekDayIndex) {
		case Calendar.MONDAY:
			return weekDayName.equals("Mon");
		case Calendar.TUESDAY:
			return weekDayName.equals("Tue");
		case Calendar.WEDNESDAY:
			return weekDayName.equals("Wed");
		case Calendar.THURSDAY:
			return weekDayName.equals("Thu");
		case Calendar.FRIDAY:
			return weekDayName.equals("Fri");
		case Calendar.SATURDAY:
			return weekDayName.equals("Sat");
		case Calendar.SUNDAY:
			return weekDayName.equals("Sun");
		default:
			return false;
		}
	}

	public List<PeakTimeRule> saveRules(List<PeakTimeRule> rules) throws TimeoutException {
		List<PeakTimeRule> peakTimeRulesList = new ArrayList<PeakTimeRule>();
		for (PeakTimeRule rule : rules) {
			if (!isValidRuleTimeFormat(rule.getTime()) || !isValidRulePeakTimeFormat(rule.getPeakTime())) {
				continue;
			}

			rule.setStatus(PeakTimeRule.Status.WORKING); // force set status to working.
			rule.setCreateDateTime(new Date());
			peakTimeRulesList.add(rule);
			peakTimeRuleRepository.create(rule);
		}
		
		return peakTimeRulesList;
	}
	
	public PeakTimeRule saveRule(PeakTimeRule rule) {
			if (!isValidRulePeakTimeFormat(rule.getPeakTime())) {
				throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
			}
			
			switch(rule.getCategory()) {
			case LONGTIME:
				if (!isValidDateRange(rule.getTime())) {
					throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			case WEEKDAY:
				if (!isValidWeekDay(rule.getTime())) {
					throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			case SPECIFICDAY:
				if (!isValidDate(rule.getTime())) {
					throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				
				if (rule.getParentRule() == null) {
					throw new WrongPeakTimeFormatException("Wrong PeakTime Format: this rule is not in any group.");
				}
				
				PeakTimeRule parentRule = peakTimeRuleRepository.find(rule.getParentRule().getId());
				if (!isTimeWithinRange(rule.getTime(), parentRule.getTime())) {
					throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			}
			
			if (!isValidTimeRangeFormat(rule.getPeakTime())) {
				throw new WrongPeakTimeFormatException("Wrong PeakTime Format: " + rule.getTime() + ", " + rule.getPeakTime());
			}
			
			rule.setId(null);
			rule.setStatus(PeakTimeRule.Status.WORKING); // force set status to working.
			rule.setCreateDateTime(new Date());
			peakTimeRuleRepository.create(rule);
			
			return rule;
	}
	
	private boolean isTimeWithinRange(String timePoint, String timeRange) {
		if (!isValidDateRange(timeRange) || !isValidDate(timePoint)) {
			return false;
		}
		
		try {
			Date fromDate = dateSimpleDateFormat.parse(timeRange.split("-")[0]);
			Date toDate = dateSimpleDateFormat.parse(timeRange.split("-")[1]);
			Date timePointDate = dateSimpleDateFormat.parse(timePoint);
			
			if (timePointDate.before(fromDate) || timePointDate.after(toDate)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void applyRuleToDate(PeakTimeRule rule, Calendar calendar) throws ParseException {
		peakTimeRepository.deleteByDate(calendar);
		
		for (String timeRange : rule.getPeakTime().split(",")) {
			Calendar startTimeCalendar = Calendar.getInstance();
			Calendar endTimeCalendar = Calendar.getInstance();
			
			startTimeCalendar.setTime(timeSimpleDateFormat.parse(timeRange.split("-")[0]));
			endTimeCalendar.setTime(timeSimpleDateFormat.parse(timeRange.split("-")[1]));
			
			Calendar timePointCalendar = Calendar.getInstance();
			timePointCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			timePointCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			timePointCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
			
			while (startTimeCalendar.before(endTimeCalendar)) {
				timePointCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalendar.get(Calendar.HOUR_OF_DAY));
				if (startTimeCalendar.get(Calendar.MINUTE) < 30 && startTimeCalendar.get(Calendar.MINUTE) > 0) {
					timePointCalendar.set(Calendar.MINUTE, 30);
				} else if (startTimeCalendar.get(Calendar.MINUTE) > 30) {
					timePointCalendar.add(Calendar.HOUR_OF_DAY, 1);
					timePointCalendar.set(Calendar.MINUTE, 0);
				} else if (startTimeCalendar.get(Calendar.MINUTE) == 30) {
					timePointCalendar.set(Calendar.MINUTE, 30);
				} else if (startTimeCalendar.get(Calendar.MINUTE) == 0) {
					timePointCalendar.set(Calendar.MINUTE, 0);
				}
				timePointCalendar.set(Calendar.SECOND, 0);
				timePointCalendar.set(Calendar.MILLISECOND, 0);
				
				PeakTime peakTime = peakTimeRepository.findByTimePoint(timePointCalendar.getTime());
				if (peakTime != null) {
					peakTime.setType(rule.getType());
					peakTimeRepository.update(peakTime);
				} else {
					peakTime = new PeakTime();
					peakTime.setTimePoint(timePointCalendar.getTime());
					peakTime.setType(rule.getType());
					peakTimeRepository.create(peakTime);
				}
				
				startTimeCalendar.add(Calendar.MINUTE, 30);
			}
		}
	}
	
	private boolean isValidRulePeakTimeFormat(String peakTime) {
		String [] ranges = peakTime.split(",");
		if (ranges.length <= 0) {
			return false;
		}
		
		for (String range : ranges) {
			try {
				Calendar startTimeCalendar = Calendar.getInstance();
				startTimeCalendar.setTime(timeSimpleDateFormat.parse(range.split("-")[0]));
				Calendar endTimeCalendar = Calendar.getInstance();
				endTimeCalendar.setTime(timeSimpleDateFormat.parse(range.split("-")[1]));
				
				if (startTimeCalendar.after(endTimeCalendar) || startTimeCalendar.get(Calendar.DATE) != endTimeCalendar.get(Calendar.DATE)) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidRuleTimeFormat(String time) {
		return isValidDateRange(time) || isValidWeekDay(time) || isValidDate(time);
	}

	private boolean isValidDate(String time) {
		Calendar twoYearLater = Calendar.getInstance();
		twoYearLater.add(Calendar.YEAR, 2);
		twoYearLater.set(Calendar.DATE, 1);
		twoYearLater.set(Calendar.MONTH, Calendar.JANUARY);
		
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = dateSimpleDateFormat.parse(time, pp);
		if (date == null || date.after(twoYearLater.getTime()) || pp.getIndex() < time.length()) {
			logger.debug("You can input a date after {}", twoYearLater.getTime());
			return false;
		}
//		if (date.before(new Date())) {
//			logger.debug("You can input a date before {}", new Date());
//			return false;
//		}
		
		return date != null;
	}

	private boolean isValidWeekDay(String time) {
		return time.matches("Mon|Tue|Wed|Thu|Fri|Sat|Sun");
	}

	private boolean isValidDateRange(String time) {
		try {
			String fromDateString = time.split("-")[0];
			String toDateString = time.split("-")[1];
			return isValidDate(fromDateString) && isValidDate(toDateString);
		} catch (IndexOutOfBoundsException e) {
			logger.debug("{} is illegel time string", time);
			return false;
		}
	}
	
	private boolean isValidTimeRangeFormat(String time) {
		for (String range : time.split(",")) {
			try {
				String fromDateString = range.split("-")[0];
				String toDateString = range.split("-")[1];
				if (!isValidTime(fromDateString) || !isValidTime(toDateString)) {
					return false;
				}
			} catch (IndexOutOfBoundsException e) {
				logger.debug("{} is illegel time string", range);
				return false;
			}
		}
		
		return true;
	}

	private boolean isValidTime(String time) {
		ParsePosition pp = new ParsePosition(0);
		
		Date date = timeSimpleDateFormat.parse(time, pp);
		
		if (date == null || pp.getIndex() < time.length() || time.split(":")[0].length() > 2 || time.split(":")[1].length() > 2) {
			return false;
		}
		
		return date != null;
	}

//	public PeakTimeRule createRule() {
//		PeakTimeRule rule = new PeakTimeRule();
//		rule.setCategory(Category.LONGTIME);
//		rule.setCreateDateTime(new Date());
//		rule.setStatus(Status.WORKING);
//		
//		return peakTimeRuleRepository.create(rule);
//	}
	
}
