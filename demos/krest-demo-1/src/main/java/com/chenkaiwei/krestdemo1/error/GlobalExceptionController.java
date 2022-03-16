package com.chenkaiwei.krestdemo1.error;

import com.chenkaiwei.krest.exceptions.KrestAuthenticationException;
import com.chenkaiwei.krest.exceptions.KrestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    //按你自己的方式来统一返回格式，此处仅做示例，为了好懂就不抽象了。
    @ExceptionHandler(KrestAuthenticationException.class)
    public ResponseEntity KrestAuthenticationExceptionHandler(KrestAuthenticationException e) {
        log.error("krestExceptionHandler");
        log.error(e.getLocalizedMessage());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("status",HttpStatus.FORBIDDEN.value());//也可以自定义更详细的状态码
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.FORBIDDEN.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.FORBIDDEN);//仅是示例，按需求定义
    }

    // 身份验证错误
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity authenticationExceptionHandler(AuthenticationException e) {
        log.error("AuthenticationException");
        log.error(e.getLocalizedMessage());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("status",HttpStatus.FORBIDDEN.value());
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.FORBIDDEN.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.FORBIDDEN);//仅是示例，按需求定义
    }

    //权限验证错误
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity unauthorizedExceptionHandler(UnauthorizedException e) {
        log.error("unauthorizedExceptionHandler");
        log.error(e.getLocalizedMessage());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("status",HttpStatus.UNAUTHORIZED.value());
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.UNAUTHORIZED.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.UNAUTHORIZED);//仅是示例，按需求定义
    }


    //对应路径不存在
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        log.error("noHandlerFoundExceptionHandler");
        log.error(e.getLocalizedMessage());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.NOT_FOUND.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.NOT_FOUND);//仅是示例，按需求定义
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        log.error("exceptionHandler");
        log.error(e.getLocalizedMessage());
        log.error(e.getStackTrace().toString());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR);//仅是示例，按需求定义
    }
}
