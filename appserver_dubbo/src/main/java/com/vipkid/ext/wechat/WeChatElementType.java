package com.vipkid.ext.wechat;

public interface WeChatElementType {
	//common element type
	String ROOT = "xml";
	String TO_USER_NAME = "ToUserName";
	String FROM_USER_NAME = "FromUserName";
	String CREATE_TIME = "CreateTime";
	String MSG_TYPE = "MsgType";

	String MSG_ID = "MsgId";

	String CONTENT = "Content";
	String FUNC_FLAG = "FuncFlag";

	String PIC_URL = "PicUrl";
    //music element type
	String TITLE = "Title";
	String DESCRITION = "Description";
	String URL = "Url";
	String MUSIC_URL = "MusicUrl";
	String HQ_MUSIC_URL = "HQMusicUrl";
	String MUSIC = "Music";

	//event element type
	String EVENT = "Event";
	String EVENT_KEY = "EventKey";
	String TICKET = "Ticket";
	String LATITUDE = "Latitude";
	String LONGITUDE = "Longitude";
	String PRECISION = "Precision";


	//location element type
	String LOCATION_X = "Location_X";
	String LOCATION_Y = "Location_Y";
	String SCALE = "Scale";
	String LABEL = "Label";



	String ARTICLE_COUNT = "ArticleCount";
	String ARTICLES = "Articles";

	String ITEM = "item";

	String MEDIAID = "MediaId";
	String FORMAT = "Format";
	String RECOGNITION = "Recognition";

	String THUMBMEDIAID = "ThumbMediaId";

	String IMAGE = "Image";

	String VOICE = "Voice";
	String VIDEO = "Video";

}
