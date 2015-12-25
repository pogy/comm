package com.vipkid.rest;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.DemoReport;
import com.vipkid.security.SecurityService;
import com.vipkid.service.DemoReportService;

@RestController
@RequestMapping(value="/api/service/private/demoReports")
public class DemoReportController {
	private Logger logger = LoggerFactory.getLogger(DemoReportController.class.getSimpleName());

	@Resource
	private DemoReportService demoReportService;
	
	@Resource
	private SecurityService securityService;
	
	@Context
	private ServletContext servletContext;

	@RequestMapping(value="/find",method = RequestMethod.GET)
	public DemoReport find(@RequestParam("id") long id) {
		logger.info("find demo report for id = {}", id);
		return demoReportService.find(id);
	}
	
	@RequestMapping(value="/findByOnlineClassId",method = RequestMethod.GET)
	public DemoReport findByOnlineClassId(@RequestParam("onlineClassId") long onlineClassId) {
		logger.info("find demo report for online class id = {}", onlineClassId);
		return demoReportService.findByOnlineClassId(onlineClassId);
	}
	
	@RequestMapping(value="/findByStudentId",method = RequestMethod.GET)
	public DemoReport findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find demo report for online class id = {}", studentId);
		return demoReportService.findByStudentId(studentId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public DemoReport update(@RequestBody DemoReport demoReport) {
		logger.info("update demo report: {}", demoReport);
		demoReportService.update(demoReport);
		return demoReport;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public DemoReport create(@RequestBody DemoReport demoReport) {
		logger.info("create demoReport: {}", demoReport);
		return demoReportService.create(demoReport);
	}
	
	@RequestMapping(value="/confirm",method = RequestMethod.PUT)
	public DemoReport confirm(@RequestBody DemoReport demoReport) {
		logger.info("confirm demo report: {}", demoReport);
		return demoReportService.confirm(demoReport);
	}
	
	@RequestMapping(value="/submit",method = RequestMethod.PUT)
	public DemoReport submit(@RequestBody DemoReport demoReport) {
		logger.info("submit demo report: {}", demoReport);
		return demoReportService.submit(demoReport);
	}
	
}
