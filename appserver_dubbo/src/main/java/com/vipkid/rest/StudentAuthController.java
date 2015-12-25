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

import com.vipkid.model.Student;
import com.vipkid.service.StudentAuthService;
import com.vipkid.service.pojo.Credential;

@RestController
@RequestMapping("/api/service/public/auth/student")
public class StudentAuthController {
	private Logger logger = LoggerFactory.getLogger(StudentAuthController.class.getSimpleName());

	@Resource
	private StudentAuthService studentAuthService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Student login(@RequestBody Credential credential) {
		return studentAuthService.login(credential);
	}

	@RequestMapping(value = "/findByFamilyId", method = RequestMethod.GET)
	public List<Student> findByFamilyId(@RequestParam("familyId") long familyId) {
		logger.debug("find students by family id = {}", familyId);
		return studentAuthService.findByFamilyId(familyId);
	}

}
