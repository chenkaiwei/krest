package com.chenkaiwei.krestdemo1.config;

import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Configuration
public class KrestConfiguration implements KrestConfigurer {

    @Autowired
    UserService userService;

    @Override
    public KrestUsernamePasswordAuthenticationInfo doGetUsernamePasswordAuthenticationInfo(UsernamePasswordToken usernamePasswordToken) {
        User userFromDB = userService.queryUserByName(usernamePasswordToken.getUsername());

        String passwordFromDB = userFromDB.getPassword();
        String salt = userFromDB.getSalt();
        JwtUser jwtUser = new JwtUser(userFromDB.getUsername(), userFromDB.getRoles());

        KrestUsernamePasswordAuthenticationInfo kpai = new KrestUsernamePasswordAuthenticationInfo(jwtUser, passwordFromDB, salt);

        return kpai;
    }

    @Override
    public CredentialsMatcher configPasswordCredentialsMatcher() {
        log.info("initPasswordCredentialsMatcher");
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);//??????????????????????????????????????????2???md5??????

        return hashedCredentialsMatcher;
    }


    @Override//????????????
    public Map<String, Collection<String>> configRolePermissionsMap() {
        return userService.getRolePermissionMap();
    }

    @Override
    public Algorithm configJwtAlgorithm() {
        return Algorithm.HMAC256("jwtSecretKeyDemo");//?????????jwt?????????
    }

    @Override
    public void configFilterChainDefinitionMap(Map<String, String> filterRuleMap) {
        //???????????????token?????????uri
        filterRuleMap.put("/static/*", "anon");
        filterRuleMap.put("/error", "anon");
        filterRuleMap.put("/register", "anon");
        filterRuleMap.put("/login", "anon");


    }

    //??????????????????????????????????????????????????????
    @Override
    public AsymmetricCrypto configTempSecretKeyCryptoAlgorithm() {

        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdor7t0PvE590FArr-hv_pqtsk1R-iXaFX0upUJ8XkmHrMU6qpZM27oZzMOm62r_DzLTWNKZal-QH987OXQj35TnhrbwLxl57PZ6wfV_hggHlMgtnp_7yYJAPgS2mVN0E5VInPmuMcES598pB-1lvnUJ0-386ny_FS9-IUJRMDBAgMBAAECgYEAlmR00aT49FFYiOc_7Lc04v9myltzLtRd3at2PZ4fze-QZN9s7IIn9Y1BHNTwy8ReiuCB4RNAAeiXFks3YFsWe5yHHsW_Y3ntN0Tla_nkVkjm2iG_dIKHS5iY3ERoheR8i0d0T1BnmwbyCwdl7-QWmjVdeZ8YPFxAQ72Wr6DLY6UCQQDwgAGW8rdxbKQjqSIoFTRfSTLfI6Ba3dsb7xNiQE3RSiq_k4LskbnNCAqf7WNy85gNjENX-W8lmP1t6rJqC5tvAkEAoSrFs6HpF2I469ALkZH6iapi7k97W4nlnnOeaNAx9uuXy9hyQiKSGZafSidxPvmbV1qV2CVxc53FhTCD5b4OzwJAK4LtRrMZD1NZiv4hqODVPdwPcSGP9ICpEK-7cQ4zRgdGHq0Ahe6DkB3BVlfrozOBMgpLcNI3ErVQPJ-2scrxzwJAWybfzisCtBD_dI-kG17evkG51mLpt-oUDjwCGfG2cJrqrYXriXAYBZTk3oHUUPPHYe5_1VHICsXu0tePob6OjQJAeZXbdfkNx7-uZ295rTj3Yq3H11uB6hB317eODHtnnCMVH0ww50C9pGnRPO2dEaShCwLUeOucxBim_usmIBaPOw";
        //??? ????????????????????????????????????????????????????????????????????? ?????????

        RSA rsa = new RSA(privateKey, null);
//        byte[] encrypt = StrUtil.bytes(cryptionString, CharsetUtil.CHARSET_UTF_8);//????????????????????????

        return rsa;//??????hutool?????????AsymmetricCrypto????????????????????????????????????
    }

    //????????????????????????????????????????????????????????????????????????????????????????????????create??????init???
    @Override
    public SymmetricCrypto createMessageBodyCryptoAlgorithm(String tempSecretKey) {

        AES aes = new AES("CBC", "PKCS5Padding",
                // ????????????????????????
                tempSecretKey.getBytes(),
                // iv?????????????????????????????????
                "1111222233334444".getBytes());
        return aes;
    }

    @Override
    public FastJsonConfig fastJsonConverterConfig() {
        log.info("fastJsonConverterConfig in demo");
        return new FastJsonConfig();
    }

//    //    @Bean
//    public FastJsonHttpMessageConverter myFastJsonHttpMessageConverter() {
//        FastJsonHttpMessageConverter res = new FastJsonHttpMessageConverter();
//        return res;
//    }
    //????????????
    @Override
    public void configHeaders(HttpServletRequest request, HttpServletResponse response) {
        //??????????????????????????????????????????????????????????????????
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:6009");

    }
}
