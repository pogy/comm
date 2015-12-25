package com.vipkid.rest.vo.query;

import java.util.Date;
import java.util.List;

import com.vipkid.model.OnlineClass.FinishType;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.StudentComment;

/**
 * OnlineClass VO 对象，和列表相关的数据
 * Created by zfl on 2015/6/12.
 */
public class OnlineClassVO {
    private Long id;
    private String onlineClassName;
    private LessonVO lesson;
    private Date scheduledDateTime;
    private TeacherQueryCourseView teacher;
    private StudentQueryStaffView[] students;
    private UserVO booker;
    private DemoReportVO demoReport;
    private FinishType finishType;
    private Status status;
    private String comments;
    private CommentVO firemanToTeacherComment;
    private CommentVO[] firemanToStudentComments;
    private CommentVO[] teacherComments;
    
    private List<StudentComment> studentComments;

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOnlineClassName() {
        return onlineClassName;
    }

    public void setOnlineClassName(String onlineClassName) {
        this.onlineClassName = onlineClassName;
    }

    public LessonVO getLesson() {
        return lesson;
    }

    public void setLesson(LessonVO lesson) {
        this.lesson = lesson;
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public TeacherQueryCourseView getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherQueryCourseView teacher) {
        this.teacher = teacher;
    }

    public StudentQueryStaffView[] getStudents() {
        return students;
    }

    public void setStudents(StudentQueryStaffView[] students) {
        this.students = students;
    }

    public UserVO getBooker() {
        return booker;
    }

    public void setBooker(UserVO booker) {
        this.booker = booker;
    }

    public DemoReportVO getDemoReport() {
        return demoReport;
    }

    public void setDemoReport(DemoReportVO demoReport) {
        this.demoReport = demoReport;
    }
    
    public FinishType getFinishType() {
		return finishType;
	}

	public void setFinishType(FinishType finishType) {
		this.finishType = finishType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public CommentVO getFiremanToTeacherComment() {
		return firemanToTeacherComment;
	}

	public void setFiremanToTeacherComment(CommentVO firemanToTeacherComment) {
		this.firemanToTeacherComment = firemanToTeacherComment;
	}

	public CommentVO[] getFiremanToStudentComments() {
		return firemanToStudentComments;
	}

	public void setFiremanToStudentComments(CommentVO[] firemanToStudentComments) {
		this.firemanToStudentComments = firemanToStudentComments;
	}

	public CommentVO[] getTeacherComments() {
		return teacherComments;
	}

	public void setTeacherComments(CommentVO[] teacherComments) {
		this.teacherComments = teacherComments;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<StudentComment> getStudentComments() {
		return studentComments;
	}

	public void setStudentComments(List<StudentComment> studentComments) {
		this.studentComments = studentComments;
	}
}
