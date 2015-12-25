package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit;
import com.vipkid.repository.AuditRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@Service
public class AuditService {
	private Logger logger = LoggerFactory.getLogger(AuditService.class.getSimpleName());
	
	@Resource
	private AuditRepository auditRepository;
	
	@Resource
	private StaffRepository staffRepository;

	public List<Audit> list(String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo, String level, String category, int start, int length) {
		logger.debug("list audit with params: search = {}, executeDateTimeFrom = {}, executeDateTimeTo = {}, level = {}, category = {}, start = {}, length = {}.", search, executeDateTimeFrom, executeDateTimeTo, level, category, start, length);
		List<Audit> audits = auditRepository.list(search, executeDateTimeFrom, executeDateTimeTo, level, category, start, length);
		return audits;
	}

	public Count count(String search, DateTimeParam executeDateTimeFrom, DateTimeParam executeDateTimeTo, String level, String category) {
		logger.debug("count audit with params: search = {}, executeDateTimeFrom = {}, executeDateTimeTo = {}, level = {}, category = {}.", search, executeDateTimeFrom, executeDateTimeTo, level, category);
		return new Count(auditRepository.count(search, executeDateTimeFrom, executeDateTimeTo, level, category));
	}
	
}
