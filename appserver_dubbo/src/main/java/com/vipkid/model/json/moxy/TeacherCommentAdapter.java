package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Student;
import com.vipkid.model.TeacherComment;

public class TeacherCommentAdapter extends XmlAdapter<TeacherComment, TeacherComment> {

	@Override
	public TeacherComment unmarshal(TeacherComment teacherComment) throws Exception {
		return teacherComment;
	}

	@Override
	public TeacherComment marshal(TeacherComment teacherComment) throws Exception {
		if(teacherComment == null) {
			return null;
		}else {
			TeacherComment simplifiedTeacherComment = new TeacherComment();
			simplifiedTeacherComment.setId(teacherComment.getId());
			simplifiedTeacherComment.setEmpty(teacherComment.isEmpty());
			simplifiedTeacherComment.setStars(teacherComment.getStars());
			simplifiedTeacherComment.setTeacherFeedback(teacherComment.getTeacherFeedback());
			Student simplifiedStudent = new Student();
			Student student = teacherComment.getStudent();
			if (null != student){
				simplifiedStudent.setId(student.getId());
			}
			simplifiedTeacherComment.setStudent(simplifiedStudent);
			return simplifiedTeacherComment;
		}	
	}

}
