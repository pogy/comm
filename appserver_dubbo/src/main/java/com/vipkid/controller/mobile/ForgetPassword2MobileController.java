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
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.ParentService;
import com.vipkid.service.exception.NoVerifyCodeServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.util.TextUtils;
@Controller
public class ForgetPassword2MobileController extends BaseWebController {
	public static final String PATH = "/mobile/forgetpassword2";
	
	public static final int HALF_HOUR = 60*30;
	
	private Logger logger = LoggerFactory.getLogger(SetPasswordMobileController.class.getSimpleName());
	
	private RedisClient redisClient = RedisClient.getInstance();
	
	@Resource
	private ParentRepository parentRepository;
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private ParentAuthService parentAuthService;
	
	@Resource
	private ParentService parentService;
	
	private String createVerifyCodeRedisKey(String mobile) {
		return "[FORGETPASSWORDVERIFYCODE]" + mobile;
	}

	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
		viewControllerRegistry.addViewController(ForgetPassword2MobileController.PATH).setViewName(ForgetPassword2MobileController.PATH);
	}

	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = ForgetPassword2MobileController.PATH, method = RequestMethod.GET)
	public String init(@ModelAttribute("username") String mobile, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
//		final String randomNumber = TextUtils.generateRandomNumber(6);
//
//		if (model.asMap().get("forgetpassword2WrongVerifyCode") == null) {
//			SendSMSResponse sendSMSResponse = SMS.sendVerificationCodeSMS(mobile, randomNumber);
//			if (sendSMSResponse.isSuccess()) {
//				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
//				model.addAttribute("verifyCode", "ok");
//				redisClient.set(createVerifyCodeRedisKey(mobile), randomNumber);
//				redisClient.expire(createVerifyCodeRedisKey(mobile), HALF_HOUR);
//			} else {
//				logger.info("Random number:" + randomNumber + ", response: " + sendSMSResponse.getCode());
//				model.addAttribute("verifyCode", null);
//			}
//		}
		
		return ForgetPassword2MobileController.PATH;
	}
	
	@RequestMapping(value=ForgetPassword2MobileController.PATH + "/finish", method=RequestMethod.POST)
	public String doFinish(@RequestParam("username") String mobile, @RequestParam("verifyCode") String verifyCode, @RequestParam("password") String password, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		logger.debug("create user by mobile = {}, verify code = {}, password = {}", mobile, verifyCode, password);
		
		RedisClient redisClient = RedisClient.getInstance();
		
		String verifyCodeInRedis = redisClient.get(createVerifyCodeRedisKey(mobile));
		
		if (verifyCode.equals(verifyCodeInRedis)) {
			Parent parentToChangePassword = parentRepository.findByMobile(mobile);
			if (parentToChangePassword != null) {
				parentToChangePassword.setPassword(password);
				parentService.changePassword(parentToChangePassword);
			} else {
				throw new UserNotExistServiceException("This mobile have not registered");
			}
			
			return "redirect:/mobile/index/";
		} else {
			redirectAttributes.addFlashAttribute("forgetpassword2WrongVerifyCode", "wrong");
			
			return Page.redirectTo(ForgetPassword2MobileController.PATH);
		}
	}
	
	@RequestMapping(value=ForgetPassword2MobileController.PATH + "/verifyCode", method=RequestMethod.POST)
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
}
