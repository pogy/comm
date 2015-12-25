package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.handler.UserHandler;
import com.vipkid.model.Role;
import com.vipkid.model.Staff;
import com.vipkid.model.User.Status;
import com.vipkid.rest.vo.query.UserVO;
import com.vipkid.security.SecurityService;
import com.vipkid.service.StaffService;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.Credential;

@RestController
@RequestMapping("/api/service/private/staffs")
public class StaffController {
	private Logger logger = LoggerFactory.getLogger(StaffController.class.getSimpleName());

	@Resource
	private StaffService staffService;

	@Resource
	private SecurityService securityService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Staff find(@RequestParam("id") long id) {
		logger.debug("find staff for id = {}", id);
		return staffService.find(id);
	}

	@RequestMapping(value = "/findByRole", method = RequestMethod.GET)
	public List<Staff> findByRole(@RequestParam("role") Role role) {
		logger.debug("find staff for role = {}", role);
		return staffService.findByRole(role);
	}

	@RequestMapping(value = "/findByUsername", method = RequestMethod.GET)
	public Staff findByRole(@RequestParam("username") String username) {
		logger.debug("find staff for username = {}", username);
		return staffService.findByRole(username);
	}
    @RequestMapping(value = "/filterByRole", method = RequestMethod.GET)
    public List<UserVO> filterByRole(@RequestParam("role") Role role) {
        logger.debug("find staff for role = {}", role);
        List<Staff> staffList = staffService.findByRole(role);
        return UserHandler.convert2UserVOList(staffList);
    }

	@RequestMapping(value = "/findByName", method = RequestMethod.GET)
	public Staff findByName(@RequestParam("name") String name) {
		logger.debug("find staff for name = {}", name);
		Staff staff = staffService.findByName(name);
		if (staff != null) {
			return staff;
		}
		throw new UserNotExistServiceException("find staff {} not exist", name);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET,produces = "application/json")
	public List<Staff> list(@RequestParam(value = "search",required = false) String search, @RequestParam(value = "role",required = false) String role,@RequestParam(value = "status",required = false) Status status, @RequestParam(value = "start",required = false) Integer start,
			@RequestParam(value = "length",required = false) Integer length) {
		logger.debug("list staff with params: search = {}, role = {}, status = {}, start = {}, length = {}.", search, role, status, start, length);
        if (null == start ) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
		return staffService.list(search, role, status, start, length);
	}

    /**
     * 数据简化版的，只返回name和id即可
     * @param search
     * @param role
     * @param status
     * @param start
     * @param length
     * @return
     */
    @RequestMapping(value = "/filter", method = RequestMethod.GET,produces = "application/json")
    public List<UserVO> filter(@RequestParam(value = "search",required = false) String search, @RequestParam(value = "role",required = false) String role,@RequestParam(value = "status",required = false) Status status, @RequestParam(value = "start",required = false) Integer start,
                            @RequestParam(value = "length",required = false) Integer length) {
        logger.debug("list staff with params: search = {}, role = {}, status = {}, start = {}, length = {}.", search, role, status, start, length);
        if (null == start ) {
            start = 0;
        }
        if (null == length) {
            length = 0;
        }
        List<Staff> staffList = staffService.list(search, role, status, start, length);
        return UserHandler.convert2UserVOList(staffList);
    }

	@RequestMapping(value = "/count", method = RequestMethod.GET,produces = "application/json")
	public Count count(@RequestParam(value = "search",required = false) String search, @RequestParam(value = "role",required = false) String role,@RequestParam(value = "status",required = false) Status status) {
		logger.info("count staff with params: search = {}, role = {}, status = {}.", search, role, status);
		return staffService.count(search, role, status);
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public Staff resetPassword(@RequestParam("id") long id) {
		return staffService.doResetPassword(id);
	}

	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public Staff lock(@RequestParam("id") long id) {
		return staffService.doLock(id);
	}

	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public Staff unlock(@RequestParam("id") long id) {
		return staffService.doUnlock(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Staff create(@RequestBody Staff staff) {
		return staffService.create(staff);

	}

	@RequestMapping(method = RequestMethod.PUT)
	public Staff update(@RequestBody Staff staff) {
		return staffService.update(staff);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	public Staff changePassword(@RequestBody Credential credential) {
		return staffService.changePassword(credential);
	}
	
	@RequestMapping(value = "/findByTeamId", method = RequestMethod.GET)
	public List<Staff> findByTeamId(@RequestParam("salesTeamId") long salesTeamId) {
		return staffService.findByTeamId(salesTeamId);
	}
}
