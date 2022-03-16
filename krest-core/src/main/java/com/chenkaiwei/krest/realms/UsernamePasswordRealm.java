package com.chenkaiwei.krest.realms;

import cn.hutool.core.lang.Assert;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.entity.KrestUsernamePasswordAuthenticationInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
//Username Password Realm，用户名密码登陆专用Realm
public class UsernamePasswordRealm extends AuthenticatingRealm {//只管验证不管鉴权

    @Autowired
    KrestConfigurer krestConfigurer;


    /*构造器*/
    public UsernamePasswordRealm() {
        super();
        log.debug("new UsernamePasswordRealm()");
        //匹配器在AutoConfig里配了
//        this.setCredentialsMatcher(krestConfigurer.initPasswordCredentialsMatcher());
    }

    /**
     * 调用{@code doGetAuthenticationInfo(AuthenticationToken)}之前会shiro会调用{@code supper.supports(AuthenticationToken)}
     * 来判断该realm是否支持对应类型的AuthenticationToken,如果相匹配则会走此realm
     *
     */
    @Override
    public Class getAuthenticationTokenClass() {
        return UsernamePasswordToken.class;
    }

//    @Override
//    public boolean supports(AuthenticationToken token) {
//        //继承但啥都不做就为了打印一下info
//        boolean res = super.supports(token);//会调用↑getAuthenticationTokenClass来判断
//        log.debug("[UsernamePasswordRealm is supports]" + res);
//        return res;
//    }

    /**
     * 用户名和密码验证，login接口专用。
     *
     * 获取数据库中的用户验证信息，即登录信息加密计算后用于比较结果的目标
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        log.info("doGetAuthenticationInfo");

        UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken) token;

        KrestUsernamePasswordAuthenticationInfo kpai=krestConfigurer.doGetUsernamePasswordAuthenticationInfo(usernamePasswordToken);

        Assert.notNull(kpai,"您尚未实现krestConfigurer.doGetUsernamePasswordAuthenticationInfo接口");

        SimpleAuthenticationInfo res = new SimpleAuthenticationInfo(kpai.getJwtUser(),kpai.getPasswordInDB(), ByteSource.Util.bytes(kpai.getSalt()),getName());
        return res;

    }
}
