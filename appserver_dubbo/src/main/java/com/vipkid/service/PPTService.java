package com.vipkid.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.PPT;
import com.vipkid.model.Resource.Type;
import com.vipkid.repository.PPTRepository;
import com.vipkid.security.SecurityService;

@Service
public class PPTService {
	@Resource
	private PPTRepository pptRepository;
	
	@Resource
	private SecurityService securityService;
	
	public PPT findByLessonIdAndType(long lessonId, Type type) {
		return pptRepository.findByLessonIdAndType(lessonId, type);
	}
	
	public PPT update(PPT ppt){
		PPT updatedPPT = pptRepository.update(ppt);
		securityService.logAudit(Level.INFO, Category.PPT_UPDATE, "Update ppt: " + updatedPPT.getId());
		return updatedPPT;
	}

}
