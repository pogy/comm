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

import com.vipkid.model.Broadcast;
import com.vipkid.service.BroadcastService;
import com.vipkid.service.param.DateParam;
import com.vipkid.service.pojo.Count;


@RestController
@RequestMapping(value="/api/service/private/broadcast")
public class BroadcastController {
	private Logger logger = LoggerFactory.getLogger(BroadcastController.class.getSimpleName());

	@Resource
	private BroadcastService broadcastService;

	@RequestMapping(value="/find",method = RequestMethod.GET)
	public Broadcast find(@RequestParam("id") long id) {
		logger.info("find Broadcast for id = {}" + id);
		return broadcastService.find(id);
	}
	
	@RequestMapping(value="/findByDate",method = RequestMethod.GET)
	public List<Broadcast> findByDate(@RequestParam("date") DateParam date) {
		logger.info("find Broadcast for date = {}" + date);
		return broadcastService.findByDate(date);
	}

	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<Broadcast> list(@RequestParam("search") String search,@RequestParam("start") int start,@RequestParam("length") int length) {
		logger.info("list Broadcasts with params: search = {}, start = {}, length = {}.", 
				search, start, length);
		List<Broadcast> broadcasts = broadcastService.list(search, start, length);
		
		return broadcasts;
	}

	@RequestMapping(value="/count",method = RequestMethod.GET)
	public Count count(@RequestParam("search") String search) {
		logger.info("count broadcasts number with params: search = {}", search);
		return broadcastService.count(search);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Broadcast create(@RequestBody Broadcast broadcast) {
		logger.info("create activity: {}", broadcast);
		broadcastService.create(broadcast);
		return broadcast;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public Broadcast update(@RequestBody Broadcast broadcast) {
		broadcastService.update(broadcast);
		return broadcast;
	}

	@RequestMapping(value="/archive",method = RequestMethod.PUT)
	public Broadcast archive(@RequestBody Broadcast broadcast) {
		broadcastService.doArchive(broadcast);
		return broadcast;
	}
}
