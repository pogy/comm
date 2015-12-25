package com.vipkid.ext.wechat;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.vipkid.ext.wechat.model.EventMessage;
import com.vipkid.ext.wechat.model.MessageHeader;
import com.vipkid.ext.wechat.model.TextMessage;

public class WeChatXmlParser {
	private Logger log = Logger.getLogger(WeChatXmlParser.class.getCanonicalName());
	private AtomicReference<DocumentBuilder> ref = new AtomicReference<DocumentBuilder>();	
	protected Document document = null;
	
	public WeChatXmlParser(InputStream is) throws WeChatServiceExecption{
		parse(is);
	}
	
	private void parse(InputStream is) throws WeChatServiceExecption{
		if (is == null){
			throw new IllegalStateException("InputStream can not be unll");
		}
		if (ref.get() == null){
			DocumentBuilder builder = null;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				log.severe("Exception found when init DocumentBuilder.");
				throw new WeChatServiceExecption(e);
			}
			ref.compareAndSet(null, builder);
		}
		try {
			document = ref.get().parse(is);
		} catch (Exception e) {
			log.severe("Exception found when parsing stream xml.");
			e.printStackTrace();
			throw new WeChatServiceExecption(e);
		} 
	}
	
	public MessageHeader getMessageHeader(){
		String toUserName = getElementByTagName(WeChatElementType.TO_USER_NAME);
		String fromUserName = getElementByTagName(WeChatElementType.FROM_USER_NAME);
		String createTime = getElementByTagName(WeChatElementType.CREATE_TIME);
		String msgType = getElementByTagName(WeChatElementType.MSG_TYPE);
		MessageHeader  messageHeader = new MessageHeader(toUserName, fromUserName, createTime, msgType);
		return messageHeader;
	}
	
	public TextMessage getTextMessage(){
		String content = getElementByTagName(WeChatElementType.CONTENT);
		String msgId = getElementByTagName(WeChatElementType.MSG_ID);
		TextMessage textMessage = new TextMessage(getMessageHeader(), content, msgId);
		return textMessage;

	}
	
	public EventMessage getEventMsg(){
		String event = getElementByTagName(WeChatElementType.EVENT);
		String eventKey = null;
		String ticket = null;
		if (event.equals(EventMessage.CLICK)) {
			eventKey = getElementByTagName(WeChatElementType.EVENT_KEY);
		} else if (event.equals(EventMessage.SUBSCRIBE) || event.equals(EventMessage.UNSUBSCRIBE)) {
			eventKey = getElementByTagName(WeChatElementType.EVENT_KEY);
			ticket = getElementByTagName(WeChatElementType.TICKET);
		}
		EventMessage eventMessage = new EventMessage(getMessageHeader());
		eventMessage.setEvent(event);
		eventMessage.setEventKey(eventKey);
		eventMessage.setTicket(ticket);
		return eventMessage;
	}
	
	public String getElementByTagName(String tagName){
		return document.getElementsByTagName(tagName).item(0).getTextContent();
	}

}
