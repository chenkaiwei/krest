package com.chenkaiwei.krest.config;

import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.entity.KrestUsernamePasswordAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

//参考WebMvcConfigurer
public interface KrestConfigurer {

    Algorithm jwtAlgorithm = null;


    /*==强制实现部分↓===*/
    /** 角色-权限对照表，以map的形式。
     * @return*/
    Map<String, Collection<String>> configRolePermissionsMap();

    /* jwt的加密策略*/
    Algorithm configJwtAlgorithm();


    /*shiro部分*/
    default void configFilterChainDefinitionMap(Map<String, String> filterRuleMap){
        //默认这四个匹配规则不验证jwt，如果覆盖后还需要这四个请自行重新加上
        filterRuleMap.put("/static/*", "anon");
        filterRuleMap.put("/error", "anon");
        filterRuleMap.put("/register", "anon");
        filterRuleMap.put("/login", "anon");//勿漏，否则会先走jwt验证而导致请求被拒
    }
    /*
        开启UsernamePassword模式后使用 ↓
    */
    default KrestUsernamePasswordAuthenticationInfo doGetUsernamePasswordAuthenticationInfo(UsernamePasswordToken usernamePasswordToken){
        return null;
    }

    default CredentialsMatcher configPasswordCredentialsMatcher(){
        return null;
    }

    /*↓Cryption策略部分*/

    /*用于加解密临时秘钥的不对称加密，和客户端的公钥成对*/
    default AsymmetricCrypto configTempSecretKeyCryptoAlgorithm(){
        return null;
    };

    /*用客户端传来的临时秘钥 生成当次请求的消息体加解密策略*/
    default SymmetricCrypto createMessageBodyCryptoAlgorithm(String tempSecretKey){
        return null;
    };


    /*可以自行配置返回消息json的策略，主要是空值时统一规范*/
    default FastJsonConfig fastJsonConverterConfig(){

        FastJsonConfig fj = new FastJsonConfig();

//　　1、WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
//　　2、WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
//　　3、DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
//　　4、WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
//　　5、WriteMapNullValue：是否输出值为null的字段,默认为false。
        fj.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty);//
        return fj;
    }

    /*固定请求头设置，主要用于跨域*/
    default void configHeaders(HttpServletRequest request, HttpServletResponse response){

    }
}
