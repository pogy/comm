package com.vipkid.ext.wechatpay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class CreateUnifiedOrderResponse {
	private boolean success;
	private String errorMessage;
	

	// 返回状态码
	@XmlElement(name = "return_code")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String returnCode;
	
	// 返回信息
	@XmlElement(name = "return_msg")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String returnMessage;
	
	// 公众账号ID
	@XmlElement(name = "appid")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String appId;
	
	// 商户号
	@XmlElement(name = "mch_id")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String merchantId;
	
	// 设备号
	@XmlElement(name = "device_info")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String deviceInfo;
	
	// 随机字符串
	@XmlElement(name = "nonce_str")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String nonceString;
	
	// 签名
	@XmlElement(name = "sign")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String sign;
	
	// 业务结果
	@XmlElement(name = "result_code")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String resultCode;
	
	// 错误代码
	@XmlElement(name = "err_code")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String errorCode;
	
	// 错误代码描述
	@XmlElement(name = "err_code_des")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String errorCodeDescribe;
	
	// 交易类型
	@XmlElement(name = "trade_type")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String tradeType;
	
	// 预支付交易会话标识
	@XmlElement(name = "prepay_id")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String prepayId;
	
	// 二维码链接
	@XmlElement(name = "code_url")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String codeUrl;

	
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getNonceString() {
		return nonceString;
	}

	public void setNonceString(String nonceString) {
		this.nonceString = nonceString;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCodeDescribe() {
		return errorCodeDescribe;
	}

	public void setErrorCodeDescribe(String errorCodeDescribe) {
		this.errorCodeDescribe = errorCodeDescribe;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getPrepayId() {
		return prepayId;
	}

	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
