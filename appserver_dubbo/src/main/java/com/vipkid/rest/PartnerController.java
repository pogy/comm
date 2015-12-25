package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Partner;
import com.vipkid.model.Partner.Type;
import com.vipkid.model.User.Status;
import com.vipkid.rest.vo.query.PartnerQueryPartnerView;
import com.vipkid.service.PartnerService;

@RestController
@RequestMapping("/api/service/private/partner")
public class PartnerController {

	private Logger logger = LoggerFactory.getLogger(PartnerController.class.getSimpleName());

	@Resource
	private PartnerService partnerService;
	

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Partner find(@RequestParam("id") long id) {
		logger.info("find partner: {}", id);
		return partnerService.find(id);
	}	


	/**
	 * 在招募端调用，获取recruit channel
	 * 2015-07-28 添加排序输出，安装字幕排序
	 * @param type
	 * @param status
	 * @param email
	 * @param name
	 * @param start
	 * @param length
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Partner> list(@RequestParam(value="type",required=false)Type type,@RequestParam(value="status",required=false)Status status, @RequestParam(value="username",required=false)String email, @RequestParam(value="search",required=false)String name, @RequestParam(value="start",required=false) Integer start, @RequestParam(value="length",required=false) Integer length) {
		logger.info("list partners: {}");
		return partnerService.list(null,status, email, name, type, start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public com.vipkid.service.pojo.Count count(@RequestParam(value="search",required=false)String search,@RequestParam(value="status",required=false)Status status, @RequestParam(value="username",required=false)String email, @RequestParam(value="username",required=false)String username, @RequestParam("start")int start, @RequestParam("length")int length) {
		return partnerService.count(search,status, email, username, start, length);
	}

	

	@RequestMapping(method = RequestMethod.PUT)
	public Partner update(@RequestBody Partner partner) {
		logger.info("update partner: {}", partner);
		return partnerService.update(partner);		
	}
	
	@RequestMapping(method = RequestMethod.POST)	
	public Partner create(@RequestBody Partner partner){
		logger.info("create partner: {}", partner);
		return partnerService.create(partner);
		
	}
	
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public List<PartnerQueryPartnerView> filter(@RequestParam(value="search",required=false)String search, @RequestParam(value="status",required=false)Status status,@RequestParam(value="email",required=false)String email, @RequestParam(value="username",required=false)String username, @RequestParam(value="start",required=false) Integer start, @RequestParam(value="length",required=false) Integer length) {
		logger.info("filter partners: {}");
		List<Partner> partnerList = partnerService.list(search, status,email, username, start, length);
		return this.getPartnerQueryResultView(partnerList);
	}
	
	private List<PartnerQueryPartnerView> getPartnerQueryResultView(List<Partner> partnerList) {
		List<PartnerQueryPartnerView> partnerViewList = new ArrayList<PartnerQueryPartnerView>();
		if (partnerList != null && partnerList.size() > 0) {
			Iterator<Partner> iterator = partnerList.iterator();
			while (iterator.hasNext()) {
				Partner partner = iterator.next();
				PartnerQueryPartnerView partnerView = new PartnerQueryPartnerView();
				partnerView.setId(partner.getId());
				partnerView.setName(partner.getName());
				partnerView.setEmail(partner.getEmail());
				partnerView.setType(partner.getType());
				//2015-07-27 添加status数据
				partnerView.setStatus(partner.getStatus());
				partnerView.setCreateDatetime(partner.getCreateDateTime());
				partnerView.setLastEditDatetime(partner.getLastEditDateTime());
				
				partnerViewList.add(partnerView);
			}
			
		}
		
		return partnerViewList;
	}
	
	@RequestMapping(value = "/lock", method = RequestMethod.GET)
	public Partner lock(@RequestParam("id") long id) {
		return partnerService.doLock(id);
	}

	@RequestMapping(value = "/unlock", method = RequestMethod.GET)
	public Partner unlock(@RequestParam("id") long id) {
		return partnerService.doUnlock(id);
	}

}
