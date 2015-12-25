package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PeakTimePerWeek implements Serializable{

	private static final long serialVersionUID = 1L;
	
	Map<String, String> periodMap = new HashMap<String, String>();
	
	public Map<String, String> getPeriodMap() {
		return periodMap;
	}

	public void setPeriodMap(Map<String, String> periodMap) {
		this.periodMap = periodMap;
	}		
		

}
