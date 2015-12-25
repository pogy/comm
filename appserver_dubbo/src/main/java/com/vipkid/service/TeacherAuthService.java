package com.vipkid.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Teacher;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.User;
import com.vipkid.model.User.Status;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.TeacherApplicationRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.repository.UserRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.SecurityService;
import com.vipkid.security.TokenGenerator;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.Recruitment;
import com.vipkid.service.pojo.TeacherAuthView;
import com.vipkid.util.Configurations;

@Service
public class TeacherAuthService {
	private Logger logger = LoggerFactory.getLogger(TeacherAuthService.class.getSimpleName());

	@Resource
	private SecurityService securityService;

	@Resource
	private TeacherRepository teacherRepository;

	@Resource
	private UserRepository userRepository;

	@Resource
	private TeacherApplicationRepository teacherApplicationRepository;

	private static final String MAGIC_WORD = "kxoucywejl";

	public TeacherAuthView login(Credential credential) {
		String username = credential.getUsername();
		String password = credential.getPassword();

		Teacher teacher = teacherRepository.findByUsernameAndPassword(username, password);
		if (teacher == null) {
			throw new UserNotExistServiceException("Teacher[username: {}] is not exist.", username);
		} else {
			if (TextUtils.isEmpty(teacher.getToken())) {
				teacher.setToken(TokenGenerator.generate());
			}
			teacher.setLastLoginDateTime(new Date());
			teacherRepository.update(teacher);
		}

        cacheInRedis(teacher);

        TeacherAuthView teacherAuthView = new TeacherAuthView();
        teacherAuthView.setId(teacher.getId());
        teacherAuthView.setName(teacher.getName());
        teacherAuthView.setToken(teacher.getToken());
        teacherAuthView.setRoles(teacher.getRoles());
        teacherAuthView.setAvatar(teacher.getAvatar());
        teacherAuthView.setTimezone(teacher.getTimezone());
        teacherAuthView.setStatus(teacher.getStatus());

		return teacherAuthView;
	}

	public Teacher loginWithTeacherRecruitmentId(Recruitment recruitment) {
		String recruitmentId = recruitment.getRecruitmentId();

		Teacher teacher = teacherRepository.findByRecruitmentId(recruitmentId);
		if (teacher == null) {
			throw new UserNotExistServiceException("Teacher for [recruitment id: {}] is not exist.", recruitmentId);
		} else {
			if (TextUtils.isEmpty(teacher.getToken())) {
				teacher.setToken(TokenGenerator.generate());
			}
			teacher.setLastLoginDateTime(new Date());
			teacherRepository.update(teacher);

			logger.info("Teacher[username: {}] is login.", teacher.getUsername());
		}
		return teacher;
	}

	public Teacher doSignUp(Teacher teacher) {

		User findTeacher = userRepository.findByUsername(teacher.getEmail());
		if (findTeacher == null) {
			teacher.setUsername(teacher.getEmail());

			String strPwd = teacher.getPassword();
			if (null == strPwd || strPwd.trim() == "") {
				strPwd = Configurations.Auth.DEFAULT_TEACHER_PASSWORD;
			}
			teacher.setPassword(PasswordEncryptor.encrypt(strPwd));

			teacher.setSerialNumber(String.format("%05d", teacherRepository.totalCount() + 1));
			teacher.setStatus(Status.NORMAL);
			teacher.setLifeCycle(LifeCycle.SIGNUP);
			teacher.setToken(TokenGenerator.generate());
			teacher.setRecrutmentId(PasswordEncryptor.encrypt(teacher.getSerialNumber() + MAGIC_WORD + teacher.getEmail()));
			teacherRepository.create(teacher);
			// set creator
			User creater = teacher;
			teacher.setCreater(creater);
			teacher.setLastEditor(creater);
			teacherRepository.update(teacher);

			securityService.logAudit(Level.INFO, Category.TEACHER_CREATE, "Sign up teacher: " + teacher.getName());

			return teacher;
		} else {
			throw new UserAlreadyExistServiceException("Teacher already exist.");
		}
	}

	public Response doForgetPwd(String email) {
		//
		Teacher teacher = teacherRepository.findByEmail(email);
		if (null != teacher) {
			String newPassword = Configurations.Auth.DEFAULT_TEACHER_PASSWORD;
			teacher.setPassword(PasswordEncryptor.encrypt(newPassword));
			teacherRepository.update(teacher);

			EMail.sendResetTeacherPasswordEmail(teacher.getName(), teacher.getEmail(), newPassword);

			return new Response(HttpStatus.OK.value());
		} else {
			//
			throw new UserNotExistServiceException("no account with the email:" + email);
		}
	}
    public void cacheInRedis(Teacher teacher) {
        if (null != teacher) {
            logger.info("Cache Teacher in redis,teacher's name is{}", teacher.getUsername());
            String redisKey = KeyGenerator.generateKey(String.valueOf(teacher.getId()), teacher.getToken());
            Teacher cacheTeacher = new Teacher();
            cacheTeacher.setId(teacher.getId());
            cacheTeacher.setToken(teacher.getToken());
            cacheTeacher.setRoles(teacher.getRoles());
            cacheTeacher.setUsername(teacher.getUsername());
            cacheTeacher.setName(teacher.getName());
            cacheTeacher.setEmail(teacher.getEmail());
            cacheTeacher.setSerialNumber(teacher.getSerialNumber());
            RedisClient.getInstance().setObject(redisKey,cacheTeacher);
        }
    }
}
