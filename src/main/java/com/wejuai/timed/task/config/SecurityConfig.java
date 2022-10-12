package com.wejuai.timed.task.config;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author YQ.Huang
 */
@Configuration
public class SecurityConfig {

    @Bean
    ApiSecurityFilter apiSecurityFilter() {
        return new ApiSecurityFilter();
    }

    @Bean
    FilterRegistrationBean<ApiSecurityFilter> apiSecurityFilterRegistration(ApiSecurityFilter apiSecurityFilter) {
        FilterRegistrationBean<ApiSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(apiSecurityFilter);
        registration.addUrlPatterns("/task/*");
        return registration;
    }

    public static class ApiSecurityFilter implements Filter {

        private static final Logger logger = LoggerFactory.getLogger(ApiSecurityFilter.class);

        @Override
        public void init(FilterConfig filterConfig) {
            logger.info("api安全框架启动");
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String ip = servletRequest.getHeader("x-real-ip");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
            logger.debug("访问用户来源ip: " + ip);
            String username = servletRequest.getParameter("username");
            String password = servletRequest.getParameter("password");
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            if (!StringUtils.equals("xxxxxx", username) || !StringUtils.equals("xxxxxx", password)) {
                servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "This is Forbidden");
                return;
            }
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
        }
    }

}
