package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
@StaticMetamodel(Audit.class)
public class Audit_ {
	
	public static volatile SingularAttribute<Audit, Long> id;
	public static volatile SingularAttribute<Audit, String> operator;
	public static volatile SingularAttribute<Audit, String> operation;
	public static volatile SingularAttribute<Audit, Level> level;
	public static volatile SingularAttribute<Audit, Category> category;
	public static volatile SingularAttribute<Audit, Date> executeDateTime;

}
