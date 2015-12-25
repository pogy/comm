package com.vipkid.util;



public class Configurations {

	public static class Auth {
		public static final boolean BYPASS = false;
		public static final String DEFAULT_STAFF_PASSWORD = "vipkid";
		public static final String DEFAULT_STUDENT_PASSWORD = "vipkid";
		public static final String DEFAULT_PARENT_PASSWORD = "vipkid";
		public static final String DEFAULT_TEACHER_PASSWORD = "vipkid";
	}
	
	public static class Deploy {
		public static final boolean ENABLE_SMS = false;
		public static final boolean ENABLE_ATTACH_DBY = false;
		public static final boolean ENABLE_EMAIL = false;
		public static final boolean ENABLE_MOXTRA = false;
	}
	
	public static class OnlineClass {
		public static final int MAX_STUDENT_NUMBER = 6;
		public static final int MIN_STUDENT_NUMBER = 3;
		public static final int BOOK_WAITING_QUEUE_THRESHOLD = 2;
		public static final int BOOK_WAITING_TIMES_THRESHOLD = 30;
	}

    public static class BaseInfo {
        public static String WWW_HOME = "www.vipkid.com.cn";
    }
	
	public static class WXT {
		public static final String WXT_TEACHER_ID = "3373878322";
		public static final String VIPKID_PREFIX = "VIPKID";
		public final static String APPKEY = "e47b8d2909c84647ab1f11436711721b";
		public final static String PARTNER = "20140224152645";
		public static final String DEFAULT_STUDENT_PASSWORD = "123456";
		public static final String DEFAULT_TEACHER_PASSWORD = "123456";
		public static final String TRIAL_PPT_ID = "0ad149dd-6be8-4f10-a288-d33425e49787";
		public static final String ERROR_14 = "repeat_arrange_to_course_error";
		public static final int ARRANGE_PPT_THREAD_TIMEOUT = 5;
		
		public static String genDemoCourseTitle(com.vipkid.model.OnlineClass onlineClass) {
			return onlineClass.getStudentEnglishNames() + TextUtils.SPACE + "demo lesson";
		}
		
		public static int genCourseLength() {
			return 30;
		}
		
		public static String genCourseTitle(com.vipkid.model.OnlineClass onlineClass) {
			return onlineClass.getStudentEnglishNames() + TextUtils.SPACE + onlineClass.getLesson().getSerialNumber() + TextUtils.SPACE + onlineClass.getLesson().getName();
		}
	} 
	
	public static class Upload {
		private static final String UPLOAD = "/Users/upload/";
		public static final String WECHAT = UPLOAD + "wechat/";
	}
	
	public static class Redis {
		public static final boolean ENABLE_LOCK = true;
		public static final String CONFIG_FILE = "redis.properties";
		public static final String PREFIX_FOR_BOOK_LOCK = "LOCK";
		public static final String LOCK_VAULE = "lock";
		//public static final String HOST = "localhost";//Don't change it  stream.vipkid.com.cn   123.57.15.149
		//public static final String PASSWORD = "";
		public static final String HOST = "123.57.15.149";//Don't change it  stream.vipkid.com.cn   123.57.15.149
		public static final String PASSWORD = "Vi1pkidCacheZAQ!";
		public static final int PORT = 6379;
		public static final int POOL_MAX_TOTAL = 1024;
		public static final int POOL_MIN_IDLE= 2;
		public static final int TIMEOUT = 1200;
	}
	
	public static class Moxtra {
		public static final String UNIQUEID_GRANT_TYPE = "http://www.moxtra.com/auth_uniqueid"; 
		
		// 线上环境
		public static final String CLIENT_ID = "p31MpzZF-Qg";
		public static final String CLIENT_SECRET = "nR9lxVY9_iM";
		public static class Service {
			public static final String GET_ACCESS_TOKEN = "https://api.moxtra.com/oauth/token";
			public static final String SCHEDULE_MEETING = "https://api.moxtra.com/v1/meets/schedule";
		}

		// 测试环境
//		public static final String CLIENT_ID = "ll5UjMiYf8Y";
//		public static final String CLIENT_SECRET = "rhrp_70s1C0";
//		public static class Service {
//			public static final String GET_ACCESS_TOKEN = "https://api.grouphour.com/oauth/token";
//			public static final String SCHEDULE_MEETING = "https://api.grouphour.com/v1/meets/schedule";
//		}	
	}
	
