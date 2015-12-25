package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Family.class)
public class Family_ {
	
	public static volatile SingularAttribute<Family, Long> id;
	public static volatile SingularAttribute<Family, String> name;
	public static volatile SingularAttribute<Family, String> province;
	public static volatile SingularAttribute<Family, String> city;
	public static volatile ListAttribute<Family, Parent> parents;
	public static volatile ListAttribute<Family, Student> students;
	public static volatile SingularAttribute<Family, Date> createDateTime;
	public static volatile ListAttribute<Family, ItTest> itTests;
	public static volatile SingularAttribute<Family, Boolean> hasTested;
}
