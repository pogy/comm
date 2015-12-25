package com.vipkid.service.pojo;

import java.io.Serializable;

public class BooleanWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean result;
	
	public BooleanWrapper(){
		
	}

	public BooleanWrapper(Boolean result) {
		this.result = result;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}
}
