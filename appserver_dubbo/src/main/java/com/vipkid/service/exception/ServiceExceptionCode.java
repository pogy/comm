package com.vipkid.service.exception;

public class ServiceExceptionCode {
	
	public static int USER_NOT_EXSIT = 600;
	public static int USER_ALREADY_EXIST = 601;
	public static int USER_LOCKED = 602;
	public static int USER_NO_VERIFY_CODE = 603;
	public static int TEAM_ALREADY_EXIST = 604;
	public static int MANAGER_MUST_BE_SALES_MANAGER = 605;
	public static int MANAGER_MUST_BE_TMK_MANAGER = 606;
	public static int MATE_MUST_BE_SALES = 607;
	public static int MATE_MUST_BE_TMK = 608;
	public static int TEAM_NOT_EXSIT = 609;
	
	public static int PARENT_WECHAT_OPENID_MISMATCH = 610;
	public static int PARENT_WECHAT_OPENID_OCCUPIED = 611;
	public static int PARENT_MOBILE_OCCUPIED = 612;
	
	public static int NO_MORE_LESSON_FOR_BOOKING = 620;
	public static int NO_MORE_CLASS_HOUR_FOR_BOOKING = 621;
	public static int ONLINE_CLASS_NOT_EXSIT = 622;
	public static int ONLINE_CLASS_ALREADY_EXIST = 623;
	public static int ONLINE_CLASS_ALREADY_BOOKED_BY_OTHERS = 624;
	public static int DATETIME_ALREADY_SCHEDULED = 625;
	public static int ONLINE_CLASS_ALREADY_REQUESTED = 626;
	public static int ONLINE_CLASS_REQUEST_TIMEOUT = 627;
	public static int ONLINE_CLASS_SERVICE_EXCEPTION = 628;
	public static int DATETIME_ALREADY_REQUESTED_FOR_STUDENT = 629;
	
	public static int FAMILY_NOT_EXSIT = 630;
	public static int FAMILY_INVITATION_NOT_ENOUGH = 631;
	public static int FAMILY_NO_LEARNING_STUDENT = 632;
	
	public static int PRODUCT_NOT_EXSIT = 640;
	
	//redis
	public static int REDIS_IO_FAILED = 645;
	
	public static int FAIL_TO_GET_MOXTRA_ACCESS_TOKEN = 650;
	public static int FAIL_TO_CREATE_MOXTRA_ONLINE_CLASSROOM = 651;
	
	public static int FAIL_TO_SEND_SMS = 660;
	
	public static int FAIL_TO_UPLOAD_FILE = 670;
	
	public static int FOREIGN_LEAD_TEACHER_ALREADY_ASSIGNED = 680;
	public static int CHINESE_LEAD_TEACHER_ALREADY_ASSIGNED = 681;
	
	//Market activity
	public static int ACTIVITY_ID_INVALID = 682;
	public static int STUDENT_ALREADY_ATTENDED_CURRENT_ACTIVITY = 683;
	public static final int ACTIVITY_IS_NOT_IN_REGISTER_SOURCE_LIST = 684;
	public static final int STUDENT_TOO_YOUNG = 685;
	public static final int STUDENT_TOO_OLD = 686;
	public static final int ACTIVITY_NOT_FOR_OLD_STUDENT = 687;
	public static final int INVENTION_CODE_ERROR = 688; 
	public static final int INVENTION_CODE_ALREADY_USED = 689; 
	public static final int NO_QUOTA = 691;
	public static final int DUPLICATE_NAME = 692;
	public static final int DUPLICATE_CHANNEL = 693;
	public static final int CHANNEL_LEVEL_ALREADY_EXIST = 694;
	
	public static final int USE_INVENTION_CODE_AGE_NOT_SUITE = 694;
	
	public static int BAD_REQUEST = 690;
	
	public static int EXCEED_MAX_PARALLEL_COUNT = 700;
	public static int OPEN_CLASS_HASBEEN_SIGN_UP = 701;//判断openclass 是否已经有学生报名，有的话不能再下架
	public static int CHANNEL_ALREADY_EXIST = 710;
	
	public static int HAVE_QUEUE = 800;
	public static int HAVE_SALES_TEAM_MATE = 801;
	
	// peak time
	public static final int WRONG_PEAK_TIME_FORMAT = 850;
	public static final int PEAK_TIME_APPLY_TIMEOUT = 851;
	public static final int WRONG_TRIAL_THRESHOLD_FORMAT = 852;
	public static final int TRIAL_THRESHOLD_APPLY_TIMEOUT = 853;
}
