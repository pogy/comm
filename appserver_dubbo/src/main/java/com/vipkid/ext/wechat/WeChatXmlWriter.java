package com.vipkid.ext.wechat;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vipkid.ext.wechat.model.MessageHeader;
import com.vipkid.ext.wechat.model.PictureTextMessage;
import com.vipkid.ext.wechat.model.TextMessage;

public class WeChatXmlWriter {
	private Logger log = Logger.getLogger(WeChatXmlWriter.class.getCanonicalName());
	private OutputStream os;
	
	private static TransformerFactory transformerFactory;
	private static  DocumentBuilder builder;
	static {
		transformerFactory = TransformerFactory.newInstance();
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	
	public WeChatXmlWriter(OutputStream os) {
		super();
		this.os = os;
	}

	public Document createNewDocument() throws WeChatServiceExecption{
		Document document = builder.newDocument();
		if (document == null){
			log.severe("Exception found when parsing stream xml.");
			throw new WeChatServiceExecption("Initialize Docment Builder failed");
		}
		return document;
	}
		
	public Element writeMsgHeader(Document document, MessageHeader msgHeader) {
		Element root = document.createElement(WeChatElementType.ROOT);
		
		Element toUserNameElement = document.createElement(WeChatElementType.TO_USER_NAME);
		toUserNameElement.setTextContent(msgHeader.getToUserName());
		Element fromUserNameElement = document.createElement(WeChatElementType.FROM_USER_NAME);
		fromUserNameElement.setTextContent(msgHeader.getFromUserName());
		Element createTimeElement = document.createElement(WeChatElementType.CREATE_TIME);
		createTimeElement.setTextContent(msgHeader.getCreateTime());
		Element msgTypeElement = document.createElement(WeChatElementType.MSG_TYPE);
		msgTypeElement.setTextContent(msgHeader.getMsgType());
		
		root.appendChild(toUserNameElement);
		root.appendChild(fromUserNameElement);
		root.appendChild(createTimeElement);
		root.appendChild(msgTypeElement);
		return root;
	}
	
	public void writeTextMsg(TextMessage textMsg) throws WeChatServiceExecption {
		Document document = createNewDocument();
		Element root = writeMsgHeader(document, textMsg.getMsgHeader());
		
		Element contentElement = document.createElement(WeChatElementType.CONTENT);
		contentElement.setTextContent(textMsg.getContent());
		
		root.appendChild(contentElement);
		
		document.appendChild(root);
		send(document);
	}
	
	public void writePictureTextMsg(PictureTextMessage message) throws WeChatServiceExecption {
		Document document = createNewDocument();
		Element root = writeMsgHeader(document, message.getMsgHeader());
		
		Element articleCount = document.createElement(WeChatElementType.ARTICLE_COUNT);
		articleCount.setTextContent(Integer.toString((message.getArticlesElement().getChildNodes().getLength() - 1) / 2));
		
//		Element articlesElement = document.createElement(WeChatElementType.ARTICLES);
//		articlesElement.appendChild(message.getArticlesElement());
		
		root.appendChild(articleCount);
		Node nodeImportedFromMessage = document.importNode(message.getArticlesElement(), true);
		root.appendChild(nodeImportedFromMessage);
		
		document.appendChild(root);
		
		send(document);
	}
	
	private void send(Document document){
		
		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(new OutputStreamWriter(os,"utf-8")));
		} 
		catch ( Exception e) {
			log.severe("Send to WeiXin platform failed");
		}
	}

}
