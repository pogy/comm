package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.ItTest.FinalResult;

@StaticMetamodel(ItTest.class)
public class ItTest_ {
	public static volatile SingularAttribute<ItTest, FinalResult> finalResult;
	public static volatile SingularAttribute<ItTest, Date> testDateTime;
	public static volatile SingularAttribute<ItTest, Boolean> current;
}
