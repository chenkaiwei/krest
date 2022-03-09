package com.chenkaiwei.krest;

import com.chenkaiwei.krest.entity.JwtUser;
import com.chenkaiwei.krest.exceptions.KrestPasswordLoginException;
import com.chenkaiwei.krest.realms.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

@Slf4j
public class KrestUtil {

    public static JwtUser getJwtUser() {

        JwtUser jwtUser = (JwtUser) SecurityUtils.getSubject().getPrincipal();
        return jwtUser;
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static DefaultWebSecurityManager getSecurityManager() {
        return (DefaultWebSecurityManager)SecurityUtils.getSecurityManager();
    }

    public static void login(UsernamePasswordToken usernamePasswordToken) {
        try {
            SecurityUtils.getSubject().login(usernamePasswordToken);
        } catch (AuthenticationException e) {
            throw new KrestPasswordLoginException("登录失败，用户名或密码错误",e);
        }
    }
    public static String createJwtTokenByUser(JwtUser jwtUser){
        return JwtUtil.createJwtTokenByUser(jwtUser);
    }

    public static String getNewJwtTokenOfCurrentUser() {
        //无论如何都会生成个新的
        return createJwtTokenByUser(getJwtUser());
    }

    public static String getNewJwtTokenIfNeeded() {
        //仅在接近过期时会生成新token，未进入即将过期的区间时返回null
        //返回null时表示尚未接近过期，无需更新

        log.info("createJwtTokenIfNeeded");
//        String jwtToken=(String)getSubject().getSession().getAttribute("jwtToken");

        Subject subject= getSubject();
        if (subject instanceof WebDelegatingSubject){
            String jwtToken=(String)(((WebDelegatingSubject) subject).getServletRequest().getAttribute("com.chenkaiwei.krest.jwtToken"));

            if (jwtToken!=null){
               return JwtUtil.createJwtTokenIfNeeded(getJwtUser(),jwtToken);
            }
        }
        return  null;
    }
}
