package com.vipkid.controller.home;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.security.HEXSHA256Signature;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.StudentService;
import com.vipkid.service.VerificationCodeService;
import com.vipkid.util.CookieUtils;
import com.vipkid.util.TextUtils;

@Controller
public class VerifyCodeWebController extends BaseWebController {
	private Logger logger = LoggerFactory.getLogger(VerifyCodeWebController.class.getSimpleName());
	
	@Autowired
	private ParentAuthService authService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private VerificationCodeService verificationCodeService;
	
	@Override
	protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
//		viewControllerRegistry.addViewController(LoginWebController.PATH).setViewName(LoginWebController.PATH);
	}
	
	/**
	 * 初始化
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sendVerifyCode", method = RequestMethod.GET)
	public  @ResponseBody String send(HttpServletRequest request, @RequestParam(value="mobile", required=true) String mobile, @RequestParam(value="imagecode", required=false) String imagecode){
		if(TextUtils.isEmpty(imagecode)){
			logger.info("获取手机号验证码，图片验证码为空！！ SB有可能在攻击");
			return null;
		}else{
			Object localImageCode = UserCacheUtil.getValueFromCookie(request, "imagecode");
			if(imagecode.equals(localImageCode)){
				logger.info("获取手机号验证码，图片验证码 = " + imagecode);
				String code = verificationCodeService.send(mobile);
				UserCacheUtil.storeVerifyCode(mobile, code);
				CookieUtils.delVIPKIDCookie("imagecode");
				return code;
			}else{
				logger.info("获取手机号验证码，图片验证码错误！！ SB有可能在攻击");
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/getImageCode", method = RequestMethod.GET)
    public @ResponseBody void getImageCode(HttpServletResponse response) throws IOException {
    	Cage cage = new GCage();
    	String code=cage.getTokenGenerator().next();
    	code=code.substring(0, 4);
    	Cookie cookie = CookieUtils.createVIPKIDCookie("imagecode", HEXSHA256Signature.sign(code));
    	response.addCookie(cookie);
    	
		OutputStream os =response.getOutputStream();
		response.setContentType("image/jpeg");
	    response.setHeader("Pragma","no-cache");
	    response.setHeader("Cache-Control","no-cache");
		cage.draw(code, os);
		os.flush();
		os.close();
    }
}
