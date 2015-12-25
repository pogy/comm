package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Staff.class)
public class Staff_ {
	public static volatile SingularAttribute<Staff, String> englishName;
	public static volatile SingularAttribute<Staff, Long> salesTeamId;
}