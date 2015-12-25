package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentComment.class)
public class StudentComment_ {
	public static volatile SingularAttribute<StudentComment, Integer> scores;
	public static volatile SingularAttribute<StudentComment, String> comment;
}
