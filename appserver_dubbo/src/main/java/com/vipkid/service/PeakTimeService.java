package com.vipkid.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.PeakTime;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.PeakTimeRepository;
import com.vipkid.service.pojo.PeakTimePerWeek;
import com.vipkid.util.Configurations.WeeklyPeakDayKey;
import com.vipkid.util.TextUtils;

@Service
public class PeakTimeService {
	private Logger logger = LoggerFactory.getLogger(PeakTimeService.class.getSimpleName());
	
	private RedisClient redisClient = RedisClient.getInstance();
	
	@Resource
	private PeakTimeRepository peakTimeRepository;
	
	public  PeakTimePerWeek getDefaultPeak(){
		PeakTimePerWeek peak = new PeakTimePerWeek();
		Map<String, String> periodMap = new HashMap<String, String>();
		peak.setPeriodMap(periodMap);
		
		RedisClient instance = RedisClient.getInstance();
		//if(instance.get(WeeklyPeakDayKey.MON_KEY) == null)
//		{
//			initDefault ();
//		}
		if (instance.get(WeeklyPeakDayKey.MON_KEY) != null) 
		{
			periodMap.put(WeeklyPeakDayKey.MON_KEY, "18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.TUES_KEY, "18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.WED_KEY, "18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.THUS_KEY, "18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.FRI_KEY, "18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.SAT_KEY, "09:00-11:00,18:00-22:00");
			periodMap.put(WeeklyPeakDayKey.SUN_KEY, "09:00-11:00,18:00-22:00");
		}
		return peak;		
	}
	
	public void initDefault(){
		logger.info("Init default value for peak time");
		redisClient.set(WeeklyPeakDayKey.MON_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.TUES_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.WED_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.THUS_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.FRI_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.SAT_KEY, "09:00-11:00,18:00-22:00");
		redisClient.set(WeeklyPeakDayKey.SUN_KEY, "09:00-11:00,18:00-22:00");
	}
	
	public void setPeakTime(String key,String value){
		
		
	}

	public List<PeakTime> getByTimeRange(Date start, Date end) {
		logger.debug("get peak time from {} to {}", start, end);
		
		return peakTimeRepository.getByTimeRange(start, end);
	}

	public boolean isPeakTime(Date date) {
		String datePeakTimeType = redisClient.get(date.toString());
		redisClient.expire(date.toString(), 48*60*60);
		if (TextUtils.isEmpty(datePeakTimeType)) {
			PeakTime peakTime = peakTimeRepository.findByTimePoint(date);
			if (peakTime == null || peakTime.getType().equals(PeakTime.Type.NORMALTIME)) {
				datePeakTimeType = "NORMALTIME";
			} else {
				datePeakTimeType = peakTime.getType().toString();
			}
			redisClient.set(date.toString(), datePeakTimeType);
			redisClient.expire(date.toString(), 48*60*60);
		}
		return !datePeakTimeType.equals(PeakTime.Type.NORMALTIME.toString());
	}

}
