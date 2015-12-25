package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.SplitOrderItemService;

@RestController
@RequestMapping(value="/api/service/public/splitorderitem")
public class SplitOrderItemController {
	
	private Logger logger = LoggerFactory.getLogger(SplitOrderItemController.class.getSimpleName());

	@Resource
	private SplitOrderItemService splitOrderItemService;
	
	@RequestMapping(value="/split",method = RequestMethod.GET)
	public Response split(){
		logger.info("split");
		return splitOrderItemService.split();
	}

}
