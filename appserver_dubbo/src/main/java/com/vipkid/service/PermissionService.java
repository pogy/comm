package com.vipkid.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vipkid.model.Permission;
import com.vipkid.service.pojo.StringWrapper;

@Service
public class PermissionService {
	
	public List<StringWrapper> findAllPermissions(){
		return Permission.getNames();
	}

}
