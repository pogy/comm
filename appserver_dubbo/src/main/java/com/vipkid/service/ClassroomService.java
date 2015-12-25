package com.vipkid.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vipkid.model.OnlineClass;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.service.pojo.Classroom;

@Service
public class ClassroomService {
	
	@Resource
	private OnlineClassRepository onlineClassRepository;

	public Classroom findOnlineClassById(long id) {
		OnlineClass onlineClass = onlineClassRepository.find(id);
		Classroom classroom = new Classroom();
		classroom.setId(onlineClass.getId());
		classroom.setScheduledDateTime(onlineClass.getScheduledDateTime().getTime());
		return classroom;
	}
}