	public static class WechatPay {
		public static final String NOTIFY_URL = "http://app-proxy.vipkid.com.cn/api/service/public/wechatpay/callBack";
		public static final String APP_ID = "wxbd56495f131c90a8";
		public static final String APP_SECRET_KEY = "8ae54a4549c1ea2aa900c233220df775";
		public static final String PAY_SIGN_KEY = "zi6VIBs41g6yaZtT1wUdJuY64t49O6sR";
		public static final String MERCHANT_ID = "1227313002";
		public static final String PRODUCT = "VIPKID";
		public static final String INPUT_CHARSET = "UTF-8";

		public static class Service {
            //微信统一下单接口
			public static final String CREATE_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		}
	}

	// 百度消息推送服务
	public static class BAIDU {
		public static final String PUSH_API_KEY = "";
		public static final String PUSH_SECURET_KEY = "";
		
		public static class FanYi {
			public static final String URL = "http://openapi.baidu.com/public/2.0/bmt/translate";
			public static final String CLIENT_ID = "3mm8Z8TVYpYiDp9Xd9qeqv3C";
			public static final String FROM = "en";
			public static final String TO = "zh";
			public static final String CHARSET = "UTF-8";
		}
	}
	
	// 云片短信服务
	public static class YUNPIAN_SMS {
		public static final String GATEWAY_URL = "http://yunpian.com/v1/sms/tpl_send.json";
		public static final String GATEWAY_APIKEY = "47e819ba7bf63e7c8cd8fe92101112d7";
	}

	// 支付宝服务
	public class ALIPAY {
		public static final String partner = "2088211510493352";
		public static final String input_charset = "utf-8";
		// 商户的私钥
		public static final String key = "tjw6h98zjxomozaa5shdwxxjjmbonm98";
		// 支付宝提供给商户的服务接入网关URL(新)
	    public static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	    //  支付宝消息验证地址
	    public static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
	    
	    public static final String RETURN_URL = "http://www.vipkid.com.cn/pay/return_url";
	    public static final String NOTIFY_URL = "http://app-proxy.vipkid.com.cn/api/service/AlipayNotifyServlet";
	    
	    public static final String PAYMETHOD = "bankPay";
	    public static final String PAYMETHOD_ALIPAY = "directPay";
	    public static final String DEFAULT_BANK = "CMB";
	    public static final String SELLER_EMAIL = "luxiao@vipkid.com.cn";
	    public static final float MIN_AMOUNT  = 500;
	}

	// 电子邮件服务
	public static class EMail {
		public static final String HOST = "smtp.exmail.qq.com";
		public static final String USERNAME = "service@vipkid.com.cn";
		public static final String PASSWORD = "vi1pki1d";
		public static final String FROM = "service@vipkid.com.cn";

		// 2015-07-10 教师招聘的邮件地址，使用独立的teachervip
		public static final String TC_USERNAME = "teachvip@vipkid.com.cn";
		public static final String TC_PASSWORD = "vipkid365";
		public static final String TC_FROM = "teachvip@vipkid.com.cn";

		// 教室招聘中结果通知中CC的邮箱
		public static final String TeacherCruit_Result_CC = "llitz@vipkid.com.cn";	// lane mail
		public static final String RECEIVER_STATISTICS = "statistics@vipkid.com.cn";
		public static final String FIRE_MAN = "fireman@vipkid.com.cn";
		public static final String TESTERHOU = "houbaoshan@vipkid.com.cn";
		public static final String DEVDENG = "dengpeng@vipkid.com.cn";
		
		//
		public static final String TeacherRecruit_Email_Contract_CC = "education@vipkid.com.cn";
		//2015-07-23 教师招募 教师管理组的邮箱
		public static final String TeacherRecruit_Email_Education_USERNAME = "education@vipkid.com.cn";
		public static final String TeacherRecruit_Email_Education_PWD = "eduedu";
		public static final String TeacherRecruit_Email_Education = "education@vipkid.com.cn";
		
		public static final String IT_Test_Email = "it_support@vipkid.com.cn";
	}

	// 微信服务
	public static class WeChat {
		public static final String TOKEN = "vipkidtest2014token";
		public static final String URL = "http://wechat.vipkid.com.cn/api/service/public/ext/wechat";
		public static final String VIPKID_BASE_URL = "parent.vipkid.com.cn";
		public static final String MENU_TRIAL_KEY = "trialKey";
		public static final String MENU_COURSE_KEY = "COURSE";
		public static final String MENU_BOOK_KEY = "BOOK";
		public static final String MENU_LESSONS_KEY = "LESSONS";
		public static final String MENU_ORDERS_KEY = "ORDERS";
		public static final String MENU_ACCOUNT_KEY = "ACCOUNT";
		public static final String MENU_STORYBOX_KEY = "STORYBOX";
		public static final String MENU_HOTARTICLE_KEY = "HOTARTICLE";
		public static final String MENU_INVATATION_KEY = "INVATATION";
	}

