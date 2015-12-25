package com.vipkid.security;

import com.vipkid.context.AppContext;
import com.vipkid.model.*;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.AuditRepository;
import com.vipkid.repository.RolePermissionRepository;
import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.security.Principal;
import java.util.Date;

/**
 * 安全访问审计service
 */
@Service
public class SecurityService {
    private Logger logger = LoggerFactory.getLogger(SecurityService.class.getSimpleName());
    private static String subfix_list = "list";
    private static String subfix_count = "count";
    private static String subfix_find = "find";
    private static String subfix_filter = "filter";
    private static String subfix_home = "/home/";
    private static String subfix_parent = "/parent/";
    private static String subfix_mobile = "/mobile/";
    private static String subfix_welcome = "/welcome";
    private static String subfix_recruitment = "/recruitment";

    @Resource
    private AuditRepository auditRepository;

    @Resource
    private RolePermissionRepository rolePermissionRepository;

    public boolean isAllowed(String uri) {
        if (StringUtils.isNotBlank(uri)) {
			if (StringUtils.containsIgnoreCase(uri, subfix_list)
					|| StringUtils.containsIgnoreCase(uri, subfix_count)
					|| StringUtils.containsIgnoreCase(uri, subfix_find)
					|| StringUtils.containsIgnoreCase(uri, subfix_filter)
					|| StringUtils.startsWith(uri, subfix_home)
					|| StringUtils.startsWith(uri, subfix_parent)
					|| StringUtils.startsWith(uri, subfix_mobile)
					|| StringUtils.startsWith(uri, subfix_welcome)
					|| StringUtils.startsWith(uri, subfix_recruitment)) {
				return true;
			}
            Principal principal = AppContext.getPrincipal();
            if (null != principal) {
                User user = ((CustomizedPrincipal)principal).getUser();
                if (null != user) {
                    String permission = Permission.getNameByUri(uri);
                    if (null != permission){
                        String key = KeyGenerator.generateKey(KeyGenerator.prefixPermissions + user.getId(),user.getToken());
                        String permissionInRedis = null;
                        try {
                            permissionInRedis = RedisClient.getInstance().get(key);
                        } catch (Exception e) {
                            logger.error("Redis Exception ",e);
                        }
						if (StringUtils.isNotBlank(permissionInRedis)) {
							String[] permissionArray = StringUtils.split(permissionInRedis, TextUtils.SPACE);
							logger.info("+++++++++++++++++++++++++++PermissionInRedis {}", permissionInRedis);
							return ArrayUtils.contains(permissionArray, permission);
						} else {
							logger.info("+++++++++++++++++++++++++++get Permission from DB");
							return rolePermissionRepository.hasPermission(permission, user.getRoleList());
						}
                    }

                } else {
                    logger.warn("++++++++++++++++++++can not get user from principal");
                }
            } else {
                logger.warn("++++++++++++++++++++can not get principal from AppContext");
            }
        }
        return false;
    }

    // 当处于同一个线程时，调用此方法记录Audit
    public void logAudit(Level level, Category category, String operation) {
        try {
            Principal principal = AppContext.getPrincipal();
            if (null != principal) {
                User user = getCurrentUser();
                logAudit(level, category, operation, user);
            }
        } catch (Throwable t) {
            logger.error("Log audit encounters problems: {}", t.getMessage());
        }
    }

    // 当处于另外一个线程时，调用此方法记录Audit
    public void logAudit(Level level, Category category, String operation, User user) {
    	String operator;
		if(user != null) {
			if(user.getName() == null) {
				operator =user.getUsername();
			}else {
				if(user instanceof Teacher) {
					operator = user.getSafeName();
				}else {
					operator = user.getName();
				}
			}
		} else{
			operator = Configurations.System.SYSTEM_USER_NAME;
		}
		
		Audit audit = new Audit();
		audit.setOperator(operator);
		audit.setLevel(level);
		audit.setCategory(category);
		audit.setExecuteDateTime(new Date());
		audit.setOperation(operation);
		auditRepository.create(audit);
    }

    public void logSystemAudit(Level level, Category category, String operation) {
        try {
            Audit audit = new Audit();
            audit.setOperator(Configurations.System.SYSTEM_USER_NAME);
            audit.setLevel(level);
            audit.setCategory(category);
            audit.setExecuteDateTime(new Date());
            audit.setOperation(operation);
            auditRepository.create(audit);
        } catch (Throwable t) {
            logger.error("Log system audit encounters problems: {}", t.getMessage());
        }
    }

    public User getCurrentUser() {
        try {
            Principal principal = AppContext.getPrincipal();
            if (null != principal) {
                return  ((CustomizedPrincipal) principal).getUser();
            }
        } catch(Throwable t){
			logger.error("Get current principal failed : {}", t.getMessage(), t);
		}
        return null;
    }
}
