package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Resource.Type;
import com.vipkid.model.Slide;
import com.vipkid.security.SecurityService;
import com.vipkid.service.SlideService;

@RestController
@RequestMapping(value = "/api/service/private/slides")
public class SlideController {
	private Logger logger = LoggerFactory.getLogger(SlideController.class.getSimpleName());
	
	@Resource
	private SlideService slideService;
	
	@Resource
	private SecurityService securityService;


	@RequestMapping(value="/find",method = RequestMethod.GET)
	public Slide find(@RequestParam("id") long id){
		return slideService.find(id);
	}
	

	@RequestMapping(value="/findByPPTIdAndPage",method = RequestMethod.GET)
	public Slide findByPPTIdAndPage(@RequestParam("pptId") long pptId, @RequestParam("page") int page){
		return slideService.findByPPTIdAndPage(pptId, page);
	}
	
	@RequestMapping(value="/findByPPTId",method = RequestMethod.GET)
	public List<Slide> findByPPTId(@RequestParam("pptId") long pptId) {
		return slideService.findByPPTId(pptId);
	}
	
	@RequestMapping(value="/findByResourceNameAndType",method = RequestMethod.GET)
	public List<Slide> findByResourceNameAndType(@RequestParam("resourceName") String resourceName, @RequestBody Type resourceType) {
		logger.debug("QueryParam resourceName={}, resourceType={}", resourceName, resourceType);
		return slideService.findByResourceNameAndType(resourceName, resourceType);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Slide update(Slide slide) {	
		slideService.update(slide);		
		securityService.logAudit(Level.INFO, Category.SLIDE_UPDATE, "Update slide: " + slide.getId());
		return slide;
	}
	
}
