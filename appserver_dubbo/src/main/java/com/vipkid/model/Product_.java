package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
@StaticMetamodel(Product.class)
public class Product_ {
	
	public static volatile SingularAttribute<Product, Long> id;
	public static volatile SingularAttribute<Product, String> name;
	public static volatile SingularAttribute<Product, Date> createDateTime;
	public static volatile SingularAttribute<Product, String> description;
	public static volatile SingularAttribute<Product, Float> classHourPrice;
	public static volatile SingularAttribute<Product, Course> course;
	public static volatile SingularAttribute<Product, Boolean> archived;
}
