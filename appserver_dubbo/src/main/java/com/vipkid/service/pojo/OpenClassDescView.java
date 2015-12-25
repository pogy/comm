package com.vipkid.service.pojo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vipkid.model.OpenClassDesc.OpenClassType;
import com.vipkid.util.DateTimeUtils;

/**
 * 
* @ClassName: OpenClassDescView 
* @Description: 公开课view
* @author zhangfeipeng 
* @date 2015年7月14日 下午2:56:23 
*
 */
public class OpenClassDescView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = -3018639456479271058L;
	
	private long id;
	private long onlineClassId;
	private int initNum;
	private String ageRange="";
	private String introduce="";
	private String imgSrc="";
	private String imgSrcPhone="";
	private boolean status;
	private String lessonSerialNumber;
	private String lessonTopic;
	private String teacherName;
	private long teacherId;
	private Date createTime;
	private String scheduledDateTime;
	private long studentCount;
	private String channelId;
	
	private OpenClassType opType;
	
	
	private String timeVew;
	
	private boolean hasPower;
	
	public boolean isHasPower() {
		return hasPower;
	}
	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}
	
	public String getTimeVew() {
		return timeVew;
	}
	public void setTimeVew(String timeVew) {
		Date time = DateTimeUtils.parse(timeVew, DateTimeUtils.DATETIME_FORMAT2);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm", Locale.CHINESE);
		this.timeVew = df.format(time);
	}
	

	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	private boolean hasOnClass;//是否已经 上课
	
	private openClassType type;//
	
	private boolean sign;//是否已经报名
	
	public boolean isSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
	public openClassType getType() {
		return type;
	}

	public enum openClassType {
		KKSFZZQ,//开课10分钟之前 
		KKSFZZN,//开课10分钟之内
		YKK, //已开课
		YJS //已结束
	}
	
	public boolean isHasOnClass() {
		return hasOnClass;
	}
	public void setHasOnClass(boolean hasOnClass) {
		this.hasOnClass = hasOnClass;
	}
	public long getStudentCount() {
		return studentCount;
	}
	public void setStudentCount(long studentCount) {
		this.studentCount = studentCount;
	}
	public String getScheduledDateTime() {
		return scheduledDateTime;
	}
	public void setScheduledDateTime(String scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
		setType(scheduledDateTime);
		setTimeVew(scheduledDateTime);
	}
	private void setType(String scheduledDateTime) {
		Date time = DateTimeUtils.parse(scheduledDateTime, DateTimeUtils.DATETIME_FORMAT2);
		long t1 = time.getTime()-10*60*1000;
		long t2 = new Date().getTime();
		long t3 = time.getTime();
		long t4 = time.getTime()+60*60*1000;
		if(t2<t1){
			this.type = openClassType.KKSFZZQ;
		}else if(t2>=t1 && t2<t3){
			this.type = openClassType.KKSFZZN;
		}else if(t2>=t3 && t2<t4){
			this.type = openClassType.YKK;
		}else if(t2>=t4){
			this.type = openClassType.YJS;
		}
	}
	public OpenClassDescView(){
		
	}
	public String getImgSrcPhone() {
		return imgSrcPhone;
	}
	public void setImgSrcPhone(String imgSrcPhone) {
		this.imgSrcPhone = imgSrcPhone;
	}
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOnlineClassId() {
		return onlineClassId;
	}

	public void setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
	}

	public int getInitNum() {
		return initNum;
	}

	public void setInitNum(int initNum) {
		this.initNum = initNum;
	}

	public String getAgeRange() {
		return ageRange;
	}
	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getLessonSerialNumber() {
		return lessonSerialNumber;
	}

	public void setLessonSerialNumber(String lessonSerialNumber) {
		this.lessonSerialNumber = lessonSerialNumber;
	}

	public String getLessonTopic() {
		return lessonTopic;
	}

	public void setLessonTopic(String lessonTopic) {
		this.lessonTopic = lessonTopic;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public OpenClassType getOpType() {
		return opType;
	}
	public void setOpType(OpenClassType opType) {
		this.opType = opType;
	}
	
	

}
