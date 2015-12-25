package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.PayrollItem;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PayrollRepository;

@Service
public class PayrollItemService {
	private Logger logger = LoggerFactory.getLogger(PayrollItemService.class.getSimpleName());
	
	@Resource
	private PayrollRepository payrollRepository;
	@Resource
	private PayrollItemRepository payrollItemRepository;
	
	public PayrollItem find(long id) {
		logger.debug("find activity for id = {}" + id);
		return payrollItemRepository.find(id);
	}
	
	public List<PayrollItem> findByPayrollId(Long payrollId) {
		logger.debug("get payroll items with params: payrollId = {}.", payrollId);
		// 2015-02-02 如果客户端错误地传递null
		if(payrollId == null) {
			return null;
		}
		List<PayrollItem> payroll = payrollItemRepository.findByPayrollId(payrollId);

		return payroll;
	}
	public PayrollItem findByOnlineClassId(Long onlineClassId) {
		logger.debug("get payroll items with params: payrollId = {}.", onlineClassId);
		// 2015-02-02 如果客户端错误地传递null
		if(onlineClassId == null) {
			return null;
		}
		PayrollItem payrollItem = payrollItemRepository.findByOnlineClassId(onlineClassId);

		return payrollItem;
	}
	
	public PayrollItem create(PayrollItem payrollItem) {
		logger.debug("create course: {}", payrollItem);
		payrollItemRepository.create(payrollItem);
		return payrollItem;
	}

	public PayrollItem update(PayrollItem payrollItem) {
		payrollItemRepository.update(payrollItem);
		return payrollItem;
	}

	public PayrollItem archive(PayrollItem payrollItem) {
		payrollItemRepository.delete(payrollItem);
		return payrollItem;
	}
}
