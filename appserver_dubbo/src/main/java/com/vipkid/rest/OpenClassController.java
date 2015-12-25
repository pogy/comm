package com.vipkid.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Channel;
import com.vipkid.model.OpenClassDesc;
import com.vipkid.service.OpenClassService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.OView;
import com.vipkid.service.pojo.OpenClassDescView;
import com.vipkid.service.pojo.TeacherView;

@RestController
@RequestMapping(value="/api/service/private/openclass")
public class OpenClassController {

	
	@Resource
	private OpenClassService openClassService;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<OpenClassDescView> list(@RequestParam(value="search",required=false) String search, 
			@RequestParam(value="startDatetime", required=false) DateTimeParam startDate, 
			@RequestParam(value="endDatetime", required=false) DateTimeParam endDate,
			@RequestParam(value="start",required=false,defaultValue="0") int start,
			@RequestParam(value="length",required=false, defaultValue="15") int length) {
		List<OpenClassDescView> openClassList = openClassService.list(search, startDate, endDate, start, length);
		return openClassList;
	}
	
	@RequestMapping(value="/count", method=RequestMethod.GET)
	public Count count(@RequestParam(value="search",required=false) String search, 
			@RequestParam(value="startDatetime", required=false) DateTimeParam startDate, 
			@RequestParam(value="endDatetime", required=false) DateTimeParam endDate,
			@RequestParam(value="start",required=false,defaultValue="0") int start,
			@RequestParam(value="length",required=false, defaultValue="15") int length) {
		Count count = openClassService.countBySearch(search, startDate, endDate);
		return count;
	}

	/**
	 * 获取一个open class的详细
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/findById", method=RequestMethod.GET)
	public OpenClassDescView findById( @RequestParam(value="id") long id) {
		OpenClassDescView openClass = openClassService.findById(id);
		return openClass;
	}
	/**
	 * 通过输入的teachername 加载下拉框
	 * @param teacherName
	 * @return
	 */
	@RequestMapping(value="/findTeacherByName", method=RequestMethod.GET)
	public List<TeacherView> findTeacherByName( @RequestParam(value="teacherName") String teacherName) {
		return openClassService.findTeacherByName(teacherName);
	}
	
	/**
	 * 通过输入的条件加载onlineclass 下拉框
	 * @param 
	 * @return
	 */
	@RequestMapping(value="/findOnlineClassSelect", method=RequestMethod.GET)
	public List<OView> findOnlineClassSelect( 
			@RequestParam(value="teacherName", required=false) String teacherName,
			@RequestParam(value="startDate", required=false) DateTimeParam startDate, 
			@RequestParam(value="endDate", required=false) DateTimeParam endDate,
			@RequestParam(value="teacherId", required=false) Long teacherId,
			@RequestParam(value="serialNumber",required=false)String serialNumber) {
		if(StringUtils.isBlank(teacherName)&&StringUtils.isBlank(serialNumber)){
			return new ArrayList<OView>();
		}
		return openClassService.findOnlineClassSelect(teacherName, startDate, endDate, teacherId, serialNumber);
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public OpenClassDesc create(@RequestBody OpenClassDesc openClassDesc) {
		
		return openClassService.create(openClassDesc);
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public OpenClassDesc update(@RequestBody OpenClassDesc openClassDesc) {
		
		return openClassService.update(openClassDesc);
	}
	
	@RequestMapping(value="/changeStatus", method=RequestMethod.POST)
	public OpenClassDesc changeStatus(long id,int status){
		return openClassService.changeStatus(id, status);
	}
	
	@RequestMapping(value="/findChannelBySourceName", method=RequestMethod.GET)
	public List<Channel> findChannelBySourceName(String sourceName){
		return openClassService.findChannelBySourceName(sourceName);
	}
	
	@RequestMapping(value="/countByCourseTypeAndTime", method=RequestMethod.GET)
	public long countByCourseTypeAndTime(long studentId,String type,long time){
		
		return openClassService.countByCourseTypeAndTime(studentId,type, time);
	}
}

