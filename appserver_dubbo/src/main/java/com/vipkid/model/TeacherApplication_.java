package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.TeacherApplication.Result;
import com.vipkid.model.OnlineClass.Status;

@StaticMetamodel(TeacherApplication.class)
public class TeacherApplication_ {
	public static volatile SingularAttribute<TeacherApplication, Long> id;
	public static volatile SingularAttribute<TeacherApplication, Teacher> teacher;
	public static volatile SingularAttribute<TeacherApplication, OnlineClass> onlineClass;
	public static volatile SingularAttribute<TeacherApplication, Date> applyDateTime;
	public static volatile SingularAttribute<TeacherApplication, Date> auditDateTime;
	public static volatile SingularAttribute<TeacherApplication, Status> status;
	public static volatile SingularAttribute<TeacherApplication, Result> result;
	public static volatile SingularAttribute<TeacherApplication, Boolean> current;
}
