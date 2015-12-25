package com.vipkid.rest;

import java.io.IOException;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.InitializeService;

@RestController
@RequestMapping(value="/api/service/public/initialize")
public class InitializeController {
	private Logger logger = LoggerFactory.getLogger(InitializeController.class.getSimpleName());
	
	@javax.annotation.Resource
	private InitializeService initializeService;
	
	@RequestMapping(value="/init",method = RequestMethod.GET)
	public Response init() throws JDOMException, IOException {
		logger.info("init");
		return initializeService.doInit();
	}
	
	@RequestMapping(value="/initForParentPortal",method = RequestMethod.GET)
	public Response initForParentPortal() {
		logger.info("initForParentPortal");
		return initializeService.doInitForParentPortal();
	}
	
	@RequestMapping(value="/initDate",method = RequestMethod.GET)
	public Response initDate(@RequestParam(value="num") int num) {
		logger.info("initDate");
		return initializeService.initDate(num);
	}
}
