package com.vipkid.service.pojo.parent;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.vipkid.model.Gender;

/**
 * 
* @ClassName: TeachersView 
* @Description: 用于展示家长端我的老师页面展示
* @author zhangfeipeng 
* @date 2015年6月9日 上午11:23:41 
*
 */
public class TeachersView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 2787802923717730894L;
	
	private long teacherId;
	private String name;
	private String avatar;
	private String shortVideo;
	private boolean hasAvailable;//是否课满
	
	private int hasAvailableWeek;
	
	public int getHasAvailableWeek() {
		return hasAvailableWeek;
	}
	public void setHasAvailableWeek(int hasAvailableWeek) {
		this.hasAvailableWeek = hasAvailableWeek;
	}
	private long studentId;
	private String tag[];
	
	private long onlineClassId;
	private String status;
	private String gender;
	public TeachersView(){
		
	}
	public TeachersView(long teacherId,
			String name,
			String avatar,
			String shortVideo,
			Gender gender){
		this.teacherId=teacherId;
		this.name=name;
		this.avatar=avatar;
		this.shortVideo=shortVideo;
		this.gender = gender==null?"MALE":gender.name();
		if(StringUtils.isBlank(avatar)){
			if(this.gender.equals("MALE")){
				this.avatar = "static/images/common/boyteacher.jpg";
			}else{
				this.avatar = "static/images/common/girlteacher.jpg";
			}
		}
		if(this.avatar!=null&&this.avatar.indexOf("http://resource.vipkid.com.cn/")>-1){
			this.avatar = this.avatar.replace("http://resource.vipkid.com.cn/", "");
		}
		
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	
	public String[] getTag() {
		return tag;
	}
	public void setTag(String[] tag) {
		this.tag = tag;
	}
	public long getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
		if(this.avatar!=null&&this.avatar.indexOf("http://resource.vipkid.com.cn/")>-1){
			this.avatar = this.avatar.replace("http://resource.vipkid.com.cn/", "");
		}
	}

	public String getShortVideo() {
		return shortVideo;
	}

	public void setShortVideo(String shortVideo) {
		this.shortVideo = shortVideo;
	}
	
	public boolean isHasAvailable() {
		return hasAvailable;
	}
	public void setHasAvailable(boolean hasAvailable) {
		this.hasAvailable = hasAvailable;
	}
	public long getOnlineClassId() {
		return onlineClassId;
	}
	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
