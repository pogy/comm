package com.vipkid.service.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.vipkid.model.OnlineClass.Status;
import com.vipkid.service.PeakTimeService;
import com.vipkid.util.ApplicationContextProvider;

public class OnlineClassPeakViewPreWeek implements Serializable {
	private static final String AVAILABLE_CONS = "available";

	private static final long serialVersionUID = 1L;

	int available;
	int booked;
	int peakTimebooked;
	int peakTimeAvailable;
	int teacherNoShow;
	int studentNoShow;
	int itProblem;
	Map<String, String> periodMap;
	private Map<Long, List<Long>> teacherTimeSlotMap = new ConcurrentHashMap<Long, List<Long>>();
	List<OnlineClassPeakViewPreHalfHour> countForPreHalfHour = new Vector<OnlineClassPeakViewPreHalfHour>();
	List<OnlineClassPeakViewPreDay> countForPreDay = new Vector<OnlineClassPeakViewPreDay>();
	
	@Resource
	private PeakTimeService peakTimeService;

	/**
	 * count for total time slots
	 * 
	 * @param onlineClassPeakTimeView
	 */
	public void countTotalBookedAndTotalTimeSolts(OnlineClassPeakTimeView onlineClassPeakTimeView) {
		Date date = onlineClassPeakTimeView.getScheduledDateTime();

		Status status = onlineClassPeakTimeView.getStatus();
		// for classes expired and canceled ,not need count in .
		if (status == Status.CANCELED) {
			return;
		}
		boolean countin = false;
		if (teacherTimeSlotMap.containsKey(onlineClassPeakTimeView.getTeacherId())) {
			List<Long> timeList = teacherTimeSlotMap.get(onlineClassPeakTimeView.getTeacherId());
			if (timeList != null) {
				if (timeList.contains(onlineClassPeakTimeView.getScheduledDateTime().getTime())) {
					countin = false;
				} else {
					timeList.add(onlineClassPeakTimeView.getScheduledDateTime().getTime());
					teacherTimeSlotMap.put(onlineClassPeakTimeView.getTeacherId(), timeList);
					countin = true;
				}
			} else {
				countin = true;
				timeList = new Vector<Long>();
				timeList.add(onlineClassPeakTimeView.getScheduledDateTime().getTime());
				teacherTimeSlotMap.put(onlineClassPeakTimeView.getTeacherId(), timeList);
			}
		} else {
			List<Long> timeList = new Vector<Long>();
			timeList.add(onlineClassPeakTimeView.getScheduledDateTime().getTime());
			teacherTimeSlotMap.put(onlineClassPeakTimeView.getTeacherId(), timeList);
			countin = true;

		}
		if (countin) {

			switch (status) {
			case AVAILABLE:
			case OPEN:
			case EXPIRED:
				// case CANCELED:
				// case REMOVED:
				if (isPeakTimeTarget(date)) {
					peakTimeAvailable++;
				}
				available++;
				break;
			case BOOKED:
			case FINISHED:
				if (isPeakTimeTarget(date)) {
					peakTimeAvailable++;
					peakTimebooked++;
				}
				available++;
				booked++;
				break;

			default:
				break;
			}
		}

	}

