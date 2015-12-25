package com.vipkid.rest.vo.query;

/**
 * Created by zfl on 2015/6/12.
 */
public class LessonVO {
    private Long id;
    private String serialNumber;
    private LearningCycleVO learningCycle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public LearningCycleVO getLearningCycle() {
		return learningCycle;
	}

	public void setLearningCycle(LearningCycleVO learningCycle) {
		this.learningCycle = learningCycle;
	}
}
