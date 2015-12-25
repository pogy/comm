package com.vipkid.ext.wechatpay;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class CDATAXmlAdapter extends XmlAdapter<String, String> {

	@Override
	public String marshal(String string) throws Exception {
		return string;
	}

	@Override
	public String unmarshal(String string) throws Exception {
		return string.replace("<![CDATA[", "").replace("]]>", "");
	}

}
