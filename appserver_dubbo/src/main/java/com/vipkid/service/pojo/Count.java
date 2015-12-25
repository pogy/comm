package com.vipkid.service.pojo;

import java.io.Serializable;

public class Count implements Serializable {
	private static final long serialVersionUID = 1L;

	private long total;
	
	public Count(){};

	public Count(long total) {
		this.total = total;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
