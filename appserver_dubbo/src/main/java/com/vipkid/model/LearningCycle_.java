package com.vipkid.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(LearningCycle.class)
public class LearningCycle_ {
	public static volatile SingularAttribute<LearningCycle, String> serialNumber;
	public static volatile SingularAttribute<LearningCycle, String> name;
	public static volatile SingularAttribute<LearningCycle, Unit> unit;
	public static volatile SingularAttribute<LearningCycle, Long> id;
	public static volatile ListAttribute<LearningCycle, Lesson> lessons;
}
