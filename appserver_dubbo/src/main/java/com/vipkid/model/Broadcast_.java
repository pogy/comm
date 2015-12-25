package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Broadcast.class)
public class Broadcast_ {
	public static volatile SingularAttribute<Broadcast, Long> id;
	public static volatile SingularAttribute<Broadcast, String> title;
	public static volatile SingularAttribute<Broadcast, String> message;
	public static volatile SingularAttribute<Broadcast, Boolean> archived;
}
