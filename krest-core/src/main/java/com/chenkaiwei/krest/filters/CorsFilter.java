package com.chenkaiwei.krest.filters;


import com.chenkaiwei.krest.config.KrestConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CorsFilter extends PathMatchingFilter {

    private KrestConfigurer krestConfiguration;

    public CorsFilter(KrestConfigurer krestConfiguration) {
        this.krestConfiguration=krestConfiguration;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        configHeaders(httpRequest, httpResponse);//options和其他方法共用
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            log.debug("收到一个OPTIONS请求1--"+httpRequest.getRequestURI());
            httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return false;
        }

        return super.preHandle(request, response);
    }

//    @Override
//    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
//
//        super.doFilterInternal(request, response, chain);
//    }

    private void configHeaders(HttpServletRequest request, HttpServletResponse response){
        //跨域的header设置
//        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));

        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
//        response.setHeader("Access-Control-Allow-Methods", "*");

        response.setHeader("Access-Control-Allow-Credentials", "true");

//        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,Cryption");
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type","application/json;charset=UTF-8");

        krestConfiguration.configHeaders(request,response);//接入用户手动配置

    }
}
