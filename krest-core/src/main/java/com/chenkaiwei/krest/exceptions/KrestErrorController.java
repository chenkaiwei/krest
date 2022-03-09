package com.chenkaiwei.krest.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.realms.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
public class KrestErrorController implements ErrorController {

    @Autowired
    ErrorAttributes errorAttributes;

    private Throwable wrapKrestException(Throwable e){

        if(e instanceof ShiroException||e instanceof JWTVerificationException){
//TODO
            log.info("wrapKrestException");
            log.info(e.getLocalizedMessage());
        }
        return e;
    }


    @RequestMapping({"${server.error.path:${error.path:/error}}"})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {

            WebRequest webRequest = new ServletWebRequest(request);
            Throwable excetption = (Throwable) webRequest.getAttribute("javax.servlet.error.exception", 0);

            Throwable wrapedException = wrapKrestException(excetption);

            Map<String, Object> errorMap = errorAttributes.getErrorAttributes(webRequest, getErrorAttributeOptions());

//            String tokenIfNeeded=null;
//            if (KrestUtil.getSubject().isAuthenticated()){//这段代码无效。
//                tokenIfNeeded=KrestUtil.createJwtTokenIfNeeded();
//            }
            //以后可以考虑登录成功且报错时，也自动刷新token。好处更不容易过期，坏处影响一点性能。不过因为重定向到/error后会丢失subject，实现起来没这么简单。可以考虑存request.attributes里

            Map<String, Object> body=getErrorResponseBody(request,errorMap,wrapedException);

            return new ResponseEntity(body, status);
        }
    }


    private ErrorAttributeOptions getErrorAttributeOptions() {
        //如果自定义，则返回如下

        ErrorAttributeOptions eao = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE);//
        return eao;
    }

    //由用户实现，自定义错误响应时的数据结构
    protected Map<String, Object> getErrorResponseBody(HttpServletRequest request,Map<String, Object> errorAttributes,Throwable exception){

        log.error("getErrorResponseBody");
        return errorAttributes;
    }

    //需要的话也可以自定义，设计尽量向BasicErrorController靠拢降低学习成本
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }


}
