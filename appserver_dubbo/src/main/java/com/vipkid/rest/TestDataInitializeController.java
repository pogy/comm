package com.vipkid.rest;

import com.vipkid.service.InitializeService;
import com.vipkid.service.TestDataInitializeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;


@RestController
@RequestMapping("/api/service/public/testDataInitialize")
public class TestDataInitializeController {
	
	private Logger logger = LoggerFactory.getLogger(InitializeService.class.getSimpleName());

	@Resource
	private TestDataInitializeService testDataInitializeService;

	@RequestMapping(value = "/initCourse", method = RequestMethod.GET)
	public Response initCourse() {
		logger.info("init course.");
		return testDataInitializeService.doInitCourse(); 
	}
}







