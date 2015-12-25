package com.vipkid.service.pojo;

import java.io.Serializable;

public class StringWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	private String word;
	
	public StringWrapper(){
		
	}

	public StringWrapper(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

}
