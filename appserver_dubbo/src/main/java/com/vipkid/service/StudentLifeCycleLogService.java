package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.StudentLifeCycleLog;
import com.vipkid.repository.StudentLifeCycleLogRepository;
import com.vipkid.repository.StudentRepository;

@Service
public class StudentLifeCycleLogService {

	private Logger logger = LoggerFactory.getLogger(StudentLifeCycleLogService.class.getSimpleName());
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private StudentLifeCycleLogRepository studentLifeCycleLogRepository;
	
	public void doChangeLifeCycle(long studentId, Student.LifeCycle from, Student.LifeCycle to) {
		logger.debug("change Life Cycle for student: {}, from {}, to {}", studentId, from, to);
		
		Student student = studentRepository.find(studentId);
		
		doChangeLifeCycle(student, from, to);
	}
	
	public void doChangeLifeCycle(Student student, Student.LifeCycle from, Student.LifeCycle to) {
		logger.debug("change Life Cycle for student: {}, from {}, to {}", student.getId(), from, to);
		
		if (null != student) {
			if (from != null && to != null && from.ordinal() > to.ordinal() && !to.equals(LifeCycle.LEARNING)) {
				// we don't go back unless it's learning.
				logger.error("We cant go from {} to {}", from, to);
				return ;
			}
			student.setLifeCycle(to);
			studentRepository.update(student);
			
			StudentLifeCycleLog studentLifeCycleLog = new StudentLifeCycleLog();
			studentLifeCycleLog.setCreatedDateTime(new Date());
			if (from == null) {
				studentLifeCycleLog.setFromStudentLifeCycle(Student.LifeCycle.DEFAULT);
			} else {
				studentLifeCycleLog.setFromStudentLifeCycle(from);
			}
			if (to == null) {
				studentLifeCycleLog.setToStudentLifeCycle(Student.LifeCycle.DEFAULT);
			} else {
				studentLifeCycleLog.setToStudentLifeCycle(to);
			}
			studentLifeCycleLog.setStudent(student);
			studentLifeCycleLogRepository.create(studentLifeCycleLog);
		}
	}

	public List<StudentLifeCycleLog> findToBeRenewedMoreThanNintyDays(int toBeRenewedDucation) {
		return studentLifeCycleLogRepository.findToBeRenewedMoreThanNintyDays(toBeRenewedDucation);
	}
}
