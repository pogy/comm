package com.vipkid.security.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.model.Agent;
import com.vipkid.model.Parent;
import com.vipkid.model.Portal;
import com.vipkid.model.Staff;
import com.vipkid.model.Teacher;
import com.vipkid.model.User;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.AgentAuthRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.security.AuthenticateService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.StaffAuthService;
import com.vipkid.service.StudentAuthService;
import com.vipkid.service.TeacherAuthService;
import com.vipkid.util.TextUtils;

/**
 * 身份验证服务
 */
@Service("authenticateService")
public class AuthenticateServiceImpl implements AuthenticateService {
    private static Logger logger = LoggerFactory.getLogger(AuthenticateServiceImpl.class);

	@Resource
	private StudentRepository studentRepository;

	@Resource
	private ParentRepository parentRepository;

	@Resource
	private TeacherRepository teacherRepository;

	@Resource
	private StaffRepository staffRepository;

	@Resource
	private AgentAuthRepository agentAuthRepository;

    @Resource
    private TeacherAuthService teacherAuthService;
    @Resource
    private StudentAuthService studentAuthService;
    @Resource
    private ParentAuthService parentAuthService;
    @Resource
    private StaffAuthService staffAuthService;


    @Override
	public User authenticate(String authorization) {
		if (StringUtils.isEmpty(authorization)) {
			return null;
		} else {
			String[] strings = authorization.split(TextUtils.SPACE);
			if (strings.length == 3 && strings[0] != null && strings[1] != null && strings[2] != null) {
				String portal = strings[0];
				long id = Long.parseLong(strings[1]);
				String token = strings[2];

                String redisUserKey = KeyGenerator.generateKey(String.valueOf(id),token);
                User userInRedis = (User) RedisClient.getInstance().getObject(redisUserKey);
                if (null != userInRedis) {
                    return userInRedis;
                }
                if (Portal.TEACHER.name().equalsIgnoreCase(portal) || Portal.TEACHERRECRUITMENT.name().equalsIgnoreCase(portal)) {
                	Teacher teacher = null;
                	if (token.equals(DigestUtils.md5Hex(portal + " " + id))){
                		logger.info("From new teacher portal id={}", id);
                		teacher = teacherRepository.find(id);
                    } else{
                    	logger.info("From old teacher portal, id={}", id);
                    	teacher = teacherRepository.findByIdAndToken(id, token);
                    }
                	if (teacher == null){
                		logger.info("Teacher is Null!");
                		return null;
                	}
                    teacherAuthService.cacheInRedis(teacher);
					return teacher;
				} else if (Portal.PARENT.name().equalsIgnoreCase(portal) || Portal.HOME.name().equalsIgnoreCase(portal) || Portal.LEARNING.name().equalsIgnoreCase(portal)) {
                    Parent parent = parentRepository.findByIdAndToken(id, token);
                    parentAuthService.cacheInRedis(parent);
                    return parent;
				}  else if (Portal.MANAGEMENT.name().equalsIgnoreCase(portal)) {
                    Staff staff = staffRepository.findByIdAndToken(id, token);
                    staffAuthService.cacheStaff2Redis(staff);
					return staff;
				} else if (Portal.AGENTMANAGEMENT.name().equalsIgnoreCase(portal)) {
					Agent agent = agentAuthRepository.findByIdAndToken(id, token);
					if (agent == null) {
						return null;
					} else {
						User user = new User();
						user.setName(agent.getName());
						user.setId(agent.getId());
						user.setUsername(agent.getEmail());
						user.setPassword(agent.getPassword());
						user.setToken(agent.getToken());
                        RedisClient.getInstance().setObject(redisUserKey,user);
						return user;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
