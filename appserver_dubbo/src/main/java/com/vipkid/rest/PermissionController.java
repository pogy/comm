package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.service.PermissionService;
import com.vipkid.service.pojo.StringWrapper;

@RestController
@RequestMapping("/api/service/private/permissions")
public class PermissionController {

	@Resource
	private PermissionService permissionService;
	
	@RequestMapping(value = "/findAllPermissions",method = RequestMethod.GET)
	public List<StringWrapper> findAllPermissions(){
		return permissionService.findAllPermissions();
	}

}
