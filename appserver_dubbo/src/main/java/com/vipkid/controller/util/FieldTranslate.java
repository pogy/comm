package com.vipkid.controller.util;


public class FieldTranslate {
	public static String getOnlineClass_Status(String type){
		String result = "";
		switch(type){
		case "AS_SCHEDULED":
			result = "正常结束";
			break;
		case "STUDENT_NO_SHOW":
			result = "24小时内取消或学生未出席（课时-1）";
			break;
		case "TEACHER_NO_SHOW":
			result = "教师未出席或者网络问题（不扣课时）";
			break;
		case "TEACHER_NO_SHOW_WITH_SHORTNOTICE":
			result = "教师未出席或者网络问题（不扣课时）";
			break;
		case "STUDENT_IT_PROBLEM":
			result = "网络问题（不扣课时）";
			break;
		case "TEACHER_IT_PROBLEM":
			result = "网络问题（不扣课时）";
			break;
		case "TEACHER_IT_PROBLEM_WITH_SHORTNOTICE":
			result = "网络问题（不扣课时）";
			break;
		case "TEACHER_CANCELLATION":
			result = "教师申请取消";
			break;
		default:
			result = type;
		}
		return result;
	}
}
