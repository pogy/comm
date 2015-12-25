package com.vipkid.service;

import org.springframework.stereotype.Service;

import com.vipkid.model.Resource;
import com.vipkid.model.Resource.Type;
import com.vipkid.repository.ResourceRepository;

@Service
public class ResourceService {

	private ResourceRepository resourceRepository;

	public Resource getResourceByLessonIdAndType(long lessonId, Type resourceType) {
		return resourceRepository.findByLessonIdAndType(lessonId, resourceType);
	}
}
