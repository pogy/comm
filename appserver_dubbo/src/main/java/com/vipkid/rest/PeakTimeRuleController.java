package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.PeakTimeRule;
import com.vipkid.service.PeakTimeRuleService;
import com.vipkid.service.PeakTimeService;

@RestController
@RequestMapping(value="/api/service/private/peakTimeRule")
public class PeakTimeRuleController {
	private Logger logger = LoggerFactory.getLogger(PeakTimeRuleController.class.getSimpleName());
	
	@Resource
	private PeakTimeService peakTimeService;
	
	@Resource
	private PeakTimeRuleService peakTimeRuleService;

	@RequestMapping(value="/findAllRules", method = RequestMethod.GET)
	public List<PeakTimeRule> findAllRules() {
		logger.debug("find all rules");
		return peakTimeRuleService.findAllRules();
	}
	
	@RequestMapping(value="/findRecentYearRules", method = RequestMethod.GET)
	public List<PeakTimeRule> findRecentYearRules() {
		logger.debug("find rules in recent year");
		return peakTimeRuleService.findRecentYearRules();
	}
	
	@RequestMapping(value="/applyRules", method = RequestMethod.POST)
	public List<PeakTimeRule> applyRules(@RequestBody List<PeakTimeRule> rules) {
		logger.debug("apply rules");
		return peakTimeRuleService.applyRules(rules);
	}
	
	@RequestMapping(value="/applyAll", method = RequestMethod.POST)
	public List<PeakTimeRule> applyAll() {
		logger.debug("apply all rules whose status is not applied");
		return peakTimeRuleService.applyAll();
	}
	
	@RequestMapping(value="/saveRule", method = RequestMethod.POST)
	public PeakTimeRule saveRules(@RequestBody PeakTimeRule rule) {
		return peakTimeRuleService.saveRule(rule);
	}
	
//	@RequestMapping(value="/createRule", method = RequestMethod.GET)
//	public PeakTimeRule createRule() {
//		return peakTimeRuleService.createRule();
//	}
}
