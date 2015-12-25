package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.RolePermission;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.RolePermissionService;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.StringWrapper;

@RestController
@RequestMapping("/api/service/private/rolePermissions")
public class RolePermissionController {
	private Logger logger = LoggerFactory.getLogger(BroadcastController.class.getSimpleName());

	@Resource
	private RolePermissionService rolePermissionService;

	@RequestMapping(method = RequestMethod.POST)
	public Response create(@RequestBody RolePermission rolePermission,HttpServletResponse res) {
		logger.info("create");
		Response response = rolePermissionService.create(rolePermission);
		if (null != res) {
			res.setStatus(response.getStatus());
        }
		return response;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Response update(@RequestBody RolePermission rolePermission,HttpServletResponse res) {
		logger.info("creat e");
		Response response = rolePermissionService.update(rolePermission);
		if (null != res) {
			res.setStatus(response.getStatus());
        }
		return response;
	}

	@RequestMapping(value = "/findAllRoleNames", method = RequestMethod.GET)
	public List<StringWrapper> findAllRoleNames() {
		logger.info("pole permission create");
		return rolePermissionService.findAllRoleNames();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<RolePermission> list(@RequestParam(value="search",required=false) String search, @RequestParam(value="start",required=false) Integer start, @RequestParam(value="length",required=false) Integer length) {
		if(null==start){
			start=0;
		}
		if(null==length){
			length=0;
		}
		logger.info("pole permission list");
		return rolePermissionService.list(search, start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value="search",required=false)  String search) {
		return rolePermissionService.count(search);
	}

}
