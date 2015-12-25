package com.vipkid.service.pojo;

import java.io.Serializable;

public class DoubleVo implements Serializable {
	private static final long serialVersionUID = 1L;

	private double value;
	
	public DoubleVo(){};

	public DoubleVo(double total) {
		this.value = total;
		
		
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}



}
