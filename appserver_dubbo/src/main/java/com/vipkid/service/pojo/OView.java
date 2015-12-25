package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;

import com.vipkid.util.DateTimeUtils;

/**
 * 
* @ClassName: OView 
* @Description: 查询onlineClass 下拉框
* @author zhangfeipeng 
* @date 2015年7月15日 下午1:35:05 
*
 */
public class OView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 3825309444896479229L;
	
	private long id;
	private String name;
	private Date scheduled_date_time;
	private String serial_number;
	private String showName;
	
	public OView(long id,String name,Date scheduled_date_time,String serial_number){
		this.id = id;
		this.name = name;
		this.scheduled_date_time = scheduled_date_time;
		this.serial_number = serial_number;
		this.showName = serial_number+" "+name+" " +DateTimeUtils.format(scheduled_date_time, DateTimeUtils.DATETIME_FORMAT2); 
	}

	public Date getScheduled_date_time() {
		return scheduled_date_time;
	}

	public void setScheduled_date_time(Date scheduled_date_time) {
		this.scheduled_date_time = scheduled_date_time;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public OView(){
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
