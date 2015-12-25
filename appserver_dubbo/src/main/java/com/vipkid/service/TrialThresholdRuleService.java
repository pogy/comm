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

import com.vipkid.model.TrialThreshold;
import com.vipkid.model.TrialThresholdRule;
import com.vipkid.model.TrialThresholdRule.Category;
import com.vipkid.model.TrialThresholdRule.Status;
import com.vipkid.repository.TrialThresholdRepository;
import com.vipkid.repository.TrialThresholdRuleRepository;
import com.vipkid.service.exception.PeakTimeApplyTimeoutException;
import com.vipkid.service.exception.TrialThresholdApplyTimeoutException;
import com.vipkid.service.exception.WrongPeakTimeFormatException;
import com.vipkid.service.exception.WrongTrialThresholdFormatException;

@Service
public class TrialThresholdRuleService {
	private Logger logger = LoggerFactory.getLogger(TrialThresholdRuleService.class.getSimpleName());
	
	@Resource
	private TrialThresholdRuleRepository trialThresholdRuleRepository;
	
	@Resource
	private TrialThresholdRepository trialThresholdRepository;
	
	private final SimpleDateFormat dateSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	private final SimpleDateFormat timeSimpleDateFormat = new SimpleDateFormat("HH:mm");

	public List<TrialThresholdRule> findAllRules() {
		logger.debug("find all the peak time rules");
		
		return trialThresholdRuleRepository.findAllRules();
	}

	public List<TrialThresholdRule> findRecentYearRules() {
		logger.debug("find all the peak time rules from recent year");
		
		return trialThresholdRuleRepository.findRecentYearRules();
	}

	public List<TrialThresholdRule> applyRules(List<TrialThresholdRule> rules) {
		for (TrialThresholdRule rule : rules) {
			applyRule(rule);
			
			List<TrialThresholdRule> weekDayRules = trialThresholdRuleRepository.findWorkingByCategoryAndParentRuleId(Category.WEEKDAY, rule.getId());
			for (TrialThresholdRule weekDayRule : weekDayRules) {
				applyRule(weekDayRule);
			}
			
			List<TrialThresholdRule> specificDateRules = trialThresholdRuleRepository.findWorkingByCategoryAndParentRuleId(Category.SPECIFICDAY, rule.getId());
			for (TrialThresholdRule specificDateRule : specificDateRules) {
				applyRule(specificDateRule);
			}
		}
		return findRecentYearRules();
	}
	
	public List<TrialThresholdRule> applyAll() {
		List<TrialThresholdRule> longTimeRules = trialThresholdRuleRepository.findByCategoryAndStatus(Category.LONGTIME, Status.WORKING);
		
		for (TrialThresholdRule longTimeRule : longTimeRules) {
			applyRule(longTimeRule);
			
			List<TrialThresholdRule> weekDayRules = trialThresholdRuleRepository.findWorkingByCategoryAndParentRuleId(Category.WEEKDAY, longTimeRule.getId());
			for (TrialThresholdRule weekDayRule : weekDayRules) {
				applyRule(weekDayRule);
			}
			
			List<TrialThresholdRule> specificDateRules = trialThresholdRuleRepository.findWorkingByCategoryAndParentRuleId(Category.SPECIFICDAY, longTimeRule.getId());
			for (TrialThresholdRule specificDateRule : specificDateRules) {
				applyRule(specificDateRule);
			}
		}
		
		return findRecentYearRules();
	}
	
