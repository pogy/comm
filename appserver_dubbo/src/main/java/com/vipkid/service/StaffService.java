package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.QueueNodeBean;
import com.vipkid.model.Role;
import com.vipkid.model.Staff;
import com.vipkid.model.User.Status;
import com.vipkid.repository.SalesTeamRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.HaveQueueServiceException;
import com.vipkid.service.exception.HaveSalesTeamMateServiceException;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserLockedServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.Credential;
import com.vipkid.util.Configurations;

@Service
public class StaffService {
	private Logger logger = LoggerFactory.getLogger(StaffService.class.getSimpleName());

	@Resource
	private StaffRepository staffRepository;
	
	@Resource
	private SalesTeamRepository salesTeamRepository;
	
	@Resource
	private SecurityService securityService;

	public Staff find(long id) {
		logger.info("find staff for id = {}", id);
		return staffRepository.find(id);
	}

	public List<Staff> findByRole(Role role) {
		logger.info("find staff for role = {}", role);
		return staffRepository.findByRole(role);
	}
	
	public Staff findByRole(String username) {
		logger.info("find staff for username = {}", username);
		return staffRepository.findByUsername(username);
	}

	public Staff findByName(String name) {
		logger.info("find staff for name = {}", name);
		List<Staff> staffs = staffRepository.findByName(name);
		if (staffs.size()>0) {
			return staffs.get(0);
		}
		
		throw new UserNotExistServiceException("find staff {} not exist",name);
	}

	public List<Staff> list(String search, String role, Status status, int start, int length) {
		logger.info("list staff with params: search = {}, role = {}, status = {}, start = {}, length = {}.", search, role, status, start, length);
		return staffRepository.list(search, role, status, start, length);
	}

	public Count count(String search, String role, Status status) {
		logger.info("count staff with params: search = {}, role = {}, status = {}.", search, role, status);
		return new Count(staffRepository.count(search, role, status));
	}
	
	public Staff doResetPassword(long id) {
		Staff staff = staffRepository.find(id);
		if(staff == null) {
			throw new UserNotExistServiceException("Staff[id: {}] is not exist.", id);
		}else {
			staff.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
			staff.setIsFirstLogin("1");
			staffRepository.update(staff);
			
			securityService.logAudit(Level.INFO, Category.STAFF_RESET_PASSWORD, "Reset password for staff: " + staff.getName());
			
			EMail.sendResetStaffPasswordEmail(staff.getEmail(), Configurations.Auth.DEFAULT_STAFF_PASSWORD);
		}
		
		return staff;
	}

	public Staff doLock(long id) {
		Staff staff = staffRepository.find(id);
		if(staff == null) {
			throw new UserNotExistServiceException("Staff[id: {}] is not exist.", id);
		}else {
			checkQueueNodeBeforeLock(staff);
			staff.setStatus(Status.LOCKED);
			staffRepository.update(staff);
			
			securityService.logAudit(Level.INFO, Category.STAFF_LOCK, "Lock staff: " + staff.getName());
		}
		
		return staff;
	}

	public Staff doUnlock(long id) {
		Staff staff = staffRepository.find(id);
		if(staff == null) {
			throw new UserNotExistServiceException("Staff[id: {}] is not exist.", id);
		}else {
			staff.setStatus(Status.NORMAL);
			staffRepository.update(staff);
			
			securityService.logAudit(Level.INFO, Category.STAFF_UNLOCK, "Unlock staff: " + staff.getName());
		}
		
		return staff;
	}

	public Staff create(Staff staff) {
		logger.info("create staff: {}", staff);	
		Staff findStaff = staffRepository.findByUsername(staff.getUsername());
		if(findStaff == null) {
			staff.setEmail(staff.getUsername());
			staff.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_STAFF_PASSWORD));
			staff.setRegisterDateTime(new Date());
			staff.setRoles(staff.getRoles().trim());
			staff.setIsFirstLogin("1");
			staffRepository.create(staff);
			
			securityService.logAudit(Level.INFO, Category.STAFF_CREATE, "Create staff: " + staff.getName());
			
