package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.PUT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.PayrollItem;
import com.vipkid.service.PayrollItemService;

@RestController
@RequestMapping("/api/service/private/payrollItems")
public class PayrollItemController {
	private Logger logger = LoggerFactory.getLogger(PayrollItemController.class.getSimpleName());
	
	@Resource
	private PayrollItemService payrollItemService;
	
	@RequestMapping(value="/find", method=RequestMethod.GET)
	public PayrollItem find(@RequestParam("id") long id) {
		logger.info("find activity for id = {}" + id);
		return payrollItemService.find(id);
	}
	
	@RequestMapping(value="/findByPayrollId", method=RequestMethod.GET)
	public List<PayrollItem> findByPayrollId(@RequestParam(value = "payrollId", required = false) Long payrollId) {
		logger.info("get payroll items with params: payrollId = {}.", payrollId);
		return payrollItemService.findByPayrollId(payrollId);

	}
	
	@RequestMapping(value="/findByOnlineClassId", method=RequestMethod.GET)
	public PayrollItem findByOnlineClassId(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.info("get payroll items with params: payrollId = {}.", onlineClassId);
		return payrollItemService.findByOnlineClassId(onlineClassId);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public PayrollItem create(@RequestBody PayrollItem payrollItem) {
		logger.info("create course: {}", payrollItem);
		return payrollItemService.create(payrollItem);
	}

	@PUT
	@RequestMapping(method=RequestMethod.PUT)
	public PayrollItem update(@RequestBody PayrollItem payrollItem) {
		logger.info("update payrollItem, payrollItem_id = {}.",payrollItem.getId());
		return payrollItemService.update(payrollItem);
	}

	@RequestMapping(value="/archive", method=RequestMethod.PUT)
	public PayrollItem archive(@RequestBody PayrollItem payrollItem) {
		logger.info("archive payrollItem, payrollItem_id = {}.",payrollItem.getId());
		return payrollItemService.archive(payrollItem);
	}
}
