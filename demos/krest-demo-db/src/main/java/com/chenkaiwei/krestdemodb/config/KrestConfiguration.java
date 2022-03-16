package com.chenkaiwei.krestdemodb.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.entity.JwtUser;
import com.chenkaiwei.krest.entity.KrestUsernamePasswordAuthenticationInfo;
import com.chenkaiwei.krestdemodb.entity.User;
import com.chenkaiwei.krestdemodb.service.RoleService;
import com.chenkaiwei.krestdemodb.service.UserService;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.*;

@Configuration
public class KrestConfiguration implements KrestConfigurer {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public Map<String, Collection<String>> configRolePermissionsMap() {

        return roleService.listAllRoelsWithPermissionsFromDB();
    }

    @Override
    public Algorithm configJwtAlgorithm() {
        return Algorithm.HMAC256("aaabbbccc");
    }


    @Override
    public KrestUsernamePasswordAuthenticationInfo doGetUsernamePasswordAuthenticationInfo(UsernamePasswordToken token) {

        // 获取账号密码
        String userName = token.getPrincipal().toString();

        User userFromDB = userService.queryUserByName(userName);
        Assert.notNull(userFromDB, "用户名错误，用户不存在");

        String passwordFromDB = userFromDB.getPassword();
        String salt = userFromDB.getSalt();

        JwtUser jwtUser=new JwtUser(userFromDB.getUsername(),userFromDB.getRoles());

        KrestUsernamePasswordAuthenticationInfo kpai=new KrestUsernamePasswordAuthenticationInfo(jwtUser,passwordFromDB,salt);

        return kpai;
    }

    @Override
    public CredentialsMatcher configPasswordCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);//密码保存策略须和注册时一致，2次md5加密
        return hashedCredentialsMatcher;
    }
}
