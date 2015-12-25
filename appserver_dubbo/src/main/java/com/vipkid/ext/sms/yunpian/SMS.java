package com.vipkid.ext.sms.yunpian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.model.LearningProgress;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.util.CharSet;
import com.vipkid.util.Configurations;
import com.vipkid.util.Configurations.YUNPIAN_SMS;
import com.vipkid.util.DateTimeUtils;
import com.vipkid.util.NetworkUtils;
import com.vipkid.util.TextUtils;

public class SMS {
	private static Logger logger = LoggerFactory.getLogger(SMS.class.getSimpleName());

	private static SendSMSResponse send(String mobile, int templateId, Map<String, String> paramMap) {
		logger.info("send sms " + mobile + " : " + paramMap.toString());
		if (Configurations.Deploy.ENABLE_SMS) {
			if(!NetworkUtils.checkMacAddressIsValidated()) {
				logger.error("try to send sms from: " + NetworkUtils.getMacAddress() + ", but dennied");
				return null;
			}
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(YUNPIAN_SMS.GATEWAY_URL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("apikey", YUNPIAN_SMS.GATEWAY_APIKEY));
			params.add(new BasicNameValuePair("mobile", mobile));
			params.add(new BasicNameValuePair("tpl_id", Integer.toString(templateId)));
			
			StringBuilder sbTemplateValue = new StringBuilder();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				sbTemplateValue.append("#").append(entry.getKey()).append("#=").append(entry.getValue()).append("&");
				
			}
			String tempateValue = sbTemplateValue.deleteCharAt(sbTemplateValue.lastIndexOf("&")).toString();
			params.add(new BasicNameValuePair("tpl_value", tempateValue));
			
			SendSMSResponse response = null;
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params, CharSet.UTF_8));
				response = httpClient.execute(httpPost, new SendSMSResponseHandler());
				
				if (response.isSuccess()) {
					logger.info("success to send SMS");
				} else {
					logger.info("fail to send SMS, error detail is {}", response.getDetail());
				}
				
			} catch (Exception e) {
				logger.error("exception when send SMS: {}", e);
			} finally {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error("exception when send SMS: {}", e);
				}
			}
			
			return response;
		} else {
			logger.info("Debug mode, skip SMS Sending");
			SendSMSResponse response = new SendSMSResponse();
			response.setSuccess(true);
			response.setMessage("SMS is disabled");
			return response;
		}
	}

	// 向用户发送手机验证码
	public static SendSMSResponse sendVerificationCodeSMS(String mobile, String code) {
		int templateId = 2;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("company", "VIPKID");
		paramMap.put("code", code);
		return send(mobile, templateId, paramMap);
	}
	
	public static SendSMSResponse sendNewParentSignupSMS(String mobile,String childEnglishName,String childUsername,String childPassword) {
		int templateId = 684027;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("linkString", "vipkid-service");
//		paramMap.put("space", "。");
//		paramMap.put("childEnglishName", childEnglishName);
//		paramMap.put("childUsername", childUsername);
//		paramMap.put("childPassword", childPassword);
//		logger.error("childEnglishName"+ mobile);
		logger.error("parentMobile"+ childEnglishName);
//		logger.error("childUsername"+ childUsername);
//		logger.error("childPassword"+ childPassword);
		
		return send(mobile, templateId, paramMap);
	}
	
	// 管理端添加家长短信模版
	public static SendSMSResponse sendNewParentSignupFromManagementPortalSMS(Parent parent, String password) {
		int templateId = 682807;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("mobile", parent.getUsername());
		paramMap.put("password", password);
		return send(parent.getMobile(), templateId, paramMap);
	}
	
	// 订单支付提醒短信
	public static SendSMSResponse sendOrderPaymentSMS(String mobile, String staffName, String studentName) {
		int templateId = 661021;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("staffName", staffName);
		paramMap.put("studentName", studentName);
		return send(mobile, templateId, paramMap);
	}
	
	// 开通学员或续费成功短信
	public static SendSMSResponse sendPaymentSuccessAckedSMS(String mobile, String studentName) {
		int templateId = 661107;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", studentName);
		return send(mobile, templateId, paramMap);
	}
	
	// 向用户发送重置后的密码
	public static SendSMSResponse sendNewPasswordToParentSMS(String mobile, String newPassword) {
		int templateId = 671871;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("newPassword", newPassword);
		return send(mobile, templateId, paramMap);
	}
	
	// 添加学生后给家长发短信
	public static SendSMSResponse sendNewStudentSignupToParentsSMS(String mobile, String studentEnglishName) {
		int templateId = 676887;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentEnglishName", studentEnglishName);
		return send(mobile, templateId, paramMap);	
	}
	
	// 充值学生密码后给家长发短信
	public static SendSMSResponse sendNewPasswordOfStudentToParentsSMS(String mobile, String studentEnglishName, String newPassword) {
		int templateId = 685217;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentEnglishName", studentEnglishName);
		paramMap.put("newPassword", newPassword);
		return send(mobile, templateId, paramMap);	
	}
	
	// 确定试听时间
	public static SendSMSResponse sendDemoOnlineClassScheduledTimeToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689593;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 确定trial时间
	public static SendSMSResponse sendTrialOnlineClassScheduledTimeToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689593; // 和demo模版一样，时间改为提前30 min
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		Date enterDateTime = DateTimeUtils.getNthMinutesLater(onlineClass.getScheduledDateTime(), -30);
		paramMap.put("time", DateTimeUtils.format(enterDateTime, DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 确定IT测试时间
	public static SendSMSResponse sendItOnlineClassScheduledTimeToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689595;	
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 取消试听
	public static SendSMSResponse sendDemoOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689597;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 取消trial
	public static SendSMSResponse sendTrialOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689597; // 和demo模版一样，时间改为提前30 min
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		Date enterDateTime = DateTimeUtils.getNthMinutesLater(onlineClass.getScheduledDateTime(), -30);
		paramMap.put("time", DateTimeUtils.format(enterDateTime, DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 取消IT测试	
	public static SendSMSResponse sendItOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689599;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// Demo当天提醒
	public static SendSMSResponse sendDemoOnlineClassReminderToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689605;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// IT测试当天提醒
	public static SendSMSResponse sendItOnlineClassReminderToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689609;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// Trial当天提醒
	public static SendSMSResponse sendTrialOnlineClassReminderToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 802143;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		Date enterDateTime = DateTimeUtils.getNthMinutesLater(onlineClass.getScheduledDateTime(), -30);
		paramMap.put("time", DateTimeUtils.format(enterDateTime, DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
		
	// Assessment当天提醒
	public static SendSMSResponse sendAssessmentOnlineClassReminderToParentsSMS(String mobile, OnlineClass onlineClass) {
        if (StringUtils.isBlank(mobile) || null == onlineClass) {
            return null;
        }
		int templateId = 802145;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// Major预约课程成功
	public static SendSMSResponse sendMajorOnlineClassIsBookedToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689613;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		paramMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
		paramMap.put("lessonName", onlineClass.getLesson().getSafeName());
		return send(mobile, templateId, paramMap);	
	}
	

	// Major取消课程成功
	public static SendSMSResponse sendMajorOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689615;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		paramMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
		paramMap.put("lessonName", onlineClass.getLesson().getSafeName());
		return send(mobile, templateId, paramMap);	
	}
	
	// 确定回访课时间
	public static SendSMSResponse sendGuideOnlineClassIsBookedToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689619;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		return send(mobile, templateId, paramMap);	
	}
	
	// 取消课程成功
	public static SendSMSResponse sendGuideOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689621;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 1-N课程预约成功
	public static SendSMSResponse sendOneToManyOnlineClassIsBookedToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689625;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	// 1-N课程开课
	public static SendSMSResponse sendOneToManyOnlineClassWillOpenToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689637;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		paramMap.put("classroom", onlineClass.getClassroom());
		return send(mobile, templateId, paramMap);	
	}
	
	// 1-N课程开课
	public static SendSMSResponse sendOneToManyOnlineClassIsCancelledToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 689643;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);
	}
	
	// 周一提醒约课
	public static SendSMSResponse sendMondayBookOnlineClassReminderToParentSMS(String mobile, Parent parent) {
		int templateId = 923435;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("parent", "家长");
		return send(mobile, templateId, paramMap);	
	}
	
	// 周四提醒约课
	public static SendSMSResponse sendThursdayBookOnlineClassReminderToParentSMS(String mobile, Student student) {
		int templateId = 923439;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", student.getSafeName());
//		paramMap.put("percent", String.valueOf(percent));
		return send(mobile, templateId, paramMap);	
	}
	
	// 下周课程提醒
	public static SendSMSResponse sendNexWeekOnlineClassReminderToParentSMS(String mobile, Student student, List<OnlineClass> onlineClasses) {
		int templateId = 689661;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder onlineClassList = new StringBuilder();
		for(OnlineClass onlineClass : onlineClasses) {
			onlineClassList.append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2))
			.append('(').append(onlineClass.getTeacher().getName()).append("老师").append(')').append(TextUtils.COMMA);
		}
		paramMap.put("studentName", student.getSafeName());
		paramMap.put("onlineClassCount", String.valueOf(onlineClasses.size()));
		paramMap.put("onlineClassList", onlineClassList.toString());
		return send(mobile, templateId, paramMap);	
	}
	
	// 今日课程提醒
	public static SendSMSResponse sendTodayOnlineClassReminderToParentSMS(String mobile, Student student, OnlineClass onlineClass) {
		int templateId = 689677;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", student.getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.TIME_FORMAT));
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		return send(mobile, templateId, paramMap);	
	}
	
	// 学生迟到提醒
	public static SendSMSResponse sendStudentIsLateForOnlineClassToParentSMS(String mobile, Student student, OnlineClass onlineClass) {
		int templateId = 689681;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", student.getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.TIME_FORMAT));
		return send(mobile, templateId, paramMap);	
	}
	
	// 课程完成提醒
	public static SendSMSResponse sendLearningProgressIsFinishedToParentSMS(String mobile, LearningProgress learningProgress) {
		int templateId = 689683;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", learningProgress.getStudent().getSafeName());
		return send(mobile, templateId, paramMap);	
	}

	public static SendSMSResponse sendNewParentSignupReplySMS(String mobile,String childEnglishName,String childUsername,String childPassword) {
		int templateId = 762845;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", childEnglishName);
		return send(mobile, templateId, paramMap);
	}
	
	public static SendSMSResponse sendFreeGongKaiKeHuiBenDiTuiSMS(String mobile,String childEnglishName,String childUsername,String childPassword) {
		int templateId = 843567;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", childEnglishName);
		return send(mobile, templateId, paramMap);
	}
	
	public static SendSMSResponse sendXPYFreeSMS(String mobile) {
		int templateId = 932099;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", "您");
		return send(mobile, templateId, paramMap);
	}
	
	// 公开课开课前两小时提醒
	public static SendSMSResponse sendOpenClassReminderToParentSMS(String mobile, Student student, OnlineClass onlineClass) {
		int templateId = 898707;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", student.getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.TIME_FORMAT));
		return send(mobile, templateId, paramMap);	
	}
	
	public static SendSMSResponse sendOpenClassReminderToParentSMSBeForeTwoHours(String mobile, Student student, OnlineClass onlineClass) {
		int templateId = 920075;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", student.getSafeName());
		paramMap.put("teacherName", onlineClass.getTeacher().getName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.TIME_FORMAT3));
		return send(mobile, templateId, paramMap);	
	}
	
	//童年制造，注册成功之后短信提醒（参数带用户名和密码）
	public static SendSMSResponse sendfreeInterViewSignupSMS(String mobile,String userName,String password) {
		int templateId = 948637;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("userName", userName);
		paramMap.put("password", password);
		return send(mobile, templateId, paramMap);
	}

	public static SendSMSResponse sendKickOffOnlineClassScheduledTimeToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 958889;	// to be determined.
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
	
	public static SendSMSResponse sendCLTCourseOnlineClassScheduledTimeToParentsSMS(String mobile, OnlineClass onlineClass) {
		int templateId = 958891;	// to be determined.
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("studentName", onlineClass.getStudents().get(0).getSafeName());
		paramMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
		return send(mobile, templateId, paramMap);	
	}
}
