package com.vipkid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Payroll;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PayrollRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.service.pojo.StringWrapper;
import com.vipkid.service.param.DateParam;

@Service
public class PayrollService {
	private Logger logger = LoggerFactory.getLogger(PayrollService.class.getSimpleName());
	
	@Resource
	private PayrollRepository payrollRepository;
	@Resource
	private PayrollItemRepository payrollItemRepository;
	@Resource
	private TeacherRepository teacherRepository;
	
	public Payroll find(long id) {
		logger.debug("find activity for id = {}" + id);
		return payrollRepository.find(id);
	}
	
	public List<StringWrapper> findMonthListWithPayroll(long teacherId) {
		List<Payroll> payrolls = payrollRepository.findMonthListWithPayroll(teacherId);
		
		List<StringWrapper> monthList = new ArrayList<StringWrapper>();
		Calendar paidCalendar = Calendar.getInstance();
		for (Payroll payroll : payrolls) {
			paidCalendar.setTime(payroll.getPaidDateTime());
			StringWrapper monthString = new StringWrapper();
			monthString.setWord(paidCalendar.get(Calendar.YEAR) + "-" + paidCalendar.get(Calendar.MONTH));
			monthList.add(monthString);
		}
		return monthList;
	}
	
	public Payroll findCurrentByTeacherUserId(Long userId) {
		logger.debug("get latest payroll with params: userId = {}.", userId);
		Payroll payroll = payrollRepository.findCurrentByTeacherUserId(userId);

		if (payroll != null) {
			payroll.setPayrollItemCount(payrollItemRepository.countByPayrollId(payroll.getId()));
		}

		return payroll;
	}
	
	public Payroll findByTeacherIdAndPaidDateTime(long teacherId,  DateParam paidDateTime) {
		Payroll payroll = payrollRepository.findByScheduledDateAndTeacher(paidDateTime.getValue(), teacherRepository.find(teacherId));
		
		if (payroll != null) {
			payroll.setPayrollItemCount(payrollItemRepository.countByPayrollId(payroll.getId()));
		}
		
		return payroll;
	}
	
	public Payroll findNextOfPayrollId(Long payrollId) {
		logger.debug("get next payroll with id: payrollId = {}.", payrollId);
		Payroll payroll = payrollRepository.findNextOfPayrollId(payrollId);

		payroll.setPayrollItemCount(payrollItemRepository.countByPayrollId(payroll.getId()));

		return payroll;
	}
	
	public Payroll findPrevOfPayrollId(Long payrollId) {
		logger.debug("get previous payroll with id: payrollId = {}.", payrollId);
		Payroll payroll = payrollRepository.findPrevOfPayrollId(payrollId);

		payroll.setPayrollItemCount(payrollItemRepository.countByPayrollId(payroll.getId()));

		return payroll;
	}
	
	public Payroll create(Payroll payroll) {
		logger.debug("create course: {}", payroll);
		payrollRepository.create(payroll);
		return payroll;
	}

	public Payroll update(Payroll payroll) {
		payrollRepository.update(payroll);
		return payroll;
	}

	public Payroll archive(Payroll payroll) {
		payrollRepository.delete(payroll);
		return payroll;
	}
}
