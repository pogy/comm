package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Teacher;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.TeacherAuthService;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.Recruitment;
import com.vipkid.service.pojo.TeacherAuthView;

@RestController
@RequestMapping("/api/service/public/auth/teacher")
public class TeacherAuthController {
	private Logger logger = LoggerFactory.getLogger(TeacherAuthController.class
			.getSimpleName());

	@Resource
	private TeacherAuthService teacherAuthService;
	
	//private static final String MAGIC_WORD = "kxoucywejl";

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public TeacherAuthView login(@RequestBody Credential credential) {
		logger.info("login teacher with params: credential = {}.", credential);
		return teacherAuthService.login(credential);
	}
	
	@RequestMapping(value = "/loginWithTeacherRecruitmentId", method = RequestMethod.POST)
	public Teacher loginWithTeacherRecruitmentId(@RequestBody Recruitment recruitment) {
		logger.info("login with teacher tecruitmentId = {}.", recruitment);
		return teacherAuthService.loginWithTeacherRecruitmentId(recruitment);
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Teacher signUp(@RequestBody Teacher teacher) {
		logger.info("sign up teacher email = {}.", teacher.getEmail());
		return teacherAuthService.doSignUp(teacher);
	}

	@RequestMapping(value = "/forgetpwd", method = RequestMethod.POST)
	public Response forgetPwd(@RequestParam("email") String email) {
		logger.info("forget pass word with email = {}.", email);
		return teacherAuthService.doForgetPwd(email);
	}
}
