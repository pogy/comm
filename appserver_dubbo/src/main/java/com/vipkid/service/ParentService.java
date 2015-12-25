package com.vipkid.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Family;
import com.vipkid.model.Parent;
import com.vipkid.model.Role;
import com.vipkid.model.User.Status;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.FamilyNotExistServiceException;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;

@Service
public class ParentService {
	private Logger logger = LoggerFactory.getLogger(ParentService.class.getSimpleName());

	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private SecurityService securityService;

	public Parent find(long id) {
		logger.debug("find parent for id = {}", id);
		return parentRepository.find(id);
	}
	
	public List<Parent> findByFamilyId(long familyId) {
		logger.debug("find parents by family id = {}", familyId);
		return parentRepository.findByFamilyId(familyId);
	}
	
	public List<Parent> findByStudentId(long studentId) {
		logger.debug("find parents by student id = {}", studentId);
		return familyRepository.findByStudentId(studentId).getParents();
	}

	public Parent findByUsername(String username) {
		logger.debug("find parents by username = {}", username);
		return parentRepository.findByUsername(username);
	}
	
	public List<Parent> list(String search, Status status, int start, int length) {
		logger.debug("list parent with params: search = {}, status = {}, start = {}, length = {}.", search, status, start, length);
		return parentRepository.list(search, status, start, length);
	}
	
	public Count count(String search, Status status) {
		logger.debug("count parent with params: search = {}, status = {}.", search, status);
		return new Count(parentRepository.count(search, status));
	}
	
	public Parent doResetPassword(long id) {
		Parent parent = parentRepository.find(id);
		if(parent == null) {
			throw new UserNotExistServiceException("Parent[id: {}] is not exist.", id);
		}else {
			String newPassword = PasswordGenerator.generate();
			parent.setPassword(PasswordEncryptor.encrypt(newPassword));
			parentRepository.update(parent);
			
			securityService.logAudit(Level.INFO, Category.PARENT_RESET_PASSWORD, "Reset password for parent: " + parent.getName());
			
			SMS.sendNewPasswordToParentSMS(parent.getMobile(), newPassword);
		}
		
		return parent;
	}
	
	public Parent lock(long id) {
		Parent parent = parentRepository.find(id);
		if(parent == null) {
			throw new UserNotExistServiceException("Parent[id: {}] is not exist.", id);
		}else {
			parent.setStatus(Status.LOCKED);
			parentRepository.update(parent);
		}
		
		return parent;
	}
	
	public Parent unlock(long id) {
		Parent parent = parentRepository.find(id);
		if(parent == null) {
			throw new UserNotExistServiceException("Parent[id: {}] is not exist.", id);
		}else {
			parent.setStatus(Status.NORMAL);
			parentRepository.update(parent);
			securityService.logAudit(Level.INFO, Category.PARENT_UPDATE, "Update parent: " + parent.getName());
		}
		
		return parent;
	}

	public Parent create(Parent parent) {
		logger.debug("create parent: {}", parent);
		
		Parent findParent = parentRepository.findByUsername(parent.getUsername());
		if(findParent == null) {
			Family findFamily = familyRepository.find(parent.getFamily().getId());
			if(findFamily != null) {
				parent.setFamily(findFamily);
				parent.setUsername(parent.getMobile());
				String parentPassword = PasswordGenerator.generate();
				parent.setInitPassword(parentPassword);
				parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
				parent.setRegisterDateTime(new Date());
				parent.setRoles(Role.PARENT.name());
				parentRepository.create(parent);
				
				if(findFamily.getParents() != null) {
					findFamily.getParents().add(parent);
				}else {
					List<Parent> parents = new ArrayList<Parent>();
					parents.add(parent);
					findFamily.setParents(parents);
				}

				SMS.sendNewParentSignupFromManagementPortalSMS(parent, parentPassword);
				
				securityService.logAudit(Level.INFO, Category.PARENT_CREATE, "Create parent: " + parent.getName());
			}else {
				throw new FamilyNotExistServiceException("Family not exist.");
			}
			
			return parent;
		}else {
			throw new UserAlreadyExistServiceException("Parent already exist.");
		}
	}

	public Parent update(Parent parent) {
		Family familyInClient = parent.getFamily();
		Family familyInDB = familyRepository.find(familyInClient.getId());
		
		if (familyInClient.getProvince() != null) {
			familyInDB.setProvince(familyInClient.getProvince());
		}
		
		if (familyInClient.getCity() != null) {
			familyInDB.setCity(familyInClient.getCity());
		}
		
		if (familyInClient.getDistrict() != null) {
			familyInDB.setDistrict(familyInClient.getDistrict());
		}
		
		if (familyInClient.getAddress() != null) {
			familyInDB.setAddress(familyInClient.getAddress());
		}
		
		familyRepository.update(familyInDB);
		
		parentRepository.update(parent);
		return parent;
	}
	
	public Parent changePassword(Parent parent) {
		parent.setPassword(PasswordEncryptor.encrypt(parent.getPassword()));
		parentRepository.update(parent);
		return parent;
	}
	
	public Parent changePassword(long id, String password, String originalPassword) {
		Parent parent = this.find(id);
		if (parent.getPassword().equals(PasswordEncryptor.encrypt(originalPassword))) {// original password correct
			parent.setPassword(PasswordEncryptor.encrypt(password));

			return this.update(parent);
		} else {
			throw new UserNotExistServiceException("Password or username is wrong");
		}
	}

	public Parent findReferredBy(long studentId){
		return parentRepository.findReferredBy(studentId);
	}
	
	public List<Parent> findByrecommendCode(String recommendCode){
		return parentRepository.findByrecommendCode(recommendCode);
	}
}
