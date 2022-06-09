package com.chenkaiwei.krest.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.chenkaiwei.krest.exceptions.KrestException;
import com.chenkaiwei.krest.exceptions.KrestTokenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BearerHttpAuthenticationFilter;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtBearerHttpAuthenticationFilter extends BearerHttpAuthenticationFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return super.preHandle(request,response);
    }

    //filter会自动在on调用login，所以不用专门在这里调用executeLogin
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws ShiroException, TokenExpiredException {
        boolean res=super.isAccessAllowed(request, response, mappedValue);
        log.info("isAccessAllowed "+res);//返回false时会调onAccessDenied，onAccessDenied会调executeLogin
        return res;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        log.info("executeLogin");
        //请求头里有Authorization时都会尝试执行登陆。（前提nosession）

        return super.executeLogin(request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

        BearerToken bearerToken=(BearerToken) token;
        log.info("onLoginSuccess");
//        subject.
        if (subject instanceof WebDelegatingSubject){
            WebDelegatingSubject wdSubject=(WebDelegatingSubject)subject;
            wdSubject.getServletRequest().setAttribute("com.chenkaiwei.krest.jwtToken",bearerToken.getToken());
//            wdSubject.getSession().setAttribute("jwtToken",bearerToken.getToken());
        }

        return super.onLoginSuccess(token, subject, request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        //https://blog.csdn.net/m0_67391521/article/details/123837073
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        boolean res=super.onAccessDenied(request, response);//jwt的登录在这里面
        log.info("onAccessDenied "+res);
        if (!res){
            throw new KrestTokenException("token失效或异常，请重新登录");//jwt验证器的错误抛不上来，应该是shiro机制的不完善（）
        }
        return res;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.info("onLoginFailure");
        log.info(e.getLocalizedMessage());

//        throw new KrestTokenException("token失效或异常，请重新登录",e);
        return super.onLoginFailure(token, e, request, response);
/*
        jwt验证失败导致的登陆失败里，拿不到jwt验证失败的具体异常，因为要试过多个realmjwt的token错了还会去试其他realm，
        导致他把具体异常截断了，这里只拿得到一个"试过所有realm但是都没登陆成功"的异常。

        好点的设计应该是把所有异常都放在cause里，即使搞成一个数组也行
*/
    }

}
