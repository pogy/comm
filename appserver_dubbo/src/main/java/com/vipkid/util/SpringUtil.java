package com.vipkid.util;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by zfl on 2015/5/19.
 * 用于在spring容器外获取容器中的bean
 */
public class SpringUtil {
    public static <T> T getSpringBean(HttpServletRequest request, String beanName, Class<T> clz) {
        ServletContext context = request.getSession().getServletContext();
        WebApplicationContext webapp = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        try {
            return webapp.getBean(beanName, clz);
        } catch (Exception e) {
            return null;
        }
    }
}
