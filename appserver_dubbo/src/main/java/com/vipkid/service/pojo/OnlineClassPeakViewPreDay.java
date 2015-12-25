package com.vipkid.service.pojo;

import java.io.Serializable;

public class OnlineClassPeakViewPreDay implements Serializable {

	private static final long serialVersionUID = 1L;
	
	int available;
	int booked;
	int peakTimebooked;
	int peakTimeAvailable;
	//List peakTimeList = new ArrayList();
	String day;
	
	
	public  void countAvailable() {
		available ++;		
	}
	
	public void countBooked(){
		booked++;
	}
	
	public  void countPeakAvailable() {
		peakTimeAvailable ++;		
	}
	
	public void countPeakBooked(){
		peakTimebooked++;
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

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	

}
