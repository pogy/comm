package com.vipkid.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;

@StaticMetamodel(Course.class)
public class Course_ {
	public static volatile SingularAttribute<Course, Long> id;
	public static volatile SingularAttribute<Course, String> serialNumber;
	public static volatile SingularAttribute<Course, String> name;
	public static volatile SingularAttribute<Course, Mode> mode;
	public static volatile SingularAttribute<Course, Type> type;
	public static volatile ListAttribute<Course, Unit> units;
}
