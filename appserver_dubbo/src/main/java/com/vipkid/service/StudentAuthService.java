package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Student;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.TokenGenerator;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Credential;

@Service
public class StudentAuthService {
	private Logger logger = LoggerFactory.getLogger(StudentAuthService.class.getSimpleName());
	
	@Context
	private ServletContext servletContext;
	
	@Resource
	private StudentRepository studentRepository;

	public Student login(Credential credential){
		String username = credential.getUsername();
		String password = credential.getPassword();
		
		Student student = studentRepository.findByUsernameAndPassword(username, password);
		if(student == null){
			throw new UserNotExistServiceException("Student[username: {}] is not exist.", username);
		}else{
			if(TextUtils.isEmpty(student.getToken())) {
				student.setToken(TokenGenerator.generate());
			}
			student.setLastLoginDateTime(new Date());
			studentRepository.update(student);
            cacheInRedis(student);
			logger.info("Student[username: {}] is login.", student.getUsername());
		}
		
		return student;
	}

    public void cacheInRedis(Student student) {
        if (null != student) {
            logger.info("Cache Student in redis,student's name is{}",student.getUsername());
            String redisKey = KeyGenerator.generateKey(String.valueOf(student.getId()),student.getToken());
            Student cachedStudent = new Student();
            cachedStudent.setId(student.getId());
            cachedStudent.setToken(student.getToken());
            cachedStudent.setRoles(student.getRoles());
            cachedStudent.setName(student.getName());
            cachedStudent.setEnglishName(student.getEnglishName());
            cachedStudent.setUsername(student.getUsername());
            RedisClient.getInstance().setObject(redisKey,cachedStudent);
        }
    }
	public List<Student> findByFamilyId(long familyId) {
		logger.debug("find students by family id = {}", familyId);
		return studentRepository.findByFamilyId(familyId);
	}
	
	
}
