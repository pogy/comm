package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(FiremanToStudentComment.class)
public class FiremanToStudentComment_ {
	public static volatile SingularAttribute<FiremanToStudentComment, Long> id;
	public static volatile SingularAttribute<FiremanToStudentComment, Boolean> empty;
	public static volatile SingularAttribute<FiremanToStudentComment, OnlineClass> onlineClass;
	public static volatile SingularAttribute<FiremanToStudentComment, Date> createDateTime;
	public static volatile SingularAttribute<FiremanToStudentComment, Student> student;

}
