package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.MoxtraUser;
import com.vipkid.model.User;
import com.vipkid.repository.MoxtraUserRepository;
import com.vipkid.repository.UserRepository;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.param.DateTimeParam;

@Service
public class MoxtraUserService {
	private Logger logger = LoggerFactory.getLogger(MoxtraUserService.class.getSimpleName());
	
	@Resource
	private MoxtraUserRepository moxtraUserRepository;
	
	@Resource
	private UserRepository userRepository;
	
	public MoxtraUser assign(long vipkidUserId) {
		MoxtraUser findMoxtraUser = moxtraUserRepository.findByVIPKIDUserId(vipkidUserId);
		if(findMoxtraUser == null) {
			User user = userRepository.find(vipkidUserId);
			if(user == null) {
				throw new UserNotExistServiceException("User[id: {}] is not exist.", vipkidUserId);
			}else {
				MoxtraUser moxtraUser = null;
				
				List<MoxtraUser> unUseMoxtraUsers = moxtraUserRepository.findByUsage(false);
				if(unUseMoxtraUsers.isEmpty()) {
					moxtraUser = new MoxtraUser();
					moxtraUser.setMoxtraUserId(String.format("%10d", moxtraUserRepository.totalCount() + 1));
					moxtraUser.setVipkidUser(user);
					moxtraUser.setInUse(true);
					moxtraUserRepository.create(moxtraUser);
				}else {
					moxtraUser = unUseMoxtraUsers.get(0);
					moxtraUser.setVipkidUser(user);
					moxtraUser.setInUse(true);
					moxtraUserRepository.update(moxtraUser);
				}
				
				return moxtraUser;
			}
		}else {
			return findMoxtraUser;
		}
	}
	
	public MoxtraUser unassign(long vipkidUserId) {
		MoxtraUser moxtraUser = moxtraUserRepository.findByVIPKIDUserId(vipkidUserId);
		if(moxtraUser != null) {
			moxtraUser.setVipkidUser(null);
			moxtraUser.setInUse(false);
			moxtraUserRepository.update(moxtraUser);
		}
		
		return moxtraUser;
	}
	
	public List<MoxtraUser> release() {
		List<MoxtraUser> moxtraUsers = moxtraUserRepository.findByUsage(true);
		for(MoxtraUser moxtraUser : moxtraUsers) {
			moxtraUser.setVipkidUser(null);
			moxtraUser.setInUse(false);
			moxtraUserRepository.update(moxtraUser);
		}
		
		return moxtraUsers;
	}
	
	public List<MoxtraUser> list(Boolean inUse, DateTimeParam fromDate, DateTimeParam toDate, Integer start, Integer length) {
		logger.debug("list moxtra user with params: inUse = {}, fromDate = {}, toDate = {}, start = {}, length = {}.", inUse, fromDate, toDate, start, length);
		return moxtraUserRepository.list(inUse, fromDate, toDate, start, length);
	}

	public Count count( Boolean inUse, DateTimeParam fromDate, DateTimeParam toDate) {
		logger.debug("count moxtra user with params: inUse = {}, fromDate = {}, toDate = {}.", inUse, fromDate, toDate);
		return new Count(moxtraUserRepository.count(inUse, fromDate, toDate));
	}
}
