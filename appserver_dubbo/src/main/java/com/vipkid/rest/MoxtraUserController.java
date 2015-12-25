package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.MoxtraUser;
import com.vipkid.service.MoxtraUserService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/moxtraUsers")
public class MoxtraUserController {
	private Logger logger = LoggerFactory.getLogger(MoxtraUserController.class.getSimpleName());
	
	@Resource
	private MoxtraUserService moxtraUserService;
	
	
	@RequestMapping(value = "/assign", method = RequestMethod.GET)
	public MoxtraUser assign(@RequestParam("vipkidUserId") long vipkidUserId) {
		logger.info("assign by vipkidUserId, vipkidUserId = {}.", vipkidUserId);
		return moxtraUserService.assign(vipkidUserId);
	}
	
	@RequestMapping(value = "/unassign", method = RequestMethod.GET)
	public MoxtraUser unassign(@RequestParam("vipkidUserId") long vipkidUserId) {
		logger.info("unassign by vipkidUserId, vipkidUserId = {}.", vipkidUserId);
		return moxtraUserService.unassign(vipkidUserId);
	}
	
	@RequestMapping(value = "/release", method = RequestMethod.GET)
	public List<MoxtraUser> release() {
		return moxtraUserService.release();
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<MoxtraUser> list(@RequestParam(value = "inUse", required = false) Boolean inUse,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length) {
		
		logger.info("list moxtra user with params: inUse = {}, fromDate = {}, toDate = {}, start = {}, length = {}.", inUse, fromDate, toDate, start, length);
		return moxtraUserService.list(inUse, fromDate == null ? null : new DateTimeParam(fromDate),
				toDate == null ? null : new DateTimeParam(toDate),
				start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value = "inUse", required = false) Boolean inUse,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate) {
		logger.info("count moxtra user with params: inUse = {}, fromDate = {}, toDate = {}.", inUse, fromDate, toDate);
		return moxtraUserService.count(inUse, fromDate == null ? null : new DateTimeParam(fromDate),
				toDate == null ? null : new DateTimeParam(toDate));
	}
}