	private void applyRule(TrialThresholdRule rule) {
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
						throw new WrongTrialThresholdFormatException("Wrong Trial Threshold Format");
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
							throw new TrialThresholdApplyTimeoutException("We do not have so much time to do this.");
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
				TrialThresholdRule parentRule = rule.getParentRule();
				if (parentRule == null) {
					return; // invalid
				}
				try {
					Date startDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[0]);
					Date endDate = dateSimpleDateFormat.parse(parentRule.getTime().split("-")[1]);
					if (startDate.after(endDate) || endDate.after(twoYearLater.getTime())) {
						logger.debug("time string wrong format");
						throw new WrongTrialThresholdFormatException("Wrong Peak Time Format");
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
				throw new WrongTrialThresholdFormatException("Wrong Peak Time Format");
			}
			break;
		case SPECIFICDAY:
			if (isValidDate(timeString)) {
				TrialThresholdRule parentRule = rule.getParentRule();
				if (parentRule == null) {
					throw new WrongTrialThresholdFormatException("Wrong Peak Time Format");
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
		rule.setStatus(TrialThresholdRule.Status.APPLIED);
		trialThresholdRuleRepository.update(rule);
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

	public List<TrialThresholdRule> saveRules(List<TrialThresholdRule> rules) throws TimeoutException {
		List<TrialThresholdRule> peakTimeRulesList = new ArrayList<TrialThresholdRule>();
		for (TrialThresholdRule rule : rules) {
			if (!isValidRuleTimeFormat(rule.getTime()) || !isValidRulePeakTimeFormat(rule.getPeakTime())) {
				continue;
			}

			rule.setStatus(TrialThresholdRule.Status.WORKING); // force set status to working.
			rule.setCreateDateTime(new Date());
			peakTimeRulesList.add(rule);
			trialThresholdRuleRepository.create(rule);
		}
		
		return peakTimeRulesList;
	}
	
	public TrialThresholdRule saveRule(TrialThresholdRule rule) {
			if (!isValidRulePeakTimeFormat(rule.getPeakTime())) {
				throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
			}
			
			switch(rule.getCategory()) {
			case LONGTIME:
				if (!isValidDateRange(rule.getTime())) {
					throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			case WEEKDAY:
				if (!isValidWeekDay(rule.getTime())) {
					throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			case SPECIFICDAY:
				if (!isValidDate(rule.getTime())) {
					throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				
				if (rule.getParentRule() == null) {
					throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: this rule is not in any group.");
				}
				
				TrialThresholdRule parentRule = trialThresholdRuleRepository.find(rule.getParentRule().getId());
				if (!isTimeWithinRange(rule.getTime(), parentRule.getTime())) {
					throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
				}
				break;
			}
			
			if (!isValidTimeRangeFormat(rule.getPeakTime()) || !isValidTrialAmount(rule.getTrialAmount())) {
				throw new WrongTrialThresholdFormatException("Wrong TrialThreshold Format: " + rule.getTime() + ", " + rule.getPeakTime());
			}
			
			rule.setId(null);
			rule.setCreateDateTime(new Date());
			trialThresholdRuleRepository.create(rule);
			
			return rule;
	}
	
	private boolean isValidTrialAmount(long trialAmount) {
		return trialAmount >= 0;
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

	private void applyRuleToDate(TrialThresholdRule rule, Calendar calendar) throws ParseException {
		//trialThresholdRepository.deleteByDate(calendar);
		
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
				
				TrialThreshold trialThreshold = trialThresholdRepository.findByTimePoint(timePointCalendar.getTime());
				if (trialThreshold != null) {
					trialThreshold.setTrialAmount(rule.getTrialAmount());
					trialThresholdRepository.update(trialThreshold);
				} else {
					trialThreshold = new TrialThreshold();
					trialThreshold.setTimePoint(timePointCalendar.getTime());
					trialThreshold.setTrialAmount(rule.getTrialAmount());
					trialThresholdRepository.create(trialThreshold);
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
		return getValidDate(time) != null;
	}
	
	private Date getValidDate(String time) {
		Calendar twoYearLater = Calendar.getInstance();
		twoYearLater.add(Calendar.YEAR, 2);
		twoYearLater.set(Calendar.DATE, 1);
		twoYearLater.set(Calendar.MONTH, Calendar.JANUARY);
		
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = dateSimpleDateFormat.parse(time, pp);
		if (date == null || date.after(twoYearLater.getTime()) || pp.getIndex() < time.length()) {
			logger.debug("You can input a date after {}", twoYearLater.getTime());
			return null;
		}
//		if (date.before(new Date())) {
//			logger.debug("You can input a date before {}", new Date());
//			return false;
//		}
		
		return date;
	}

	private boolean isValidWeekDay(String time) {
		return time.matches("Mon|Tue|Wed|Thu|Fri|Sat|Sun");
	}

	private boolean isValidDateRange(String time) {
		try {
			String fromDateString = time.split("-")[0];
			String toDateString = time.split("-")[1];
			
			Date fromDate = getValidDate(fromDateString);
			Date toDate = getValidDate(toDateString);
			
			return fromDate != null && toDate != null && fromDate.before(toDate);
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
//				if (!isValidTime(fromDateString) || !isValidTime(toDateString)) {
//					return false;
//				}
				
				Date fromDate = getValidTime(fromDateString);
				Date toDate = getValidTime(toDateString);
				if (fromDate == null || toDate == null || fromDate.after(toDate)) {
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
		return getValidTime(time) != null;
	}
	
	private Date getValidTime(String time) {
		ParsePosition pp = new ParsePosition(0);
		
		Date date = timeSimpleDateFormat.parse(time, pp);
		
		if (date == null || pp.getIndex() < time.length() || time.split(":")[0].length() > 2 || time.split(":")[1].length() > 2) {
			return null;
		}
		
		return date;
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
