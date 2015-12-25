package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.Teacher.RecruitmentChannel;

@StaticMetamodel(Teacher.class)
public class Teacher_ {
	public static volatile SingularAttribute<Teacher, String> mobile;
	public static volatile SingularAttribute<Teacher, LifeCycle> lifeCycle;
	public static volatile SingularAttribute<Teacher, Country> country;
	public static volatile ListAttribute<Teacher, Course> certificatedCourses;
	public static volatile ListAttribute<Teacher, OnlineClass> onlineClasses;
	public static volatile ListAttribute<Teacher, TeacherApplication> teacherApplications;
	public static volatile SingularAttribute<Teacher, Timezone> timezone;
	public static volatile SingularAttribute<Teacher, Date> contractStartDate;
	public static volatile SingularAttribute<Teacher, Date> contractEndDate;
	public static volatile SingularAttribute<Teacher, Teacher.Type> type;
	public static volatile SingularAttribute<Teacher, Teacher.Hide> hide;
	public static volatile SingularAttribute<Teacher, RecruitmentChannel> recruitmentChannel;
	public static volatile SingularAttribute<Teacher, String> realName;
	public static volatile SingularAttribute<Teacher, Partner> partner;
	public static volatile ListAttribute<Teacher, ItTest> itTests;
	public static volatile SingularAttribute<Teacher, Boolean> hasTested;
	public static volatile SingularAttribute<Teacher, String> teacherTags;
	
	//2015-08-15 manager相关的名字
	public static volatile SingularAttribute<Teacher, Staff> manager;
	public static volatile SingularAttribute<Teacher, String> managerName;
	//2015-08-15 quit相关的操作员名称
	public static volatile SingularAttribute<Teacher, Staff> quitOperator;
	public static volatile SingularAttribute<Teacher, String> operatorName;
	public static volatile SingularAttribute<Teacher, String> email;
	public static volatile SingularAttribute<Teacher, Date> quitTime;
	//fail相关操作
	public static volatile ListAttribute<Teacher, TeacherLifeCycleLog> teacherLifeCycleLogs;
	
	
}
