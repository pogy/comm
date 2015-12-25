package com.vipkid.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Parent;
import com.vipkid.model.User.Status;
import com.vipkid.service.ParentService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Count;
import com.vipkid.util.TextUtils;

@RestController
@RequestMapping("/api/service/private/parents")
public class ParentController {
	private Logger logger = LoggerFactory.getLogger(ParentController.class.getSimpleName());

	@Resource
	private ParentService parentService;

	@Resource
	private StudentService studentService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Parent find(@QueryParam("id") long id) {
		logger.info("find parent for id = {}", id);
		return parentService.find(id);
	}
	
	@RequestMapping(value = "/findByFamilyId", method = RequestMethod.GET)
	public List<Parent> findByFamilyId(@QueryParam("familyId") long familyId) {
		logger.info("find parents by family id = {}", familyId);
		return parentService.findByFamilyId(familyId);
	}
	
	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<Parent> findByStudentId(@QueryParam("studentId") long studentId) {
		logger.info("find parents by student id = {}", studentId);
		return parentService.findByStudentId(studentId);
	}

	@RequestMapping(value = "/findByUsername", method = RequestMethod.GET)
	public Parent findByUsername(@QueryParam("username") String username) {
		logger.info("find parents by username = {}", username);
		return parentService.findByUsername(username);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Parent> list(@QueryParam("search") String search, @QueryParam("status") Status status, @QueryParam("start") int start, @QueryParam("length") int length) {
		logger.info("list parent with params: search = {}, status = {}, start = {}, length = {}.", search, status, start, length);
		return parentService.list(search, status, start, length);
	}
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@QueryParam("search") String search, @QueryParam("status") Status status) {
		logger.info("count parent with params: search = {}, status = {}.", search, status);
		return parentService.count(search, status);
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public Parent resetPassword(@QueryParam("id") long id) {
		logger.info("reset password by id,id = {}.", id);
		return parentService.doResetPassword(id);
	}
	
	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public Parent lock(@QueryParam("id") long id) {
		logger.info("lock by id,id = {}.", id);
		return parentService.lock(id);
	}
	
	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public Parent unlock(@QueryParam("id") long id) {
		logger.info("unlock by id, id = {}.", id);
		return parentService.unlock(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Parent create(@RequestBody Parent parent) {
		logger.info("create parent: {}", parent);
		return parentService.create(parent);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Parent update(@RequestBody Parent parent) {
		return parentService.update(parent);
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	public Parent changePassword(@RequestBody Parent parent) {
		logger.info("change password , id = {}.", parent.getId());
		return parentService.update(parent);
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public Parent changePassword(@RequestParam("id") long id, @RequestParam("password") String password, @RequestParam("originalPassword") String originalPassword) {
		logger.info("change password , id = {}.", id);
	
		return parentService.changePassword(id, password, originalPassword);
	}

	/**
	 * 根据学生id，查询学生的推荐人
	 * @param studentId
	 * @return
	 */
	@RequestMapping(value="/findReferredBy",method = RequestMethod.GET)
	public Parent findReferredBy(@RequestParam("studentId") long studentId){
		logger.info("find ReferredBy by student id = {}", studentId);
		Parent parent = parentService.findReferredBy(studentId);
		if(null != parent){
			if(!TextUtils.isEmpty(parent.getRecommendCode())){
				Parent referred = parentService.findByUsername(parent.getRecommendCode());
				if(null != referred){
					return referred;
				}
			}
		}
		return null;
	}
	
	/**
	 * 查找推荐的家长,一个学生可能有两个家长
	 * @param studentId
	 * @return
	 */
	@RequestMapping(value="/findReferredParentsBystudentId",method = RequestMethod.GET)
	public List<Parent> findReferredParentsBystudentId(@RequestParam("studentId") long studentId){
		logger.info("find findReferredParentsBystudentId by student id = {}", studentId);
		List<Parent> parents = parentService.findByStudentId(studentId);
		List<Parent> results = new ArrayList<Parent>();
		if(null != parents && parents.size() > 0){
			for(Parent parent : parents){
				if(!TextUtils.isEmpty(parent.getMobile())){
					List<Parent> list = parentService.findByrecommendCode(parent.getMobile());
					results.addAll(list);
				}
			}
		}
		return results;
	}
}
