package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.Course.Type;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.json.moxy.DateTimeAdapter;

public class OnlineClassPeakTimeView implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledDateTime;	
	
	private Status status;
	
	private FinishType finishType;
	
	private Type type;
	
	private long teacherId;
	
	public long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}

	public OnlineClassPeakTimeView(long id, Status status,FinishType finishType, Date scheduledDateTime,long teacherId){
		this.id = id;
		this.status = status;
		this.finishType = finishType;
		this.scheduledDateTime = scheduledDateTime;
		this.teacherId = teacherId;
		//this.type = type;
		
	}
	
	public OnlineClassPeakTimeView(long id, Status status,FinishType finishType, Date scheduledDateTime,Type type,long teacherId){
		this.id = id;
		this.status = status;
		this.finishType = finishType;
		this.scheduledDateTime = scheduledDateTime;
		this.type = type;
        this.teacherId = teacherId;
		
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public FinishType getFinishType() {
		return finishType;
	}

	public void setFinishType(FinishType finishType) {
		this.finishType = finishType;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	


}
