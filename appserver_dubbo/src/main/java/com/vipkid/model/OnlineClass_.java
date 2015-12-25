package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;

@StaticMetamodel(OnlineClass.class)
public class OnlineClass_ {
	
	public static volatile SingularAttribute<OnlineClass, Long> id;
	public static volatile SingularAttribute<OnlineClass, Lesson> lesson;
	public static volatile SingularAttribute<OnlineClass, Teacher> teacher;
	public static volatile ListAttribute<OnlineClass, StudentComment> studentComments;
	public static volatile ListAttribute<OnlineClass, Student> students;
	public static volatile SingularAttribute<OnlineClass, Date> scheduledDateTime;
	public static volatile SingularAttribute<OnlineClass, Status> status;
	public static volatile SingularAttribute<OnlineClass, FinishType> finishType;
	public static volatile SingularAttribute<OnlineClass, String> classroom;
	public static volatile SingularAttribute<OnlineClass, Boolean> shortNotice;
	public static volatile SingularAttribute<OnlineClass, Boolean> archived;
	public static volatile SingularAttribute<OnlineClass, Long> familyId;
	public static volatile SingularAttribute<OnlineClass, PayrollItem> payrollItem;
	public static volatile SingularAttribute<OnlineClass, DemoReport> demoReport;
	public static volatile ListAttribute<OnlineClass, TeacherComment> teacherComments;
	public static volatile SingularAttribute<OnlineClass, FiremanToTeacherComment> firemanToTeacherComment;
	public static volatile ListAttribute<OnlineClass, FiremanToStudentComment> firemanToStudentComments;
	
	// 2015-07-15 添加课程类型： 1v1 or 1vN
	public static volatile SingularAttribute<OnlineClass, Course.Mode> courseMode;
}
