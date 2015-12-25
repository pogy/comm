package com.vipkid.controller.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.vipkid.controller.parent.model.StudentVO;
import com.vipkid.redis.RedisClient;
import com.vipkid.util.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCacheUtil {
	private static final Logger logger = LoggerFactory.getLogger(UserCacheUtil.class);
	public static final String STUDENT_ENGLISHNAME = "studentEnglishName";
	public static final String STAR_COUNT = "starCount";
	public static final String CURRENT_STUDENT_AVATAR = "currentStudentAvatar";
	public static final String MEDAL_COUNT = "medalCount";
	
	public static final String AVATAR_PREFIX = "http://resource.vipkid.com.cn/static/images/common/avatar/";
	public static final String AVATAR_SUFFIX = ".png";
	
	public static final String PARENT_NAME = "parent_name";
	
	public static void storeMedalCount(long id, long count){
		RedisClient.getInstance().setObject(id + "_" + UserCacheUtil.MEDAL_COUNT, count);
	}
	
	public static long getMedalCount(long id){
		return (long) RedisClient.getInstance().getObject(id + "_" + UserCacheUtil.MEDAL_COUNT);
	}
	
	public static void storeCurrentUser(String key, StudentVO user){
        logger.info("Cache student to Redis,key={}",key);
		RedisClient.getInstance().setObject("[currentUser]" + key, user);
	}
	
	public static StudentVO getCurrentUser(long id){
		StudentVO obj = (StudentVO) RedisClient.getInstance().getObject("[currentUser]" + id);
		if(obj != null){
			return obj;
		}
		return null;
	}
	
	public static Object getValueFromCookie(HttpServletRequest request, String key){
		Cookie[] cookies = null;
		Object value = null;
		if(request != null){
			cookies = request.getCookies();
		}
		if(cookies != null && key != null){
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals(key)){
					value = cookies[i].getValue().trim();
					break;
				}
			}
		}
		return value;
	}
	
	public static boolean hasLogin(HttpServletRequest request){
		Object token = UserCacheUtil.getValueFromCookie(request, CookieUtils.HTTP_COOKIE_AUTHENTICATION);
		if(token == null){
			return false;
		}else{
			return true;
		}
	}
	
//	private static Map<String, String> codeMap = new HashMap<String, String>();
//	public static void storeVerifyCode(String mobile, String code){
//		if(codeMap.containsKey(mobile)){
//			codeMap.remove(mobile);
//		}
//		codeMap.put(mobile, code);
//	}
//	
//	public static String getVerifyCode(String mobile){
//		return codeMap.get(mobile);
//	}
//
//	public static void delVerifyCode(String mobile){
//		codeMap.remove(mobile);
//	}
	
	public static void storeVerifyCode(String mobile, String code){
		RedisClient.getInstance().set("[VERIFY]" + mobile, code);
		RedisClient.getInstance().expire("[VERIFY]" + mobile, 60*30);
	}
	
	public static String getVerifyCode(String mobile){
		return RedisClient.getInstance().get("[VERIFY]" + mobile);
	}

	public static void delVerifyCode(String mobile){
		RedisClient.getInstance().del("[VERIFY]" + mobile);
	}
	
	public static void storeParentName(String parentId, String parentName){
		RedisClient.getInstance().set(parentId + PARENT_NAME, parentName);
	}
	
	public static String getParentName(String parentId){
		return RedisClient.getInstance().get(parentId + PARENT_NAME);
	}
}
