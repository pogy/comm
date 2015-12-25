package com.vipkid.service.pojo.parent;

import java.io.Serializable;
/**
 * 
 * @author zhangfeipeng
 * @TODO 用于统计需要续费学生 定时任务
 *
 */
public class StatisticsRenewStudentView implements Serializable{

	private static final long serialVersionUID = 1206006123585391669L;
	
	private long studentId;
	private int leftClassHour;
	private String name;
	private Long cltTeacherId;
	private String cltTeacherName;
	
	

	public Long getCltTeacherId() {
		return cltTeacherId;
	}
	public void setCltTeacherId(Long cltTeacherId) {
		this.cltTeacherId = cltTeacherId;
	}
	public String getCltTeacherName() {
		return cltTeacherName;
	}
	public void setCltTeacherName(String cltTeacherName) {
		this.cltTeacherName = cltTeacherName;
	}
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public int getLeftClassHour() {
		return leftClassHour;
	}
	public void setLeftClassHour(int leftClassHour) {
		this.leftClassHour = leftClassHour;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
