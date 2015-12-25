package com.vipkid.model.json.moxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Family;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;

public class StudentAdapter extends XmlAdapter<Student, Student> {

	@Override
	public Student unmarshal(Student student) throws Exception {
		return student;
	}

	@Override
	public Student marshal(Student student) throws Exception {
		if(student == null) {
			return null;
		}else {
			Student simplifiedStudent = new Student();
			simplifiedStudent.setId(student.getId());
			simplifiedStudent.setName(student.getName());
			simplifiedStudent.setEnglishName(student.getEnglishName());
			simplifiedStudent.setCreater(student.getCreater());
			simplifiedStudent.setCreateDateTime(student.getCreateDateTime());
			simplifiedStudent.setRegisterDateTime(student.getRegisterDateTime());
			simplifiedStudent.setLastEditor(student.getLastEditor());
			simplifiedStudent.setLastEditDateTime(student.getLastEditDateTime());
			simplifiedStudent.setWelcome(student.isWelcome());
			simplifiedStudent.setStars(student.getStars());
			simplifiedStudent.setKnowTheStudent(student.getKnowTheStudent());
			simplifiedStudent.setSales(student.getSales());
			
			Family simplifiedFamily = new Family();
			List<Parent> simplifiedParents = new ArrayList<Parent>();
			if(student.getFamily() != null) {
				simplifiedFamily.setId(student.getFamily().getId());
				simplifiedFamily.setPhone(student.getFamily().getPhone());
				if (student.getFamily().getParents() != null){
					for(Parent parent : student.getFamily().getParents()) {
						Parent simplifiedParent = new Parent();
						simplifiedParent.setId(parent.getId());
						simplifiedParent.setName(parent.getName());
						simplifiedParent.setMobile(parent.getMobile());
						simplifiedParent.setRelation(parent.getRelation());
						simplifiedParent.setDecisionMaker(parent.isDecisionMaker());
						simplifiedParents.add(simplifiedParent);
					}
				}
			}			
			simplifiedFamily.setParents(simplifiedParents);
			simplifiedStudent.setFamily(simplifiedFamily);
			simplifiedStudent.arrangeParents();
			return simplifiedStudent;
		}	
	}

}
