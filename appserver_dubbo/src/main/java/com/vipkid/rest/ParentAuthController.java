package com.vipkid.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Channel;
import com.vipkid.model.MarketingActivity;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.ChannelService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Binding;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.Signup;
import com.vipkid.service.pojo.SignupAndBinding;
import com.vipkid.service.pojo.SignupFromInvitation;
import com.vipkid.service.pojo.SudoCredential;

/**
 * 
 * @author VIPKID
 * history: 添加数据导入的feature。
 */
@RestController
@RequestMapping("/api/service/public/auth/parent")
public class ParentAuthController {

	private Logger logger = LoggerFactory.getLogger(ParentAuthController.class.getSimpleName());
	
	@Resource
	private ParentAuthService parentAuthService;
	
	@Resource
	private ChannelService channelService;
	
	@Resource
	private StudentService studentService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Parent login(@RequestBody Credential credential) {
		logger.info("Parent[username: {}] is logining.", credential.getUsername());
		return parentAuthService.login(credential);
	}
	
	@RequestMapping(value = "/sudo_login", method = RequestMethod.POST)
	public Parent sudoLogin(@RequestBody SudoCredential credential) {
		logger.info("Parent[username: {}] is logining. Operate by admin[username: {}]", credential.getUserName(), credential.getAdminName());
		return parentAuthService.sudoLogin(credential);
	}
	
	@RequestMapping(value = "/loginByUserNamePasswordOpenid", method = RequestMethod.POST)
	public Parent loginByUserNamePasswordOpenid(@RequestBody Binding binding) {
		logger.info(" do login by username password openid,username = {}, openid = {}",binding.getUsername(),binding.getWechatOpenId());
		return parentAuthService.loginByUserNamePasswordOpenid(binding);
	}
	
	@RequestMapping(value = "/signupFromInvitation", method = RequestMethod.POST)
	public Parent signupFromInvitation(@RequestBody SignupFromInvitation signupFromInvitation) {
		Parent parent = parentAuthService.doSignupFromInvitation(signupFromInvitation);
		if (signupFromInvitation != null && signupFromInvitation.getStudent() != null) {
			leadsQueueSender.sendText(String.valueOf(signupFromInvitation.getStudent().getId()));
		}
		return parent;
	}
	
	@RequestMapping(value = "/signup4MarketActivity", method = RequestMethod.POST)
	public com.vipkid.rest.vo.Response signup4MarketActivity(@RequestBody Signup signup) {
		Response response = parentAuthService.doSignup4MarketActivity(signup);
		if (signup != null && signup.getStudent() != null) {
			leadsQueueSender.sendText(String.valueOf(signup.getStudent().getId()));
		}
		return response;
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Parent signup(@RequestBody Signup signup) {
		Parent parent = parentAuthService.doSignup(signup);
		if (signup != null && signup.getStudent() != null) {
			leadsQueueSender.sendText(String.valueOf(signup.getStudent().getId()));
		}
		return parent;
	}
	
	// 2015-04-23 -- 导入数据进入，作为注册学生
	/**
	 * 数据, QueryParam("salername") String strSalerName
	 * @return
	 */
	@RequestMapping(value = "/importSignupDataOpeation1", method = RequestMethod.POST)
	public Response importDataOperation1(@RequestBody Signup signup) {
		Response response =  parentAuthService.doImportDataOperation1(signup);
		Student student = studentService.findRecentResisteringByParentId(signup.getParent().getId());
		if(student != null) {
			leadsQueueSender.sendText(String.valueOf(student.getId()));
		}
		return response;
	}
	
	
	@RequestMapping(value = "/bindParentOpenId", method = RequestMethod.POST)
	public Parent bindParentOpenId(@RequestBody Binding binding) {
		logger.info("Binding  with openId,openId = {}. ",binding.getWechatOpenId());
		return parentAuthService.doBindParentOpenId(binding);
	}
	
	@RequestMapping(value = "/unbindParentOpenIdByUsername", method = RequestMethod.POST)
	public Parent unbindParentOpenIdByUsername(@RequestParam(value="username") String username) {
		logger.info("unBinding  with username = " + username);
		return parentAuthService.doUnbindParentOpenIdByUsername(username);
	}
	
	@RequestMapping(value = "/signUpAndBinding", method = RequestMethod.POST)
	public Parent signUpAndBinding(@RequestBody SignupAndBinding signupAndBinding) {
		Parent parent = parentAuthService.doSignUpAndBinding(signupAndBinding);
		if (signupAndBinding != null && signupAndBinding.getStudent() != null) {
			leadsQueueSender.sendText(String.valueOf(signupAndBinding.getStudent().getId()));
		}
		return parent;
	}
	
	
	@RequestMapping(value = "/findByOpenId", method = RequestMethod.GET)
	public Parent findByOpenId(@RequestParam("openId") String openId) {
		logger.info("find parent with openid,openid = {}", openId);
		return parentAuthService.findByOpenId(openId);
	}


	@RequestMapping(value = "/findRegisteredByMobile", method = RequestMethod.GET)
	public Parent findRegisteredByMobile(@RequestParam("mobile") String mobile) {
		logger.info("find parent with mobile,mobile = {}.", mobile);
		return parentAuthService.findRegisteredByMobile(mobile);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public Response logout(@RequestParam("id") long id) {
		logger.info("logout with id,id = {}.", id); 
		return parentAuthService.logout(id);
	}
	
	@RequestMapping(value = "/findByUsername", method = RequestMethod.GET)
	public Parent findByUsername(@RequestParam("username") String username) {
		logger.info("find parents by username = {}", username);
		return parentAuthService.findByUsername(username);
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
	public Parent changePassword(@RequestBody Parent parent) {
		logger.info("start to change password for parent,parentid = {}", parent.getId());
		return parentAuthService.changePassword(parent);
	}

	@RequestMapping(value = "/findByChannel", method = RequestMethod.GET)
	public MarketingActivity findByChannel(@RequestParam(value = "sourceId",required = false) String sourceId,@RequestParam(value = "channelId",required = false) Long channelId){
		logger.info("findByChannel channel: {}", sourceId);
		if(null!=channelId&&channelId!=0){
			return parentAuthService.find(Long.valueOf(sourceId));
		}else{
			return parentAuthService.findBySourceId(sourceId);
		}
	}
	
	
	@RequestMapping(value = "/registByMarketingActiviy", method = RequestMethod.POST)
	public Response registByMarketingActiviy(@RequestBody Signup signup) {
		Response response = parentAuthService.doRegistByMarketingActiviy(signup);
		if (signup != null && signup.getStudent() != null && signup.getStudent().getId() > 0) {
			logger.info("registByMarketingActiviy,studentId = {}", signup.getStudent().getId());
			leadsQueueSender.sendText(String.valueOf(signup.getStudent().getId()));
		}
		return response;
	}
	
	@RequestMapping(value = "/findByOldSource", method = RequestMethod.GET)	
	public Channel findByOldSource(@RequestParam(value = "oldSourceName", required = true) String oldSourceName){
		logger.info("find channel by OldSource");
		return channelService.findByOldSource(oldSourceName);
	}
}
