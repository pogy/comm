package com.vipkid.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseEncodingFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(ResponseEncodingFilter.class);
    @Override
    public void destroy() {}

	@Override
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Credentials","true");
            response.setHeader("Access-Control-Allow-Headers","X-Requested-With, content-type, accept, origin, authorization, x-csrftoken");
            response.setHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("设置Response Header 时出错，error message = {}",e);
        }
    }

    @Override
	public void init(FilterConfig fConfig) throws ServletException {}

}
