package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.FiremanOnlineClassSupportingStatusRedisService;
import com.vipkid.service.pojo.FiremanLogRedisView;
import com.vipkid.service.pojo.LongList;

@RestController
@RequestMapping(value="/api/service/private/FiremanOnlineClassSupportingStatusRedisService")
public class FiremanOnlineClassSupportingStatusRedisController {
	private Logger logger = LoggerFactory.getLogger(FiremanOnlineClassSupportingStatusRedisController.class.getSimpleName());
	
	@Resource
	private FiremanOnlineClassSupportingStatusRedisService firemanOnlineClassSupportingStatusRedisService;

	@RequestMapping(value = "/setTeacherHavingProblem")
	public Response setTeacherHavingProblem(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set teacher having problem for onlineclass {}", onlineClassId);
		
		return firemanOnlineClassSupportingStatusRedisService.setTeacherHavingProblem(onlineClassId);
	}
	
	@RequestMapping(value = "/setTeacherResolvedProblem", method = RequestMethod.GET)
	public Response setTeacherResolvedProblem(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set teacher having problem for onlineclass {}", onlineClassId);
		
		return firemanOnlineClassSupportingStatusRedisService.setTeacherResolvedProblem(onlineClassId);
	}
	
	@RequestMapping(value = "/setStudentHavingProblem", method = RequestMethod.GET)
	public Response setStudentHavingProblem(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set student having problem for onlineclass {}", onlineClassId);
		
		return firemanOnlineClassSupportingStatusRedisService.setStudentHavingProblem(onlineClassId);
	}
	
	@RequestMapping(value = "/setStudentResolvedProblem", method = RequestMethod.GET)
	public Response setStudentResolvedProblem(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set student having problem for onlineclass {}", onlineClassId);

		return firemanOnlineClassSupportingStatusRedisService.setStudentResolvedProblem(onlineClassId);
	}
	
	@RequestMapping(value = "/setStudentInClassroom", method = RequestMethod.GET)
	public Response setStudentInClassroom(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set student in the classroom for onlineclass {}", onlineClassId);
		
		return firemanOnlineClassSupportingStatusRedisService.setStudentInClassroom(onlineClassId);
	}
	
	@RequestMapping(value = "/setTeacherInClassroom", method = RequestMethod.GET)
	public Response setTeacherInClassroom(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.error("[FIREMAN] set teacher in the classroom for onlineclass {}", onlineClassId);
		return firemanOnlineClassSupportingStatusRedisService.setTeacherInClassroom(onlineClassId);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public List<FiremanLogRedisView> list(@RequestBody LongList ids) {
		logger.error("[FIREMAN] looking for fireman log status for onlineclasses");
		
		return firemanOnlineClassSupportingStatusRedisService.list(ids);
	}
}
