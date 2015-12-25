package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TeacherComment.class)
public class TeacherComment_ {
	public static volatile SingularAttribute<TeacherComment, Long> id;
	public static volatile SingularAttribute<TeacherComment, Boolean> empty;
	public static volatile SingularAttribute<TeacherComment, OnlineClass> onlineClass;
	public static volatile SingularAttribute<TeacherComment, Date> createDateTime;
	public static volatile SingularAttribute<TeacherComment, Student> student;
	public static volatile SingularAttribute<TeacherComment, Teacher> teacher;
	public static volatile SingularAttribute<TeacherComment,StudentPerformance> currentPerformance;
}
