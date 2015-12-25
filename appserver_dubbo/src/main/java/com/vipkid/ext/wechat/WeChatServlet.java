package com.vipkid.ext.wechat;

import com.vipkid.ext.wechat.model.*;
import com.vipkid.service.FamilyService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.util.Configurations.Upload;
import com.vipkid.util.Configurations.WeChat;
import com.vipkid.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Servlet implementation class WeChatServlet
 * 微信服务
 */
@WebServlet(value = "/service/public/ext/wechat")
public class WeChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(WeChatServlet.class.getCanonicalName());
	
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public WeChatServlet() {
        super();
    }

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Enter verifyUrl()");
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echoStr = request.getParameter("echostr");
		
		if (signature == null){
			throw new IllegalStateException("signature can not be null.");
		}
		if (timestamp == null){
			throw new IllegalStateException("timestamp can not be null.");
		}
		if (nonce == null){
			throw new IllegalStateException("nonce can not be null.");
		}
		if (echoStr == null){
			throw new IllegalStateException("echoStr can not be null.");
		}
		
		Writer out = response.getWriter();
		String formatedString = WeChatHelper.getWeChatRequriedFormat(signature, timestamp, nonce);
		if (signature.equals(formatedString)) {
			logger.debug("Verified, it's from WeChat and it's consistent with WeChat.");
			out.write(echoStr);
		} else {
			logger.error("signature is not passed in verification phase." + signature + " " + timestamp + " " + nonce);
			out.write("");
		}
		out.flush();
		out.close();
		logger.debug("Leave verifyUrl()");
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		InputStream is = request.getInputStream();
		OutputStream os = response.getOutputStream();
	
		try {
			WeChatXmlParser weChatXmlParser = new WeChatXmlParser(is);
			MessageHeader msgHeader4Read = weChatXmlParser.getMessageHeader();
			
			System.out.println("FromUserName :" + msgHeader4Read.getFromUserName());
			System.out.println("toUserName :" + msgHeader4Read.getToUserName());
			System.out.println("createTime :" + WeChatHelper.getWeChatFormattedDate());
			System.out.println("msgType :" + msgHeader4Read.getMsgType());
			
			if (msgHeader4Read.getMsgType().equals(AbstractMessage.MSG_TYPE_TEXT)){
				TextMessage textMessage = null;
				try {
					textMessage = weChatXmlParser.getTextMessage();
				} catch (NullPointerException e) {
					logger.debug("no text message");
				}

				MessageHeader msgHeader4Write = new MessageHeader(
						msgHeader4Read.getFromUserName(),
						msgHeader4Read.getToUserName(),
						WeChatHelper.getWeChatFormattedDate(),
						AbstractMessage.MSG_TYPE_TEXT);
				
				String replyMsg = "";
				if (textMessage.getContent().equals("丁一晨")||textMessage.getContent().equals("漫画")||textMessage.getContent().equals("翻牌子")) {
					replyMsg = "#丁小点福利社# VIPKID北美外教任您翻牌！点击链接，现在就免费体验美国小学在家上："
							+ "http://t.cn/RAjhZj5。您现在在服务号中，欢迎关注【大米科技】订阅号（橙色logo），获得更多免费少儿英语学习内容！";
				} else {
					replyMsg = "您好！您的信息我们已经收到，小编会尽快回复哒～谢谢关注哦:)";
				}
				TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
				textMsg4Write.setContent(replyMsg);
				WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
				wxXmlWriter.writeTextMsg((textMsg4Write));
			} else if (msgHeader4Read.getMsgType().equals(AbstractMessage.MSG_TYPE_EVENT)) {
				EventMessage eventMsg4Read = weChatXmlParser.getEventMsg();
				System.out.println("evet is " + eventMsg4Read.getEvent());
				
				if (eventMsg4Read.getEvent().equals(EventMessage.SUBSCRIBE)){
					MessageHeader msgHeader4Write = new MessageHeader(
							msgHeader4Read.getFromUserName(),
							msgHeader4Read.getToUserName(),
							WeChatHelper.getWeChatFormattedDate(),
							AbstractMessage.MSG_TYPE_TEXT);
					TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
					textMsg4Write.setContent("亲爱滴爸爸妈妈好！欢迎您加入VIPKID大家庭，最酷的美国小学课堂，最专业的教育干货，都在我们这里啦！回复001~008，阅读Lane老师教育专栏~ 走起！");
					WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
					wxXmlWriter.writeTextMsg((textMsg4Write));
				}
				
				if (eventMsg4Read.getEvent().equals(EventMessage.CLICK)){
					System.out.println("Get Click event and evet key is:" + eventMsg4Read.getEventKey());
					//System.out.println("Get Click event and evet key is:" + eventMsg4Read.getEventKey());
					if (eventMsg4Read.getEventKey().equals(WeChat.MENU_TRIAL_KEY)){
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						textMsg4Write.setContent("感谢您选择VIPKID，请回复手机号码＋姓名，我们的课程顾问会尽快联系您，给您安排试听课程！");
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_COURSE_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						
						StringBuilder bookLinkMessageBuilder = new StringBuilder();
						bookLinkMessageBuilder.append("点击本链接进入\n");
						bookLinkMessageBuilder.append("<a href=\"http://" + WeChat.VIPKID_BASE_URL + "/login/?openid=" + msgHeader4Read.getFromUserName()
								+ "&target=home\">VIPKID首页</a>");
						bookLinkMessageBuilder.append("\n");
						bookLinkMessageBuilder.append("注意：不要将此信息转发给别人。\n");
						
						textMsg4Write.setContent(bookLinkMessageBuilder.toString());
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_BOOK_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						
						StringBuilder bookLinkMessageBuilder = new StringBuilder();
						bookLinkMessageBuilder.append("点击本链接进入\n");
						bookLinkMessageBuilder.append("<a href=\"http://" + WeChat.VIPKID_BASE_URL + "/login/?openid=" + msgHeader4Read.getFromUserName()
								+ "&target=teacherList\">VIPKID约课页面</a>");
						bookLinkMessageBuilder.append("\n");
						bookLinkMessageBuilder.append("注意：不要将此信息转发给别人。\n");
						
						textMsg4Write.setContent(bookLinkMessageBuilder.toString());
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_LESSONS_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						
						StringBuilder bookLinkMessageBuilder = new StringBuilder();
						bookLinkMessageBuilder.append("点击以下链接进入\n");
						bookLinkMessageBuilder.append("<a href=\"http://" + WeChat.VIPKID_BASE_URL + "/login/?openid=" + msgHeader4Read.getFromUserName()
								+ "&target=dashboard\">VIPKID课程管理-我的课程表</a>");
						bookLinkMessageBuilder.append("\n");
						bookLinkMessageBuilder.append("注意：不要将此信息转发给别人。\n");
						
						textMsg4Write.setContent(bookLinkMessageBuilder.toString());
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_ORDERS_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						
						StringBuilder bookLinkMessageBuilder = new StringBuilder();
						bookLinkMessageBuilder.append("点击以下链接进入\n");
						bookLinkMessageBuilder.append("<a href=\"http://" + WeChat.VIPKID_BASE_URL + "/login/?openid=" + msgHeader4Read.getFromUserName()
								+ "&target=orders\">VIPKID订单页面</a>");
						bookLinkMessageBuilder.append("\n");
						bookLinkMessageBuilder.append("注意：不要将此信息转发给别人。\n");
						
						textMsg4Write.setContent(bookLinkMessageBuilder.toString());
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_ACCOUNT_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						
						StringBuilder bookLinkMessageBuilder = new StringBuilder();
						bookLinkMessageBuilder.append("点击以下链接进入\n");
						bookLinkMessageBuilder.append("<a href=\"http://" + WeChat.VIPKID_BASE_URL + "/login/?openid=" + msgHeader4Read.getFromUserName()
								+ "&target=account\">VIPKID个人信息页面</a>");
						bookLinkMessageBuilder.append("\n");
						bookLinkMessageBuilder.append("注意：不要将此信息转发给别人。\n");
						
						textMsg4Write.setContent(bookLinkMessageBuilder.toString());
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_STORYBOX_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_IMAGE_TEXT);
						
						PictureTextMessage pictureTextMsg4Write = new PictureTextMessage(msgHeader4Write);
						
						WeChatUserXmlFileParser userXmlFileParser = new WeChatUserXmlFileParser(new FileInputStream(Upload.WECHAT + "storybox.xml"));
						
						Node articlesElement = userXmlFileParser.getArticlesElement();
						
						pictureTextMsg4Write.setArticlesElement(articlesElement);
						
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writePictureTextMsg(pictureTextMsg4Write);
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_HOTARTICLE_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_IMAGE_TEXT);
						PictureTextMessage pictureTextMsg4Write = new PictureTextMessage(msgHeader4Write);
						
						WeChatUserXmlFileParser userXmlFileParser = new WeChatUserXmlFileParser(new FileInputStream(Upload.WECHAT + "hotartical.xml"));
						
						Node articlesElement = userXmlFileParser.getArticlesElement();
						
						pictureTextMsg4Write.setArticlesElement(articlesElement);
						
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writePictureTextMsg(pictureTextMsg4Write);
					} else if (eventMsg4Read.getEventKey().equals(WeChat.MENU_INVATATION_KEY)) {
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_IMAGE_TEXT);
						PictureTextMessage pictureTextMsg4Write = new PictureTextMessage(msgHeader4Write);
						
						WeChatUserXmlFileParser userXmlFileParser = new WeChatUserXmlFileParser(new FileInputStream(Upload.WECHAT + "invatation.xml"));
                        FamilyService familyService = SpringUtil.getSpringBean(request,"familyService",FamilyService.class);
                        ParentAuthService parentAuthService = SpringUtil.getSpringBean(request,"parentAuthService",ParentAuthService.class);
						userXmlFileParser.setUrlElementAppendOpenidOrInvitationId(msgHeader4Read.getFromUserName(), familyService, parentAuthService);
						
						Node articlesElement = userXmlFileParser.getArticlesElement();
						
						pictureTextMsg4Write.setArticlesElement(articlesElement);
						
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writePictureTextMsg(pictureTextMsg4Write);
					}
				}
			}
		} catch(WeChatServiceExecption wse){
			System.out.println(wse.getLocalizedMessage());
		}
		
		is.close();
		os.close();
		
		System.out.println("Leave doPost()");
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	 */
	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Enter doPost()");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		InputStream is = request.getInputStream();
		OutputStream os = response.getOutputStream();
	
		try {
			WeChatXmlParser weChatXmlParser = new WeChatXmlParser(is);
			MessageHeader msgHeader4Read = weChatXmlParser.getMessageHeader();
			logger.error("FromUserName :" + msgHeader4Read.getFromUserName());
			logger.error("toUserName :" + msgHeader4Read.getToUserName());
			logger.error("createTime :" + WeChatHelper.getWeChatFormattedDate());
			logger.error("msgType :" + msgHeader4Read.getMsgType());
			if (msgHeader4Read.getMsgType().equals(AbstractMessage.MSG_TYPE_TEXT)){
				//TextMsg textMsg4Read = wxXmlParser.getTextMsg();
				MessageHeader msgHeader4Write = new MessageHeader(
						msgHeader4Read.getFromUserName(),
						msgHeader4Read.getToUserName(),
						WeChatHelper.getWeChatFormattedDate(),
						AbstractMessage.MSG_TYPE_TEXT);
				String receivedMsg = weChatXmlParser.getTextMessage().getContent();
				String replyMsg = "";
				Matcher matcher = Pattern.compile("\\s*(\\d{11})+(\\w+)").matcher(receivedMsg);
				if (matcher.matches()){
					String phoneInfo = matcher.group(0);
					String name = matcher.group(1);
					replyMsg ="感谢" + name + "先生／女士，" + "预约试听课，我们的课程顾问会尽快联系你。";
					 // when a user click "申请试听",send email to consultant focal,use new email template
					AbstractTemplates template= new EmailTemplates(this.getServletContext());//
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					paramsMap.put(EmailTemplates.NewTrialEmailTemplate.TRIAL_USER_NAME, name);
					paramsMap.put(EmailTemplates.NewTrialEmailTemplate.TRIAL_USER_CONTACT_INFO, phoneInfo);
					String content = template.render(EmailTemplates.NewTrialEmailTemplate.NAME, paramsMap);
				    //send email out
					MailSenderFactory.getMailSender().sendFromVIPKID("zhaohongliang@vipkid.com.cn", EmailTemplates.NewTrialEmailTemplate.SUBJECT, content);
				
				} else{
					replyMsg = "请您输入如下格式：\n" 
				                + "手机号码＋姓名。 \n" 
							    + "	例如：" 
				                + "18600671593＋赵先生";
				}
				TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
				textMsg4Write.setContent(replyMsg);
				WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
				wxXmlWriter.writeTextMsg((textMsg4Write));
			} else if (msgHeader4Read.getMsgType().equals(AbstractMessage.MSG_TYPE_EVENT)){
				EventMessage eventMsg4Read = weChatXmlParser.getEventMsg();
				logger.error("evet is " + eventMsg4Read.getEvent());
				if (eventMsg4Read.getEvent().equals(EventMessage.SUBSCRIBE)){
					MessageHeader msgHeader4Write = new MessageHeader(
							msgHeader4Read.getFromUserName(),
							msgHeader4Read.getToUserName(),
							WeChatHelper.getWeChatFormattedDate(),
							AbstractMessage.MSG_TYPE_TEXT);
					TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
					textMsg4Write.setContent("Welcome to VIPKID!");
					WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
					wxXmlWriter.writeTextMsg((textMsg4Write));
				}
				if (eventMsg4Read.getEvent().equals(EventMessage.CLICK)){
					logger.error("Get Click event and evet key is:" + eventMsg4Read.getEventKey());
					//logger.debug("Get Click event and evet key is:" + eventMsg4Read.getEventKey());
					if (eventMsg4Read.getEventKey().equals(WeChat.MENU_TRIAL_KEY)){
						MessageHeader msgHeader4Write = new MessageHeader(
								msgHeader4Read.getFromUserName(),
								msgHeader4Read.getToUserName(),
								WeChatHelper.getWeChatFormattedDate(),
								AbstractMessage.MSG_TYPE_TEXT);
						TextMessage textMsg4Write = new TextMessage(msgHeader4Write);
						textMsg4Write.setContent("感谢您选择VIPKID，请回复手机号码＋姓名，我们的课程顾问会尽快联系您，给您安排试听课程！");
						WeChatXmlWriter wxXmlWriter = new WeChatXmlWriter(os);
						wxXmlWriter.writeTextMsg((textMsg4Write));
					}
				}
			} 
		} catch(WeChatServiceExecption wse){
			logger.error(wse.getLocalizedMessage());
		}
		is.close();
		os.close();
		logger.debug("Leave doPost()");
	}
	*/

}
