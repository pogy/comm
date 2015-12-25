package com.vipkid.model;


public enum StudentPerformance {
//	NOPERF(0,""),		// 缺省未评估
//	BELOW(1,"Below"), 		// 低于评估
//	ONTARGET(2,"OnTarget"),	// 符合评估
//	ABOVE(3,"Above");		// 超出评估
	
	NOPERF,		// 缺省未评估
	BELOW, 		// 低于评估
	ONTARGET,	// 符合评估
	ABOVE	// 超出评估
	
	/*
	private int performanceValue;
	private String performanceName;
	
	private StudentPerformance(int performance) {
		this.performanceValue = performance;
		performanceName = "";
	}
	private StudentPerformance(int performance, String performanceName) {
		this.performanceValue = performance;
		this.performanceName = performanceName;
	}
	
	public int getValue() {
		return performanceValue;
	}
	
	public String getName() {
		return this.performanceName;
	}
	
	@Override
	public String toString() {
		return this.performanceName; 
	}
	*/
}