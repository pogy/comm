package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Student.Source;

@StaticMetamodel(Student.class)
public class Student_ {
	public static volatile SingularAttribute<Student, String> englishName;
	public static volatile ListAttribute<Student, LearningProgress> learningProgresses;
	public static volatile SingularAttribute<Student, Family> family;
	public static volatile SingularAttribute<Student, Date> birthday;
	public static volatile SingularAttribute<Student, LifeCycle> lifeCycle;
	public static volatile SingularAttribute<Student, Integer> customerStage;
	public static volatile ListAttribute<Student, Order> orders;
	public static volatile SingularAttribute<Student, Source> source;
	public static volatile SingularAttribute<Student, Staff> sales;
	public static volatile SingularAttribute<Student, Staff> chineseLeadTeacher;
	public static volatile ListAttribute<Student, FollowUp> followUps;
	public static volatile SingularAttribute<Student, MarketingActivity> marketingActivity;
	public static volatile SingularAttribute<Student, Channel> channel;
	public static volatile SingularAttribute<Student, StudentPerformance> currentPerformance;
	
}
