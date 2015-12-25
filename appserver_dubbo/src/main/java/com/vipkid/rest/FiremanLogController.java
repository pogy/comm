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

import com.vipkid.model.FiremanLog;
import com.vipkid.model.FiremanLog.Event;
import com.vipkid.service.FiremanLogService;
import com.vipkid.service.pojo.FiremanLogView;

@RestController
@RequestMapping(value="/api/service/private/firemanLog")
public class FiremanLogController {
	private Logger logger = LoggerFactory.getLogger(FiremanLogController.class.getSimpleName());
	
	@Resource
	private FiremanLogService firemanLogService;

	@RequestMapping(value="/create",method = RequestMethod.POST)
	public FiremanLog create(@RequestBody FiremanLog firemanLog) {
		logger.info("create fireman log: {}", firemanLog);
		return firemanLogService.create(firemanLog);
	}
	
	@RequestMapping(value="/update",method = RequestMethod.PUT)
	public FiremanLog update(@RequestBody FiremanLogView firemanLogView) {
		logger.info("update fireman log: {}", firemanLogView);
		return firemanLogService.update(firemanLogView);
	}
	
	@RequestMapping(value="/findRecentLogByOnlineClassIdAndEvent",method = RequestMethod.GET)
	public FiremanLog findRecentLogByOnlineClassIdAndEvent(@RequestParam("onlineClassId") long onlineClassId, @RequestBody Event event) {
		logger.info("findRecentLogByOnlineClassId: " + onlineClassId);
		return firemanLogService.findRecentLogByOnlineClassIdAndEvent(onlineClassId, event);
	}
	
	@RequestMapping(value="/findOnlineClassSupportingStatus",method = RequestMethod.GET)
	public List<FiremanLogView> findOnlineClassSupportingStatus(@RequestParam("courseIds")  List<Long> courseIds) {
		logger.info("findRecentLogByOnlineClassId: ");
		return firemanLogService.findOnlineClassSupportingStatus(courseIds);
	}
	
	@RequestMapping(value="/resolvedStudentProblem",method = RequestMethod.GET)
	public FiremanLog resolvedStudentProblem(@RequestParam("onlineClassId") long onlineClassId) {
		logger.info("resolvedStudentProblem: " + onlineClassId);
		return firemanLogService.resolvedStudentProblem(onlineClassId);
	}
	
	@RequestMapping(value="/resolvedTeacherProblem",method = RequestMethod.GET)
	public FiremanLog resolvedTeacherProblem(@RequestParam("onlineClassId") long onlineClassId) {
		logger.info("resolvedTeacherProblem: " + onlineClassId);
		return firemanLogService.resolvedTeacherProblem(onlineClassId);
	}
	
	@RequestMapping(value="/checkTeacherNotEnterYell",method = RequestMethod.GET)
	public FiremanLog checkTeacherNotEnterYell(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.info("checkTeacherNotEnterYell: " + onlineClassId);
		return firemanLogService.checkTeacherNotEnterYell(onlineClassId);
	}
	
	@RequestMapping(value="/checkStudentNotEnterYell",method = RequestMethod.GET)
	public FiremanLog checkStudentNotEnterYell(@RequestParam("onlineClassId") Long onlineClassId) {
		logger.info("checkStudentNotEnterYell: " + onlineClassId);
		return firemanLogService.checkStudentNotEnterYell(onlineClassId);
	}
	
}
