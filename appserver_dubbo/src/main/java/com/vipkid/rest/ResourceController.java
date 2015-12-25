package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Resource.Type;
import com.vipkid.service.ResourceService;

@RestController
@RequestMapping("/api/service/private/resources")
public class ResourceController {
	@Resource
	private ResourceService resourceService;
	private Logger logger = LoggerFactory.getLogger(BroadcastController.class.getSimpleName());
	
	
	@RequestMapping(value="/getResourceByLessonIdAndType",method = RequestMethod.GET)
	public com.vipkid.model.Resource getResourceByLessonIdAndType(@RequestParam("lessonId") long lessonId, @RequestBody Type resourceType) {
		logger.info("getResourceByLessonIdAndType");
		return resourceService.getResourceByLessonIdAndType(lessonId, resourceType);
	}
}
