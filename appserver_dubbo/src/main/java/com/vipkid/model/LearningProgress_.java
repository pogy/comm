package com.vipkid.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.LearningProgress.Status;

@StaticMetamodel(LearningProgress.class)
public class LearningProgress_ {
	public static volatile SingularAttribute<LearningProgress, Long> id;
	public static volatile SingularAttribute<LearningProgress, Course> course;
	public static volatile SingularAttribute<LearningProgress, Integer> leftClassHour;
	public static volatile SingularAttribute<LearningProgress, Status> status;
	public static volatile ListAttribute<LearningProgress, OnlineClass> completedOnlineClasses;
}
