package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.StudentExam;

public class StudentExamAdapter extends XmlAdapter<StudentExam, StudentExam> {
	@Override
	public StudentExam unmarshal(StudentExam studentExam) throws Exception {
		return studentExam;
	}

	@Override
	public StudentExam marshal(StudentExam studentExam) throws Exception {
		if(studentExam == null) {
			return null;
		}else {
			//
			return studentExam;
		}	
	}
}
