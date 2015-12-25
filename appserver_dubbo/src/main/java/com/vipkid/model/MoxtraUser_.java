package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(MoxtraUser.class)
public class MoxtraUser_ {
	
	public static volatile SingularAttribute<MoxtraUser, Long> id;
	public static volatile SingularAttribute<MoxtraUser, Boolean> inUse;
	public static volatile SingularAttribute<MoxtraUser, Date> createDateTime;
}
