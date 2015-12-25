package com.vipkid.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Unit.class)
public class Unit_ {
	public static volatile SingularAttribute<Unit, Course> course;
	public static volatile SingularAttribute<Unit, String> serialNumber;
	public static volatile SingularAttribute<Unit, String> name;
	public static volatile SingularAttribute<Unit, Long> id;
	public static volatile ListAttribute<Unit, LearningCycle> learningCycles;
}
