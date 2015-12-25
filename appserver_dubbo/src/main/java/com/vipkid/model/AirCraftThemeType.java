package com.vipkid.model;

import java.io.Serializable;

public class AirCraftThemeType implements Serializable {
	private static final long serialVersionUID = 1L;

	private AirCraft airCraft;
	private String name;
	private String introduction;
	private String url;
	private int price;
	private int level;

	public AirCraft getAirCraft() {
		return airCraft;
	}

	public void setAirCraft(AirCraft airCraft) {
		this.airCraft = airCraft;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}	
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
