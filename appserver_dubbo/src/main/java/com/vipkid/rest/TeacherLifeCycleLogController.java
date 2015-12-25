package com.vipkid.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.TeacherLifeCycleLog;
import com.vipkid.model.User;
import com.vipkid.service.TeacherLifeCycleLogService;

@RestController
@RequestMapping("/api/service/private/teacherLifeCycleLog")
public class TeacherLifeCycleLogController {
private Logger logger = LoggerFactory.getLogger(TeacherLifeCycleLogController.class.getSimpleName());
	
	@Resource
	private TeacherLifeCycleLogService teacherLifeCycleLogService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOperatorOptions", method = RequestMethod.GET)
	@ResponseBody
	public List<JSONObject> getOperatorOptions(@RequestParam("lifeCycle") String lifeCycle) {
		logger.info("getQuitOperatorOptions for lifeCycle = {}", lifeCycle);
		LifeCycle LC = LifeCycle.valueOf(lifeCycle);
		List<User> teacherLifeCycleLogList = teacherLifeCycleLogService.getOperatorOptions(LC);
		if(teacherLifeCycleLogList != null && !teacherLifeCycleLogList.isEmpty()){
			List<JSONObject> result = new ArrayList<JSONObject>();
			for(User teacherLifeCycleLog : teacherLifeCycleLogList){
				JSONObject name = new JSONObject();
				name.put("value", teacherLifeCycleLog.getId());
				name.put("label", teacherLifeCycleLog.getName());
				result.add(name);
			}
			return result;
		}
		return null;
	}
	
	
}
