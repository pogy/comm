package com.vipkid.rest;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.PPT;
import com.vipkid.model.Resource.Type;
import com.vipkid.service.PPTService;

@RestController
@RequestMapping("/api/service/private/ppts")
public class PPTController {
	
	@Resource
	private PPTService pptService;
	
	
	@RequestMapping(value = "/findByLessonIdAndType", method = RequestMethod.GET)
	public PPT findByLessonIdAndType(@RequestParam("lessonId") long lessonId, @RequestParam("type") Type type) {
		return pptService.findByLessonIdAndType(lessonId, type);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public PPT update(PPT ppt){
		return pptService.update(ppt);
	}

}
