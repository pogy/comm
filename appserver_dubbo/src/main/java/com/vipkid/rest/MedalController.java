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

import com.vipkid.model.MarketActivity;
import com.vipkid.model.Medal;
import com.vipkid.service.MedalService;

@RestController
@RequestMapping("/api/service/private/medal")
public class MedalController {

	private Logger logger = LoggerFactory.getLogger(MedalController.class.getSimpleName());
	
	@Resource
	private MedalService medalService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Medal find(@RequestParam("id") long id){
		return medalService.find(id);
	}
	
	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<Medal> findByStudentId(@RequestParam("studentId") long studentId){
		logger.info("find StudentId: {}", studentId);
		return medalService.findByStudentId(studentId);
	}
	
	@RequestMapping(value = "/findByStudentIdAndAcitivity", method = RequestMethod.GET)
	public List<Medal> findByStudentIdAndWellcome(@RequestParam("studentId") long studentId, @RequestParam("activity") MarketActivity activity){
		logger.info("find StudentId: {} activity: {}", studentId,activity);
		return medalService.findByStudentIdAndWellcome(studentId,activity);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public Medal update(@RequestBody Medal medal) {
		logger.info("update medal: {}", medal);
		return medalService.update(medal);
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Medal create(@RequestBody Medal medal) {
		logger.info("create medal: {}", medal);
		return medalService.create(medal);
	}
	
}
