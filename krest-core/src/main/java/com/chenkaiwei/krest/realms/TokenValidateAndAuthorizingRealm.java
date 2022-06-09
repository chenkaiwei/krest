package com.chenkaiwei.krest.realms;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.chenkaiwei.krest.JwtUtil;
import com.chenkaiwei.krest.entity.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.*;

//验证token与鉴权realm
@Slf4j
public class TokenValidateAndAuthorizingRealm extends AuthorizingRealm {
//为了有鉴权方法，最高只能继承AuthorizingRealm，但是这个父类会自动生成一个SimpleCredentialsMatcher强制走matcher。只能说这套Realm的继承树不够成熟
//鉴权方法放到usernamePasswordRealm里实现也可以，这样这个Realm可以直接继承Realm，最清爽简洁。但是逻辑上应该放这里，毕竟除了login之外的所有业务接口都靠这个realm验证身份。且u+p的验证方式是可选项，jwt的必然存在


    @Resource(name="rolePermissionsMap")
    Map<String, List<String>> rolePermissionsMap;
/*↑
可以通过@Resource(name="beanName") 指定被注入的bean的名称, 要是未指定name属性, 默认使用成员属性的变量名,一般不用写name属性.
@Resource(name="beanName")指定了name属性,按名称注入但没找到bean, 就不会再按类型装配了.
===
@Autowired
@Qualifier("bean的名字")
按名称装配Bean,与@Autowired组合使用，解决按类型匹配找到多个Bean问题。
https://blog.csdn.net/weixin_40423597/article/details/80643990
*/

    public TokenValidateAndAuthorizingRealm() {
        //CredentialsMatcher，自定义比较策略（即验证jwt token的策略）
        super(new CredentialsMatcher() {
            //紧密绑定配套使用且只用一次的类没必要单独定义，直接写匿名类
            @Override
            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
                log.info("doCredentialsMatch token合法性验证");

                BearerToken bearerToken = (BearerToken) authenticationToken;
                String bearerTokenString = bearerToken.getToken();
                log.debug(bearerTokenString);

                boolean verified = JwtUtil.verifyTokenOfUser(bearerTokenString);

                return verified;

            }
        });

//        this.setCachingEnabled(true);
    }

    @Override
    public String getName() {
        return "TokenValidateAndAuthorizingRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        boolean res;
/*
        根据token类型来判断是否该这个realm处理，是BearerToken则归这里管，要是pwToken请去隔壁upLoginRealm
        框架里PSW验证的写法
        return token != null && BearerToken.class.isAssignableFrom(token.getClass());
                父类.class.isAssignableFrom(子类.class)
        子类实例 instanceof 父类类型
*/
        res = token != null && (token instanceof BearerToken);//换个语法，不直接抄框架

        log.debug("[TokenValidateRealm is supports]" + res);
        return res;
    }


    @Override//鉴权部分
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.debug("doGetAuthorizationInfo 权限验证");
        //设定：最低权限的注册用户user不用建立role的关联，使用时直接按是否为验证用户（RequiresAuthentication）使用。减轻数据读写压力。

        JwtUser user = (JwtUser) SecurityUtils.getSubject().getPrincipal();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRoles(user.getRoles());//roles跟着user走，放到token里。普通功能直接用token中的。只有在重要操作时才需去数据库验一遍，减轻压力


//        Collection<String> stringPermissions= roleService.getPermissionsByRoles(user.getRoles());


        Set<String> stringPermissions = new HashSet<String>();
        for (String role : user.getRoles()) {
            stringPermissions.addAll(rolePermissionsMap.get(role));
        }
        simpleAuthorizationInfo.addStringPermissions(stringPermissions);
        return simpleAuthorizationInfo;
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException, TokenExpiredException {

        log.debug("doGetAuthenticationInfo 将token装载成用户信息");

        BearerToken bearerToken = (BearerToken) authenticationToken;
        String bearerTokenString = bearerToken.getToken();

//        Assert.isTrue(JwtUtil.verifyTokenOfUser(bearerTokenString), "token检测不通过");
// 任何抛出异常的语句都会被shiro框架截获并转换。所以不要用断言
//        boolean verified=JwtUtil.verifyTokenOfUser(bearerTokenString);
        //验证的操作在filter里做了，这里是装载用户信息（也和方法名doGetAuthenticationInfo符合。）。

        JwtUser user = JwtUtil.recreateUserFromToken(bearerTokenString);//只带着用户名和roles

        SimpleAuthenticationInfo res = new SimpleAuthenticationInfo(user, bearerTokenString, this.getName());
        /*Constructor that takes in an account's identifying principal(s) and its corresponding credentials that verify the principals.*/
//        这个返回值是造Subject用的，返回值供createSubject使用
        return res;
    }

}
