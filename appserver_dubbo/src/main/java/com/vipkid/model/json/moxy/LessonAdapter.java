package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Lesson;

public class LessonAdapter extends XmlAdapter<Lesson, Lesson> {

	@Override
	public Lesson unmarshal(Lesson lesson) throws Exception {
		return lesson;
	}

	@Override
	public Lesson marshal(Lesson lesson) throws Exception {
		if(lesson == null) {
			return null;
		}else {
			Lesson simplifiedLesson = new Lesson();
			simplifiedLesson.setId(lesson.getId());
			simplifiedLesson.setName(lesson.getName());
			simplifiedLesson.setSequence(lesson.getSequence());
			simplifiedLesson.setSerialNumber(lesson.getSerialNumber());
			simplifiedLesson.setLearningCycle(lesson.getLearningCycle());
			simplifiedLesson.setObjective(lesson.getObjective());
			simplifiedLesson.setGoal(lesson.getGoal());
			simplifiedLesson.setVocabularies(lesson.getVocabularies());
			simplifiedLesson.setSentencePatterns(lesson.getSentencePatterns());
			simplifiedLesson.setDbyDocument(lesson.getDbyDocument());
			simplifiedLesson.setDomain(lesson.getDomain());
			return simplifiedLesson;
		}
	}

}
