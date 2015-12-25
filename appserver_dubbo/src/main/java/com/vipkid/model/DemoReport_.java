package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.DemoReport.LifeCycle;

@StaticMetamodel(DemoReport.class)
public class DemoReport_ {
	
	public static volatile SingularAttribute<DemoReport, Long> id;
	public static volatile SingularAttribute<DemoReport, LifeCycle> lifeCycle;

}
