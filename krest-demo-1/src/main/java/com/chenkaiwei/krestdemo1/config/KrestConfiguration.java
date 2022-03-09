package com.chenkaiwei.krestdemo1.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.entity.JwtUser;
import com.chenkaiwei.krest.entity.KrestUsernamePasswordAuthenticationInfo;
import com.chenkaiwei.krestdemo1.entity.User;
import com.chenkaiwei.krestdemo1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class KrestConfiguration implements KrestConfigurer {

    @Autowired
    UserService userService;

    @Override
    public KrestUsernamePasswordAuthenticationInfo doGetUsernamePasswordAuthenticationInfo(UsernamePasswordToken usernamePasswordToken) {
        User userFromDB=userService.queryUserByName(usernamePasswordToken.getUsername());

        String passwordFromDB = userFromDB.getPassword();
        String salt = userFromDB.getSalt();
        JwtUser jwtUser=new JwtUser(userFromDB.getUsername(),userFromDB.getRoles());

        KrestUsernamePasswordAuthenticationInfo kpai=new KrestUsernamePasswordAuthenticationInfo(jwtUser,passwordFromDB,salt);

        return kpai;
    }

    @Override
    public CredentialsMatcher createPasswordCredentialsMatcher() {
        log.info("initPasswordCredentialsMatcher");
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);//密码保存策略须和注册时一致，2次md5加密

        return hashedCredentialsMatcher;
    }


    @Override//强制实现
    public Map<String, List<String>> createRolePermissionsMap() {
        return userService.getRolePermissionMap();
    }

    @Override
    public Algorithm createJwtAlgorithm() {
        return Algorithm.HMAC256("jwtSecretKeyDemo");//入参是jwt的秘钥
    }

    @Override
    public void configFilterChainDefinitionMap(Map<String, String> filterRuleMap) {
        //配置不参与token验证的uri
        filterRuleMap.put("/static/*", "anon");
        filterRuleMap.put("/error", "anon");
        filterRuleMap.put("/register", "anon");
        filterRuleMap.put("/login", "anon");
    }

}
