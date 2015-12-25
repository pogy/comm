package com.vipkid.model.validation;



public class ValidateRegularExpression {
	public static final String MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
	public static final String PHONE = "^((0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";
}
