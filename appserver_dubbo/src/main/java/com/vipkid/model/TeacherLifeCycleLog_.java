package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TeacherLifeCycleLog.class)
public class TeacherLifeCycleLog_ {
	
	public static volatile SingularAttribute<TeacherLifeCycleLog, Long> id;
	public static volatile SingularAttribute<TeacherLifeCycleLog, Teacher> teacher;
	public static volatile SingularAttribute<TeacherLifeCycleLog, User> operator;
	public static volatile SingularAttribute<TeacherLifeCycleLog, Date> createDateTime;
//	public static volatile SingularAttribute<TeacherLifeCycleLog, Date> operationFromDateTime;
//	public static volatile SingularAttribute<TeacherLifeCycleLog, Date> operationToDateTime;
	

}
