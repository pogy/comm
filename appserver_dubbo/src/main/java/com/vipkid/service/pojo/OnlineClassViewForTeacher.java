package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.vipkid.model.Lesson;
import com.vipkid.model.Student;
import com.vipkid.model.TeacherComment;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;

public class OnlineClassViewForTeacher implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	
	private Lesson lesson;
	
	private List<Student> students = new LinkedList<Student>();
	
	private Date scheduledDateTime;
	
	private Status status;
	
	private FinishType finishType;
	
	private List<TeacherComment> teacherComments;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
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

	public List<TeacherComment> getTeacherComments() {
		return teacherComments;
	}

	public void setTeacherComments(List<TeacherComment> teacherComments) {
		this.teacherComments = teacherComments;
	}
	
}
