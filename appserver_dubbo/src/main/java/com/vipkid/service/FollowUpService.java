package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.FollowUp;
import com.vipkid.model.FollowUp.Category;
import com.vipkid.model.Leads;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.repository.FollowUpRepository;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.Count;

@Service
public class FollowUpService {
	private Logger logger = LoggerFactory.getLogger(FollowUpService.class.getSimpleName());

	@Resource
	private FollowUpRepository followUpRepository;
	
	@Resource
	private SecurityService securityService;
	@Resource
	private LeadsRepository leadsRepository;
	@Resource
	private LeadsManageService leadsManageService;
	@Resource
	private OrderRepository orderRepository;
	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;

	public FollowUp find(long id) {
		logger.debug("find follow up for id = {}", id);
		return followUpRepository.find(id);
	}

	public List<FollowUp> list(long studentId,Category category,int start,int length) {
		logger.debug("list followUp with params: studentId = {}, category = {}, start = {}, length = {}.", studentId, category, start, length);
		return followUpRepository.list(studentId, category, start, length);
	}

	public Count count(long studentId,Category category) {
		logger.debug("count followUp with params: studentId = {}, category = {}.", studentId, category);
		return new Count(followUpRepository.count(studentId, category));
	}

	public FollowUp create(FollowUp followUp) {
		logger.debug("create followUp: {}", followUp);
		Staff creater = (Staff) securityService.getCurrentUser();
		followUp.setCreater(creater);
		followUp.setStatus(FollowUp.Status.CREATED);
		Student student = followUp.getStakeholder();
		if(student != null) {
			List<FollowUp> followUps = followUpRepository.findByStudentId(student.getId());
			for(FollowUp findFollowUp : followUps) {
				findFollowUp.setCurrent(false);
			}
			
			if (followUps.size() <= 0 || student.getLifeCycle().equals(LifeCycle.ASSIGNED)) { // the first contact or the lifecycle is still assigned for some reason.
				Long payConfirmedOrdersCount = orderRepository.countPayConfirmedByStudentId(student.getId());
				if (payConfirmedOrdersCount > 0) {
					studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.LEARNING);
				} else {
					studentLifeCycleLogService.doChangeLifeCycle(student, student.getLifeCycle(), Student.LifeCycle.CONTACTED);
				}
			}
		}
		followUp.setCurrent(true);
		followUpRepository.create(followUp);
		if (followUp.getStakeholder() != null) {
			leadsManageService.updateLeadsStatus(followUp.getStakeholder().getId(), creater.getId(), Leads.Status.CONTACTED.getCode());//更新leads状态
		}
		securityService.logAudit(Level.INFO, Audit.Category.FOLLOW_UP_CREATE, "Create followUp: " + followUp.getId());
		return followUp;
	}
}
