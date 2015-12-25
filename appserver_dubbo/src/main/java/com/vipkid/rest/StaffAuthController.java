package com.vipkid.rest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.vipkid.service.StaffAuthService;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.StaffAuthView;

@RestController
@RequestMapping("/api/service/public/auth/staff")
public class StaffAuthController {
	private Logger logger = LoggerFactory.getLogger(StaffAuthController.class.getSimpleName());

	@Resource
	private StaffAuthService staffAuthService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public StaffAuthView login(@RequestBody Credential credential,HttpServletResponse response) {
        logger.info("login,credential={}", JSON.toJSONString(credential));
        return staffAuthService.login(credential);
    }

}
