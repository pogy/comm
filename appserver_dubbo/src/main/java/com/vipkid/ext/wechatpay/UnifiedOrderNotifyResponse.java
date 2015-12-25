package com.vipkid.ext.wechatpay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class UnifiedOrderNotifyResponse {
	
	@XmlElement(name = "return_code")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String returnCode;
	
	@XmlElement(name = "return_msg")
	@XmlJavaTypeAdapter(CDATAXmlAdapter.class)
	private String returnMsg;

	public UnifiedOrderNotifyResponse(String returnCode,String returnMsg) {
		this.returnCode = returnCode;
		this.returnMsg = returnMsg;
	}
	
	public UnifiedOrderNotifyResponse() {
		
	}
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

}
