package com.vipkid.ext.wechat;

import java.io.InputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vipkid.model.Parent;
import com.vipkid.service.FamilyService;
import com.vipkid.service.ParentAuthService;

public class WeChatUserXmlFileParser extends WeChatXmlParser {
//  for user upload xml file parsing, this file would be like:
//	<xml>
//	<ToUserName><![CDATA[toUser]]></ToUserName>
//	<FromUserName><![CDATA[fromUser]]></FromUserName>
//	<CreateTime>12345678</CreateTime>
//	<MsgType><![CDATA[news]]></MsgType>
//	<ArticleCount>2</ArticleCount>
//	<Articles>
//	<item>
//	<Title><![CDATA[title1]]></Title> 
//	<Description><![CDATA[description1]]></Description>
//	<PicUrl><![CDATA[picurl]]></PicUrl>
//	<Url><![CDATA[url]]></Url>
//	</item>
//	<item>
//	<Title><![CDATA[title]]></Title>
//	<Description><![CDATA[description]]></Description>
//	<PicUrl><![CDATA[picurl]]></PicUrl>
//	<Url><![CDATA[url]]></Url>
//	</item>
//	</Articles>
//	</xml>
	public WeChatUserXmlFileParser(InputStream is) throws WeChatServiceExecption {
		super(is);
	}
	
	public Node getArticlesElement () {
		return document.getElementsByTagName(WeChatElementType.ARTICLES).item(0);
	}
	
	public void setUrlElementAppendOpenidOrInvitationId (String openid, FamilyService familyService, ParentAuthService parentAuthService) {
		NodeList urlElements = document.getElementsByTagName("Url");
		for (int i = 0; i <= urlElements.getLength()-1; i++) {

			String urlString = urlElements.item(i).getTextContent();
			if (urlString.matches(".*openid$")) {
				urlString += "=" + openid;
			} else if (urlString.matches(".*invitation_id$")) {
				Parent parent = parentAuthService.findByOpenId(openid);
				long familyId = parent.getFamily().getId();
				String invitation_id = "=" + familyService.setUrlParamForInvitationPage(familyId).getInvitationId();
				urlString += invitation_id;
			} else {
				urlString += "openid=" + openid;
				urlElements.item(i).setTextContent(urlString);
			}
			urlElements.item(i).setTextContent(urlString);
		}
	}

}
