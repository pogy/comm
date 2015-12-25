package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.StudentComment;

public class StudentCommentAdapter extends XmlAdapter<StudentComment, StudentComment>{

	@Override
	public StudentComment unmarshal(StudentComment studentComment) throws Exception {
		return studentComment;
	}

	@Override
	public StudentComment marshal(StudentComment studentComment) throws Exception {
		if(studentComment == null) {
			return null;
		}else {
			StudentComment simplifiedStudentComment = new StudentComment();
			simplifiedStudentComment.setId(studentComment.getId());
			simplifiedStudentComment.setScores(studentComment.getScores());
			simplifiedStudentComment.setComment(studentComment.getComment());
			return simplifiedStudentComment;
		}	
	}

}
