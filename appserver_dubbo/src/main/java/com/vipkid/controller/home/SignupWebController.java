package com.vipkid.controller.home;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.vipkid.controller.parent.base.BaseWebController;
import com.vipkid.controller.util.Page;
import com.vipkid.controller.util.UserCacheUtil;
import com.vipkid.model.Channel;
import com.vipkid.model.Family;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.service.ChannelService;
import com.vipkid.service.ParentAuthService;
import com.vipkid.service.ParentService;
import com.vipkid.service.StudentService;
import com.vipkid.service.pojo.Signup;
import com.vipkid.util.CookieUtils;
import com.vipkid.util.TextUtils;
import com.vipkid.util.UrlUtil;

@Controller
public class SignupWebController extends BaseWebController {
    public static final String PATH = "/home/signup";


    private Logger logger = LoggerFactory.getLogger(SignupWebController.class.getSimpleName());

    @Autowired
    private ParentAuthService authService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ParentAuthService parentAuthService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private ChannelService channelService;

    @Override
    protected void registerViewController(ViewControllerRegistry viewControllerRegistry) {
        viewControllerRegistry.addViewController(SignupWebController.PATH).setViewName(SignupWebController.PATH);
    }

    /**
     * 初始化
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String init(Model model) {
        model.addAttribute("signupInfo", new Signup());
        return SignupWebController.PATH;
    }

    /**
     * 注册
     */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@RequestParam("parentMobile") String parentMobile, @RequestParam("parentPassword") String parentPassword, @RequestParam("inventionCode") String inventionCode, @RequestParam("cmdCode") String cmdCode, @RequestParam(value = "url", required = false) String url, HttpServletResponse response) {
    	
        logger.info("www new user sign up[parentMobile: {}, parentPassword: {}, inventionCode:{}, cmdCode:{}, url:{}].", parentMobile, parentPassword, inventionCode, cmdCode, url);
        String code = UserCacheUtil.getVerifyCode(parentMobile);
        if (code != null && inventionCode != null && code.equals(inventionCode)) {
            Parent parent = new Parent();
            parent.setMobile(parentMobile);
            parent.setPassword(parentPassword);
            parent.setRecommendCode(cmdCode);


            Student student = new Student();

            Signup signupObject = new Signup();
            signupObject.setParent(parent);
            signupObject.setStudent(student);
            signupObject.setInventionCode(inventionCode);

            Channel channel = null;
            if (StringUtils.indexOf(url, channelService.CHANNEL_ID_KEY) > 0) {
                url = UrlUtil.decodeURL(url);
                logger.info("channel url is : {}", url);
                channel = channelService.findChannelByURL(url);
                if (null == channel) {
                    logger.info("Can not get channel from url,url={},get default channel", url);
                    channel = channelService.getDefaultChannel();
                }
                if (StringUtils.indexOf(url, channelService.CHANNEL_KEYWORD) > 0) {
                    String kw = StringUtils.substring(url, StringUtils.indexOf(url, channelService.CHANNEL_KEYWORD) + channelService.CHANNEL_KEYWORD.length());
                    logger.info("SEO,keyword={}",kw);
                    if (StringUtils.isNotBlank(kw)) {
                        parent.setChannelKeyword(kw);
                    }
                }
            }
            if (null != channel) {
                parent.setChannel_id(channel.getId());
            }
            if(!TextUtils.isEmpty(cmdCode)){
            	Parent rparent = parentService.findByUsername(cmdCode);
            	if(rparent != null){
            		parent.setChannel_id(217);
            	}
            }
            try {
                Parent prt = parentAuthService.doSignupExceptStudent(signupObject);
                Family family = prt.getFamily();

                Cookie authCookie = CookieUtils.createVIPKIDCookie(CookieUtils.HTTP_COOKIE_AUTHENTICATION, new StringBuilder("parent").append(" ").append(parent.getId()).append(" ").append(parent.getToken()).toString());
                Cookie familyIdCookie = CookieUtils.createVIPKIDCookie("familyId", String.valueOf(family.getId()));
                Cookie parentIdCookie = CookieUtils.createVIPKIDCookie("parentId", String.valueOf(parent.getId()));

                authCookie.setMaxAge(Integer.MAX_VALUE);
                familyIdCookie.setMaxAge(Integer.MAX_VALUE);
                parentIdCookie.setMaxAge(Integer.MAX_VALUE);

                response.addCookie(authCookie);
                response.addCookie(familyIdCookie);
                response.addCookie(parentIdCookie);

                UserCacheUtil.storeParentName(String.valueOf(prt.getId()), prt.getUsername());

            } catch (Exception e) {
                logger.error("Create family error", e);
            }

            return Page.redirectTo("/welcome");
        }
        return SignupWebController.PATH;
    }

    @RequestMapping(value = "/checkUserExist", method = RequestMethod.GET)
    public
    @ResponseBody
    Boolean checkUserExist(@RequestParam("username") String username) {
        if (parentAuthService.findByUsername(username) == null) {
            return false;
        } else {
            return true;
        }
    }
    
}
