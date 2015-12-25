package com.vipkid.controller.mobile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.ext.sms.yunpian.SendSMSResponse;
import com.vipkid.model.Parent;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.exception.NoVerifyCodeServiceException;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.pojo.Signup;
import com.vipkid.util.TextUtils;

@Controller
public class SetPasswordMobileController extends BaseWebController {
	public static final String PATH = "/mobile/setpassword";
	public static final int HALF_HOUR = 60*30;
	
	private Logger logger = LoggerFactory.getLogger(SetPasswordMobileController.class.getSimpleName());
	
	private RedisClient redisClient = RedisClient.getInstance();
	
	private String createVerifyCodeRedisKey(String mobile) {
		return "[VERIFYCODE]" + mobile;
	}
	
	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private ParentAuthService parentAuthService;
	
	@Resource
    LeadsQueueSender leadsQueueSender;

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(SetPasswordMobileController.PATH).setViewName(SetPasswordMobileController.PATH);
	}

	@RequestMapping(value=SetPasswordMobileController.PATH, method=RequestMethod.GET)
	public String init(@ModelAttribute("username") String mobile, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		logger.debug("the mobile is {}", mobile);
		if (model.asMap().get("wrongVerifyCode") == null) {
			final String randomNumber = TextUtils.generateRandomNumber(6);
	
			SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(mobile, randomNumber);
			if (sendSMSResponse.isSuccess()) {
				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
				model.addAttribute("verifyCode", "ok");
				redisClient.set(createVerifyCodeRedisKey(mobile), randomNumber);
				redisClient.expire(createVerifyCodeRedisKey(mobile), HALF_HOUR);
			} else {
				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
				model.addAttribute("verifyCode", null);
			}
		}
		
		return SetPasswordMobileController.PATH;
	}
	
	@RequestMapping(value=SetPasswordMobileController.PATH + "/verifyCode", method=RequestMethod.POST)
	@ResponseBody
	public void getVerifyCode(@RequestParam(value="username") String mobile) {
		if (mobile != null) {
			final String randomNumber = TextUtils.generateRandomNumber(6);
	
			redisClient.set(createVerifyCodeRedisKey(mobile), randomNumber);
			redisClient.expire(createVerifyCodeRedisKey(mobile), HALF_HOUR);
			
			SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(mobile, randomNumber);
			if (sendSMSResponse.isSuccess()) {
				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
				return;
			} else {
				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
				throw new NoVerifyCodeServiceException("create verify code failed");
			}
		} else {
			throw new NoVerifyCodeServiceException("Please input mobile");
		}
	}
	
	@RequestMapping(value=SetPasswordMobileController.PATH + "/finish", method=RequestMethod.POST)
	public String doFinish(@RequestParam("username") String mobile, @RequestParam("verifyCode") String verifyCode, @RequestParam("password") String password, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		logger.debug("create user by mobile = {}, verify code = {}, password = {}", mobile, verifyCode, password);
		
		RedisClient redisClient = RedisClient.getInstance();
		
		String verifyCodeInRedis = redisClient.get(createVerifyCodeRedisKey(mobile));
		
		if (verifyCode.equals(verifyCodeInRedis)) {
			if (parentRepository.findByMobile(mobile) == null) {
				//Family family = new Family();
				Parent parent = new Parent();
				parent.setMobile(mobile);
				parent.setUsername(mobile);
				parent.setPassword(password);
				Signup signup = new Signup();
				signup.setParent(parent);
				signup.setStudent(null);
				parentAuthService.doSignup(signup);
				if (signup != null && signup.getStudent() != null) {
					leadsQueueSender.sendText(String.valueOf(signup.getStudent().getId()));
				}
			} else {
				throw new UserAlreadyExistServiceException("already there");
			}
			
			return Page.redirectTo(ChildrenInfoMobileController.PATH);
		} else {
			redirectAttributes.addFlashAttribute("wrongVerifyCode", "wrong");
			
			return Page.redirectTo(ForgetPassword2MobileController.PATH);
		}
	}
	
}
