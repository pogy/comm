package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.InventionCode;
import com.vipkid.service.InventionCodeService;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.StringWrapper;

@RestController
@RequestMapping(value="/api/service/private/inventionCodes")
public class InventionCodeController {
	private Logger logger = LoggerFactory.getLogger(InventionCodeController.class.getSimpleName());
	
	@Resource
	private InventionCodeService inventionCodeService;
	
	@RequestMapping(value="/findByMarketingActivityIdAndStatus",method = RequestMethod.GET)
	public List<InventionCode> findByMarketingActivityIdAndStatus(@RequestParam("marketingActivityId") long marketingActivityId, @RequestParam("status") boolean status) {
		logger.info("find InventionCode for marketingActivityId = {}, status = {}", marketingActivityId, status);
		return inventionCodeService.findByMarketingActivityIdAndStatus(marketingActivityId, status);
	}
	
	@RequestMapping(value="/findByCode",method = RequestMethod.GET)
	public InventionCode findByCode(@RequestParam("code") String code) {
		logger.info("find InventionCode for code = {}", code);
		return inventionCodeService.findByCode(code);
	}
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<InventionCode> list(@RequestParam(value="hasUsed",required=false) String hasUsed,@RequestParam("marketingActivityId")long marketingActivityId,@RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list InventionCode with params: hasUsed={},marketingActivityId={}, start = {}, length = {}.", hasUsed,marketingActivityId, start, length);
		return inventionCodeService.list(hasUsed,marketingActivityId,start, length);
	}

	@RequestMapping(value="/count",method = RequestMethod.GET)
	public Count count( @RequestParam(value="hasUsed",required=false) String hasUsed,@RequestParam("marketingActivityId")long marketingActivityId) {
		logger.info("count InventionCode with params: hasUsed = {},marketingActivityId={}.", hasUsed,marketingActivityId);
		return inventionCodeService.count(hasUsed, marketingActivityId);
	}
	
	@RequestMapping(value="/downLoadExcel",method = RequestMethod.GET)
	public StringWrapper downLoadExcel( @RequestParam(value="hasUsed",required=false) String hasUsed,@RequestParam("marketingActivityId")long marketingActivityId,HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		logger.info("export excel with params: hasUsed = {},marketingActivityId={}.", hasUsed,marketingActivityId);
		return inventionCodeService.downLoadExcel(hasUsed, marketingActivityId, httpRequest, httpResponse);
	}
	
}
