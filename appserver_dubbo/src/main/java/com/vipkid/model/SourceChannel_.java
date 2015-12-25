package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SourceChannel.class)
public class SourceChannel_ {
	public static volatile SingularAttribute<SourceChannel, Long> channel_id;
	
	public static volatile SingularAttribute<SourceChannel, Channel> sourceChannel;
	
	public static volatile SingularAttribute<SourceChannel, String> sourceName;
}
