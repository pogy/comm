package com.vipkid.util;


public enum MacAddress { // 只有从配置的mac 地址才发送短信和邮件。 注意字母需要大写。
	PRODUCE_01("00:16:3E:00:02:37"), // 生产环境
	PRODUCE_02("00:16:3E:00:11:A9"), // 生产环境10.172.249.85
    PRODUCE_03("00:16:3E:00:10:87"), // 生产环境10.171.33.192
	PRODUCE_04("00:16:3E:00:3B:D3"), // 生产环境
	PRODUCE_05("00:16:3E:00:0A:9B"), // 生产环境10.251.209.97
	PRODUCE_06("00:16:3E:00:05:7C"), // 生产环境10.251.211.175
	STA("00:16:3E:00:39:9A"), // sta测试环境
	BETA_01("00:16:3E:00:03:AA"), //10.251.213.238
	BETA_02("00:16:3E:00:03:65"); //10.251.213.111
	
	private String address;
	
	private MacAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}

