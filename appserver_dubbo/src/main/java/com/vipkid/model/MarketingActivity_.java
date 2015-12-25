package com.vipkid.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.vipkid.model.MarketingActivity.Type;

@StaticMetamodel(MarketingActivity.class)
public class MarketingActivity_ {
	public static volatile SingularAttribute<MarketingActivity, Long> id;
	public static volatile SingularAttribute<MarketingActivity, Agent> agent;
	public static volatile SingularAttribute<MarketingActivity, String> serialNumber;
	public static volatile SingularAttribute<MarketingActivity, String> banner;
	public static volatile SingularAttribute<MarketingActivity, String> statement;
	public static volatile SingularAttribute<MarketingActivity, String> name;
	public static volatile SingularAttribute<MarketingActivity, Type> type;
	public static volatile SingularAttribute<MarketingActivity, Boolean> hasLimited;
	public static volatile SingularAttribute<MarketingActivity, Long> limitedNumber;
	public static volatile SingularAttribute<MarketingActivity, Long> inventionCodeNumber;
	public static volatile SingularAttribute<MarketingActivity, Date> createDateTime;
	public static volatile SingularAttribute<MarketingActivity, Boolean> hasReleased;
	public static volatile SingularAttribute<MarketingActivity, Channel> channel;
}
