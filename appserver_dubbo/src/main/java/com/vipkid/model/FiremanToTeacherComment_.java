package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(FiremanToTeacherComment.class)
public class FiremanToTeacherComment_ {
	public static volatile SingularAttribute<FiremanToTeacherComment, Long> id;
	public static volatile SingularAttribute<FiremanToTeacherComment, Boolean> empty;
	public static volatile SingularAttribute<FiremanToTeacherComment, OnlineClass> onlineClass;
	public static volatile SingularAttribute<FiremanToTeacherComment, Date> createDateTime;
	public static volatile SingularAttribute<FiremanToTeacherComment, Teacher> teacher;
}
