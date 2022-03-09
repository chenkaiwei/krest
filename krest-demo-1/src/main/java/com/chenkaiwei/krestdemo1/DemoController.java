package com.chenkaiwei.krestdemo1;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.chenkaiwei.krest.exceptions.KrestException;
import com.chenkaiwei.krest.exceptions.KrestPasswordLoginException;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krestdemo1.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {
//public class DemoController implements ErrorController {
//
///*error试验区*/
//    @Autowired
//    private ErrorAttributes errorAttributes;
//
//    @RequestMapping("/error")
//    Map error(HttpServletRequest request) throws Throwable {
//        log.info("error");
//        WebRequest webRequest = new ServletWebRequest(request);
//        Throwable error = (Throwable) webRequest.getAttribute("javax.servlet.error.exception", 0);
//
//        if (error instanceof ShiroException||
//                error instanceof JWTVerificationException||
//                error.getCause() instanceof ShiroException)
//        {
//            throw error;
//        }
//
//        ErrorAttributeOptions eao = ErrorAttributeOptions.of(
//                ErrorAttributeOptions.Include.EXCEPTION);
//        Map<String, Object> errorMap = errorAttributes.getErrorAttributes(webRequest, eao);
//
//
//        return errorMap;
//    }
////
//    @ExceptionHandler(KrestException.class)
//    public Map<String,String> krestException(KrestException e) {
////TODO shiro里配的会自动跳到error路径上处理，不走这个，待解决。
//        Map<String,String> res=new HashMap<>();
//        res.put("result",e.getLocalizedMessage());
//
//        return res;
//    }



    /**
     * 登陆
     */
    @PostMapping("/login")
    public Map login(@RequestBody User userInput) throws Exception {

        log.info(userInput.toString());
        String username = userInput.getUsername();
        String password = userInput.getPassword();

        Assert.notNull(username, "username不能为空");
        Assert.notNull(password, "password不能为空");

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

//        try{
            KrestUtil.login(usernamePasswordToken);
//        }catch (KrestPasswordLoginException e){
//            log.error("用户名或密码错误");
//
//            Map<String,String> res=new HashMap<>();
//            res.put("result","登录失败，用户名或密码错误");
//            return res;
//        }

        //只要登陆成功，对应jwtUser就会装载到框架里。
        //KrestUtil.getJwtUser();//如果要获取用户信息的话

        Map<String,String> res=new HashMap<>();
        res.put("token",KrestUtil.getNewJwtTokenOfCurrentUser());
        res.put("result","login success or other result message");
        return res;
    }

    @GetMapping("/whoami")
    public Map whoami(){


        Map<String,String> res=new HashMap<>();
        res.put("result","you are "+KrestUtil.getJwtUser().toString());
        res.put("token",KrestUtil.getNewJwtTokenIfNeeded());

        return res;
    }

    @GetMapping("/permissionDemo")
    @RequiresPermissions("pd")
    public Map permissionDemo(){

        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [permissionDemo]");
        res.put("token",KrestUtil.getNewJwtTokenIfNeeded());

        return res;
    }
}
