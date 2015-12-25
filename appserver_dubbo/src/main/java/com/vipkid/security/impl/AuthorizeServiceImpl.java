package com.vipkid.security.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.vipkid.model.User;
import com.vipkid.security.AuthorizeService;
import com.vipkid.security.SecurityService;

/**
 * Created by zfl on 2015/5/19.
 * 鉴权服务
 */
@Service("authorizeService")
public class AuthorizeServiceImpl implements AuthorizeService {
	
	//private static Logger logger = LoggerFactory.getLogger(AuthorizeServiceImpl.class.getSimpleName());
	
	@Resource
	private SecurityService securityService;
	
    @Override
    public boolean authorize(String uri, User user) {
    	return securityService.isAllowed(uri);

    }
}
