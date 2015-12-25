package com.vipkid.model.json.moxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.OnlineClass;
import com.vipkid.model.Student;
import com.vipkid.model.StudentComment;
import com.vipkid.model.Teacher;

public class OnlineClassAdapter extends XmlAdapter<OnlineClass, OnlineClass> {

	@Override
	public OnlineClass unmarshal(OnlineClass onlineClass) throws Exception {
		return onlineClass;
	}

	@Override
	public OnlineClass marshal(OnlineClass onlineClass) throws Exception {
		if(onlineClass == null || onlineClass.getId() == 0) {
			return null;
		}else {
			OnlineClass simplifiedOnlineClass = new OnlineClass();
			simplifiedOnlineClass.setId(onlineClass.getId());
			simplifiedOnlineClass.setSerialNumber(onlineClass.getSerialNumber());
			simplifiedOnlineClass.setScheduledDateTime(onlineClass.getScheduledDateTime());
			simplifiedOnlineClass.setAbleToEnterClassroomDateTime(onlineClass.getAbleToEnterClassroomDateTime());
			simplifiedOnlineClass.setStudentEnglishNames(onlineClass.getStudentEnglishNames());
			simplifiedOnlineClass.setComments(onlineClass.getComments());
			simplifiedOnlineClass.setPayrollItem(onlineClass.getPayrollItem());
			simplifiedOnlineClass.setConsumeClassHour(onlineClass.isConsumeClassHour());
			simplifiedOnlineClass.setStatus(onlineClass.getStatus());
			simplifiedOnlineClass.setLesson(onlineClass.getLesson());

			//I can not remove teacher directly because I don't know where it was used. So simplify it
			Teacher simplifiedTeacher = new Teacher();
			if(onlineClass.getTeacher()!=null){
				simplifiedTeacher.setName(onlineClass.getTeacher().getName());
				simplifiedTeacher.setRealName(onlineClass.getTeacher().getRealName());
				simplifiedTeacher.setId(onlineClass.getTeacher().getId());
				simplifiedTeacher.setSkype(onlineClass.getTeacher().getSkype());
			}
			simplifiedOnlineClass.setTeacher(simplifiedTeacher);
			simplifiedOnlineClass.setDemoReport(onlineClass.getDemoReport());
			simplifiedOnlineClass.setAttatchDocumentSucess(onlineClass.isAttatchDocumentSucess());
			
			for (Student student : onlineClass.getStudents()) {
				Student simplifiedStudent = new Student();
				simplifiedStudent.setId(student.getId());
				simplifiedStudent.setName(student.getName());
				simplifiedStudent.setEnglishName(student.getEnglishName());
				simplifiedOnlineClass.addStudent(simplifiedStudent);
			}
			
			List<StudentComment> list = new ArrayList<StudentComment>();
			List<StudentComment> studentComments = onlineClass.getStudentComments();
			if (null != studentComments) {
				for (StudentComment comment : studentComments) {
					StudentComment simplifiedStudentComment = new StudentComment();
					simplifiedStudentComment.setScores(comment.getScores());
					simplifiedStudentComment.setComment(comment.getComment());
					list.add(simplifiedStudentComment);
				}
				if (list.size() > 0) {
					simplifiedOnlineClass.setStudentComments(list);
				}
			}
			simplifiedOnlineClass.setFinishType(onlineClass.getFinishType());
			return simplifiedOnlineClass;
		}	
	}
}

