package com.vipkid.util;

public enum Redis { // 记录所有key
	GLOBAL_SETTING_TRIAL_PARALLEL("global-setting-trial-parallel"),;
	//GLOBAL_SETTING_CLASSES_PER_WEEK("global-setting-classes-per-week"),;  // 控制trial 课同一时间并发数量

	public static class Trial {
		public static final int DEFAULT_PARALLEL = 3;
		public static final String KEY = "global-setting-trial-parallel";
	}
	
	public static class ClassSetting {
		public static final int DEFAULT_CLASSES_PER_WEEK = 5;
		public static final String KEY = "global-setting-classes-per-week";
	}
	
	public static class GlobalSetting {
		public static final String DEFAULT_KEY = "global-setting";
	}
	
	public static class MarketingSetting {
		public static final String DEFAULT_KEY = "marketing-setting";
		public static final String CHANNEL_LEVEL_KEY = "marketing-setting-channelLevel-";
	}

	private String key;
	
	private Redis(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}

