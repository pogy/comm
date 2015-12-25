package com.vipkid.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.QueueNodeBean;
import com.vipkid.model.Role;
import com.vipkid.model.SalesTeam;
import com.vipkid.model.SalesTeam.Type;
import com.vipkid.model.Staff;
import com.vipkid.repository.SalesTeamRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.rest.vo.query.SalesTeamVO;
import com.vipkid.rest.vo.query.SalesVO;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.ManagerIsNotSalesServiceException;
import com.vipkid.service.exception.ManagerIsNotTMKServiceException;
import com.vipkid.service.exception.MateIsNotSalesServiceException;
import com.vipkid.service.exception.MateIsNotTMKServiceException;
import com.vipkid.service.exception.SalesTeamNotExistServiceException;
import com.vipkid.service.exception.TeamAlreadyExistServiceException;
import com.vipkid.service.pojo.BooleanWrapper;
import com.vipkid.service.pojo.Count;

@Service
public class SalesTeamService {
	private Logger logger = LoggerFactory.getLogger(SalesTeamService.class.getSimpleName());

	@Resource
	private SalesTeamRepository salesTeamRepository;
	
	@Resource
	private StaffRepository staffRepository;
	
	@Resource
	private SecurityService securityService;

	public SalesTeam find(long id) {
		logger.info("find sales team for id = {}", id);
		return salesTeamRepository.find(id);
	}
	
	public List<SalesTeam> findAll() {
		logger.info("find all sales team");
		return salesTeamRepository.findAll();
	}
	
	public List<SalesTeamVO> findByType(Type type) {
		logger.info("find sales team for type = {}", type);
		List<SalesTeam> salesTeams = salesTeamRepository.findByType(type);
		List<SalesTeamVO> salesTeamVOs = new LinkedList<SalesTeamVO>();
		if(!salesTeams.isEmpty()) {
			for(SalesTeam salesTeam : salesTeams) {
				SalesTeamVO salesTeamVO = new SalesTeamVO();
				toSalesTeamVO(salesTeam, salesTeamVO);
				salesTeamVOs.add(salesTeamVO);
			}
		}
		return salesTeamVOs;
	}

	public SalesTeam createSalesTeam(SalesTeam salesTeam) {
		logger.info("create sales team: {}", salesTeam);	
		SalesTeam findSalesTeam = salesTeamRepository.findByManagerId(salesTeam.getManagerId());
		if(findSalesTeam == null) {
			checkIfSalesManager(salesTeam.getManagerId());
			salesTeam.setType(Type.SALES);
			salesTeamRepository.create(salesTeam);
			securityService.logAudit(Level.INFO, Category.SALES_TEAM_CREATE, "Create sales team with manager id: " + salesTeam.getManagerId());
			
			return salesTeam;
		}else{
			throw new TeamAlreadyExistServiceException("Team already exist.");
		}
	}
	
	public void doAddMangerToTeam(SalesTeam salesTeam) {
		try {
			Staff manager = staffRepository.find(salesTeam.getManagerId());
			if(manager != null) {
				manager.setSalesTeamId(salesTeam.getId());
				staffRepository.update(manager);
			}
		} catch (Exception e) {
			logger.error("fail to doAddMangerToTeam for salesTeam = {}, error = {}", salesTeam, e.toString());
		}
	}
	
	public SalesTeam createTMKTeam(SalesTeam TMKTeam) {
		logger.info("create TMK team: {}", TMKTeam);	
		SalesTeam findSalesTeam = salesTeamRepository.findByManagerId(TMKTeam.getManagerId());
		if(findSalesTeam == null) {
			checkIfTMKManager(TMKTeam.getManagerId());
			TMKTeam.setType(Type.TMK);
			salesTeamRepository.create(TMKTeam);
			securityService.logAudit(Level.INFO, Category.SALES_TEAM_CREATE, "Create sales team with manager id: " + TMKTeam.getManagerId());
			
			return TMKTeam;
		}else{
			throw new TeamAlreadyExistServiceException("Team already exist.");
		}
	}
	
	public List<SalesVO> listForSalesTeam(String role, Long salesTeamId, Long managerId, Boolean autoAssignLeads, String searchText,Boolean isInTeam, Integer start, Integer length) {
		logger.info("list staff for sales team with params: role = {}, salesTeamId = {}, managerId = {}, autoAssignLeads = {},searchText = {},isInTeam = {}, start = {}, length = {}.", role, salesTeamId, managerId, autoAssignLeads, searchText, isInTeam, start, length);
		List<SalesVO> salesVOs = new ArrayList<SalesVO>();
		if(salesTeamId == null && managerId != null) { // 通过manager id 找到team id
			SalesTeam salesTeam = salesTeamRepository.findByManagerId(managerId);
			if(salesTeam != null) {
				salesVOs = salesTeamRepository.listForSalesTeam(role, salesTeam.getId(), autoAssignLeads, searchText, isInTeam, start, length);
			}else {
				logger.info("can not find sales team from manager id = {}", managerId);
				return salesVOs;
			}
		}else if(salesTeamId != null){
			salesVOs = salesTeamRepository.listForSalesTeam(role, salesTeamId, autoAssignLeads, searchText, isInTeam, start, length);
		}else if(salesTeamId == null && managerId == null) {
			salesVOs = salesTeamRepository.listForSalesTeam(role, null, autoAssignLeads, searchText,isInTeam,  start, length);
		}
		if(!salesVOs.isEmpty()) {
			for(SalesVO salesVO : salesVOs) {
				setSalesVOManagerInfo(salesVO);
			}
		}
		return salesVOs;
	}
	
