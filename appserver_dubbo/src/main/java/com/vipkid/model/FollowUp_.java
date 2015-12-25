package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.FollowUp.Category;
import com.vipkid.model.FollowUp.Status;
@StaticMetamodel(FollowUp.class)
public class FollowUp_ {
	
	public static volatile SingularAttribute<FollowUp, Long> id;
	public static volatile SingularAttribute<FollowUp, Status> status;
	public static volatile SingularAttribute<FollowUp, Category> category;
	public static volatile SingularAttribute<FollowUp, Date> createDateTime;
	public static volatile SingularAttribute<FollowUp, Student> stakeholder;
	public static volatile SingularAttribute<FollowUp, Date> targetDateTime;
	public static volatile SingularAttribute<FollowUp, Boolean> current;
}
