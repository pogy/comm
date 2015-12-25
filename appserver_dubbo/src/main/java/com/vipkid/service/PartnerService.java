package com.vipkid.service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Partner;
import com.vipkid.model.Partner.Type;
import com.vipkid.model.User.Status;
import com.vipkid.repository.PartnerRepository;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.util.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class PartnerService {

	private Logger logger = LoggerFactory.getLogger(PartnerService.class.getSimpleName());
	
	@Resource
	private SecurityService securityService;

	@Resource
	private PartnerRepository partnerRepository;
	

	public Partner find(long id) {
		logger.debug("find partner: {}", id);
		return partnerRepository.find(id);
	}	

	/**
	 * 招募端使用list， 
	 * @param search
	 * @param status
	 * @param email
	 * @param username
	 * @param start
	 * @param length
	 * @return
	 */
	public List<Partner> list(String search,Status status, String email, String username, Type type, Integer start, Integer length) {
		logger.debug("list partners: {}");
		return partnerRepository.list(search,status, email, username, start, length, type);
	}
	
	public List<Partner> list(String search,Status status, String email, String username, Integer start, Integer length) {
		logger.debug("list partners: {}");
		return partnerRepository.list(search,status, email, username, start, length,null);
	}

	public Count count(String search, Status status,String email, String username, int start, int length) {
		return new Count(partnerRepository.count(search,status, email, username, start, length));
	}

	

	public Partner update(Partner partner) {
		logger.debug("update partner: {}", partner);
		partnerRepository.update(partner);
		securityService.logAudit(Level.INFO, Category.STAFF_UPDATE, "Update staff: " + partner.getName());
		return partner;
	}
	
	public Partner create(Partner partner){
		logger.debug("create partner: {}", partner);
		partner.setType(Type.TEACHER_RECRUITMENT);
		partner.setPassword(PasswordEncryptor.encrypt(Configurations.Auth.DEFAULT_TEACHER_PASSWORD));
		partner.setRegisterDateTime(new Date());
		partner.setEmail(partner.getUsername());
		return partnerRepository.create(partner);
		
	}
	

	public Partner doLock( long id) {
		
		Partner partner = partnerRepository.find(id);
		if(partner == null) {
			throw new UserNotExistServiceException("Staff[id: {}] is not exist.", id);
		}else {
			partner.setStatus(Status.LOCKED);
			partnerRepository.update(partner);
			
			securityService.logAudit(Level.INFO, Category.STAFF_LOCK, "Lock staff: " + partner.getName());
		}
		
		return partner;
	}
	

	public Partner doUnlock(long id) {
		
		Partner partner = partnerRepository.find(id);
		if(partner == null) {
			throw new UserNotExistServiceException("Staff[id: {}] is not exist.", id);
		}else {
			partner.setStatus(Status.NORMAL);
			partnerRepository.update(partner);
			
			securityService.logAudit(Level.INFO, Category.STAFF_UNLOCK, "Unlock staff: " + partner.getName());
		}		
		return partner;
	}	

}
