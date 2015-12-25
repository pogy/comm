package com.vipkid.constants;


import com.google.common.collect.ImmutableMap;
import com.vipkid.config.MapConfig;

public final class GlobalConstants {

    /**
     * 公共配置
     */
    public static final ImmutableMap<String, String> GLOBAL_CONFIG = MapConfig.pasreConf("global_config.xml");
    /**
     * 短信模板配置
     */
    public static final ImmutableMap<String, String> SmsConfig = MapConfig.pasreConf("user_sms_tml.xml");
    /**
     * 邮件模板配置
     */
    public static final ImmutableMap<String, String> EmailConfig = MapConfig.pasreConf("user_email_tml.xml");

    public static final String ADULT_ORIG_PRICE = "AD";
    public static final String QUNAR_QNC_DOMAIN_KEY = "qunar_qnc_domain";
    public static final String CPA_BIDDING_MESSAGE_KEY = "cpa_bidding_message";

    private GlobalConstants() {
    }
}
