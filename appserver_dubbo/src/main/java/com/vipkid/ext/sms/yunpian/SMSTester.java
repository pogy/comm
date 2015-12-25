package com.vipkid.ext.sms.yunpian;


public class SMSTester {

	public static void main(String[] args) {
		String mobile = "13520444739";
		String code = "123456";
		
		SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(mobile, code);
		if(sendSMSResponse.isSuccess()) {
			System.out.println(sendSMSResponse.getCode());
		}else {
			System.out.println(sendSMSResponse.getCode());
		}
	}

}
