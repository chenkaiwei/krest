package com.chenkaiwei.krestdemoquickstart.error;

import com.chenkaiwei.krest.exceptions.KrestAuthenticationException;
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
    //当前版本shiro的异常和Krest的异常混用用，在之后的版本会逐步用Krest的异常+中文提示全面取代。
    @ExceptionHandler({KrestAuthenticationException.class,AuthenticationException.class})
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
        return new ResponseEntity(body, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        log.error("exceptionHandler");
        log.error(e.getLocalizedMessage());

        Map<String,Object> body=new HashMap<String,Object>();
        body.put("message",e.getLocalizedMessage());
        body.put("exception",e.getClass().getName());
        body.put("error",HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
