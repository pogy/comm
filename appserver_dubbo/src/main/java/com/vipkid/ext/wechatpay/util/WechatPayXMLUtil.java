package com.vipkid.ext.wechatpay.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WechatPayXMLUtil {

    
    public static <T> String marshal(T jaxbElement,Class<T> clazz) throws JAXBException {
    	JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
        Writer stringWriter = new StringWriter();
        jaxbMarshaller.marshal(jaxbElement, stringWriter);
        return stringWriter.toString();
    }
    
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String xml, Class<T> clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Reader stringReader = new StringReader(xml);
		return (T) unmarshaller.unmarshal(stringReader);
	}
	

	/**
	 * 把微信支付 报文 xml 解析为 map
	 * @param xml
	 * @return
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public static Map<String,String> parseToMap(String xml) throws SAXException, IOException, ParserConfigurationException{
		Map<String,String> map = new HashMap<String,String>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					map.put(node.getNodeName(), node.getTextContent());
				}
				
			}
		}
		
		return map;
	}
	

}
