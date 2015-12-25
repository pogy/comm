package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.User.AccountType;
import com.vipkid.model.User.Status;

@StaticMetamodel(User.class)
public class User_ {
	
	public static volatile SingularAttribute<User, Long> id;
	public static volatile SingularAttribute<User, String> username;
	public static volatile SingularAttribute<User, String> name;
	public static volatile SingularAttribute<User, String> roles;
	public static volatile SingularAttribute<User, Date> registerDateTime;
	public static volatile SingularAttribute<User, Date> lastEditDateTime;
	public static volatile SingularAttribute<User, Gender> gender;
	public static volatile SingularAttribute<User, Status> status;
//	public static volatile SingularAttribute<User, Boolean> archived;
	// 2015-08-08 添加account type
	public static volatile SingularAttribute<User, AccountType> accountType;
}
