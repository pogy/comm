package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.TrialThreshold;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.TrialThresholdRepository;
import com.vipkid.util.TextUtils;

@Service
public class TrialThresholdService {
	private Logger logger = LoggerFactory.getLogger(TrialThresholdService.class.getSimpleName());
	
	private RedisClient redisClient = RedisClient.getInstance();
	
	@Resource
	private TrialThresholdRepository trialThresholdRepository;
	
	public void setTrialThreshold(String key,long value){
		// we don't need to use this yet
		
	}

	public List<TrialThreshold> getByTimeRange(Date start, Date end) {
		logger.debug("get peak time from {} to {}", start, end);
		
		return trialThresholdRepository.getByTimeRange(start, end);
	}

	public long getTrialThreshold(Date date) {
		String trialThresholdNumber = redisClient.get(date.toString());
		redisClient.expire(date.toString(), 48*60*60);
		if (TextUtils.isEmpty(trialThresholdNumber)) {
			TrialThreshold trialThreshold = trialThresholdRepository.findByTimePoint(date);
			if (trialThreshold == null) {
				trialThresholdNumber = "0";
			} else {
				trialThresholdNumber = Long.toString(trialThreshold.getTrialAmount());
			}
			redisClient.set(date.toString(), trialThresholdNumber);
			redisClient.expire(date.toString(), 48*60*60);
		}
		return Long.parseLong(trialThresholdNumber);
	}

}
