package com.vipkid.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.MarketingActivity;
import com.vipkid.model.MarketingActivity.Type;
import com.vipkid.rest.vo.query.MarketingActivityView;
import com.vipkid.service.MarketingActivityService;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/marketingActivities")
public class MarketingActivityController {
	private Logger logger = LoggerFactory.getLogger(MarketingActivityController.class.getSimpleName());
	
	@Resource
	private MarketingActivityService marketingActivityService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public MarketingActivity find(@RequestParam("id") long id) {
		logger.info("find MarketingActivity for id = {}", id);
		return marketingActivityService.find(id);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<MarketingActivity> list(@RequestParam(value ="search",required = false) String search, @RequestParam(value = "type",required = false) Type type, @RequestParam(value = "fromCreateDate",required = false) DateTimeParam fromCreateDate, @RequestParam(value="toCreateDate",required = false) DateTimeParam toCreateDate, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list MarketingActivity with params: search = {}, type = {}, fromCreateDate = {}, toCreateDate = {}, start = {}, length = {}.", search, type, fromCreateDate, toCreateDate, start, length);
		return marketingActivityService.list(search, type, fromCreateDate, toCreateDate, start, length);
	}
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value="search",required = false) String search, @RequestParam(value = "type",required = false) Type type, @RequestParam(value = "fromCreateDate",required = false) DateTimeParam fromCreateDate, @RequestParam(value = "toCreateDate",required = false) DateTimeParam toCreateDate) {
		logger.info("count MarketingActivity with params: search = {}, type = {}, fromCreateDate = {}, toCreateDate = {}.", search, type, fromCreateDate, toCreateDate);
		return marketingActivityService.count(search, type, fromCreateDate, toCreateDate);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public MarketingActivity create(@RequestBody MarketingActivity marketingActivity) {
		return marketingActivityService.create(marketingActivity);
	}
	
	@RequestMapping(value = "/findListByAgentId", method = RequestMethod.GET)
	public List<MarketingActivity> findListByAgentId(@RequestParam("agentId") long agentId,  @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list MarketingActivity with params: agentId= {}, start = {}, length = {}.", agentId, start, length);
		return marketingActivityService.findListByAgentId(agentId, start, length);
	}
	
	@RequestMapping(value = "/countByAgentId", method = RequestMethod.GET)
	public Count countByAgentId(@RequestParam("agentId") long agentId) {
		logger.info("count MarketingActivity with params: agentId= {}.", agentId);
		return marketingActivityService.countByAgentId(agentId);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public MarketingActivity update(@RequestBody MarketingActivity activity) {
		logger.info("update MarketingActivity: {}", activity);
		return marketingActivityService.update(activity);
	}
	
	@RequestMapping(value = "/listForStudentSelect", method = RequestMethod.GET)
	public List<MarketingActivityView> listForStudentSelect(){
		
		List<MarketingActivity>  activityList =  marketingActivityService.listForStudentSelect();
		return getMarketingActivityQueryView(activityList);
	}

	private List<MarketingActivityView> getMarketingActivityQueryView(
			List<MarketingActivity> activityList) {
		List<MarketingActivityView> activityViewList = new ArrayList<MarketingActivityView>();
		if (activityList != null && activityList.size() > 0) {
			for (MarketingActivity activity : activityList) {
				MarketingActivityView activityView = new MarketingActivityView();
				activityView.setId(activity.getId());
				if (null != activity.getChannel()) {
					activityView.setChannel(activity.getChannel().getSourceName());
				}
				activityViewList.add(activityView);
			}
		}
		return activityViewList;
	}
	
}
