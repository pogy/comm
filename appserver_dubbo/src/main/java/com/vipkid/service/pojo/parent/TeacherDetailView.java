package com.vipkid.service.pojo.parent;

import java.io.Serializable;

/**
 * 
* @ClassName: TeacherDetailView 
* @Description: 家长端教师详情
* @author zhangfeipeng 
* @date 2015年6月24日 下午4:35:38 
*
 */
public class TeacherDetailView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 7319076619063960438L;
	
	private long teacherId;
	private String name;
	private String avatar;
	private String shortVideo;
	private boolean hasAvailable;//是否课满
	private long studentId;
	private String introduction;
	private String tag[];
	private String graduatedFrom;//毕业学校
	private String vipkidRemarks;//vipkid 评价
	
	private String gender;
	private int hasAvailableWeek;




	public int getHasAvailableWeek() {
		return hasAvailableWeek;
	}

	public void setHasAvailableWeek(int hasAvailableWeek) {
		this.hasAvailableWeek = hasAvailableWeek;
	}

	public TeacherDetailView(){
		
	}
	
	public String getGender() {
		return gender;
	}
	
	
	public void setGender(String gender) {
		this.gender = gender;
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

	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public String[] getTag() {
		return tag;
	}
	public void setTag(String[] tag) {
		this.tag = tag;
	}
	public String getGraduatedFrom() {
		return graduatedFrom;
	}
	public void setGraduatedFrom(String graduatedFrom) {
		this.graduatedFrom = graduatedFrom;
	}
	public String getVipkidRemarks() {
		return vipkidRemarks;
	}
	public void setVipkidRemarks(String vipkidRemarks) {
		this.vipkidRemarks = vipkidRemarks;
	}

}