	public Count countForSalesTeam(String role, Long salesTeamId, Long managerId, Boolean autoAssignLeads, String searchText, Boolean isInTeam) {
		logger.info("count staff for sales team with params: role = {}, salesTeamId = {}, managerId = {}, autoAssignLeads = {}, searchText = {}, isInTeam = {}.", role, salesTeamId, managerId, autoAssignLeads,searchText,isInTeam);
		if(salesTeamId == null && managerId != null) { // 通过manager id 找到team id
			SalesTeam salesTeam = salesTeamRepository.findByManagerId(managerId);
			if(salesTeam != null) {
				return new Count(salesTeamRepository.countForSalesTeam(role, salesTeam.getId(), autoAssignLeads, searchText, isInTeam));
			}else {
				return new Count(0L); 
			}
		}else if(salesTeamId != null){
			return new Count(salesTeamRepository.countForSalesTeam(role, salesTeamId, autoAssignLeads, searchText, isInTeam));
		}else if(salesTeamId == null && managerId == null) {
			return new Count(salesTeamRepository.countForSalesTeam(role, null, autoAssignLeads, searchText, isInTeam));
		}
		return new Count(0L);
	}
	
	private SalesVO setSalesVOManagerInfo(SalesVO salesVO) {
		if(salesVO.getSalesTeamId() != null) {
			Staff manager = staffRepository.findManagerBySalesTeamId(salesVO.getSalesTeamId());
			if(manager != null) {
				salesVO.setManagerEnglishName(manager.getEnglishName());
				salesVO.setManagerName(manager.getName());
			}
		}
		return salesVO;
	}
	
	public void doAssignToSalesTeam(List<SalesVO> salesVOs) {
		logger.info("assing staffs to sales team with params: salesVOs = {}.", salesVOs);
		if(!salesVOs.isEmpty()) {
			Long managerId = salesVOs.get(0).getManagerId();
			if(managerId != null) {
				checkIfSalesManager(managerId);
				SalesTeam salesTeam = salesTeamRepository.findByManagerId(managerId);
				if(salesTeam != null && salesTeam.getId() != 0) {
					for(SalesVO salesVO : salesVOs) {
						if(salesVO.getId() != null) {
							Staff staff = staffRepository.find(salesVO.getId());
							checkIfSales(staff);
							staff.setSalesTeamId(salesTeam.getId());
							staffRepository.update(staff);
						}
					}
				}else {
					throw new SalesTeamNotExistServiceException("team not exist, please build the team first.");
				}
			}
		}
	}
	
	private void checkIfSalesManager(Long managerId) {
		Staff staff = staffRepository.find(managerId);
		String roles = staff.getRoles();
		if(roles.indexOf(Role.STAFF_SALES_MANAGER.name()) < 0) {
			throw new ManagerIsNotSalesServiceException("the manager is not a sales manager");
		}
	}
	
	private void checkIfSales(Staff staff) {
		String roles = staff.getRoles();
		if(roles.indexOf(Role.STAFF_SALES.name()) < 0) {
			throw new MateIsNotSalesServiceException("the team mate is not a sales");
		}
	}
	
	public void doAssignToTMKTeam(List<SalesVO> salesVOs) {
		logger.info("assing staffs to TMK team with params: salesVOs = {}.", salesVOs);
		if(!salesVOs.isEmpty()) {
			Long managerId = salesVOs.get(0).getManagerId();
			if(managerId != null) {
				checkIfTMKManager(managerId);
				SalesTeam salesTeam = salesTeamRepository.findByManagerId(managerId);
				if(salesTeam != null && salesTeam.getId() != 0) {
					for(SalesVO salesVO : salesVOs) {
						if(salesVO.getId() != null) {
							Staff staff = staffRepository.find(salesVO.getId());
							checkIfTMK(staff);
							staff.setSalesTeamId(salesTeam.getId());
							staffRepository.update(staff);
						}
					}
				}else {
					throw new SalesTeamNotExistServiceException("team not exist, please build the team first.");
				}
			}
		}
	}
	
	private void checkIfTMKManager(Long managerId) {
		Staff staff = staffRepository.find(managerId);
		String roles = staff.getRoles();
		if(roles.indexOf(Role.STAFF_TMK_MANAGER.name()) < 0) {
			throw new ManagerIsNotTMKServiceException("the manager is not a TMK manager");
		}
	}
	
	private void checkIfTMK(Staff staff) {
		String roles = staff.getRoles();
		if(roles.indexOf(Role.STAFF_TMK.name()) < 0) {
			throw new MateIsNotTMKServiceException("the team mate is not a TMK"); 
		}
	}
	
	private void toSalesTeamVO(SalesTeam salesTeam, SalesTeamVO salesTeamVO) {
		salesTeamVO.setId(salesTeam.getId());
		salesTeamVO.setType(salesTeam.getType());
		salesTeamVO.setManagerId(salesTeam.getManagerId());
		Staff manager = staffRepository.find(salesTeam.getManagerId());
		if(manager != null) {
			salesTeamVO.setManagerName(manager.getName());
		}
	}
	
	public BooleanWrapper findIfHasQueueNodeByUserId(Long userId) {
		logger.info("find if has queue node by user id = {}", userId);
		List<QueueNodeBean> queueNodeBeans = salesTeamRepository.findQueueNodeByUserId(userId);
		if(queueNodeBeans.isEmpty()) {
			return new BooleanWrapper(false);
		}else {
			return new BooleanWrapper(true);
		}
	}
}
