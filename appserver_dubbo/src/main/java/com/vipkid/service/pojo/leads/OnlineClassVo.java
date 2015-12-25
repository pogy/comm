package com.vipkid.service.pojo.leads;

import java.util.ArrayList;
import java.util.List;

import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;

public class OnlineClassVo {
	private Long id;
	private Long scheduledDateTime;
	private String courseName;
	private String lessonSerialNumber;
	private Status status;
	private Long salesId;
	private String salesName;
	private Long tmkId;
	private String tmkName;
	private FinishType finishType;
	private LessonVo lesson;
	private List<StudentVo> students = new ArrayList<StudentVo>();
	private TeacherVo teacher;
	private String classroom;
	private String dbyDocument;
	private boolean canUndoFinish;
	private boolean attatchDocumentSucess;
	private Long cltId;
	private String cltName;
	
	public List<StudentVo> getStudents() {
		return students;
	}
	public void setStudents(List<StudentVo> students) {
		this.students = students;
	}
	public TeacherVo getTeacher() {
		return teacher;
	}
	public void setTeacher(TeacherVo teacher) {
		this.teacher = teacher;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getScheduledDateTime() {
		return scheduledDateTime;
	}
	public void setScheduledDateTime(Long scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getLessonSerialNumber() {
		return lessonSerialNumber;
	}
	public void setLessonSerialNumber(String lessonSerialNumber) {
		this.lessonSerialNumber = lessonSerialNumber;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Long getSalesId() {
		return salesId;
	}
	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	public Long getTmkId() {
		return tmkId;
	}
	public void setTmkId(Long tmkId) {
		this.tmkId = tmkId;
	}
	public String getTmkName() {
		return tmkName;
	}
	public void setTmkName(String tmkName) {
		this.tmkName = tmkName;
	}
	public FinishType getFinishType() {
		return finishType;
	}
	public void setFinishType(FinishType finishType) {
		this.finishType = finishType;
	}

	public LessonVo getLesson() {
		return lesson;
	}
	public void setLesson(LessonVo lesson) {
		this.lesson = lesson;
	}
	
	public String getClassroom() {
		return classroom;
	}
	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}
	public String getDbyDocument() {
		return dbyDocument;
	}
	public void setDbyDocument(String dbyDocument) {
		this.dbyDocument = dbyDocument;
	}
	public boolean isCanUndoFinish() {
		return canUndoFinish;
	}
	public void setCanUndoFinish(boolean canUndoFinish) {
		this.canUndoFinish = canUndoFinish;
	}
	public boolean isAttatchDocumentSucess() {
		return attatchDocumentSucess;
	}
	public void setAttatchDocumentSucess(boolean attatchDocumentSucess) {
		this.attatchDocumentSucess = attatchDocumentSucess;
	}
	public Long getCltId() {
		return cltId;
	}
	public void setCltId(Long cltId) {
		this.cltId = cltId;
	}
	public String getCltName() {
		return cltName;
	}
	public void setCltName(String cltName) {
		this.cltName = cltName;
	}
}
