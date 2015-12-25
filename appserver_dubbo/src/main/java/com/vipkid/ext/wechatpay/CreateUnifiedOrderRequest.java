package com.vipkid.ext.wechatpay;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.vipkid.ext.wechatpay.util.WeChatPaySignUtil;
import com.vipkid.model.Order;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.util.Configurations.WechatPay;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class CreateUnifiedOrderRequest {
	
	public enum TradeType {
		JSAPI,
		NATIVE,
		APP
	}

	// 公众账号ID, 必填
	@XmlElement(name = "appid")
	private String appId;
	
	// 商品描述, 必填
	@XmlElement(name = "body")
	private String product;
	
	// 商户号, 必填
	@XmlElement(name = "mch_id")
	private String merchantId;
	
	// 随机字符串, 必填
	@XmlElement(name = "nonce_str")
	private String nonceString;
	
	// 通知地址,必填
	@XmlElement(name = "notify_url")
	private String notifyUrl;
	
	// 用户标识, 必填
	@XmlElement(name = "openid")
	private String openId;
	
	// 商户订单号, 必填
	@XmlElement(name = "out_trade_no")
	private String orderSerialNumber;
	
	// 终端IP,必填
	@XmlElement(name = "spbill_create_ip")
	private String customerIP;
	
	// 总金额,必填
	@XmlElement(name = "total_fee")
	private int totalFee;
	
	// 交易类型,必填
	@XmlElement(name = "trade_type")
	private String tradeType;
	
	// 签名, 必填
	@XmlElement(name = "sign")
	private String sign;
	
	// 设备号
	@XmlElement(name = "device_info")
	private String deviceInfo;
	
	// 商品详情
	@XmlElement(name = "detail")
	private String detail;
	
	// 附加数据
	@XmlElement(name = "attach")
	private String attach;
	
	// 货币类型
	@XmlElement(name = "fee_type")
	private String feeType;
	
	// 交易起始时间
	@XmlElement(name = "time_start")
	private Date startDateTime;
	
	// 交易结束时间
	@XmlElement(name = "time_expire")
	private Date expireDateTime;
	
	// 商品标记
	@XmlElement(name = "goods_tag")
	private String goodsTag;
	
	// 商品ID
	@XmlElement(name = "product_id")
	private String productId;
	
	public CreateUnifiedOrderRequest(){}
	
	public CreateUnifiedOrderRequest(Order order, String customerIP, TradeType tradeType, String openId) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.appId = WechatPay.APP_ID;
		this.merchantId = WechatPay.MERCHANT_ID;
		this.nonceString = PasswordGenerator.generate(10);
		this.product = WechatPay.PRODUCT;
		this.orderSerialNumber = String.valueOf(order.getId());
		this.totalFee = (int) (order.getTotalDealPrice() * 100);
		this.customerIP = customerIP;
		this.notifyUrl = WechatPay.NOTIFY_URL;
		this.tradeType = tradeType.name();
		this.openId = openId;
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("appid", appId);
		paramMap.put("body", product);
		paramMap.put("mch_id", merchantId);
		paramMap.put("nonce_str", nonceString);
		paramMap.put("notify_url", notifyUrl);
		paramMap.put("openid", openId);
		paramMap.put("out_trade_no", orderSerialNumber);
		paramMap.put("spbill_create_ip", customerIP);
		paramMap.put("total_fee", Integer.toString(totalFee));
		paramMap.put("trade_type", this.tradeType);
		
		this.sign =  WeChatPaySignUtil.sign(paramMap, WechatPay.PAY_SIGN_KEY, WechatPay.INPUT_CHARSET,false);

		
		
	}
	
}