			return staff;
		}else{
			throw new UserAlreadyExistServiceException("Staff already exist.");
		}
	}

	public Staff update(Staff staff) {
		logger.info("update staff: {}", staff);
		
		staff.setRoles(staff.getRoles().trim());
		staff.setEmail(staff.getUsername());
		Staff findStaff = staffRepository.find(staff.getId());
		checkQueueNodeBeforeRemoveSalesOrTMKRole(staff, findStaff);
		checkTeamBeforeRemoveManagerRole(staff, findStaff);
		if (checkIfNeedRemoveSalesTeamInfo(staff, findStaff)) {
			staff.setSalesTeamId(-1L);
		}
		staffRepository.update(staff);
		
		securityService.logAudit(Level.INFO, Category.STAFF_UPDATE, "Update staff: " + staff.getName());
		
		return staff;
	}
	
	private void checkToRemoveUnusedSalesTeam(Staff staff, Staff findStaff) {
		if (findStaff != null) {
			String currentRoles = staff.getRoles();
			String findRoles = findStaff.getRoles();
			if ((!this.containsRole(currentRoles, Role.STAFF_SALES_MANAGER) && this.containsRole(findRoles, Role.STAFF_SALES_MANAGER))
				|| (!this.containsRole(currentRoles, Role.STAFF_TMK_MANAGER) && this.containsRole(findRoles, Role.STAFF_TMK_MANAGER))) {
				List<Staff> salesTeamMates = staffRepository.findSalesTeamMateByManagerId(staff.getId());
				if(salesTeamMates.size() <= 1) {
					salesTeamRepository.removeTeamByManagerId(staff.getId());
				}
			
			}
			
		}
	}

	boolean checkIfNeedRemoveSalesTeamInfo(Staff staff, Staff findStaff) {
		if (findStaff != null) {
			String currentRoles = staff.getRoles();
			String findRoles = findStaff.getRoles();
			if ((!this.containsRole(currentRoles, Role.STAFF_SALES_MANAGER) && this.containsRole(findRoles, Role.STAFF_SALES_MANAGER))
				|| (!this.containsRole(currentRoles, Role.STAFF_TMK_MANAGER) && this.containsRole(findRoles, Role.STAFF_TMK_MANAGER))) {
				List<Staff> salesTeamMates = staffRepository.findSalesTeamMateByManagerId(staff.getId());
				if(salesTeamMates.size() <= 1) {
					return true;
				}
			
			}
			
			if(((this.containsRole(findStaff.getRoles(),Role.STAFF_SALES) && !this.containsRole(currentRoles,Role.STAFF_SALES))
					||(this.containsRole(findStaff.getRoles(), Role.STAFF_TMK) && !this.containsRole(currentRoles, Role.STAFF_TMK)))
					&& !this.containsRole(findStaff.getRoles(),Role.STAFF_SALES_MANAGER) && !this.containsRole(currentRoles,Role.STAFF_SALES_MANAGER)
					&& !this.containsRole(findStaff.getRoles(),Role.STAFF_TMK_MANAGER) && !this.containsRole(currentRoles,Role.STAFF_TMK_MANAGER)) {
				return true;
			}
		}
		return false;
	}
	
	public Staff changePassword(Credential credential) {	
		String username = credential.getUsername();
		String password = credential.getPassword();
		String newPassword = credential.getNewPassword();
		
		Staff staff = staffRepository.findByUsernameAndPassword(username, password);
		if(staff == null) {
			throw new UserNotExistServiceException("Staff[username: {}]'s password is wrong.", username);
		}else {
			if(staff.getStatus() == Status.LOCKED) {
				throw new UserLockedServiceException("Staff[username: {}] is locked.", username);
			}
			
			if(newPassword != null) {
				staff.setPassword(PasswordEncryptor.encrypt(newPassword));
				staff.setLastEditDateTime(new Date());
				staff.setLastEditor(staff);
				//设置密码后就不是首次登陆了
				staff.setIsFirstLogin("0");
				staffRepository.update(staff);
				
				logger.info("change password for staff: {}.", staff.getName());
				
				securityService.logAudit(Level.INFO, Category.STAFF_CHANGE_PASSWORD, "Change password for staff: " + staff.getName());
			}			
		}
	
		return staff;
	}
	
	public List<Staff> findByTeamId(long salesTeamId) {
		logger.info("find staff for salesTeamId = {}", salesTeamId);
		return staffRepository.findByTeamId(salesTeamId);
	}
	
	private void checkQueueNodeBeforeLock(Staff staff) {
		List<QueueNodeBean> queueNodeBeans = salesTeamRepository.findQueueNodeByUserId(staff.getId());
		if(!queueNodeBeans.isEmpty()) {
			throw new HaveQueueServiceException("please remove from queue before lock the staff");
		}
	}
	
	private void checkQueueNodeBeforeRemoveSalesOrTMKRole(Staff staff, Staff findStaff) {
		if(findStaff != null) {
			String currentRoles = staff.getRoles();
			if(this.containsRole(findStaff.getRoles(),Role.STAFF_SALES) && !this.containsRole(currentRoles,Role.STAFF_SALES)) {
				List<QueueNodeBean> queueNodeBeans = salesTeamRepository.findQueueNodeByUserId(staff.getId());
				if(!queueNodeBeans.isEmpty()) {
					throw new HaveQueueServiceException("please remove from queue before remove role");
				}
			}
			if(this.containsRole(findStaff.getRoles(), Role.STAFF_TMK) && !this.containsRole(currentRoles, Role.STAFF_TMK)) {
				List<QueueNodeBean> queueNodeBeans = salesTeamRepository.findQueueNodeByUserId(staff.getId());
				if(!queueNodeBeans.isEmpty()) {
					throw new HaveQueueServiceException("please remove from queue before remove role");
				}
			}
		}else {
			throw new UserNotExistServiceException("Staff not exist.");
		}
	}
	
	private void checkTeamBeforeRemoveManagerRole(Staff staff, Staff findStaff) {
		if(findStaff != null) {
			String currentRoles = staff.getRoles();
			String findRoles = findStaff.getRoles();
			// 移除manager时检查
//			if(!currentRoles.contains("_MANAGER") && findRoles.contains("_MANAGER")) {
//				checkIfStillHasSalesTeamMate(staff);
//			}
			
			if ((!this.containsRole(currentRoles, Role.STAFF_SALES_MANAGER) && this.containsRole(findRoles, Role.STAFF_SALES_MANAGER))
					|| (!this.containsRole(currentRoles, Role.STAFF_TMK_MANAGER) && this.containsRole(findRoles, Role.STAFF_TMK_MANAGER))) {
				checkIfStillHasSalesTeamMate(staff);
			}
			
			// 由sales manager变为tmk manager或由tmk manager变为sales manager时检查
			if((this.containsRole(currentRoles, Role.STAFF_SALES_MANAGER) && this.containsRole(findRoles, Role.STAFF_TMK_MANAGER))
					|| (this.containsRole(currentRoles, Role.STAFF_TMK_MANAGER) && this.containsRole(findRoles, Role.STAFF_SALES_MANAGER))) {
				checkIfStillHasSalesTeamMate(staff);
			}
		}else {
			throw new UserNotExistServiceException("Staff not exist.");
		}
	}
	
	private void checkIfStillHasSalesTeamMate(Staff staff) {
		List<Staff> salesTeamMates = staffRepository.findSalesTeamMateByManagerId(staff.getId());
		if(salesTeamMates.size() > 1) { // 默认manager在自己的队伍中，如果队伍中多于1人，则抛错，需要把其他人员assign到其他队伍
			throw new HaveSalesTeamMateServiceException("please remove mate from sales team before remove manager role");
		}else if(salesTeamMates.size() == 1) { // 检查最后的一个成员是否是自己，如果不是，则抛错，需要把其assign到其他队伍
			Staff salesTeamMate = salesTeamMates.get(0);
			if(salesTeamMate.getId() != staff.getId()) {
				throw new HaveSalesTeamMateServiceException("please remove mate from sales team before remove manager role");
			}
		}
	}
	
	private boolean containsRole(String roles,Role role) {
		boolean flag = false;
		if (role != null && StringUtils.isNotBlank(roles)) {
			String[] roleArray = roles.split("\\s+");
			List<String> roleList = Lists.newArrayList(roleArray);
			if (roleList.contains(role.name())) {
				flag = true;
			}
		}
		return flag;
	}
}
