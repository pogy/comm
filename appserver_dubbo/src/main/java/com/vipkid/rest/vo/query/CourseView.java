package com.vipkid.rest.vo.query;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;


public class CourseView {
	private Long id;
	private String name;
    private Mode mode;
    private Course.Type type;
    private boolean sequential;//是否有序课
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}

    public Course.Type getType() {
        return type;
    }

    public void setType(Course.Type type) {
        this.type = type;
    }

    public boolean isSequential() {
        return sequential;
    }

    public void setSequential(boolean sequential) {
        this.sequential = sequential;
    }
}
