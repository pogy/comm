package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Parent.class)
public class Parent_ {
	public static volatile SingularAttribute<Parent, Long> id;
	public static volatile SingularAttribute<Parent, String> mobile;	
	public static volatile SingularAttribute<Parent, Long> familyId;
	}
