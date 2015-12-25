package com.vipkid.service.pojo.parent;

import java.io.Serializable;

/**
 * 
* @ClassName: TeView 
* @Description: 用于加载预约课程教师搜索框下拉展示
* @author zhangfeipeng 
* @date 2015年6月19日 下午5:34:23 
*
 */
public class TeView implements Serializable{
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 7161873044931941388L;
	public TeView(){
		
	}
	private String title;
	private String avatar;
	private long id;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
		if(this.avatar!=null&&this.avatar.indexOf("http://resource.vipkid.com.cn/")>-1){
			this.avatar = this.avatar.replace("http://resource.vipkid.com.cn/", "");
		}
		this.avatar = "http://resource.vipkid.com.cn/"+this.avatar;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

}
