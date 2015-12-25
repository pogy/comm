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

import com.vipkid.model.Payroll;
import com.vipkid.service.PayrollService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.pojo.StringWrapper;

@RestController
@RequestMapping("/api/service/private/payroll")
public class PayrollController {
	private Logger logger = LoggerFactory.getLogger(PayrollController.class.getSimpleName());
	
	@Resource
	private PayrollService payrollService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Payroll find(@RequestParam("id") long id) {
		logger.info("find activity for id = {}" + id);
		return payrollService.find(id);
	}
	
	@RequestMapping(value = "/findMonthListWithPayroll", method = RequestMethod.GET)
	public List<StringWrapper> findMonthListWithPayroll(@RequestParam("teacherId") long teacherId) {
		logger.info("find monthlist with payroll, teacherid = {}.", teacherId );
		return payrollService.findMonthListWithPayroll(teacherId);
	}
	
	@RequestMapping(value = "/findCurrentByTeacherUserId", method = RequestMethod.GET)
	public Payroll findCurrentByTeacherUserId(@RequestParam("userId") Long userId) {
		logger.info("get latest payroll with params: userId = {}.", userId);
		return payrollService.findCurrentByTeacherUserId(userId);
	}
	
	@RequestMapping(value = "/findByTeacherIdAndPaidDateTime", method = RequestMethod.GET)
	public Payroll findByTeacherIdAndPaidDateTime(@RequestParam("teacherId") long teacherId, @RequestParam("paidDateTime") DateParam paidDateTime) {
		logger.info("find by teacherid and paiddatetime, teacherId = {}, paidDateTime = {}",teacherId, paidDateTime.getValue());
		return payrollService.findByTeacherIdAndPaidDateTime(teacherId, paidDateTime);
	}
	
	@RequestMapping(value = "/findNextOfPayrollId", method = RequestMethod.GET)
	public Payroll findNextOfPayrollId(@RequestParam("payrollId") Long payrollId) {
		logger.info("get next payroll with id: payrollId = {}.", payrollId);
		return payrollService.findNextOfPayrollId(payrollId);
	}
	
	@RequestMapping(value = "/findPrevOfPayrollId", method = RequestMethod.GET)
	public Payroll findPrevOfPayrollId(@RequestParam("payrollId") Long payrollId) {
		logger.info("get previous payroll with id: payrollId = {}.", payrollId);
		return payrollService.findPrevOfPayrollId(payrollId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Payroll create(@RequestBody Payroll payroll) {
		logger.info("create course: {}", payroll);
		return payrollService.create(payroll);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Payroll update(@RequestBody Payroll payroll) {
		logger.info("update payroll,payroll_id = {}.", payroll.getId());
		return payrollService.update(payroll);
	}

	@RequestMapping(value = "/archive", method = RequestMethod.PUT)
	public Payroll archive(@RequestBody Payroll payroll) {
		logger.info("archive payroll,payroll_id = {}.", payroll.getId());
		return payrollService.archive(payroll);
	}
}
