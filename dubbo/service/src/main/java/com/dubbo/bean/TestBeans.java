package com.dubbo.bean;

import java.io.Serializable;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public class TestBeans implements Serializable{

	private static final long serialVersionUID = 9073050569774968593L;

	private String initstr;
	
	private Integer size;
	
	private Double dubbos;
	
	public String getInitstr() {
		return initstr;
	}

	public void setInitstr(String initstr) {
		this.initstr = initstr;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Double getDubbos() {
		return dubbos;
	}

	public void setDubbos(Double dubbos) {
		this.dubbos = dubbos;
	}
	
}
