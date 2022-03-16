package com.chenkaiwei.krest;

import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.config.KrestJwtProperties;
import com.chenkaiwei.krest.entity.JwtUser;
import com.chenkaiwei.krest.exceptions.KrestPasswordLoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KrestUtil {


    private static KrestConfigurer krestConfigurer;

    @Autowired
    public void setKrestConfigurer(KrestConfigurer krestConfigurer){
        KrestUtil.krestConfigurer =krestConfigurer;
    }
//    private static SymmetricCrypto getMessageBodyCryptoAlgorithm(){
//        return messageBodyCryptoAlgorithm;
//    }



    public static JwtUser getJwtUser() {

        JwtUser jwtUser = (JwtUser) SecurityUtils.getSubject().getPrincipal();
        return jwtUser;
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    //供本类其他方法使用
    private static Object getRequestAttributeByName(String attributeName) {
        return (((WebSubject) getSubject()).getServletRequest().getAttribute(attributeName));
    }

    public static String getJwtToken(){
        return (String) getRequestAttributeByName("com.chenkaiwei.krest.jwtToken");
    }

    public static String getTempSecretKey(){
        return (String) getRequestAttributeByName("com.chenkaiwei.krest.TEMP_SECRET_KEY");
    }
    private static SymmetricCrypto getMessageBodyCryptoAlgorithm(){
        return (SymmetricCrypto) getRequestAttributeByName("com.chenkaiwei.krest.messageBodyCryptoAlgorithm");
    }

    public static DefaultWebSecurityManager getSecurityManager() {
        return (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
    }


    public static String createJwtTokenOfCurrentUser() {
        //强制生成个新的，按即将过期生成新的用createNewJwtTokenIfNeeded()
        return createJwtTokenByUser(getJwtUser());
    }

    public static String createNewJwtTokenIfNeeded() {
        //仅在接近过期时会生成新token，未进入即将过期的区间时返回null
        //返回null时表示尚未接近过期，无需更新

        log.info("createJwtTokenIfNeeded");
//        String jwtToken=(String)getSubject().getSession().getAttribute("jwtToken");

        String jwtToken = getJwtToken();
        if (jwtToken != null) {
            return JwtUtil.createJwtTokenIfNeeded(getJwtUser(), jwtToken);
        }
        return null;
    }

    public static void login(UsernamePasswordToken usernamePasswordToken) {
        try {
            SecurityUtils.getSubject().login(usernamePasswordToken);
        }catch (UnsupportedTokenException e){
            throw new KrestPasswordLoginException("没有找到对应的realm，请确保krest.enable-username-password-realm=true", e);
        } catch (AuthenticationException e) {
            throw new KrestPasswordLoginException("登录失败，用户名或密码错误", e);
        }
    }

    public static String createJwtTokenByUser(JwtUser jwtUser) {
        return JwtUtil.createJwtTokenByUser(jwtUser);
    }

/*加解密部分，cryption部分*/
    //加解密方法
    public static String decryptMessageBody(String encryptedMessageBody) {

        return getMessageBodyCryptoAlgorithm().decryptStr(encryptedMessageBody);
    }
    //解密方法
    public static String encryptMessageBody(String messageBody) {

        return getMessageBodyCryptoAlgorithm().encryptBase64(messageBody);
    }
}
