package com.vipkid.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;


/**
 * 在线公开课 model
 */
@Entity
@Table(name = "open_class_desc", schema = DBInfo.SCHEMA)
public class OpenClassDesc extends Base {

	
	private static final long serialVersionUID = 1L;

	public enum OpenClassType{
		NORMAL,		//普通公开课
		DEDICATED,	//专属某个渠道公开课
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@NotNull(message = ValidateMessages.NOT_NULL)
	@Column(name = "create_id")
	private long createId;
	
	@NotNull(message = ValidateMessages.NOT_NULL)
	@Column(name = "online_class_id")
	private long onlineClassId;
	
	@NotNull(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "channel_id")
	private String channelId="-1";
	

	@Column(name = "init_num")
	private int initNum;
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "age_range")
	private String ageRange="";
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "introduce")
	private String introduce="";
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "img_src")
	private String imgSrc="";
	
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "img_src_phone")
	private String imgSrcPhone="";
	
	@Enumerated(EnumType.STRING)
	@Column(name = "openclass_type")
	private OpenClassType opType = OpenClassType.NORMAL;
	
	public String getImgSrcPhone() {
		return imgSrcPhone;
	}

	public void setImgSrcPhone(String imgSrcPhone) {
		this.imgSrcPhone = imgSrcPhone;
	}

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "status")
	private boolean status;
	
	public OpenClassDesc(){
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCreateId() {
		return createId;
	}

	public void setCreateId(long createId) {
		this.createId = createId;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public OpenClassType getOpType() {
		return opType;
	}

	public void setOpType(OpenClassType opType) {
		this.opType = opType;
	}

	
}
