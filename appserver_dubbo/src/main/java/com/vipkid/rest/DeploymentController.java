package com.vipkid.rest;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.DeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value="/api/service/public/deployment")
public class DeploymentController {
	private Logger logger = LoggerFactory.getLogger(DeploymentController.class.getSimpleName());

	@Resource
	private DeploymentService deploymentService;
	
	@RequestMapping(value="/etl",method = RequestMethod.GET)
	public Response data() {
		return deploymentService.updateData();
	}

	@RequestMapping(value="/deploy2",method = RequestMethod.GET)
	public Response deploy2() {
		return deploymentService.deploy2();
	}
	
	@RequestMapping(value="/deployOneToMany",method = RequestMethod.GET)
	public Response deployOneToMany() {
		return deploymentService.createOpen1Course();
	}

	@RequestMapping(value="/deploy",method = RequestMethod.GET)
	public Response deploy() {
		return deploymentService.deploy();
	}


	@RequestMapping(value="/initTeacherRecruitment",method = RequestMethod.GET)
	public Response initTeacherRecruitment() {
		return deploymentService.initTeacherRecruitment();
	}

	@RequestMapping(value="/initGuide",method = RequestMethod.GET)
	public Response initGuide() {
		return deploymentService.initGuide();
	}
	
	@RequestMapping(value="/initTrial",method = RequestMethod.GET)
	public Response initTrial() {
		return deploymentService.initTrial();
	}
	
	@RequestMapping(value="/initAssessment",method = RequestMethod.GET)
	public Response initAssessment() {
		return deploymentService.initAssessment();
	}
	
	@RequestMapping(value = "/depoyLTAndAssessmentCourse", method = RequestMethod.GET)
	public Response depoyLTAndAssessmentCourse(HttpServletResponse response) {
		 logger.info("Enter depoyLTAndAssessmentCourse()");
		 Response res = deploymentService.doInitLTAndAccessment();
	        if (null != res) {
	            response.setStatus(res.getStatus());
	        }
	        logger.info("Leave depoyLTAndAssessmentCourse()");   
	     return res;
	}
}
