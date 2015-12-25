package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.TrialThresholdRule;
import com.vipkid.service.TrialThresholdRuleService;
import com.vipkid.service.TrialThresholdService;

@RestController
@RequestMapping(value="/api/service/private/trialThresholdRule")
public class TrialThresholdRuleController {
	private Logger logger = LoggerFactory.getLogger(TrialThresholdRuleController.class.getSimpleName());
	
	@Resource
	private TrialThresholdService trialThresholdService;
	
	@Resource
	private TrialThresholdRuleService trialThresholdRuleService;

	@RequestMapping(value="/findAllRules", method = RequestMethod.GET)
	public List<TrialThresholdRule> findAllRules() {
		logger.debug("find all rules");
		return trialThresholdRuleService.findAllRules();
	}
	
	@RequestMapping(value="/findRecentYearRules", method = RequestMethod.GET)
	public List<TrialThresholdRule> findRecentYearRules() {
		logger.debug("find rules in recent year");
		return trialThresholdRuleService.findRecentYearRules();
	}
	
	@RequestMapping(value="/applyRules", method = RequestMethod.POST)
	public List<TrialThresholdRule> applyRules(@RequestBody List<TrialThresholdRule> rules) {
		logger.debug("apply rules");
		return trialThresholdRuleService.applyRules(rules);
	}
	
	@RequestMapping(value="/applyAll", method = RequestMethod.POST)
	public List<TrialThresholdRule> applyAll() {
		logger.debug("apply all rules whose status is not applied");
		return trialThresholdRuleService.applyAll();
	}
	
	@RequestMapping(value="/saveRule", method = RequestMethod.POST)
	public TrialThresholdRule saveRules(@RequestBody TrialThresholdRule rule) {
		return trialThresholdRuleService.saveRule(rule);
	}
	
//	@RequestMapping(value="/createRule", method = RequestMethod.GET)
//	public PeakTimeRule createRule() {
//		return peakTimeRuleService.createRule();
//	}
}
