package com.vipkid.service.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.Course;
import com.vipkid.model.Lesson;
import com.vipkid.model.Student;
import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;

public class OnlineClassView implements Serializable {
	
		private static final long serialVersionUID = 1L;

		private long id;

		private String serialNumber;

		private Lesson lesson;

		@XmlJavaTypeAdapter(DateTimeAdapter.class)
		private Date scheduledDateTime;

		private Status status;
		
		private FinishType finishType;
		
		private String classroom;
		
		@XmlJavaTypeAdapter(StudentAdapter.class)
		private List<Student> students = new LinkedList<Student>();
		
		private String studentEnglishNames;
		
		private boolean shortNotice;
		
		private boolean backup;
		
		//2015-07-15 添加course mode类型（1v1 1vN）
		private Course.Mode courseMode;

		public boolean isBackup() {
			return backup;
		}

		public void setBackup(boolean backup) {
			this.backup = backup;
		}

		public boolean isShortNotice() {
			return shortNotice;
		}

		public void setShortNotice(boolean shortNotice) {
			this.shortNotice = shortNotice;
		}

		public String getStudentEnglishNames() {
			return studentEnglishNames;
		}

		public void setStudentEnglishNames(String studentEnglishNames) {
			this.studentEnglishNames = studentEnglishNames;
		}

		public List<Student> getStudents() {
			return students;
		}

		public void setStudents(List<Student> students) {
			this.students = students;
		}

		public String getClassroom() {
			return classroom;
		}

		public void setClassroom(String classroom) {
			this.classroom = classroom;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public void setLesson(Lesson lesson) {
			this.lesson = lesson;
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

		public Course.Mode getCourseMode() {
			return courseMode;
		}

		public void setCourseMode(Course.Mode courseMode) {
			this.courseMode = courseMode;
		}
		
}
