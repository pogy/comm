package com.vipkid.service.pojo.parent;

import java.io.Serializable;
import java.util.Date;

import com.vipkid.model.OnlineClass.Status;
/**
 * 
* @ClassName: OnlineClassesView 
* @Description: 用于展示pc家长端预约课程功能的时间表
* @author zhangfeipeng 
* @date 2015年6月17日 下午5:20:51 
*
 */
public class OnlineClassesView implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = -3627381422745956312L;
	
	private long id;
	private Date scheduledDateTime;
	private long teacherId;
	private String teacherName;
	private String status;
	private String y;
	private String x;
	private int type;//教师模式下 1 当前用户约的所选老师的课  2 当前用户时间已约其他用户
	private long oid;//1 当前用户已约 当前时间其他老师课程的onlineclassid
	private long oteacherId;//当前用户当前时间已约其他老师的id
	private String oteacherName;//当前用户当前时间已约其他老师的name
	
	//日历模式下 
	//-1、表明当前学生当前时间已经约定课程 ，但教师不在目前查询条件下 并且当前没有老师有空闲时间 
	//1、 表明此时间点 仅有一个老师 并且已被当前学生约  
	//2、表明当前学生当前时间已经约定课程 ，但教师不在目前查询条件下  并且当前有老师有空闲时间  
	//3、表明当前学生当前时间已经约定课程 ，教师在目前查询条件下 ，并且当前时间内还有其他老师有空余时间
	//4、表明当前时间老师有空余时间，并且当前时间当前学生没有预约任何课程
	//修改版 CalendarTy 1 可预约 ,2已预约
	private int CalendarType;
	
	
	private int size;//日历模式下用户统计某一时间点上 存在多少个老师有空余时间
	private int bookSize;//日历模式下用户统计某一时间点上 存在多少个老师book
	
	
	
	
	public OnlineClassesView(){}
	public OnlineClassesView(long id,
			Status status,
			Date scheduledDateTime,
			long teacherId,
			String teacherName
			){
		this.id = id;
		this.scheduledDateTime = scheduledDateTime;
		this.teacherId = teacherId;
		this.status = status==null?"":status.toString();
		this.teacherName = teacherName;
		
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public long getOteacherId() {
		return oteacherId;
	}
	public void setOteacherId(long oteacherId) {
		this.oteacherId = oteacherId;
	}
	public String getOteacherName() {
		return oteacherName;
	}
	public void setOteacherName(String oteacherName) {
		this.oteacherName = oteacherName;
	}
	
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getScheduledDateTime() {
		return scheduledDateTime;
	}
	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}
	public long getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getCalendarType() {
		return CalendarType;
	}
	public void setCalendarType(int calendarType) {
		CalendarType = calendarType;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getBookSize() {
		return bookSize;
	}
	public void setBookSize(int bookSize) {
		this.bookSize = bookSize;
	}

}
