package com.vipkid.model;

import java.sql.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(StudentExam.class)
public class StudentExam_ {

	public static volatile SingularAttribute<StudentExam, Long> id;
//	public static volatile SingularAttribute<StudentExam, Long> studentId;
	public static volatile SingularAttribute<StudentExam, Student> student;
	
	public static volatile SingularAttribute<StudentExam, Integer> examScore;
	
	public static volatile SingularAttribute<StudentExam, String> examLevel;
	public static volatile SingularAttribute<StudentExam, String> examComment;

	public static volatile SingularAttribute<StudentExam, String> recordUuid;
	public static volatile SingularAttribute<StudentExam, Date> createDatetime;
	public static volatile SingularAttribute<StudentExam, Date> endDatetime;
	
	public static volatile SingularAttribute<StudentExam, Integer> status;
}
