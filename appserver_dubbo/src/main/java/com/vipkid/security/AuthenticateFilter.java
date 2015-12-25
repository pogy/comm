package com.vipkid.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.vipkid.context.AppContext;
import com.vipkid.model.User;
import com.vipkid.util.Configurations;
import com.vipkid.util.CookieUtils;
import com.vipkid.util.RequestUtil;
import com.vipkid.util.SpringUtil;

/**
 * Created by davieli on 2015/5/19.
 * 认证、鉴权Filter
 */
public class AuthenticateFilter implements Filter {

    private static final String URI_PRIVATE = "/api/service/private/";
    private static final String URI_PARENT = "/parent/";
    private static final String URI_MOBILE = "/mobile/";
    private static final String URI_RECRUITMENT = "/recruitment/";
    private static final String URI_VIPKID_MOBILE = "/vipkid/mobile/";//用于约课httpclient 请求 研发 自测
    private static final String URI_WELCOME = "/welcome";
    private static final String HTTP_HEADER_TOKEN = "Authorization";

    private static final int DEFAULT_MAX_RETRY = 3;

    /**
     * 不需要做鉴权的url
     */
    protected Pattern[] excludes;
    /**
     * 身份验证的bean name
     */
    private String authenticateBeanName = "authenticateService";
    /**
     * 鉴权服务的bean name
     */
    private String authorizeBeanName = "authorizeService";


    private int maxRetry;

    /**
     * 用户处理器bean name
     */
    private String userHandlerBeanName = "userHandler";
    private static final int MY_FORBIDDEN_CODE = 488;
    private static final Set<String> IMG_EXT = new HashSet<String>();

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateFilter.class);

    static {
        IMG_EXT.add("png");
        IMG_EXT.add("gif");
        IMG_EXT.add("jpg");
        IMG_EXT.add("bmp");
        IMG_EXT.add("jpeg");
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        if (config.getInitParameter("userHandlerBeanName") != null) {
            userHandlerBeanName = config.getInitParameter("userHandlerBeanName");
            logger.info("Get initPareamter = {}", userHandlerBeanName);
        }
        if (config.getInitParameter("authenticateBeanName") != null) {
            authenticateBeanName = config.getInitParameter("authenticateBeanName");
        }
        if (config.getInitParameter("authorizeBeanName") != null) {
            authorizeBeanName = config.getInitParameter("authorizeBeanName");
        }
        if (config.getInitParameter("maxretry") != null) {
            try {
                maxRetry = Integer.parseInt(config.getInitParameter("maxretry"));
            } catch (Exception e) {
                maxRetry = DEFAULT_MAX_RETRY;
            }
        } else {
            maxRetry = DEFAULT_MAX_RETRY;
        }
        String excludeConfig = config.getInitParameter("excludeURL");
        if (excludeConfig != null && excludeConfig.trim().length() > 0) {
            String[] excludePatterns = excludeConfig.split(",");
            excludes = new Pattern[excludePatterns.length];
            for (int i = 0; i < excludePatterns.length; i++) {
                try {
                    excludes[i] = Pattern.compile(transStringToPattern(excludePatterns[i].toLowerCase()));
                } catch (Exception e) {
                    logger.error("exclude url config error! " + e.getMessage());
                    System.exit(1);
                }
            }
        } else {
            excludes = new Pattern[0];
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            long begin = System.currentTimeMillis();
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            String URI = RequestUtil.getRequestURI(request);
            if (request.getMethod().equals("OPTIONS")) {
                response.setStatus(HttpStatus.OK.value());
                return;
            } else {
                if (!Configurations.Auth.BYPASS) {
                    String domain = request.getServerName();
                    boolean hasError = false;
                    if ((URI.startsWith(URI_PRIVATE) || URI.startsWith(URI_PARENT) || URI.startsWith(URI_WELCOME) || URI.startsWith(URI_MOBILE) || URI.startsWith(URI_VIPKID_MOBILE) || URI.startsWith(URI_RECRUITMENT)) && !isExclude(URI)) {
                        String token = request.getHeader(HTTP_HEADER_TOKEN);
                        if (StringUtils.isBlank(token)) {
                        	token = CookieUtils.get(request.getCookies(), CookieUtils.HTTP_COOKIE_AUTHENTICATION);
                        }
                        if (StringUtils.isBlank(token)) {
                            response.sendError(HttpStatus.UNAUTHORIZED.value());
                            return;
                        }
                        //身份验证，通过，则继续，否则拦截
                        AuthenticateService authenticateService = SpringUtil.getSpringBean(request, authenticateBeanName, AuthenticateService.class);
                        User user = authenticateService.authenticate(token);
                        if (user == null) {
                            response.sendError(HttpStatus.UNAUTHORIZED.value());
                            return;
                        }
                        CustomizedPrincipal principal = new CustomizedPrincipal(user);

                        AppContext.setPrincipal(principal);

                        AuthorizeService authorizeService = SpringUtil.getSpringBean(request, authorizeBeanName, AuthorizeService.class);
                        if (null == authorizeService) {
                            logger.error("++++++++++++++++++++++++++++++++can not get bean from the container");
                        }
                        //鉴权，鉴权成功，则继续，否则拦截
                        for (int i = 0; i < maxRetry; i++) {
                            try {
                                if (!authorizeService.authorize(URI, user)) {
                                    logger.info("user{},domain{},url{},cost{},authenticate fail", user.getUsername(), domain, URI, System.currentTimeMillis() - begin);
                                    response.sendError(MY_FORBIDDEN_CODE);
                                    return;
                                }
                                hasError = false;
                                break;
                            } catch (Exception e) {
                                hasError = true;
                                logger.warn("unhandled exception {}", e);
                            }
                        }
                        if (hasError) {
                            logger.info("user{},domain{},url{},cost{},authenticate fail because retried {} times but none success", user.getUsername(), domain, URI, System.currentTimeMillis() - begin, maxRetry);
                            response.sendError(MY_FORBIDDEN_CODE);//.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
            }
            chain.doFilter(req, resp);
        } catch (IOException e) {
            logger.error("Do filter IO error,error msg = {}", e);
        } catch (ServletException e) {
            logger.error("Do filter ServletException ,error msg = {}", e);
        } finally {
            AppContext.releaseAppResource();
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 根据配置判断当前请求是否需要鉴权。某些请求如登录/登出/JS/CSS/图片等是不需要鉴权的
     *
     * @param URI
     * @return
     */
    protected boolean isExclude(String URI) {
        boolean isExclude = false;
        if (isStaticResource(URI)) {
            isExclude = true;
        } else if (excludes.length > 0) {
            for (Pattern pattern : excludes) {
                if (pattern.matcher(URI).matches()) {
                    isExclude = true;
                    break;
                }
            }
        } else {
            isExclude = true;
        }
        return isExclude;
    }

    protected String transStringToPattern(String string) {
        String result;
        result = string.replaceAll("\\.", "\\\\.");
        result = result.replaceAll("\\*", ".*");
        return result;
    }

    private boolean isStaticResource(String resource) {
        int dotIndex = resource.lastIndexOf('.');
        String ext = resource.substring(dotIndex + 1);
        return isCss(ext) || isJs(ext) || isImg(ext);

    }

    private boolean isCss(String resource) {
        return resource.equalsIgnoreCase("css");
    }

    private boolean isJs(String resource) {
        return resource.equalsIgnoreCase("js");
    }

    private boolean isImg(String resource) {
        return IMG_EXT.contains(resource.toLowerCase());
    }
}
