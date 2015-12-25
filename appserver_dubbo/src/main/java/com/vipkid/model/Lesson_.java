package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Lesson.class)
public class Lesson_ {
	public static volatile SingularAttribute<Lesson, Long> id;
	public static volatile SingularAttribute<Lesson, String> serialNumber;
	public static volatile SingularAttribute<Lesson, String> name;
	public static volatile SingularAttribute<Lesson, LearningCycle> learningCycle;

}
