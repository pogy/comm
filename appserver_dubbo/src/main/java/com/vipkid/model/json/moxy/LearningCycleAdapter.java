package com.vipkid.model.json.moxy;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.LearningCycle;
import com.vipkid.model.Lesson;

public class LearningCycleAdapter extends XmlAdapter<LearningCycle, LearningCycle> {

	@Override
	public LearningCycle unmarshal(LearningCycle learningCycle) throws Exception {
		return learningCycle;
	}

	@Override
	public LearningCycle marshal(LearningCycle learningCycle) throws Exception {
		if(learningCycle == null) {
			return null;
		}else {
			LearningCycle simplifiedLearningCycle = new LearningCycle();
			simplifiedLearningCycle.setId(learningCycle.getId());
			simplifiedLearningCycle.setName(learningCycle.getName());
			simplifiedLearningCycle.setHighFrenquncyWords(learningCycle.getHighFrenquncyWords());
			simplifiedLearningCycle.setGrammar(learningCycle.getGrammar());
			simplifiedLearningCycle.setSentencePatterns(learningCycle.getSentencePatterns());
			simplifiedLearningCycle.setCcssLanguageArt(learningCycle.getCcssLanguageArt());
			simplifiedLearningCycle.setMathTopic(learningCycle.getMathTopic());
			simplifiedLearningCycle.setCcssMath(learningCycle.getMathTopic());
			
			List<Lesson> simplifiedLessons = new LinkedList<Lesson>();
			if (learningCycle.getLessons() != null){
				for(Lesson lesson : learningCycle.getLessons()){
					Lesson simplifiedLesson = new Lesson();
					simplifiedLesson.setId(lesson.getId());
					simplifiedLesson.setName(lesson.getName());
					simplifiedLesson.setSerialNumber(lesson.getSerialNumber());
				}
			}
			
			simplifiedLearningCycle.setLessons(simplifiedLessons);
			
			simplifiedLearningCycle.setUnit(learningCycle.getUnit());
			
			return simplifiedLearningCycle;
		}	
	}

}
