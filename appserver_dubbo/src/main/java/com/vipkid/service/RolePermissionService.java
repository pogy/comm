package com.vipkid.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.model.RolePermission;
import com.vipkid.repository.RolePermissionRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.StringWrapper;

@Service
public class RolePermissionService {
	
	@Resource
	private RolePermissionRepository rolePermissionRepository;
	
	public Response create(RolePermission rolePermission) {
		rolePermissionRepository.create(rolePermission);
		return new Response(HttpStatus.OK.value());
	}
	
	public Response update(RolePermission rolePermission) {
		rolePermissionRepository.update(rolePermission);
		return new Response(HttpStatus.OK.value());
	}
	
	public List<StringWrapper> findAllRoleNames(){
		
		List<String> roles = rolePermissionRepository.findAllRoles();
		List<StringWrapper> roleNames = new ArrayList<StringWrapper>();
		for (String role : roles){
			roleNames.add(new StringWrapper(role));
		}
		return roleNames;
	}
	
	public List<RolePermission> list(String search, int start, int length) {
		return rolePermissionRepository.list(search, start, length);
	}

	public Count count(String search) {
		return new Count(rolePermissionRepository.count(search));
	}

}
