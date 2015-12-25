package com.vipkid.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Channel;
import com.vipkid.model.InventionCode;
import com.vipkid.model.MarketingActivity;
import com.vipkid.model.MarketingActivity.Type;
import com.vipkid.repository.ChannelRepository;
import com.vipkid.repository.InventionCodeRepository;
import com.vipkid.repository.MarketingActivityRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.NAMEDUPLICATEServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@Service
public class MarketingActivityService {
	private Logger logger = LoggerFactory.getLogger(MarketingActivityService.class.getSimpleName());
	
	@Resource
	private MarketingActivityRepository marketingActivityRepository;
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private InventionCodeRepository inventionCodeRepository;
	
	@Resource
	private ChannelRepository channelRepository;
	

	public MarketingActivity find(long id) {
		logger.info("find MarketingActivity for id = {}", id);
		return marketingActivityRepository.find(id);
	}
	
	public List<MarketingActivity> list(String search, Type type, DateTimeParam fromCreateDate, DateTimeParam toCreateDate, int start, int length) {
		logger.info("list MarketingActivity with params: search = {}, type = {}, fromCreateDate = {}, toCreateDate = {}, start = {}, length = {}.", search, type, fromCreateDate, toCreateDate, start, length);
		return marketingActivityRepository.list(search, type, fromCreateDate, toCreateDate, start, length);
	}
	
	public Count count(String search, Type type, DateTimeParam fromCreateDate, DateTimeParam toCreateDate) {
		logger.info("count MarketingActivity with params: search = {}, type = {}, fromCreateDate = {}, toCreateDate = {}.", search, type, fromCreateDate, toCreateDate);
		return new Count(marketingActivityRepository.count(search, type, fromCreateDate, toCreateDate));
	}
	
	public MarketingActivity create(MarketingActivity marketingActivity) {
		logger.info("create marketingActivity: {}", marketingActivity);
		
		//判断名称是否已经存在
		MarketingActivity activity = marketingActivityRepository.findByName(marketingActivity.getName());
		if(activity != null){
			throw new NAMEDUPLICATEServiceException("activity NAME already exist.");
		}
		
		//判断来源是否已经存在
		Channel channel = marketingActivityRepository.findByChannel(marketingActivity.getChannel());
		
		marketingActivity.setChannel(channel);
		marketingActivity.setCreateDateTime(new Date());
		marketingActivity.setHasReleased(false);
		marketingActivityRepository.create(marketingActivity);
		
		//生成邀请码
		if(marketingActivity.getType() == Type.INVENTION){
			long num = marketingActivity.getInventionCodeNumber();
			if(num>0){
				for(int i = 0; i < num; i++){
					String uuid = UUID.randomUUID().toString();
//			        String result = "MK" + uuid.substring(0, 10);
			        String result = "MK" + uuid.substring(0,8)+uuid.substring(9,11);
			        InventionCode code = new InventionCode();
			        code.setCode(result.toUpperCase());
			        code.setHasUsed(false);
			        code.setMarketingActivity(marketingActivity);
			        inventionCodeRepository.create(code);
				}
			}
		}
		securityService.logAudit(Level.INFO, Category.MARKETING_ACTIVITY_CREATE, "Create MarketingActivity: " + marketingActivity.getName());
		return marketingActivity;
	}
	
	public List<MarketingActivity> findListByAgentId(long agentId, int start, int length) {
		logger.info("list MarketingActivity with params: agentId= {}, start = {}, length = {}.", agentId, start, length);
		return marketingActivityRepository.findListByAgentId(agentId, start, length);
	}
	
	public Count countByAgentId(long agentId) {
		logger.info("count MarketingActivity with params: agentId= {}.", agentId);
		return new Count(marketingActivityRepository.countByAgentId(agentId));
	}
	
	public MarketingActivity update(MarketingActivity activity) {
		logger.info("update MarketingActivity: {}", activity);
		Channel channelWithNewName = activity.getChannel();
		Channel channel = channelRepository.findBySourceName(channelWithNewName.getSourceName());
		activity.setChannel(channel);
		marketingActivityRepository.update(activity);
		securityService.logAudit(Level.INFO, Category.MARKETING_ACTIVITY_UPDATE, "Update MarketActivity:"+activity.getId());
		return activity;
	}
	
	public List<MarketingActivity> listForStudentSelect(){
		return marketingActivityRepository.listForStudentSelect();
	}
}