	// 阿里云存储服务
	public static class OSS {
		public static final String BUCKET     = "vipkid";
		public static final String ENDPOINT   = "http://oss-cn-beijing.aliyuncs.com";
		public static final String KEY_ID     = "fhUsW648dBIpFGub";
		public static final String KEY_SECRET = "cd9D2myOMkztzd7kWFueAcpvv8Ldrw";
		public static final String SUFFIX     = "_orig";
		
		public static final String AVATAR     = "avatar";
		public static final String IMAGE      = "image";
		public static final String VIDEO      = "video";
		public static final String AUDIO      = "audio";
		public static final String REPORT     = "report";
		public static final String FILE     = "file";
		public static final String UNIT_TEST     = "unit_test";
		public static final String shrinkURl = "http://image-process.vipkid.com.cn/";
		public static final String URL_FIX   =  "http://resource.vipkid.com.cn";
		
		public static final String LEARNING_URL = "http://learning.vipkid.com.cn";
//		public static final String LEARNING_URL = "http://learning.vipkid.com:8043";

		public static class Template {
			public static final String PPT  = "http://resource.vipkid.com.cn/teaching_resource/ppt/{{lessonSerialNumber}}/index.html";
		}
		
		public static class Parameter {
			public static final String PPT  = "{{lessonSerialNumber}}";
		}
	}
	
	public static class Schedule {
		public static final int BACKUP_TEACHER_THRESHOLD = 4;
		public static final int AVAILABLE_HOUR_NEXT_MONTH_LIMITATION = 70;
	}
	
	public static class Payroll {
		public static final int PAY_DATE_TIME = 5;
		public static final int TRANSFOR_FEE = 12;
	}
	
	public static class StudentUsername {
		public static final int BASE = 6978;
	}
	
	public static class System {
		public static final String SYSTEM_USERNAME = "system@vipkid.com.cn";
		public static final String SYSTEM_USER_NAME = "system";
	}
	
	public static class Learning {
		public static final int TOTAL_CLASS_HOUR = 12; // 标识至少共有多少节课才进行下一步操作
	}
	
	public static class DefaultMacAddress {
		public static final String[] PRODUCE = {"00:16:3E:00:02:37","00:16:3E:00:11:A9","00:16:3E:00:10:87","00:16:3E:00:3B:D3", "00:16:3E:00:0A:9B", "00:16:3E:00:05:7C"};
		public static final String STA = "00:16:3E:00:39:9A";
		public static final String[] BETA = {"00:16:3E:00:03:AA", "00:16:3E:00:03:65"};
//		public static final String APE = ""; // ape用了线上数据，默认不发邮件
	}
	
	public static class WeeklyPeakDayKey{
		public static final String MON_KEY = "Mon";
		public static final String TUES_KEY = "Tue";
		public static final String WED_KEY = "Wed";
		public static final String THUS_KEY = "Thu";
		public static final String FRI_KEY = "Fri";
		public static final String SAT_KEY = "Sat";
		public static final String SUN_KEY = "Sun";
		
	}
	
	public static class Channel{
		public static final String WWW_DEFAULT_CHANNEL = "网页自然流量网站";
		public static final String WEIXIN_DEFAULT_CHANNEL = "微信订阅号";
		public static final String MOBILE_DEFAULT_CHANNEL = "移动端自然流量";
		
		public static final String WWW_DEFAULT_CHANNEL_NAME1 = "organic";
		public static final String WWW_DEFAULT_CHANNEL_NAME2 = "pc";
		public static final String WWW_DEFAULT_CHANNEL_NAME3 = "website";
		public static final String WWW_DEFAULT_CHANNEL_NAME4 = "";
		public static final String WWW_DEFAULT_CHANNEL_NAME5 = "";
	}
	
	public static class YouDao {
		public static final String FANYI_URL = "http://fanyi.youdao.com/openapi.do";
		public static final String KEY_FROM = "VIPKID";
		public static final String KEY = "1762985591";
		public static final String TYPE = "data";
		public static final String DOCT_YPE = "json";
		public static final String VERSION = "1.1";
		public static final String ONLY = "translate";
	}
	
	public static class ServerFileAddress{
		public static final String ROOT ="";
		public static final String AVATAR = "";
		public static final String CONTRACT = "";
		public static final String SHORTVIDEO = "";
		public static final String IMAGE = "";
		
		
		
		
	}

}

