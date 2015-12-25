package com.vipkid.service.exception;

import com.google.common.collect.Maps;
import com.vipkid.util.StringPrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 统一的异常处理拦截
 */
public class ServiceExceptionResolver extends DefaultHandlerExceptionResolver implements HandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(ServiceExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        super.resolveException(request,response,handler,ex);
        logger.error("Catch Exception,excepton message={}: ", ex);
        if (ex instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) ex;
            try {
                response.sendError(serviceException.getStatus(), serviceException.getMessage());
            } catch (IOException e) {
                logger.error("异常拦截处理时出错，I/O异常，异常信息：{}", e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return null;
        } else {
            Map<String, String> errorMap = Maps.newHashMap();
            StringPrintWriter stringPrintWriter = new StringPrintWriter();
            ex.printStackTrace(stringPrintWriter);
            errorMap.put("errorMsg", stringPrintWriter.getString());
            logger.error("server exception={}",ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }
    }
}
