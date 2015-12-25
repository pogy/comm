package com.vipkid.ext.wechatpay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class UnifiedOrderNotifyRequest {
	
	private boolean isSuccess ;
	private String errorMessage ;


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

	// 用户标识
	@XmlElement(name = "openid")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String openId;
	
	// 是否关注公众账号
	@XmlElement(name = "is_subscribe")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String isSubscribe;
	
	// 交易类型
	@XmlElement(name = "trade_type")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String tradeType;
	
	// 付款银行
	@XmlElement(name = "bank_type")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String bankType;
	
	// 总金额
	@XmlElement(name = "total_fee")
	private int totalFee;
	
	// 货币种类
	@XmlElement(name = "fee_type")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String feeType;
	
	//现金支付金额
	@XmlElement(name = "cash_fee")
	private int cashFee;
	
	//现金支付货币类型
	@XmlElement(name = "cash_fee_type")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String cashFeeType;

	//现金券金额
	@XmlElement(name = "coupon_fee")
	private  int couponFee;
	
	//代金券或立减优惠使用数量
	@XmlElement(name = "coupon_count")
	private  int couponCount;
	
	// 微信支付订单号
	@XmlElement(name = "transaction_id")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String transactionId;

	// 商户订单号
	@XmlElement(name = "out_trade_no")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String outTradeNo;
	
	// 商家数据包
	@XmlElement(name = "attach")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String attach;
	
	// 支付完成时间 yyyyMMddhhmmss
	@XmlElement(name = "time_end")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String timeEnd;
	

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

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getIsSubscribe() {
		return isSubscribe;
	}

	public void setIsSubscribe(String isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public int getCouponFee() {
		return couponFee;
	}

	public void setCouponFee(int couponFee) {
		this.couponFee = couponFee;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
