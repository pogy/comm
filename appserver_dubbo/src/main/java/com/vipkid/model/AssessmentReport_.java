package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AssessmentReport.class)
public class AssessmentReport_ {
	
	public static volatile SingularAttribute<AssessmentReport, Long> id;
	public static volatile SingularAttribute<AssessmentReport, String> name;
	public static volatile SingularAttribute<AssessmentReport, Student> student;
	public static volatile SingularAttribute<AssessmentReport, Date> createDateTime;
}
