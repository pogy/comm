package com.vipkid.ext.wechat.model;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.annotations.SerializedName;
import com.vipkid.model.json.gson.GsonManager;
import com.vipkid.util.CharSet;

public class MessageToUser {
	
	//@Expose(serialize=false)
	private static String ACCESS_TOKEN = "vb6pOjYrzSrxrlQyX-Ik1PFQJE3JIu76ux-hAtEHPWXJm5P2hHCFl_Dj2b14ZyZAFKXwZQcImNQLBQ1fP2psAQ";
	
	private String touser;
	private String msgtype;
	
	@SerializedName("text")
	private TextMessage textMessage;
	
	@SerializedName("image")
	private ImageMessage imageMessage;
	
	@SerializedName("voice")
	private VoiceMessage voiceMessage;
	
	@SerializedName("video")
	private VideoMessage videoMessage;

	
	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public TextMessage getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(TextMessage textMessage) {
		this.textMessage = textMessage;
	}
	
	public ImageMessage getImageMessage() {
		return imageMessage;
	}

	public void setImageMessage(ImageMessage imageMessage) {
		this.imageMessage = imageMessage;
	}

	public VoiceMessage getVoiceMessage() {
		return voiceMessage;
	}

	public void setVoiceMessage(VoiceMessage voiceMessage) {
		this.voiceMessage = voiceMessage;
	}

	public VideoMessage getVideoMessage() {
		return videoMessage;
	}

	public void setVideoMessage(VideoMessage videoMessage) {
		this.videoMessage = videoMessage;
	}

	@Override
	public String toString() {
		return GsonManager.getInstance().getGson().toJson(this);
	}
	
	public void sendMessage() {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			//accesToken还存在过期的问题（从获取开始，有效期限7200s）
			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + MessageToUser.ACCESS_TOKEN;
			HttpPost httpPost = new HttpPost(url);
			StringEntity message = new StringEntity(this.toString(), CharSet.UTF_8);
	        message.setContentType("application/json; charset=UTF-8");
	        httpPost.setEntity(message);
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
	        httpClient.execute(httpPost);
	        httpClient.close();
		} catch(Exception e) {
			System.out.println("change to gson error :" + e.getMessage());
		}
	}
}
