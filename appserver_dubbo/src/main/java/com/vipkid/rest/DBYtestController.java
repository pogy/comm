package com.vipkid.rest;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.ItTestRole;
import com.vipkid.service.DBYtestService;

@RestController
@RequestMapping(value="/api/service/public/ittest")
public class DBYtestController {
	private Logger logger = LoggerFactory.getLogger(DBYtestController.class.getSimpleName());
	
	@Resource
	private DBYtestService dbYtestService;
	
	@RequestMapping(value="/callback",method = RequestMethod.GET)
	public com.vipkid.rest.vo.Response receive(@RequestParam("testResult") String testResult, @RequestParam("lang") String lang, @RequestParam("role") ItTestRole itTestRole, @RequestParam("id") String id) {
		logger.info("DBY IT Test call back,testResult = {}, lang = {}, role = {}, id = {}", testResult, lang, itTestRole, id);
		return dbYtestService.doReceive(testResult, lang, itTestRole, id);
	}
	
}
