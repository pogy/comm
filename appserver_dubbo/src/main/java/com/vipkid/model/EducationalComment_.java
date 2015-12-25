package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EducationalComment_.class)
public class EducationalComment_ {
	public static volatile SingularAttribute<EducationalComment, Long> id;
	public static volatile SingularAttribute<EducationalComment, Boolean> empty;
	public static volatile SingularAttribute<EducationalComment, OnlineClass> onlineClass;
	public static volatile SingularAttribute<EducationalComment, Date> createDateTime;
}
