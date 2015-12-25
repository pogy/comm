package com.vipkid.service;

import com.vipkid.model.Staff;
import com.vipkid.model.User.Status;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.RolePermissionRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.security.TokenGenerator;
import com.vipkid.service.exception.UserLockedServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.StaffAuthView;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import java.util.Date;
import java.util.List;

@Service
public class StaffAuthService {
    private Logger logger = LoggerFactory.getLogger(StaffAuthService.class.getSimpleName());

    @Context
    private ServletContext servletContext;

    @Resource
    private StaffRepository staffRepository;

    @Resource
    private RolePermissionRepository rolePermissionRepository;

    public StaffAuthView login(Credential credential) {
        String username = credential.getUsername();
        String password = credential.getPassword();

        Staff staff = staffRepository.findByUsernameAndPassword(username, password);
        if (staff == null) {
            throw new UserNotExistServiceException("Staff[username: {}] is not exist.", username);
        } else {
            if (staff.getStatus() == Status.LOCKED) {
                throw new UserLockedServiceException("Staff[username: {}] is locked.", username);
            }

            if (TextUtils.isEmpty(staff.getToken())) {
                staff.setToken(TokenGenerator.generate());
            }
            staff.setLastLoginDateTime(new Date());
            staffRepository.update(staff);
            cacheStaff2Redis(staff);
            logger.info("Staff[username: {}] is login.", staff.getUsername());
            StaffAuthView staffAuthView = new StaffAuthView();
            staffAuthView.setId(staff.getId());
            staffAuthView.setName(staff.getName());
            staffAuthView.setToken(staff.getToken());
            staffAuthView.setRoles(staff.getRoles());
            staffAuthView.setSalesTeamId(staff.getSalesTeamId());
            staffAuthView.setIsFirstLogin(staff.getIsFirstLogin());
            List<String> permissionCodes = rolePermissionRepository.findPermissionCodes(staff.getRoleList());
            staffAuthView.setPermissionCodes(permissionCodes);
            cachePermissions2Redis(staff);
            return staffAuthView;
        }
    }

    public void cacheStaff2Redis(Staff staff) {
        if (null != staff) {
            Staff staffInCache = new Staff();
            staffInCache.setId(staff.getId());
            staffInCache.setToken(staff.getToken());
            staffInCache.setRoles(staff.getRoles());
            staffInCache.setUsername(staff.getUsername());
            staffInCache.setName(staff.getName());
            staffInCache.setEnglishName(staff.getEnglishName());
            staffInCache.setMobile(staff.getMobile());
            staffInCache.setSalesTeamId(staff.getSalesTeamId());
            String redisKey = KeyGenerator.generateKey(String.valueOf(staff.getId()), staff.getToken());
            RedisClient.getInstance().setObject(redisKey, staffInCache);
            logger.info("Cache staff in Redis,Staff's username={}",staff.getUsername());
        }
    }

    public void cachePermissions2Redis(Staff staff) {
        if (null != staff) {
            //缓存权限
            String permissions = rolePermissionRepository.findPermissions(staff.getRoleList()); // 只将编码后的permisson 序列化回前台
            String permissionsKey = KeyGenerator.generateKey(KeyGenerator.prefixPermissions + staff.getId(), staff.getToken());
            try {
                RedisClient.getInstance().set(permissionsKey, permissions);
                RedisClient.getInstance().expire(permissionsKey, 24 * 3600);
                logger.info("Cache staff's permissions in Redis,Staff's username={}",staff.getUsername());
            } catch (Exception e) {
                logger.error("Redis Exception", e);
            }
        }
    }

}
