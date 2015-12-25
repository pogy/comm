package com.vipkid.service.pojo.leads;

/**
 * Created by zfl on 2015/6/12.
 */
public class LessonVo {
    private Long id;
    private String serialNumber;
    private LearningCycleVo learningCycle;
    private String dbyDocument;

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
    
    public LearningCycleVo getLearningCycle() {
		return learningCycle;
	}

	public void setLearningCycle(LearningCycleVo learningCycle) {
		this.learningCycle = learningCycle;
	}

	public String getDbyDocument() {
		return dbyDocument;
	}

	public void setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
	}
}
