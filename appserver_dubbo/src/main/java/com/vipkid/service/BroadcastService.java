package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Broadcast;
import com.vipkid.repository.BroadcastRepository;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.param.DateParam;

@Service
public class BroadcastService {
	private Logger logger = LoggerFactory.getLogger(BroadcastService.class.getSimpleName());

	@Resource
	private BroadcastRepository broadcastRepository;

	public Broadcast find(long id) {
		logger.debug("find Broadcast for id = {}" + id);
		return broadcastRepository.find(id);
	}

	public List<Broadcast> findByDate(DateParam date) {
		logger.debug("find Broadcast for date = {}" + date);
		return broadcastRepository.findByDate(date.getValue());
	}

	public List<Broadcast> list(String search, int start, int length) {
		logger.debug("list Broadcasts with params: search = {}, start = {}, length = {}.", 
				search, start, length);
		List<Broadcast> broadcasts = broadcastRepository.list(search, start, length);
		
		return broadcasts;
	}

	public Count count(String search) {
		logger.debug("count broadcasts number with params: search = {}", search);
		return new Count(broadcastRepository.count(search));
	}

	public Broadcast create(Broadcast broadcast) {
		logger.debug("create activity: {}", broadcast);
		broadcastRepository.create(broadcast);
		return broadcast;
	}

	public Broadcast update(Broadcast broadcast) {
		broadcastRepository.update(broadcast);
		return broadcast;
	}

	public Broadcast doArchive(Broadcast broadcast) {
		broadcastRepository.archive(broadcast);
		return broadcast;
	}
}
