package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineClassPeakViewPreHalfHour implements Serializable {

	private static final long serialVersionUID = 1L;

	int available;
	int booked;
	long time;
	List<Long> onlineClassIdList = new ArrayList<Long>();
	Map<String, Integer> availTypeCountMap = new HashMap<String, Integer>();
	Map<String, Integer> bookTypeCountMap = new HashMap<String, Integer>();

	public Map <String, Integer>getBookTypeCountMap() {
		return bookTypeCountMap;
	}

	public void setBookTypeCountMap(Map <String, Integer>bookTypeCountMap) {
		this.bookTypeCountMap = bookTypeCountMap;
	}

	public List<Long> getOnlineClassIdList() {
		return onlineClassIdList;
	}

	public void setOnlineClassIdList(List<Long> onlineClassIdList) {
		this.onlineClassIdList = onlineClassIdList;
	}

	public Map<String, Integer> getAvailTypeCountMap() {
		return availTypeCountMap;
	}

	public void setAvailTypeCountMap(Map<String, Integer> typeCountMap) {
		this.availTypeCountMap = typeCountMap;
	}

	public void countAvailable() {
		available++;
	}

	public void countBooked() {
		booked++;
	}

	public long getTime() {
		return time;
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

	public void setTime(long time) {
		this.time = time;
	}

}
