package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.LearningProgress;

public class LearningProgressAdapter extends XmlAdapter<LearningProgress, LearningProgress> {

	@Override
	public LearningProgress unmarshal(LearningProgress learningProgress) throws Exception {
		return learningProgress;
	}

	@Override
	public LearningProgress marshal(LearningProgress learningProgress) throws Exception {
		if(learningProgress == null) {
			return null;
		}else {
			LearningProgress simplifiedLearningProgress = new LearningProgress();
			simplifiedLearningProgress.setId(learningProgress.getId());
			simplifiedLearningProgress.setCourse(learningProgress.getCourse());
			return simplifiedLearningProgress;
		}	
	}
}