	/**
	 * count for every day .
	 * 
	 * @param onlineClassPeakTimeView
	 * @param isAvailOrBooked
	 */
	public void countAvailableOrBooked(OnlineClassPeakTimeView onlineClassPeakTimeView, boolean isAvailOrBooked) {
		Date date = onlineClassPeakTimeView.getScheduledDateTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = simpleDateFormat.format(date);

		boolean isExistInDayList = false;
		boolean isExistInHourList = false;

		for (OnlineClassPeakViewPreDay onlineClassPeakViewPreDay : countForPreDay) {
			if (onlineClassPeakViewPreDay.getDay().equals(dateString)) {
				if (isAvailOrBooked) {
					if (isPeakTimeTarget(date)) {
						onlineClassPeakViewPreDay.countPeakAvailable();
					}
					onlineClassPeakViewPreDay.countAvailable();
				} else {
					if (isPeakTimeTarget(date)) {
						onlineClassPeakViewPreDay.countPeakBooked();
					}
					onlineClassPeakViewPreDay.countBooked();
				}
				isExistInDayList = true;
			}
		}

		if (!isExistInDayList) {
			OnlineClassPeakViewPreDay day = new OnlineClassPeakViewPreDay();

			if (isAvailOrBooked) {
				if (isPeakTimeTarget(date)) {
					day.countPeakAvailable();
				}

				day.countAvailable();

			} else {
				if (isPeakTimeTarget(date)) {
					day.countPeakBooked();
				}
				day.countBooked();
			}
			day.setDay(dateString);
			countForPreDay.add(day);
		}

		if (isAvailOrBooked) {
			// available class.
			for (OnlineClassPeakViewPreHalfHour onlineClassPeakViewPreHalfHour : countForPreHalfHour) {

				if (onlineClassPeakViewPreHalfHour.getTime() == date.getTime()) {
					onlineClassPeakViewPreHalfHour.countAvailable();
					isExistInHourList = true;
					Map<String, Integer> typeCountMap = onlineClassPeakViewPreHalfHour.getAvailTypeCountMap();
					if (typeCountMap.containsKey(AVAILABLE_CONS)) {
						int value = (int) typeCountMap.get(AVAILABLE_CONS);
						typeCountMap.put(AVAILABLE_CONS, value + 1);
					} else {
						typeCountMap.put(AVAILABLE_CONS, 1);
					}
					List<Long> onlineClassIdList = onlineClassPeakViewPreHalfHour.getOnlineClassIdList();
					if (!onlineClassIdList.contains(onlineClassPeakTimeView.getId())) {
						onlineClassIdList.add(onlineClassPeakTimeView.getId());
					}
				}
			}
			if (!isExistInHourList) {
				OnlineClassPeakViewPreHalfHour hour = new OnlineClassPeakViewPreHalfHour();
				hour.countAvailable();
				hour.setTime(date.getTime());
				Map<String, Integer> typeCountMap = hour.getAvailTypeCountMap();
				countForPreHalfHour.add(hour);
				typeCountMap.put(AVAILABLE_CONS, 1);
				List<Long> onlineClassIdList = hour.getOnlineClassIdList();
				if (!onlineClassIdList.contains(onlineClassPeakTimeView.getId())) {
					onlineClassIdList.add(onlineClassPeakTimeView.getId());
				}
			}
		} else {

			// booked class.
			for (OnlineClassPeakViewPreHalfHour onlineClassPeakViewPreHalfHour : countForPreHalfHour) {

				if (onlineClassPeakViewPreHalfHour.getTime() == date.getTime()) {
					onlineClassPeakViewPreHalfHour.countBooked();
					isExistInHourList = true;
					Map<String, Integer> typeCountMap = onlineClassPeakViewPreHalfHour.getBookTypeCountMap();
					if (onlineClassPeakTimeView.getType() != null) {
						if (typeCountMap.containsKey(onlineClassPeakTimeView.getType().toString())) {
							int value = (int) typeCountMap.get(onlineClassPeakTimeView.getType().toString());
							typeCountMap.put(onlineClassPeakTimeView.getType().toString(), value + 1);
						} else {
							typeCountMap.put(onlineClassPeakTimeView.getType().toString(), 1);
						}
					}
					List<Long> onlineClassIdList = onlineClassPeakViewPreHalfHour.getOnlineClassIdList();
					if (!onlineClassIdList.contains(onlineClassPeakTimeView.getId())) {
						onlineClassIdList.add(onlineClassPeakTimeView.getId());
					}
				}
			}
			if (!isExistInHourList) {
				OnlineClassPeakViewPreHalfHour hour = new OnlineClassPeakViewPreHalfHour();
				hour.countBooked();
				hour.setTime(date.getTime());
				countForPreHalfHour.add(hour);
				Map<String, Integer> typeCountMap = hour.getBookTypeCountMap();
				if (onlineClassPeakTimeView.getType() != null) {
					typeCountMap.put(onlineClassPeakTimeView.getType().toString(), 1);
				}
				List<Long> onlineClassIdList = hour.getOnlineClassIdList();
				if (!onlineClassIdList.contains(onlineClassPeakTimeView.getId())) {
					onlineClassIdList.add(onlineClassPeakTimeView.getId());
				}
			}
		}
	}

	private boolean isPeakTimeTarget(Date date) {
		PeakTimeService peakTimeService = ApplicationContextProvider.getApplicationContext().getBean(PeakTimeService.class);
		return peakTimeService.isPeakTime(date);
	}

	public void countTeacherNoShow() {
		teacherNoShow++;
	}

	public void countStudentNoShow() {
		studentNoShow++;
	}

	public void countItProblem() {
		itProblem++;
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public int getBooked() {
		return booked;
	}

	public void setBooked(int booked) {
		this.booked = booked;
	}

	public int getPeakTimebooked() {
		return peakTimebooked;
	}

	public void setPeakTimebooked(int peakTimebooked) {
		this.peakTimebooked = peakTimebooked;
	}

	public int getPeakTimeAvailable() {
		return peakTimeAvailable;
	}

	public void setPeakTimeAvailable(int peakTimeAvailable) {
		this.peakTimeAvailable = peakTimeAvailable;
	}

	public int getTeacherNoShow() {
		return teacherNoShow;
	}

	public void setTeacherNoShow(int teacherNoShow) {
		this.teacherNoShow = teacherNoShow;
	}

	public int getStudentNoShow() {
		return studentNoShow;
	}

	public void setStudentNoShow(int studentNoShow) {
		this.studentNoShow = studentNoShow;
	}

	public int getItProblem() {
		return itProblem;
	}

	public void setItProblem(int itProblem) {
		this.itProblem = itProblem;
	}

	public List<OnlineClassPeakViewPreHalfHour> getCountForPreHalfHour() {
		return countForPreHalfHour;
	}

	public void setCountForPreHalfHour(List<OnlineClassPeakViewPreHalfHour> countForPreHalfHour) {
		this.countForPreHalfHour = countForPreHalfHour;
	}

	public List<OnlineClassPeakViewPreDay> getCountForPreDay() {
		return countForPreDay;
	}

	public void setCountForPreDay(List<OnlineClassPeakViewPreDay> countForPreDay) {
		this.countForPreDay = countForPreDay;
	}

	public Map<String, String> getPeriodMap() {
		return periodMap;
	}

	public void setPeriodMap(Map<String, String> periodMap) {
		this.periodMap = periodMap;
	}

}
