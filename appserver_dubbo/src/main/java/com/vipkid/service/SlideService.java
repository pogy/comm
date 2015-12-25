package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Resource.Type;
import com.vipkid.model.Slide;
import com.vipkid.repository.SlideRepository;
import com.vipkid.security.SecurityService;

@Service
public class SlideService {
	private Logger logger = LoggerFactory.getLogger(SlideService.class.getSimpleName());
	
	@Resource
	private SlideRepository slideRepository;
	
	@Resource
	private SecurityService securityService;

	public Slide find(long id){
		return slideRepository.find(id);
	}

	public Slide findByPPTIdAndPage(long pptId, int page){
		return slideRepository.findByPPTIdAndPage(pptId, page);
	}

	public List<Slide> findByPPTId(long pptId) {
		return slideRepository.findByPPTId(pptId);
	}
	
	public List<Slide> findByResourceNameAndType(String resourceName, Type resourceType) {
		logger.debug("resourceName={}, resourceType={}", resourceName, resourceType);
		return slideRepository.findByResourceNameAndType(resourceName, resourceType);
	}
	
	public Slide update(Slide slide) {	
		slideRepository.update(slide);
		
		securityService.logAudit(Level.INFO, Category.SLIDE_UPDATE, "Update slide: " + slide.getId());
		
		return slide;
	}
	
}
