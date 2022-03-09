package com.chenkaiwei.krest.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.entity.KrestUsernamePasswordAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import java.util.List;
import java.util.Map;

//参考WebMvcConfigurer
public interface KrestConfigurer {

    /*shiro部分*/
    default KrestUsernamePasswordAuthenticationInfo doGetUsernamePasswordAuthenticationInfo(UsernamePasswordToken usernamePasswordToken){
        return null;
    }

    default CredentialsMatcher createPasswordCredentialsMatcher(){
        return null;
    }

    default void configFilterChainDefinitionMap(Map<String, String> filterRuleMap){
        //默认这四个匹配规则不验证jwt，如果覆盖这四个也要重新加上
        filterRuleMap.put("/static/*", "anon");
        filterRuleMap.put("/error", "anon");
        filterRuleMap.put("/register", "anon");
        filterRuleMap.put("/login", "anon");//确保即使login里带了token也能不参与验证
    }

    /** 角色-权限对照表，以map的形式。*/
    Map<String, List<String>> createRolePermissionsMap();

    /*↓ jwt部分*/
    /*
    * jwt的加密策略*/
    Algorithm createJwtAlgorithm();

}
