package com.vipkid.rest;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.vipkid.service.OAuthService;

@RestController
@RequestMapping("/api/service/public/oauth")
public class OAuthController {
	
	private Logger logger = LoggerFactory.getLogger(OAuthController.class.getSimpleName());
	
	@Resource
	private OAuthService  oAuthService;
	
	@RequestMapping(value = "/findOpenidURL", method = RequestMethod.GET)
	public ModelAndView findOpenidURL(@RequestParam(value="code",required=false) String code) throws IOException, URISyntaxException {
		logger.info("redirect to url with openid, code = {}", code);
		return oAuthService.findOpenidURL(code);
	}
	
	@RequestMapping(value = "/findDashboard", method = RequestMethod.GET)
	public ModelAndView findDashboard(@RequestParam(value="code",required=false) String code) throws IOException, URISyntaxException {
		logger.info("redirect to url with openid, code = {}", code);
		return oAuthService.findDashboard(code);
	}
	
	@RequestMapping(value = "/findOrders", method = RequestMethod.GET)
	public ModelAndView findOrders(@RequestParam(value="code",required=false) String code) throws IOException, URISyntaxException {
		logger.info("redirect to url with openid, code = {}", code);
		return oAuthService.findOrders(code);
	}
	
	@RequestMapping(value = "/findAccount", method = RequestMethod.GET)
	public ModelAndView findAccount(@RequestParam(value="code",required=false) String code) throws IOException, URISyntaxException {
		logger.info("redirect to url with openid, code = {}", code);
		return oAuthService.findAccount(code);
	}
	
}
