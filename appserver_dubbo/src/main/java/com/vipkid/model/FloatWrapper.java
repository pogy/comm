package com.vipkid.model;

import java.io.Serializable;

public class FloatWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private float content;
    
    public FloatWrapper(){
    	
    }
    
	public FloatWrapper(float content) {
		super();
		this.content = content;
	}

	public float getContent() {
		return content;
	}
	public void setContent(float content) {
		this.content = content;
	}
    
    
}
